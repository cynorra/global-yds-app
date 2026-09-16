# Play Store Listing Draft

Two separate listings below: **English (en-US) — Play Console's default listing language**, and **Turkish (tr-TR) — a translated listing**, not a duplicate of the default. Do not paste the Turkish copy into the default-language fields; Play Console shows the default listing to every locale that has no dedicated translation, so a Turkish default would show Turkish text to e.g. German or Japanese users with no tr-TR override.

---

## English (en-US) — default listing

### Title (30 char limit, 26 used)
VocabMaster: Learn English

### Short description (80 char limit, 75 used)
Vocabulary flashcards, grammar, reading & timed mock exams to learn English

### Full description (4000 char limit, ~1500 used)

VocabMaster: Learn English — the all-in-one app for building vocabulary, mastering grammar, and acing English exams.

Stop just memorizing word lists. VocabMaster brings vocabulary flashcards, grammar lessons, reading passages, and realistic timed mock exams together in one app — built for anyone preparing for an English proficiency exam or simply leveling up their everyday English.

📚 VOCABULARY BUILDER
• Flashcards leveled to your progress
• Track learned vs. unlearned words
• Instant translation support

📝 MOCK EXAMS
• Multiple-choice questions in a real exam format
• Instant results and performance breakdown
• Daily practice exam
• Review your past mistakes — Mistakes Box

📖 GRAMMAR & READING
• Topic-by-topic English grammar lessons
• Exam-style reading passages and questions

🏆 STAY MOTIVATED
• Level and XP system
• Daily study streaks
• Achievement badges
• Global leaderboard

🌍 AVAILABLE IN 10 LANGUAGES
English, Turkish, German, French, Spanish, Italian, Dutch, Polish, Portuguese, and Russian.

🔔 DAILY REMINDERS
Never lose your study streak — we'll nudge you at just the right time.

Every feature is completely free — sign up and start learning today.

Whether you're preparing for an English exam or just want to sharpen your everyday English, VocabMaster brings vocabulary, grammar, reading, and exams together in one app.

Download now and start learning today!

---

## Turkish (tr-TR) — translated listing

### Title (30 char limit, 26 used)
VocabMaster: Learn English

### Short description (80 char limit, ~79 chars)
Kelime, gramer, okuma, YDS deneme sınavı ve liderlik tablosuyla İngilizce öğren

### Full description (4000 char limit, ~1500 chars used)

VocabMaster: Learn English — YDS ve İngilizce sınavlarına hazırlanan herkes için tek uygulama.

Kelime ezberlemekle sınırlı kalmayın: VocabMaster; kelime, gramer, okuma parçaları ve gerçek sınav formatında deneme sınavlarını tek çatı altında topluyor. YDS, YÖKDİL ve genel İngilizce seviye sınavlarına hazırlananlar için özel olarak tasarlandı.

📚 KELİME ÖĞRENME
• Seviyene uygun kelime bankası
• Öğrenilen/öğrenilmeyen kelime takibi
• Anlık çeviri desteği

📝 DENEME SINAVLARI
• Gerçek sınav formatında çoktan seçmeli sorular
• Anlık sonuç ve performans analizi
• Günlük deneme sınavı
• Yanlış yaptığın soruları tekrar çalış - Mistakes Box

📖 GRAMER VE OKUMA
• Konu konu İngilizce gramer anlatımları
• YDS tarzı okuma parçaları ve sorular

🏆 MOTİVASYON VE İLERLEME
• Seviye (Level) ve XP sistemi
• Günlük çalışma serisi (Streak)
• Başarım rozetleri (Achievements)
• Global liderlik tablosu

🌍 10 DİLDE ARAYÜZ
Türkçe, İngilizce, Almanca, Fransızca, İspanyolca, İtalyanca, Hollandaca, Lehçe, Portekizce ve Rusça.

🔔 GÜNLÜK HATIRLATICI
Çalışma alışkanlığını kaybetme, seni doğru zamanda hatırlatırız.

Tüm özellikler tamamen ücretsiz — kayıt ol ve hemen kullanmaya başla.

İster YDS'ye hazırlanıyor ol, ister günlük İngilizceni geliştirmek iste — VocabMaster: Learn English, kelimeden sınava tüm süreci tek uygulamada birleştirir.

Hemen indir, bugün öğrenmeye başla!

