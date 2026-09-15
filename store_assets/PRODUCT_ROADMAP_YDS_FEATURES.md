# YDS Rekabet Stratejisi & Gelecek Özellik Yol Haritası

**Durum: Bunların HİÇBİRİ şu an uygulamada yok.** Bu dosya, geliştirilecek özellikler için bir strateji/backlog notudur — Play Store açıklamasına veya herhangi bir kullanıcıya dönük metne bu içerikten hiçbir şey kopyalanmamalı, ta ki gerçekten kodlanıp test edilene kadar.

## Pazar analizi (2026-09-15 itibarıyla)

Android ekosistemindeki YDS uygulamaları genellikle birbirinin kopyası olan statik arayüzler ve basit soru bankalarından ibaret.

| Kategori | Mevcut Android Uygulamalarında Olanlar | Rakiplerin Eksik / Zayıf Kaldığı Noktalar | Fark Yaratacak Çözüm (henüz yapılmadı) |
| --- | --- | --- | --- |
| **Kelime Öğrenimi** | Standart flashcard (kart çevirme), çoktan seçmeli eşleştirme testleri, basit aralıklı tekrar (SRS). | Kelimeler soyut kalıyor; YDS formatındaki cümle içi eşanlamlı (synonym/collocation) bağlamı zayıf. | **Collocation & Academic Corpus Engine:** Kelimeleri tekil değil, ÖSYM'nin sorduğu bağlam kalıplarıyla (örn. drastic drop, contribute to) ve anlık seviyeye göre üretilen mini okuma pasajlarıyla öğretmek. |
| **Okuma & Paragraf** | Statik akademik makale çevirileri, paragraf altı çoktan seçmeli sorular. | Cümle analizi yok; uzun cümlelerde özne-yüklem bağını koparan adaylar metinde kayboluyor. | **İnteraktif Cümle Ayrıştırıcı (Parsing Tool):** Uzun akademik cümleleri modüler bloklara (noun clause, relative clause, reduction) ayıran, tek dokunuşla bağlaç mantığını gösteren yapı. |
| **Soru Çözümü & Deneme** | Çıkmış soru arşivi, deneme modları, süre sayacı. | Çözüm açıklamaları genellikle PDF formatında veya yetersiz 1-2 cümlelik kalıp metinler. | **Dinamik Çeldirici Analizi:** Doğru cevabın neden doğru olduğunu değil, işaretlenen yanlış şıkkın adayı neden yanılttığını (çeldirici türü: aşırı genelleme, zıt anlam, konu dışı vb.) gösteren mantık. |
| **Performans & İlerleme** | Doğru/yanlış sayıları, temel başarı yüzdeleri ve çizgi grafikleri. | Yüzeysel metrikler; adayın hangi soru tipinde (restatement, cloze test, çeviri) tam olarak nerede patladığını söylemiyor. | **YDS Hedef Puan Algoritması & Zayıf Halka Radarı:** Soru tipi bazlı hata ağırlıklandırması yaparak adaya nokta atışı eksik listesi çıkaran ve o günkü çalışma planını otomatik revize eden adaptif motor. |

### Mevcut uygulamaların ortak teknik ve tasarım sorunları
- **Kötü UX/UI:** Birçoğu eski WebView sarmalayıcıları kullanıyor ya da Material 3'e uymayan, yoğun reklamlı, göz yoran arayüzlere sahip.
- **Kalıplaşmış içerik:** Sadece 2013-2020 arası çıkmış soruları dönüştürüp sunuyorlar; yeni nesil YDS soru kalıplarına uyum sağlayamıyorlar.
- **Kişiselleştirme eksikliği:** 50 puan hedefleyen bir lisans mezunu ile 85+ hedefleyen bir akademisyene aynı akışı sunuyorlar.

### Ürünü ayrıştıracak 3 temel koz (öncelik sırasıyla değil, henüz kodlanmadı)
1. **Akıllı Bağlaç & Soru Tipi Motoru** — YDS bir gramer değil, okuduğunu anlama/mantık sınavı; "çeviri yapmadan bağlaç ve mantık ipuçlarıyla soru çözme" tekniklerine göre kurgulamak.
2. **Mikro Denemeler (Bite-sized Sınavlar)** — 80 soru/180 dakikalık tam deneme yerine 10-15 dakikalık odaklı bloklar (5 Paragraf Tamamlama + 5 Cümle Tamamlama) + anlık analiz.
3. **Kişiselleştirilmiş Hata Defteri** — yanlış yapılan her soruyu etiketleyip (Zaman Uyumu Hatası, Bağlaç Yanılgısı vb.), 3 gün ve 7 gün sonra aynı kavramı farklı bir soru cümlesiyle tekrar sorma (spaced repetition, mevcut Mistakes Box'ın üzerine inşa edilebilir).

## Şu an gerçekten var olan (Play Store açıklamasında kullanılabilir)
Bkz. [[project-global-yds-app-roadmap]] memory kaydı ve `PLAY_STORE_LISTING.md` — kelime bankası, gramer konuları, okuma parçaları, deneme sınavı, level/XP, streak, rozetler, global liderlik tablosu, Premium, 10 dilde arayüz, günlük hatırlatma bildirimi.
