<#
.SYNOPSIS
    Adds new words to the shared VocabMaster word bank (Firestore collection "word_bank")
    without needing a new app release.

.DESCRIPTION
    Reads a plain text file of new English words (one per line, optionally with a hand-written
    example sentence), fetches a missing example sentence from the free Dictionary API
    (https://api.dictionaryapi.dev - no key required), and writes each word as a new document
    to Firestore. Existing words are never touched: new documents get IDs continuing after the
    current highest ID in word_bank, and the Firestore security rules only allow *creating* new
    word_bank documents from a signed-in client, never updating or deleting - so this script
    cannot corrupt existing words even if run incorrectly.

    Every device that opens the Vocabulary tab after this runs will automatically pick up the
    new words and translate them into that device's language on the fly - no further action
    needed.

.PARAMETER InputFile
    Path to the word list file. Defaults to new_words.txt next to this script.
    Each line is either just a word ("Ambitious") or a word and example sentence separated by
    a pipe ("Ambitious|She has always been ambitious about her career."). Blank lines and lines
    starting with # are ignored.

.PARAMETER Email
    Email of an existing VocabMaster account (any account you use to log into the app works -
    the Firestore rules only require a signed-in user, not an admin role). If omitted, a saved
    credential is used if one exists (see -SaveCredential), otherwise you are prompted.

.PARAMETER Password
    Password for the account above. If omitted, a saved credential is used if one exists,
    otherwise you are prompted (input hidden).

.PARAMETER SaveCredential
    After a successful sign-in, saves the email/password to
    tools\.word_tool_credential.xml, encrypted with Windows DPAPI so it can only be decrypted
    by your Windows account on this machine (the file is useless if copied elsewhere or opened
    by another user). Future runs then need no -Email/-Password and won't prompt.

.PARAMETER ClearSavedCredential
    Deletes the saved credential file, if any, and exits.

.EXAMPLE
    .\add_words.ps1 -InputFile .\new_words.txt

.EXAMPLE
    .\add_words.ps1 -Email you@example.com -SaveCredential

.EXAMPLE
    .\add_words.ps1 -ClearSavedCredential
#>

param(
    [string]$InputFile = "$PSScriptRoot\new_words.txt",
    [string]$Email,
    [securestring]$Password,
    [switch]$SaveCredential,
    [switch]$ClearSavedCredential
)

$ErrorActionPreference = "Stop"

$ProjectId = "global-yds-app"
$ApiKey = "AIzaSyAysczGPLwix9UL4kWrIPVeLfCm2NbW3VM"
$FirestoreBase = "https://firestore.googleapis.com/v1/projects/$ProjectId/databases/(default)/documents"
$CredentialFile = "$PSScriptRoot\.word_tool_credential.xml"

if ($ClearSavedCredential) {
    if (Test-Path $CredentialFile) {
        Remove-Item $CredentialFile
        Write-Host "Saved credential removed." -ForegroundColor Green
    } else {
        Write-Host "No saved credential found."
    }
    exit 0
}

function Get-IdToken {
    param([string]$Email, [securestring]$Password, [switch]$SaveCredential)

    $usingSaved = $false
    if (-not $Email -and -not $Password -and (Test-Path $CredentialFile)) {
        try {
            $saved = Import-Clixml -Path $CredentialFile
            $Email = $saved.UserName
            $Password = $saved.Password
            $usingSaved = $true
            Write-Host "Using saved credential for $Email (run with -ClearSavedCredential to remove it)."
        } catch {
            Write-Host "Saved credential file couldn't be read (maybe from a different Windows account) - ignoring it." -ForegroundColor Yellow
        }
    }

    if (-not $Email) {
        $Email = Read-Host "VocabMaster account e-mail"
    }
    if (-not $Password) {
        $Password = Read-Host "Password" -AsSecureString
    }
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Password)
    )

    $body = @{ email = $Email; password = $plainPassword; returnSecureToken = $true } | ConvertTo-Json
    $response = Invoke-RestMethod -Method Post `
        -Uri "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$ApiKey" `
        -ContentType "application/json" -Body $body

    if ($SaveCredential -and -not $usingSaved) {
        New-Object System.Management.Automation.PSCredential($Email, $Password) | Export-Clixml -Path $CredentialFile
        Write-Host "Credential saved (DPAPI-encrypted, usable only by your Windows account on this machine) to $CredentialFile" -ForegroundColor Green
    }

    return $response.idToken
}

