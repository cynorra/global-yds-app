# Word bank tooling

`add_words.ps1` adds new words to the shared vocabulary bank (Firestore `word_bank`
collection) without needing a new app release. Every device automatically downloads and
translates only the new words the next time it opens the Vocabulary tab.

## One-time setup

1. Make sure the updated `firestore.rules` (repo root) has been pasted into
   **Firebase Console → Firestore Database → Rules** and published. Without this, the app's
   backend-driven word bank, grammar bank, and exam titles will fail silently.
2. You need an existing VocabMaster account (any account you already use to log into the app
   works - no special admin role is required, the security rules only check that you're
   signed in).

## Adding words

1. Copy `new_words.txt.example` to `new_words.txt` in this same folder and edit it - one word
   per line, optionally `Word|Your example sentence.`. Leave the example off and the script
   will try to fetch one automatically from the free Dictionary API.
2. Run from PowerShell, in this folder:
   ```
   .\add_words.ps1
   ```
   It will prompt for your account e-mail/password, then add each word as a new document.
   Consider using a dedicated account for this (created via the app's own Register screen) so
   your personal login never touches this script - no special role is needed, any signed-in
   account works.
3. Done - no app update needed. `new_words.txt` is gitignored-safe to reuse for the next batch
   (just clear it out or add a new batch of lines).

### Not typing the password every time

Add `-SaveCredential` the first time:
```
.\add_words.ps1 -SaveCredential
```
This saves the email/password to `tools\.word_tool_credential.xml`, encrypted with Windows
DPAPI - it can only be decrypted by your Windows account, on this machine (useless if copied
elsewhere, gitignored regardless). Every run after that picks it up automatically with no
prompt. To remove it: `.\add_words.ps1 -ClearSavedCredential`.

## Notes

- The script only ever *creates* new documents; it never touches existing words. The Firestore
  rules also enforce this server-side (`allow update, delete: if false` on `word_bank`), so a
  mistake here can't corrupt existing content.
- If you want to fix a typo in an already-added word, do it directly in the Firebase console
  (Firestore Database → word_bank) - that goes through admin access and isn't restricted by the
  client rules.
- The same free-API pattern could be extended to grammar topics later if you want a similar
  script for `grammar_topics`.