---

## Play Console fields (ready to paste)
- **Privacy Policy URL:** https://cynorra.github.io/global-yds-app/
- **Account deletion instructions URL** (Data safety section, if asked separately): https://cynorra.github.io/global-yds-app/#account-deletion
- **Support email:** support@cynorra.com
- Both URLs are served by GitHub Pages from this repo's `docs/index.html` (source: `main` branch, `/docs`), confirmed live and HTTPS-enforced as of 2026-09-16. The repo itself is private, but Pages serves `docs/` publicly regardless — that's expected, not a leak: only `docs/` is exposed, no app source.

## Notes
- **English listing added 2026-09-16.** Realized the only draft that existed was the Turkish one — but the app's own base locale (`res/values/`, no suffix) is English, and Play Console's default listing language is en-US, so a Turkish-only listing meant every non-Turkish, non-translated locale would see Turkish store text. Wrote a genuinely English-optimized listing rather than machine-translating the Turkish copy: dropped "YDS"/"YÖKDİL" (Turkish national exam names with ~zero English search volume) and reframed around generic English-search-intent terms an actual English-speaking searcher types — "vocabulary", "flashcards", "grammar", "reading", "mock exams" — front-loaded the same way the Turkish short description front-loads "YDS"/"gramer"/"okuma". Kept "10 languages including Turkish" as a real selling point for the English listing, since a large share of this app's actual users search Play Store in English while wanting the app UI in their own language.
- Keyword strategy (Turkish listing): "YDS", "gramer", "okuma", "deneme sınavı" front-loaded in the short description and opening lines — these match actual Turkish search intent for this exam-prep category, not generic "vocabulary app" terms every competitor uses.
- Differentiation angle: positioned as the all-in-one app (vocab + grammar + reading + timed exams + gamification) vs single-purpose competitor apps that only do flashcards or only do question banks.
- First ~250 characters are the highest-weight section for both Play Store search ranking and the "before more" preview users see, and what AI answer engines (Gemini/ChatGPT/Perplexity) tend to extract when summarizing the app - kept it as clear factual statements rather than vague marketing copy.
- Screenshots and the feature graphic were RGBA (had an alpha channel) — flattened to 24-bit RGB on 2026-09-16 (Play Console rejects transparency there; the app icon correctly keeps its alpha channel and was left untouched).
- All "(Premium)" mentions and the standalone "⭐ PREMIUM" section were removed on 2026-09-16 — the app's Play Billing paywall was fully reverted, every feature (leaderboard, daily exam, mistakes box) is now free for all users.
- **App icon (2026-09-16):** there were 3 different 512x512 icon drafts in this folder (two byte-identical gold owl+shield+book designs, one unrelated graduation-cap design) — deleted the duplicate and the mismatched one, keeping `app_icon_512x512.png` as the single canonical Play Store icon. It matches the actual in-app launcher icon (`app/src/main/res/mipmap-*/ic_launcher.png`). Also fixed a real bug in the app's *adaptive* icon (Android 8+): the foreground layer had no transparent safe-zone padding, so it clipped at the corners under a circular launcher mask (e.g. stock Pixel) — rescaled to fit Android's 66dp safe circle.
- **Mockup screenshots had broken/blank glyphs (2026-09-16):** `tools/generate_play_store_assets.py` (the actual generator behind the feature graphic + all phone/tablet screenshots) drew emoji icons (📖📝🔍👤🔊⏱🥈🥉👑🏆⚡✓) using the plain Segoe UI font, which has no emoji glyphs — they rendered as tofu boxes. Country flag emoji (🇺🇸🇬🇧 etc.) were worse: Windows' bundled color-emoji font (`seguiemj.ttf`) doesn't include flags at all, not even as a box — they rendered as nothing. Fixed the generator to draw pictographic icons with the emoji font (`embedded_color=True` + RAQM shaping) and replaced flags with plain country-name text (there was no font-based fix available for those), then regenerated and re-flattened to RGB every affected file. Also deleted `screenshot_1_vocabulary.png`/`screenshot_2_exam.png`/`screenshot_3_leaderboard.png` — an orphaned duplicate set from a different, unused generator (`tools/generate_clean_screenshots.py`) with the exact same designs as `phone_screenshot_*.png`, just also broken and not mapped to any actual Play Console upload slot.