function Get-NextWordId {
    param([string]$IdToken)

    $headers = @{ Authorization = "Bearer $IdToken" }
    $maxId = -1
    $pageToken = $null

    do {
        $uri = "$FirestoreBase/word_bank?pageSize=300"
        if ($pageToken) { $uri += "&pageToken=$pageToken" }
        $page = Invoke-RestMethod -Method Get -Uri $uri -Headers $headers

        foreach ($doc in $page.documents) {
            $docId = $doc.name.Split("/")[-1]
            $numericId = 0
            if ([int]::TryParse($docId, [ref]$numericId) -and $numericId -gt $maxId) {
                $maxId = $numericId
            }
        }
        $pageToken = $page.nextPageToken
    } while ($pageToken)

    return $maxId + 1
}

function Get-ExampleSentence {
    param([string]$Word)

    try {
        $entries = Invoke-RestMethod -Method Get -Uri "https://api.dictionaryapi.dev/api/v2/entries/en/$Word"
        foreach ($entry in $entries) {
            foreach ($meaning in $entry.meanings) {
                foreach ($definition in $meaning.definitions) {
                    if ($definition.example) { return $definition.example }
                }
            }
        }
    } catch {
        # Word not found in the free dictionary, or API unreachable - caller handles the empty result.
    }
    return ""
}

function New-WordDocument {
    param([string]$IdToken, [string]$Id, [string]$Word, [string]$Example)

    $headers = @{ Authorization = "Bearer $IdToken" }
    $body = @{
        fields = @{
            id              = @{ stringValue = $Id }
            englishWord     = @{ stringValue = $Word }
            exampleSentence = @{ stringValue = $Example }
        }
    } | ConvertTo-Json -Depth 5

    Invoke-RestMethod -Method Post `
        -Uri "$FirestoreBase/word_bank?documentId=$Id" `
        -Headers $headers -ContentType "application/json" -Body $body | Out-Null
}

# --- Main ---

if (-not (Test-Path $InputFile)) {
    Write-Host "Word list not found: $InputFile" -ForegroundColor Red
    Write-Host "Create it with one word per line, e.g.:"
    Write-Host "  Ambitious|She has always been ambitious about her career."
    Write-Host "  Diligent"
    exit 1
}

Write-Host "Signing in..."
$idToken = Get-IdToken -Email $Email -Password $Password -SaveCredential:$SaveCredential

Write-Host "Checking current word bank..."
$nextId = Get-NextWordId -IdToken $idToken
Write-Host "Next available ID: $nextId"

$lines = Get-Content $InputFile | Where-Object { $_.Trim() -ne "" -and -not $_.Trim().StartsWith("#") }

$added = 0
$skipped = 0

foreach ($line in $lines) {
    $parts = $line.Split("|", 2)
    $word = $parts[0].Trim()
    $example = if ($parts.Length -gt 1) { $parts[1].Trim() } else { "" }

    if (-not $example) {
        Write-Host "  '$word': no example given, looking it up..." -NoNewline
        $example = Get-ExampleSentence -Word $word
        if ($example) {
            Write-Host " found." -ForegroundColor Green
        } else {
            Write-Host " none found - adding with an empty example; edit it in the Firebase console later." -ForegroundColor Yellow
        }
    }

    $id = "$nextId"
    try {
        New-WordDocument -IdToken $idToken -Id $id -Word $word -Example $example
        Write-Host "  Added '$word' as word_bank/$id" -ForegroundColor Green
        $nextId++
        $added++
    } catch {
        Write-Host "  Failed to add '$word': $_" -ForegroundColor Red
        $skipped++
    }
}

Write-Host ""
Write-Host "Done. Added $added word(s), $skipped failed." -ForegroundColor Cyan
Write-Host "Every device will pick up the new words next time it opens the Vocabulary tab."
