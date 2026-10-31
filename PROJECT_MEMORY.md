# Proje Hafızası (Project Memory & Architecture Ledger)
## Global English Exam Hub (IELTS TOEIC TOEFL English Prep)

* **Son Güncelleme:** 31 Ekim 2026
* **Güncel Sürüm:** `v2.0.5` (`versionCode 9`, `versionName "2.0.5"`)
* **Paket Kimliği (Application ID):** `com.cynorra.vocabmaster.app`
* **GitHub Deposu:** [https://github.com/cynorra/global-yds-app](https://github.com/cynorra/global-yds-app) (Public)
* **SDK Seviyeleri:** `minSdk 24`, `targetSdk 36`, `compileSdk 36`, `buildToolsVersion 36.0.0`, `Java 17`

---

## 1. Google Play Mağaza & ASO Kimliği

* **Global / Varsayılan Başlık (30 Karakter):**  
  `IELTS TOEIC TOEFL English Prep`
* **Türkiye Mağazası Başlığı (28 Karakter):**  
  `İngilizce Sınav: YDS & IELTS`
* **Strateji:** "VocabMaster" marka adı aramalarda hacim kaplamaması için başlıklardan kaldırıldı; yerine küresel ölçekte arama hacmi en yüksek sınav anahtar kelimeleri yerleştirildi.

---

## 2. Güvenlik, Kimlik Doğrulama & SHA-1 Bilgileri

Google Cloud Console ve Firebase Console üzerinde uygulanan güvenlik kısıtlamaları:
* **Paket Adı:** `com.cynorra.vocabmaster.app`
* **Debug (Geliştirici / Emülatör) SHA-1:**
  ```text
  EC:89:BF:70:2E:CE:2F:27:C0:99:72:3D:09:E9:F5:5C:25:7A:CF:E2
  ```
* **Debug SHA-256:**
  ```text
  F7:9A:B3:A7:62:02:A6:8B:52:5F:B4:74:9B:E3:0E:C2:1C:6F:B1:A5:C4:AC:9E:3C:D5:31:0A:73:C9:23:58:18
  ```
* **Canlı / Play Store (Play App Signing) SHA-1:**
  * Google Play Console > Kurulum (Setup) > Uygulama İmzalama (App Signing) sayfasından alınır.
* **Mevcut OAuth İstemci Hash (`google-services.json`):**
  ```text
  84:24:73:48:5A:72:2D:0C:F9:4A:DE:CD:90:25:DE:35:09:67:D5:79
  ```
* **Güvenlik Sertleştirmeleri:**
  * API anahtarı Google Cloud Console üzerinde **Android apps** (yukarıdaki SHA-1 ve paket adı) ile kısıtlandı.
  * API yetkileri yalnızca Firebase servislerine (Cloud Firestore, Firebase Auth, Remote Config) sınırlandırıldı.
  * `.gitignore` dosyasına `*.jks`, `*.keystore`, `*.key`, `*.p12`, `*.pem`, `*.pfx`, `.env*` eklendi.
  * `tools/add_words.ps1` içindeki sabit API key kaldırıldı, `$env:FIREBASE_API_KEY` veya yerel JSON'dan dinamik okunuyor.

---

## 3. Desteklenen 10 Küresel Sınav & Modül Mimarisi

Uygulama baştan uca 10 sınavı destekler:
1. **TOEIC** (~7M aday) - İş İngilizcesi & YDS formatına en yakın sınav
2. **IELTS** (~3.5-4M aday) - Akademik & Genel Göçmenlik
3. **TOEFL** (~2.5-3M aday) - Akademik Amerikan İngilizcesi
4. **Cambridge** (~5.5-6M aday) - B2 First, C1 Advanced, C2 Proficiency
5. **SAT / ACT** (~2M aday) - Eleştirel Okuma ve Analiz
6. **PTE Academic** (~1.5-2M aday) - Hızlı Bilgisayar Tabanlı
7. **DET (Duolingo English Test)** (~1.5-2M aday) - Uyarlamalı Online Sınav
8. **EIKEN** (~3.5-4M aday) - Japonya Resmi İngilizce Sınavı
9. **GRE / GMAT** (~500K aday) - İleri Seviye Kelime & Mantık
10. **YDS / YÖKDİL** (~400K aday) - Türkiye Akademik & Kamu Sınavları

### Modül Entegrasyonları:
* **`ExamType.kt`:** Tüm 10 sınav + `ALL` enum tipi.
* **Ana Ekran (`HomeFragment.java` & `HomeExamCardAdapter.kt`):** Sınav seçim carousel'ı ve aday sayıları yerelleştirilmiş formatta (`exam_candidates_format`).
* **Sınavlar Ekranı (`ExamFragment.java` & `fragment_exam.xml`):** 10 sınav için özel çip filtreleme.
* **Kelimeler Ekranı (`VocabularyFragment.java` & `fragment_vocabulary.xml`):** 10 sınav kategorisine göre kelime bankası filtreleme.
* **Okuma Parçaları (`ReadingPassage.kt` & `ReadingAdapter.kt`):** Sınav rozeti (`tvPassageBadge`) ve hedef sınav filtreleme.
* **Gramer Konuları (`GrammarTopic.kt` & `GrammarAdapter.kt`):** Sınav rozeti (`tvGrammarBadge`).
* **Soru Bankası & Tohum Veri (`FirebaseExamRepositoryImpl.kt`):** Tüm sınav tipleri için gerçekçi soru havuzları.

---

## 4. CI/CD & Derleme Otomasyonu (GitHub Actions)

* **İş Akışı Dosyası:** `.github/workflows/release-aab.yml`
* **Tetikleyici:** `v*.*.*` tag'i push edildiğinde otomatik başlar.
* **Üretilen Çıktılar:**
  1. `app-release.aab` (Google Play Store için imzalı Android App Bundle)
  2. `app-release.apk` (Doğrudan cihazlara kurulabilir imzalı APK)
* **Otomatik GitHub Release:** Tag bazında otomatik release oluşturulur ve her iki dosya eklenir.
* **İmzalama Değişkenleri (GitHub Secrets):**
  * `RELEASE_KEYSTORE_BASE64`
  * `RELEASE_KEYSTORE_PASSWORD`
  * `RELEASE_KEY_ALIAS`
  * `RELEASE_KEY_PASSWORD`

---

## 5. Kritik Hata Çözümleri & Dikkat Edilecekler (Troubleshooting)

1. **GitHub Actions Runner JVM Hatası:**
   * `gradle.properties` içine Windows'a özel `-Djava.io.tmpdir=D:/gradle_cache/tmp` ASLA yazılmamalıdır; Linux runner'ları çökertir.
2. **Java - Kotlin SAM Interface Uyumsuzluğu:**
   * Java'dan çağrılan Kotlin listener'ları `(ExamType) -> Unit` yerine `fun interface OnHomeExamClickListener` olarak tanımlanmalıdır.
3. **XML `tools:text` Namespace Hatası (`AttributePrefixUnbound`):**
   * Herhangi bir XML layout dosyasına `tools:text` eklendiğinde en üst kapsayıcıya mutlaka `xmlns:tools="http://schemas.android.com/tools"` eklenmelidir.
4. **Veri Modeli Varsayılan Değerleri:**
   * `Word.kt` içinde `turkishMeaning: String = ""` varsayılan değer verilerek birim testlerde ve tohum verilerde esneklik sağlandı.
