# Play Store Listing Draft

## Title (30 char limit)
VocabMaster: Learn English

## Short description (80 char limit, ~79 chars)
Kelime, gramer, okuma, YDS deneme sınavı ve liderlik tablosuyla İngilizce öğren

## Full description (4000 char limit, ~1500 chars used)

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
- Keyword strategy: "YDS", "gramer", "okuma", "deneme sınavı" front-loaded in the short description and opening lines — these match actual Turkish search intent for this exam-prep category, not generic "vocabulary app" terms every competitor uses.
- Differentiation angle: positioned as the all-in-one app (vocab + grammar + reading + timed exams + gamification) vs single-purpose competitor apps that only do flashcards or only do question banks.
- First ~250 characters are the highest-weight section for both Play Store search ranking and the "before more" preview users see, and what AI answer engines (Gemini/ChatGPT/Perplexity) tend to extract when summarizing the app - kept it as clear factual statements rather than vague marketing copy.
- Screenshots and the feature graphic were RGBA (had an alpha channel) — flattened to 24-bit RGB on 2026-09-16 (Play Console rejects transparency there; the app icon correctly keeps its alpha channel and was left untouched).
- All "(Premium)" mentions and the standalone "⭐ PREMIUM" section were removed on 2026-09-16 — the app's Play Billing paywall was fully reverted, every feature (leaderboard, daily exam, mistakes box) is now free for all users.
- **App icon (2026-09-16):** there were 3 different 512x512 icon drafts in this folder (two byte-identical gold owl+shield+book designs, one unrelated graduation-cap design) — deleted the duplicate and the mismatched one, keeping `app_icon_512x512.png` as the single canonical Play Store icon. It matches the actual in-app launcher icon (`app/src/main/res/mipmap-*/ic_launcher.png`). Also fixed a real bug in the app's *adaptive* icon (Android 8+): the foreground layer had no transparent safe-zone padding, so it clipped at the corners under a circular launcher mask (e.g. stock Pixel) — rescaled to fit Android's 66dp safe circle.
