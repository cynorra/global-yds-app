# -*- coding: utf-8 -*-
import json
import os

OUTPUT_DIR = os.path.join("app", "src", "main", "assets")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# ==============================================================================
# 1. COMPREHENSIVE WORDS BANK (400 Curated Words Across All 10 Exam Types)
# ==============================================================================

words_data = []

def add_words(exam_type, word_list):
    for idx, (word, tr, ex) in enumerate(word_list, 1):
        words_data.append({
            "id": f"word_{exam_type.lower()}_{idx}",
            "englishWord": word,
            "turkishMeaning": tr,
            "exampleSentence": ex,
            "isLearned": False,
            "targetExam": exam_type
        })

# --- 1. IELTS (Academic Band 7-9 Words) ---
add_words("IELTS", [
    ("Analyze", "Analiz etmek / Çözümlemek", "Researchers analyze the empirical data to uncover climate patterns."),
    ("Approach", "Yaklaşım / Yaklaşmak", "The university adopted a multidisciplinary approach to environmental science."),
    ("Assess", "Değerlendirmek / Paha biçmek", "Examiners assess candidates on fluency, coherence, and lexical resource."),
    ("Assume", "Varsaymak / Üstlenmek", "Scientists assume that solar fluctuations have minimal long-term effects."),
    ("Authority", "Otorite / Yetki / Uzman", "Dr. Vance is a recognized authority on marine biology."),
    ("Benefit", "Yarar sağlamak / Fayda", "Bilingual children benefit from enhanced executive cognitive function."),
    ("Concept", "Kavram / Fikir", "The concept of sustainability is central to modern urban planning."),
    ("Consistent", "Tutarlı / İstikrarlı", "The laboratory results are consistent with the original hypothesis."),
    ("Context", "Bağlam / Ortam", "You must interpret the author's argument within its historical context."),
    ("Contract", "Sözleşme / Daralmak", "Metals contract as the ambient temperature drops significantly."),
    ("Derive", "Türetmek / Çıkarsamak", "Many pharmaceutical treatments derive from indigenous plant species."),
    ("Distribute", "Dağıtmak / Paylaştırmak", "Relief agencies distribute essential food supplies across affected regions."),
    ("Establish", "Kurmak / Saptamak", "The government aims to establish stricter carbon emission quotas."),
    ("Estimate", "Tahmin etmek / Hesaplamak", "Economists estimate that renewable investments will double by 2030."),
    ("Evident", "Açık / Belirgin", "The positive impact of the education reform became evident in exam scores."),
    ("Identify", "Tespit etmek / Tanımlamak", "Satellite sensors identify deforestation hotspots in real time."),
    ("Interpret", "Yorumlamak / Anlamlandırmak", "Historians interpret ancient inscriptions through cross-referencing."),
    ("Involve", "İçermek / Dahil etmek", "The experimental protocol involves rigorous randomized trials."),
    ("Issue", "Mesele / Konu / Yayınlamak", "Water scarcity is the most pressing issue confronting the valley."),
    ("Major", "Büyük / Başlıca", "Urban sprawl is a major driver of habitat fragmentation."),
    ("Method", "Yöntem / Metot", "The researcher developed an innovative method for carbon sequestration."),
    ("Occur", "Meydana gelmek / Oluşmak", "Seismic tremors occur frequently along tectonic boundary zones."),
    ("Period", "Dönem / Çağ", "The Renaissance was a period of extraordinary artistic renewal."),
    ("Principle", "İlke / Prensip", "Conservation of energy is a fundamental principle of physics."),
    ("Proceed", "İlerlemek / Devam etmek", "The construction team may proceed once environmental clearance is granted."),
    ("Process", "Süreç / İşlemden geçirmek", "Photosynthesis is the biochemical process that sustains plant life."),
    ("Require", "Gerektirmek / İstemek", "Advanced aerospace applications require ultra-lightweight alloys."),
    ("Research", "Araştırma / İncelemek", "Recent neurological research sheds light on early memory storage."),
    ("Respond", "Yanıt vermek / Tepki göstermek", "Marine ecosystems respond sensitively to sea surface temperature rises."),
    ("Role", "Rol / Görev", "Enzymes play a vital role in accelerating cellular metabolism."),
    ("Section", "Bölüm / Kısım", "The introductory section outlines the scope and methodology."),
    ("Sector", "Sektör / Kesim", "The renewable energy sector experienced double-digit growth this year."),
    ("Significant", "Önemli / Anlamlı", "There was a significant disparity in economic recovery rates."),
    ("Similar", "Benzer / Eşdeğer", "Both fossil species display remarkably similar cranial morphology."),
    ("Source", "Kaynak / Menşe", "Geothermal vents serve as a primary source of mineral nutrition."),
    ("Specific", "Belirli / Özgül", "The antibody binds exclusively to a specific viral surface protein."),
    ("Structure", "Yapı / Bünye", "Crystalline structure dictates the electrical conductivity of graphene."),
    ("Theory", "Teori / Kuram", "Darwin formulated his theory of evolution during the Beagle voyage."),
    ("Vary", "Çeşitlilik göstermek / Değişmek", "Sedimentation rates vary according to seasonal river discharge."),
    ("Corroborate", "Doğrulamak / Teyit etmek", "Peer-reviewed studies corroborated the effectiveness of the vaccine.")
])

# --- 2. TOEFL (Academic Campus & Lecture Lexicon) ---
add_words("TOEFL", [
    ("Adjacent", "Bitişik / Komşu", "The laboratory is adjacent to the main botanical greenhouse."),
    ("Aggregate", "Toplam / Bir araya getirilmiş", "Aggregate global temperatures have risen steadily over the century."),
    ("Albeit", "Olsa da / -e rağmen", "The experiment was successful, albeit more time-consuming than expected."),
    ("Ambiguous", "Muğlak / Çift anlamlı", "The survey question was ambiguous, yielding unreliable participant responses."),
    ("Anticipate", "Öngörmek / Ummak", "Geologists anticipate heightened seismic activity near the fault line."),
    ("Apparent", "Aşikar / Görünürdeki", "The apparent calm in the eye of the hurricane is deceptive."),
    ("Approximate", "Yaklaşık / Yaklaşmak", "The approximate distance between stellar clusters is measured in light-years."),
    ("Arbitrary", "Keyfi / Rastgele", "The classification of specimens was criticized for being arbitrary."),
    ("Aspect", "Yön / Boyut", "Every aspect of the organism's morphology was cataloged meticulously."),
    ("Attain", "Erişmek / Ulaşmak", "Larvae attain adult coloration only after their final molt."),
    ("Attitude", "Tutum / Yaklaşım", "Public attitudes toward artificial intelligence have shifted favorably."),
    ("Attribute", "Bağlamak / Nitelik", "Climatologists attribute the severe drought to persistent ocean warming."),
    ("Cease", "Durmak / Sona erdirmek", "Volcanic emissions did not cease until late in the subsequent epoch."),
    ("Clarify", "Açıklığa kavuşturmak", "The professor sought to clarify the distinction between meiosis and mitosis."),
    ("Coincide", "Çakışmak / Denk gelmek", "The peak of bird migration coincided with the sudden cold snap."),
    ("Commence", "Başlamak / Başlatmak", "Field excavations will commence as soon as the winter thaw finishes."),
    ("Compatible", "Uyumlu / Bağdaşan", "The newly synthesized polymer is fully compatible with human tissue."),
    ("Compensate", "Telafi etmek / Dengelemek", "Solar arrays compensate for seasonal dips in hydroelectric generation."),
    ("Compile", "Derlemek / Biriktirmek", "Graduate students compile demographic data from historical censuses."),
    ("Complement", "Tamamlamak / Bütünlemek", "Practical lab assignments complement the theoretical lecture modules."),
    ("Comprehensive", "Kapsamlı / Geniş çaplı", "The encyclopedia provides a comprehensive survey of medieval astronomy."),
    ("Conceive", "Düşünmek / Tasarlamak", "Archimedes conceived the principle of buoyancy while in Syracuse."),
    ("Concurrent", "Eşzamanlı / Aynı andaki", "The two chemical reactions are concurrent and interdependent."),
    ("Confer", "Danışmak / Bahşetmek", "The University Senate voted to confer an honorary doctorate on the poet."),
    ("Confine", "Sınırlamak / Hapsetmek", "Ecologists caution against confining large predators to small reserves."),
    ("Conform", "Uymak / Boyun eğmek", "All test apparatus must conform to international safety regulations."),
    ("Consent", "Onay / Rıza göstermek", "Clinical trials cannot proceed without written informed patient consent."),
    ("Consequent", "Sonuç olarak ortaya çıkan", "Deforestation and the consequent loss of topsoil devastated agriculture."),
    ("Considerable", "Kayda değer / Hatırı sayılır", "The grant provided a considerable boost to high-energy physics research."),
    ("Consist", "Oluşmak / Meydana gelmek", "Sedimentary rocks consist predominantly of compressed mineral grains."),
    ("Constitute", "Oluşturmak / Teşkil etmek", "Microscopic plankton constitute the foundation of marine food webs."),
    ("Constrain", "Kısıtlamak / Zorlamak", "Budgetary cutbacks severely constrain deep-space exploration missions."),
    ("Construct", "İnşa etmek / Kurmak", "Engineers constructed an earthquake-resistant suspension bridge."),
    ("Consult", "Danışmak / Başvurmak", "Anthropologists consult tribal elders to verify oral traditions."),
    ("Consume", "Tüketmek / Harcamak", "Metabolic tissues consume large quantities of glucose during exercise."),
    ("Contradict", "Çelişmek / Aksini iddia etmek", "The fossil discoveries contradict previous evolutionary timetables."),
    ("Contrary", "Aksine / Zıt", "Contrary to popular belief, bats possess well-developed vision."),
    ("Contrast", "Karşılaştırmak / Zıtlık", "The stark contrast between nocturnal and diurnal adaptations is striking."),
    ("Contribute", "Katkıda bulunmak", "Volcanic ash plumes contribute substantially to atmospheric cooling."),
    ("Convene", "Toplanmak / Bir araya gelmek", "International delegates convene annually to review biodiversity protocols.")
])

# --- 3. TOEIC (Business, Commerce & Workplace) ---
add_words("TOEIC", [
    ("Acquisition", "Şirket devralma / Satın alma", "The board approved the strategic acquisition of a German robotics firm."),
    ("Agenda", "Gündem / Toplantı planı", "The chairperson circulated the annual general meeting agenda in advance."),
    ("Allocate", "Tahsis etmek / Paylaştırmak", "The CFO decided to allocate additional capital to digital infrastructure."),
    ("Audit", "Denetim / Mali teftiş", "External consultants conducted a thorough financial audit of the branch."),
    ("Authorization", "Yetkilendirme / İzin", "Expenditures exceeding five thousand dollars require prior authorization."),
    ("Benchmark", "Kıyaslama noktası / Kriter", "Our manufacturing plant set the benchmark for low carbon intensity."),
    ("Bid", "Teklif vermek / İhale teklifi", "Three engineering firms submitted a competitive bid for the port project."),
    ("Briefing", "Bilgilendirme toplantısı", "The project manager led an executive briefing on supply chain delays."),
    ("Budget", "Bütçe", "Marketing expenses must remain strictly within the allocated budget."),
    ("Candidate", "Aday / Başvuran", "The hiring committee interviewed five shortlisted candidates for the post."),
    ("Clause", "Madde / Hüküm (Sözleşme)", "The confidentiality clause remains binding for two years after termination."),
    ("Collaborate", "İş birliği yapmak", "Engineers collaborate with sales reps to tailor custom client solutions."),
    ("Compensation", "Tazminat / Ücret paketi", "The executive package includes competitive salary and stock compensation."),
    ("Compliance", "Mevzuata uyum", "The compliance officer confirmed that our policies satisfy GDPR criteria."),
    ("Contractor", "Müteahhit / Taşeron", "The general contractor promised project completion before the deadline."),
    ("Deadline", "Son teslim tarihi", "All quarterly performance metrics must be submitted before Friday's deadline."),
    ("Deficit", "Açık / Bütçe açığı", "The trade deficit narrowed due to an unexpected surge in manufacturing exports."),
    ("Deliberate", "Müzakere etmek / Kasıtlı", "The directors deliberated for four hours before finalizing the merger."),
    ("Discrepancy", "Tutarsızlık / Uyuşmazlık", "The accountant identified an unexplained discrepancy in the ledgers."),
    ("Eligible", "Uygun / Şartları taşıyan", "Employees with one year of service are eligible for parental leave."),
    ("Enterprise", "Girişim / Şirket", "The enterprise expanded its cloud offerings across Southeast Asia."),
    ("Feasibility", "Uygulanabilirlik / Fizibilite", "We conducted a feasibility study before purchasing the commercial property."),
    ("Fluctuation", "Dalgalanma / Değişkenlik", "Currency fluctuation has eroded profit margins in foreign markets."),
    ("Forecast", "Tahmin / Öngörü", "The sales forecast indicates sustained double-digit revenue expansion."),
    ("Incentive", "Teşvik / Prim", "Performance-based bonuses provide a strong incentive for the sales team."),
    ("Invoice", "Fatura / Fatura kesmek", "Accounts payable will settle the outstanding invoice within thirty days."),
    ("Liability", "Yükümlülük / Mali sorumluluk", "The company insured itself against third-party environmental liability."),
    ("Merger", "Şirket birleşmesi", "The merger between the two airline carriers received regulatory clearance."),
    ("Negotiation", "Müzakere / Pazarlık", "Contract negotiations stalled over patent licensing royalties."),
    ("Overhaul", "Kapsamlı bakım / Baştan yenileme", "The IT department scheduled an overhaul of the server architecture."),
    ("Personnel", "Personel / Çalışanlar", "All security personnel received training on incident response protocols."),
    ("Portfolio", "Yatırım portföyü", "Diversifying the equity portfolio mitigates long-term macroeconomic risks."),
    ("Quarterly", "Üç aylık / Çeyreklik", "Shareholders praised the record quarterly dividend distribution."),
    ("Reimburse", "Geri ödemek / Masraf karşılamak", "The company will reimburse travel expenses backed by itemized receipts."),
    ("Remittance", "Para havalesi / Ödeme", "Foreign remittance transfers were processed through automated clearing."),
    ("Restructure", "Yeniden yapılandırmak", "The firm restructured operations into three autonomous operating units."),
    ("Revenue", "Gelir / Hasılat", "Annual subscription revenue surpassed fifty million dollars this fiscal year."),
    ("Stipulate", "Şart koşmak / Belirtmek", "The agreement stipulates that disputes will be settled through arbitration."),
    ("Substantial", "Önemli / Kayda değer", "The marketing revamp yielded a substantial boost in brand awareness."),
    ("Vendor", "Tedarikçi / Satıcı", "We selected a cloud vendor certified for ISO-27001 data integrity.")
])

# --- 4. CAMBRIDGE (C1 Advanced & C2 Proficiency Collocations) ---
add_words("CAMBRIDGE", [
    ("Accountable", "Sorumlu / Mesul", "Public officials must remain accountable to the communities they serve."),
    ("Adversity", "Zorluk / Sıkıntı / Güçlük", "She showed remarkable resilience in the face of profound adversity."),
    ("Advocate", "Savunmak / Destekçi", "The senator is a vocal advocate of international maritime protection."),
    ("Affluent", "Varlıklı / Zengin", "Subsidies were redirected from affluent suburbs to underfunded urban zones."),
    ("Alleviate", "Hafifletmek / Yatıştırmak", "Government subsidies helped alleviate fuel poverty during the winter."),
    ("Ambiguity", "Muğlaklık / Belirsizlik", "The wording of the legal statute was revised to remove all ambiguity."),
    ("Antithesis", "Zıt kutup / Tam tersi", "Selfish indifference is the absolute antithesis of humanitarian service."),
    ("Articulate", "Fikirlerini açıkça ifade eden", "The spokesperson gave an articulate defence of the new urban policy."),
    ("Ascertain", "Aslını öğrenmek / Saptamak", "Forensic experts worked diligently to ascertain the cause of the blaze."),
    ("Aversion", "Nefret / Şiddetli isteksizlik", "Many children have an innate aversion to bitter-tasting leafy vegetables."),
    ("Benevolent", "Hayırsever / İyiliksever", "A benevolent benefactor financed the construction of the public library."),
    ("Bolster", "Desteklemek / Güçlendirmek", "The central bank intervened with rate cuts to bolster consumer confidence."),
    ("Breach", "İhlal / Gedik açmak", "Failing to safeguard client confidentiality constitutes a serious breach."),
    ("Candid", "İçten / Samimi / Açık sözlü", "The director gave a candid evaluation of the team's ongoing shortcomings."),
    ("Catalyst", "Katalizör / Harekete geçirici", "Technological innovation served as the catalyst for the digital revolution."),
    ("Cohesive", "Bütünlük arz eden / Kaynaşmış", "A cohesive argument requires tightly structured logic and evidence."),
    ("Complacent", "Rehavete kapılmış / Kendinden memnun", "Despite winning the primary, the candidate warned staff not to be complacent."),
    ("Conducive", "Elverişli / Katkıda bulunan", "A quiet and orderly study space is conducive to deep intellectual focus."),
    ("Conspicuous", "Göze batan / Belirgin", "His absence from the ministerial banquet was conspicuous to all reporters."),
    ("Constraint", "Kısıtlama / Zorunluluk", "Severe financial constraints forced the university to postpone the project."),
    ("Contemplate", "Düşünüp taşınmak / Düşünmek", "The philosopher contemplated the ethical dilemmas raised by biotechnology."),
    ("Curtail", "Kısmak / Sınırlamak", "Emergency austerity measures aimed to curtail excessive public borrowing."),
    ("Daunting", "Göz korkutucu / Ürkütücü", "Climbing the steep north face presented a daunting physical challenge."),
    ("Debilitate", "Güçten düşürmek / Sarsmak", "Prolonged drought debilitated rural farming communities throughout the state."),
    ("Defiant", "Meydan okuyan / Başkaldıran", "The protestors remained defiant despite repeated dispersal warnings."),
    ("Delineate", "Ana hatlarıyla çizmek", "The strategic blueprint clearly delineates departmental responsibilities."),
    ("Detrimental", "Zararlı / Hasar verici", "Excessive screen exposure before sleep can have a detrimental effect on rest."),
    ("Disclose", "Açığa vurmak / İfşa etmek", "Whistleblowers chose to disclose internal safety test manipulations."),
    ("Disdain", "Tepeden bakmak / Küçümseme", "The critic treated superficial commercial art with unconcealed disdain."),
    ("Disregard", "Göz ardı etmek / Yok saymak", "Motorists who disregard pedestrian crossing signs face hefty fines."),
    ("Dissent", "Farklı düşünce / Muhalefet", "Judicial dissent in landmark cases frequently shapes future legislation."),
    ("Elusive", "Ele geçmez / Zor yakalanır", "A universally accepted definition of human consciousness remains elusive."),
    ("Empirical", "Deneysel / Gözleme dayalı", "Scientists require robust empirical data before endorsing new therapies."),
    ("Endeavour", "Çaba / Gayret etmek", "The space mission represents a multinational endeavour of historic scale."),
    ("Exemplify", "Örnek oluşturmak / Somutlaştırmak", "Her tireless humanitarian work exemplifies the spirit of selfless service."),
    ("Feasible", "Uygulanabilir / Mümkün", "Constructing offshore floating wind farms is both feasible and cost-effective."),
    ("Foster", "Geliştirmek / Teşvik etmek", "The incubator program aims to foster homegrown digital startup enterprise."),
    ("Frail", "Narin / Çelimsiz / Kırılgan", "The historical manuscript was preserved in an inert gas chamber as it was frail."),
    ("Hinder", "Engel olmak / Aksatmak", "Bureaucratic red tape should not hinder the delivery of humanitarian aid."),
    ("Inherent", "Doğasında olan / İçkin", "There is an inherent level of financial volatility in cryptocurrency trading.")
])

# --- 5. SAT (Reading & Writing Advanced Analytical Lexicon) ---
add_words("SAT", [
    ("Aberration", "Sapma / Norm dışı durum", "The sudden temperature spike was dismissed as an atmospheric aberration."),
    ("Abstain", "Çekinmek / Uzak durmak", "Several delegates chose to abstain during the final vote on sanctions."),
    ("Acumen", "Keskin zeka / Sezgi kabiliyeti", "Her business acumen allowed the startup to pivot and dominate the niche."),
    ("Admonish", "Tatlı sert uyarmak / Nasihat etmek", "The judge admonished the attorney for badgering the vulnerable witness."),
    ("Aesthetic", "Estetik / Sanatsal", "Minimalist Scandinavian design balances practical utility and aesthetic purity."),
    ("Ambivalence", "Duygu karmaşası / Çelişki", "Voters expressed ambivalence regarding both proposed candidates."),
    ("Amicable", "Dostane / Barışçıl", "The partners agreed to an amicable dissolution of their joint venture."),
    ("Anachronism", "Çağ aşımı / Tarih hatası", "Depicting an ancient Roman wearing a wristwatch is a blatant anachronism."),
    ("Anomaly", "Anormallik / Kuraldışılık", "Astronomers detected a gravitational anomaly near the orbit of Neptune."),
    ("Antipathy", "Derin hoşnutsuzluk / Antipati", "There was longstanding antipathy between the rival academic departments."),
    ("Apprehension", "Endişe / Kaygı / Kavrama", "Freshmen entered the calculus auditorium with perceptible apprehension."),
    ("Arbitrary", "Gelişigüzel / Keyfi", "Decisions on zoning permits appeared arbitrary and lacked clear rubrics."),
    ("Audacious", "Cüretkar / Gözü pek", "The museum heist was an audacious plan executed with military precision."),
    ("Augment", "Artırmak / Büyütmek", "Surgeons augment standard microscopes with high-definition digital feeds."),
    ("Austere", "Sade / Çileli / Katı", "Monastic cloisters are renowned for their austere architectural beauty."),
    ("Autonomous", "Özerk / Bağımsız", "The university granted the research institute an autonomous administrative status."),
    ("Banal", "Sıradan / Bayağı", "The plot relied on banal tropes rather than original narrative elements."),
    ("Benevolent", "Cömert / İyiliksever", "The hospital wing was built through the bequest of a benevolent donor."),
    ("Capricious", "Maymun iştahlı / Sağı solu belli olmayan", "The capricious mountain weather shifted from brilliant sun to blizzard."),
    ("Castigate", "Şiddetle azarlamak / Kınamak", "The committee castigated the contractor for substandard structural safety."),
    ("Circumspect", "Tedbirli / İhtiyatlı", "Diplomats must remain circumspect when addressing territorial disputes."),
    ("Cogent", "İkna edici / Mantıklı", "The defense lawyer presented a cogent argument grounded in forensic fact."),
    ("Corroborate", "Delille kanıtlamak", "Witness testimonies served to corroborate the suspect's ironclad alibi."),
    ("Curtail", "Kısmak / Kesmek", "The municipality was forced to curtail transit subsidies due to deficits."),
    ("Deference", "Hürmet / Saygı gösterme", "In deference to local custom, visitors removed their footwear at the shrine."),
    ("Diatribe", "Yerin dibine batıran eleştiri", "The editorial was a scathing diatribe against speculative real estate bubbles."),
    ("Diffident", "Çekingen / Özgüvensiz", "Though brilliantly insightful, the young theorist was diffident in debate."),
    ("Disparage", "Kötülemek / Önemsizleştirmek", "Unprofessional critics tend to disparage genres they do not understand."),
    ("Dispassionate", "Tarafsız / Soğukkanlı", "A forensic accountant must offer a dispassionate review of the books."),
    ("Dogmatic", "Dogmatik / Körü körüne bağnaz", "A dogmatic insistence on outdated doctrine stifles scientific innovation."),
    ("Ebullient", "Coşkulu / Hayat dolu", "The championship team was met with ebullient cheers at the parade."),
    ("Eclectic", "Çeşitli kaynaklardan derlenmiş", "His music collection is an eclectic mix of baroque sonatas and modern jazz."),
    ("Eloquent", "Fesahat sahibi / Etkili konuşan", "The keynote speaker made an eloquent plea for biosphere preservation."),
    ("Emulate", "Taklit ederek aşmaya çalışmak", "Junior programmers aspire to emulate the software architect's clean style."),
    ("Ephemeral", "Fani / Geçici / Kısa ömürlü", "The cherry blossoms' beauty lies partly in their ephemeral springtime bloom."),
    ("Equivocal", "Muğlak / Çift taraflı", "The spokesperson gave an equivocal answer that evaded direct confirmation."),
    ("Exonerate", "Suçsuzluğunu kanıtlamak", "The ballistic report exonerated the police officer from wrongful conduct."),
    ("Fastidious", "Titiz / Müşkülpesent", "The restorer took fastidious care when treating the ancient silk scroll."),
    ("Fervent", "Ateşli / Hararetli", "She is a fervent advocate for civil liberties and digital privacy rights."),
    ("Frugal", "Tutumlu / Tutumlu yaşayan", "Frugal management of public revenues allowed the town to avoid all debt.")
])

# --- 6. GRE (Verbal Reasoning & High-Frequency Lexicon) ---
add_words("GRE", [
    ("Alacrity", "Büyük heves / Çeviklik / İsteklilik", "The eager apprentice accepted the master's instruction with alacrity."),
    ("Anomalous", "Kuralsız / Anormal", "The spectrometer revealed anomalous emission lines in the distant nebula."),
    ("Approbase", "Onaylamak / Tasvip etmek", "The council refused to approbate the unverified environmental assessment."),
    ("Assiduous", "Gayretli / Sebatkar / Çalışkan", "Her assiduous archival research uncovered forgotten 17th-century diaries."),
    ("Belie", "Aksini göstermek / Maskelemek", "Her relaxed smile belied the intense pressure she felt before the defense."),
    ("Castigation", "Şiddetli kınama / Cezalandırma", "The audit report was a blistering castigation of municipal wastefulness."),
    ("Chicanery", "Hilekarlık / Düzenbazlık", "Legal chicanery was employed to circumvent statutory monopoly regulations."),
    ("Cogent", "Akla yatkın / İkna edici", "Her cogent critique dismantled the foundations of the opposing hypothesis."),
    ("Complaisant", "Kolay yola gelen / Boyun eğen", "The complaisant board rarely questioned the chief executive's directives."),
    ("Convoluted", "Son derece karmaşık / Girift", "The tax code had become so convoluted that even experts required software."),
    ("Decorum", "Görgü kuralları / Ağırbaşlılık", "Senators are strictly expected to preserve parliamentary decorum."),
    ("Denigrate", "Karalamak / Değerini düşürmek", "It is unfair to denigrate the achievements of early pioneering aviators."),
    ("Desiccate", "Kurumak / Suyunu çekmek", "The scorching desert winds desiccate vegetation in a matter of hours."),
    ("Diatribe", "Acımasız taşlama / Yergi", "The philosopher penned a diatribe against materialistic utilitarianism."),
    ("Disabuse", "Gözünü açmak / Yanılgıdan kurtarmak", "Field data quickly disabused the team of their overly optimistic expectations."),
    ("Disparate", "Tamamen farklı / Ayrı türden", "The symposium gathered disparate viewpoints from neurobiology and philosophy."),
    ("Dissemble", "Duygularını gizlemek / Niyetini saklamak", "A seasoned diplomat knows when to speak plainly and when to dissemble."),
    ("Dogmatism", "Dogmatizm / Bağnaz inatçılık", "Scientific enquiry stands in direct opposition to uncritical dogmatism."),
    ("Duplicity", "İkiyüzlülük / Sahtekarlık", "The deceitful advisor's financial duplicity was finally uncovered in court."),
    ("Eclectic", "Farklı ekollerden seçen", "Her research employs an eclectic methodology spanning genetics and linguistics."),
    ("Efficacy", "Yararlılık / Etkinlik / Şifa", "Clinical trials confirmed the superior therapeutic efficacy of the drug."),
    ("Elegy", "Ağıt / Hüzünlü şiir", "The poet recited a moving elegy dedicated to fallen wartime comrades."),
    ("Enervate", "Güçten düşürmek / Takatsiz bırakmak", "The stifling equatorial heat seemed to enervate the entire expedition team."),
    ("Engender", "Yol açmak / Doğurmak", "Lack of transparency tends to engender deep suspicion among citizens."),
    ("Ephemeral", "Gelip geçici / Kısa süreli", "Fashion trends in the digital era are notoriously ephemeral and fleeting."),
    ("Equivocate", "Lafı çevirmek / Belirsiz konuşmak", "When pressed for dates, the minister began to equivocate repeatedly."),
    ("Erudite", "Alim / Engin bilgili", "The professor delivered an erudite lecture on Hellenistic textual traditions."),
    ("Esoteric", "Sadece dar bir zümrenin anladığı", "Quantum chromodynamics remains an esoteric field for lay audiences."),
    ("Eulogy", "Övgü dolu konuşma / Medhiye", "The senator delivered a heartfelt eulogy honoring his late mentor."),
    ("Exculpate", "Aklamak / Temize çıkarmak", "Fresh forensic evidence emerged to completely exculpate the prime suspect."),
    ("Exigent", "Acil / Israrcı / Zorlayıcı", "The humanitarian agency responded immediately to exigent food shortages."),
    ("Facetious", "Yersiz şaka yapan / Laubali", "The judge warned the witness that facetious commentary would not be tolerated."),
    ("Fatuous", "Aptalca / Boş / Manasız", "Making sweeping generalizations without data is a fatuous undertaking."),
    ("Garrulous", "Geveze / Çenesi düşük", "The garrulous cab driver kept talking throughout the two-hour transit."),
    ("Gregarious", "Sokulgan / Sürü halinde yaşayan", "Dolphins are inherently gregarious mammals that travel in social pods."),
    ("Harangue", "Ateşli ve saldırgan nutuk", "The union leader delivered a fiery harangue against workplace automation."),
    ("Iconoclast", "Gelenekleri yıkan / Tabu yıkan kimse", "The artist was hailed as an iconoclast who overturned classical realism."),
    ("Immutable", "Değişmez / Sabit", "Physicists investigate whether gravitational constants are truly immutable."),
    ("Inchoate", "Henüz olgunlaşmamış / Başlangıç evresindeki", "At this stage, our business model is merely an inchoate collection of ideas."),
    ("Ingenuous", "Saf / Temiz kalpli / Hilesiz", "The child gave an ingenuous explanation that disarmed everyone in the room.")
])

# --- 7. PTE (Pearson Test of English Academic Collocations) ---
add_words("PTE", [
    ("Accommodate", "Barındırmak / Uyum sağlamak", "The lecture theatre was enlarged to accommodate up to five hundred students."),
    ("Accumulate", "Biriktirmek / Yığılmak", "Toxic heavy metals can accumulate in the tissues of predatory fish."),
    ("Administer", "Yönetmek / İlaç tatbik etmek", "Trained clinicians must administer the experimental vaccine intravenously."),
    ("Aggregate", "Toplu / Kümülatif", "The aggregate data suggests a subtle correlation between sleep and retention."),
    ("Allocate", "Pay ayırmak / Tahsis etmek", "The university voted to allocate funds for upgrading server infrastructure."),
    ("Alter", "Değiştirmek / Başkalaşmak", "Genetic mutations can alter the tertiary structure of cellular enzymes."),
    ("Ambiguity", "Belirsizlik", "The wording of survey instructions must leave no room for ambiguity."),
    ("Amend", "Yasa/metin değiştirmek", "Parliament voted to amend the maritime commerce act of nineteen ninety."),
    ("Apparent", "Aşikar / Belirgin", "The benefits of regular cardiovascular conditioning become apparent quickly."),
    ("Append", "Sonuna eklemek / İliştirmek", "Please append your bibliography to the final draft of your dissertation."),
    ("Arbitrary", "Rastlantısal / Keyfi", "Grading rubrics must be standardized to prevent arbitrary scoring discrepancies."),
    ("Aspect", "Görünüş / Yön", "Every aspect of the flight telemetry was evaluated by ground controllers."),
    ("Assimilate", "Özümsemek / Sindirmek", "Immigrant families gradually assimilate elements of the host nation's culture."),
    ("Attain", "Ulaşmak / Erişmek", "Athletes must train rigorously to attain peak international fitness levels."),
    ("Attitude", "Tavır / Tutum", "Positive student attitudes toward group work foster superior collaboration."),
    ("Attribute", "Nitelik / Dayandırmak", "Biologists attribute migratory navigation to geomagnetic sensory organs."),
    ("Channel", "Yönlendirmek / Kanalize etmek", "The government aims to channel private investments toward green transport."),
    ("Coherent", "Tutarlı / Ahenkli", "A dissertation must advance a coherent line of logical argument."),
    ("Coincide", "Zaman olarak çakışmak", "Lunar eclipses coincide with full moon phases under precise celestial alignments."),
    ("Collapse", "Çökmek / Yıkılmak", "Structural faults caused the abandoned storage facility to collapse."),
    ("Commence", "Başlamak / Start almak", "The keynote presentation will commence promptly at nine o'clock."),
    ("Compatible", "Bağdaşabilir / Uyumlu", "Ensure your citation management software is compatible with LaTeX templates."),
    ("Compensate", "Karşılamak / Telafi etmek", "Extra credit assignments cannot fully compensate for poor test performances."),
    ("Compile", "Derlemek / Toparlamak", "Librarians compile comprehensive archives of digital periodicals."),
    ("Complement", "Tamamlayıcı / Bütünlemek", "Interactive laboratory seminars complement the main lecture curriculum."),
    ("Comprehensive", "Tüm unsurları kapsayan", "The review article provides a comprehensive overview of CRISPR technology."),
    ("Conceive", "Zihinde canlandırmak", "Engineers conceived the sub-sea tunnel project more than two decades ago."),
    ("Concurrent", "Aynı anda gerçekleşen", "The conference organized three concurrent tracks on artificial intelligence."),
    ("Confer", "Müzakere etmek / Derece vermek", "Academic councils confer master's degrees upon completing all requirements."),
    ("Confine", "Sınır koymak / Hapsetmek", "Please confine your introductory remarks to three minutes or fewer."),
    ("Conform", "Kurallara uymak", "Manufactured components must conform strictly to aerospace tolerances."),
    ("Consent", "Onay vermek / Muvafakat", "Subject consent forms must be securely archived for seven years."),
    ("Consequent", "Bunun sonucunda olan", "Soil erosion and consequent agricultural declines triggered migration."),
    ("Considerable", "Kayda değer oranda", "The new catalyst demonstrated considerable acceleration of reaction speed."),
    ("Consist", "Oluşmak / Teşekkül etmek", "The interview panel will consist of three senior department members."),
    ("Constitute", "Oluşturmak / Sayılmak", "These unexpected test findings constitute an important scientific advance."),
    ("Constrain", "Kısıtlamak / Baskılamak", "Tight supply chain bottlenecks constrain production capacity worldwide."),
    ("Construct", "Kuram oluşturmak / İnşa etmek", "Sociologists construct predictive models based on longitudinal survey data."),
    ("Consult", "Başvurmak / Danışmak", "Students should consult their faculty advisors before enrolling in electives."),
    ("Consume", "Tüketmek / Yok etmek", "Cryptocurrency mining data centres consume immense quantities of electricity.")
])

# --- 8. DET (Duolingo English Test - Practical, Academic & Fluency) ---
add_words("DET", [
    ("Accessible", "Erişilebilir / Kolay anlaşılır", "The new museum layout makes exhibits fully accessible to wheelchair users."),
    ("Accomplish", "Başarmak / Sonuçlandırmak", "Dedication and persistent effort enabled her to accomplish her academic goals."),
    ("Accurate", "Doğru / Hatasız", "Modern GPS devices provide accurate coordinates down to within centimeters."),
    ("Adaptable", "Uyum sağlayabilen", "Successful species are remarkably adaptable to changing environmental conditions."),
    ("Adequate", "Yeterli / Kafi", "Ensure that you get adequate sleep before undertaking a long drive."),
    ("Affordable", "Hesaplı / Karşılanabilir", "The university opened affordable student residences close to the main campus."),
    ("Ambition", "Hırs / Büyük hedef", "His lifelong ambition has always been to direct an international orchestra."),
    ("Apparent", "Aşikar / Meydanda", "The apparent cause of the software glitch was a missing semi-colon."),
    ("Appreciate", "Takdir etmek / Değerini bilmek", "Art historians appreciate the subtle brushwork of Impressionist painters."),
    ("Appropriate", "Uygun / Yerinde", "Please select appropriate formal attire for the scholarship awards ceremony."),
    ("Authentic", "Orijinal / Hakiki", "The gallery verified that the Renaissance portrait was completely authentic."),
    ("Benefit", "Fayda / Yarar sağlamak", "Regular physical exercise offers undeniable benefits for mental wellbeing."),
    ("Broaden", "Genişletmek / Ufkunu açmak", "Studying abroad provides a unique opportunity to broaden one's perspective."),
    ("Capable", "Becerikli / Yetenekli", "The newly appointed manager proved capable of handling tense negotiations."),
    ("Circumstance", "Koşul / Hal / Şart", "Under no circumstance should students open test booklets before the bell."),
    ("Collaborate", "Birlikte çalışmak", "Designers collaborate with engineers to optimize ergonomic handles."),
    ("Comprehend", "Kavramak / Tam manasıyla anlamak", "It was difficult to comprehend the scale of the subterranean cave network."),
    ("Concentrate", "Odaklanmak / Yoğunlaşmak", "Please eliminate distractions so that you can concentrate on reading."),
    ("Conclude", "Sonuçlandırmak / Karara varmak", "The panel concluded that the proposal met all necessary criteria."),
    ("Confident", "Özgüvenli / Kendinden emin", "She sounded confident during her presentation on renewable technologies."),
    ("Consistent", "Tutarlı / İstikrarlı", "Consistent daily practice is the most reliable path to linguistic fluency."),
    ("Contribute", "Katkı sunmak / Yardım etmek", "Every member of the community was encouraged to contribute to the festival."),
    ("Crucial", "Hayati derecede mühim", "Timely medical intervention is crucial for recovery from acute stroke."),
    ("Demonstrate", "Göstermek / İspatlamak", "Lab simulations demonstrate how ocean acidification impairs coral reef growth."),
    ("Diverse", "Çeşitli / Farklı", "The campus boasts a diverse student population hailing from eighty nations."),
    ("Emphasize", "Vurgulamak / Altını çizmek", "The professor emphasized the necessity of citing all secondary sources."),
    ("Engage", "Meşgul olmak / Katılım sağlamak", "The interactive workshop was designed to engage high school students."),
    ("Enhance", "Geliştirmek / Artırmak", "Audiobooks can enhance vocabulary acquisition among young foreign readers."),
    ("Essential", "Vazgeçilmez / Temel", "Water and sunlight are essential nutrients for all terrestrial plant life."),
    ("Establish", "Kurmak / Tesis etmek", "The foundation established a scholarship for underprivileged STEM students."),
    ("Evidence", "Kanıt / Delil", "Archeologists unearthed evidence of ancient grain cultivation in the valley."),
    ("Expand", "Genişlemek / Büyümek", "The retail chain plans to expand operations into three European nations."),
    ("Flexible", "Esnek / Uyumlu", "Modern work environments favor flexible scheduling and remote collaboration."),
    ("Fundamental", "Temel / Esas", "Respect for human rights is a fundamental principle of international law."),
    ("Highlight", "Öne çıkarmak / Vurgulamak", "The documentary highlights the plight of endangered sea turtle habitats."),
    ("Illustrate", "Örneklerle açıklamak", "Case studies illustrate how supply chain disruptions impact product retail."),
    ("Impact", "Etki / Etkilemek", "Deforestation has a profound impact on local hydrology and precipitation."),
    ("Indicate", "İşaret etmek / Göstermek", "Survey metrics indicate growing public support for wind energy subsidies."),
    ("Influence", "Etki / Nüfuz etmek", "Classical Greek philosophy exerted immense influence over Western thought."),
    ("Initiative", "Girişim / İnisiyatif", "The city council launched an ambitious clean river initiative this spring.")
])

# --- 9. EIKEN (Grade Pre-1 & Grade 1 Vocabulary) ---
add_words("EIKEN", [
    ("Abolish", "Yürürlükten kaldırmak / İptal etmek", "Humanitarian groups campaigned tirelessly to abolish capital punishment."),
    ("Abundant", "Bol / Bereketli", "The coastal waters teem with abundant shoals of migratory sardines."),
    ("Acquaint", "Tanıştırmak / Haberdar etmek", "New employees must acquaint themselves with our health and safety guide."),
    ("Afflict", "Musallat olmak / Izdırap vermek", "Tropical diseases continue to afflict populations in low-lying delta zones."),
    ("Allocate", "Ödenek ayırmak", "The ministry agreed to allocate emergency funds to reinforce flood dykes."),
    ("Anticipate", "Önceden sezinlemek", "Retailers anticipate a record surge in purchases ahead of the holiday week."),
    ("Apathy", "Kayıtsızlık / Umursamazlık", "Voter apathy is often cited as a symptom of declining trust in politicians."),
    ("Appease", "Yatıştırmak / Gönlünü yapmak", "The government offered tax exemptions in an effort to appease angry farmers."),
    ("Arbitrary", "Hakkaniyetsiz / Keyfi", "Critics condemned the decision to dismiss the editor as entirely arbitrary."),
    ("Arduous", "Meşakkatli / Çok zahmetli", "The mountaineering team completed an arduous trek through frozen terrain."),
    ("Ascertain", "Tahkik etmek / Aslını öğrenmek", "Inspectors examined the wreckage to ascertain whether mechanical failure occurred."),
    ("Aspire", "Göz dikmek / Can atmak", "Many young apprentices aspire to master traditional ceramic techniques."),
    ("Austere", "Gösterişsiz / Sade", "The monastery was built according to the austere rules of the early order."),
    ("Autonomous", "Kendi kendini yöneten", "The island prefecture enjoys autonomous control over educational funding."),
    ("Banish", "Sürgün etmek / Kafasından atmak", "She attempted to banish all anxious doubts before entering the interview."),
    ("Barren", "Çorak / Verimsiz", "After decades of overgrazing, the once lush plain turned into barren dust."),
    ("Belittle", "Küçümsemek / Hakir görmek", "Supervisors should never belittle junior researchers when questions arise."),
    ("Benign", "Zararsız / Huylu (Tıp)", "The biopsy confirmed that the cellular growth was completely benign."),
    ("Bewilder", "Afallatmak / Hayrete düşürmek", "The sudden shift in monetary regulations bewildered international investors."),
    ("Blatant", "Bariz / Apaçık / Saygısızca", "Ignoring sanitary protocols was a blatant violation of laboratory ethics."),
    ("Bountiful", "Bol bereketli / Cömert", "The orchard produced a bountiful harvest of honeycrisp apples this autumn."),
    ("Brevity", "Kısalık / Öz ve az oluş", "Brevity is the soul of effective business and technical communication."),
    ("Calamity", "Büyük felaket / Afet", "The eruption was a catastrophic calamity that displaced thousands of villagers."),
    ("Capacious", "Geniş / Çok şey alan", "The museum storage vault is a capacious room equipped with climate controls."),
    ("Captivate", "Büyülemek / Kendine hayran bırakmak", "Her virtuosic violin solo captivated the audience at the grand theatre."),
    ("Catastrophe", "Yıkım / Facia", "The tsunami proved to be a natural catastrophe of unprecedented magnitude."),
    ("Chastise", "Azarlamak / Tekdir etmek", "The headmaster chastised the pupils for damaging school gymnasium property."),
    ("Chronological", "Zaman sırasına göre", "The exhibition arranges the master's woodblock prints in chronological order."),
    ("Circumscribe", "Sınırlamak / Çevrelemek", "New laws circumscribe the circumstances under which public land can be sold."),
    ("Coerce", "Zorlamak / Baskı altına almak", "Defendants argued that confessions were coerced under psychological duress."),
    ("Cognizant", "Farkında / Haberdar", "Engineers must remain fully cognizant of seismic vulnerability standards."),
    ("Colossal", "Muazzam / Devasa", "The construction of the hydro dam required a colossal investment of resources."),
    ("Commemorate", "Anmak / Hatırasını yaşatmak", "A bronze statue was unveiled to commemorate the town's founding poet."),
    ("Compassion", "Merhamet / Şefkat", "Hospice nurses treat every palliative care resident with extraordinary compassion."),
    ("Compelling", "Son derece ikna edici", "The forensic evidence presented a compelling case for the defendant's guilt."),
    ("Complacent", "Rehavete düşmüş", "World champions cannot afford to become complacent between Olympic tournaments."),
    ("Compliant", "Kurallara harfiyen uyan", "The newly manufactured aircraft was certified as compliant with all emissions laws."),
    ("Comprehensive", "Ayrıntılı ve etraflı", "The ministry published a comprehensive report on demographic aging trends."),
    ("Concur", "Aynı fikirde olmak", "Medical specialists concur that preventative exercise lowers stroke hazards."),
    ("Condone", "Göz yummak / Hoş görmek", "The university administration will never condone academic plagiarism.")
])

# --- 10. YDS / YÖKDİL / YDT (Academic Turkish Context) ---
add_words("YDS", [
    ("Compensate", "Telafi etmek / Dengelemek", "The government agreed to compensate farmers for severe drought damages."),
    ("Ambiguity", "Muğlaklık / Belirsizlik", "The legal wording leaves no room for ambiguity regarding patent rights."),
    ("Precaution", "Önlem / Tedbir", "Always take every reasonable safety precaution when operating high-voltage tools."),
    ("Subsequent", "Ardından gelen / Sonraki", "The initial tremor and subsequent aftershocks caused extensive destruction."),
    ("Undermine", "Baltalamak / Temelini zayıflatmak", "Persistent budget cuts threaten to undermine the quality of public healthcare."),
    ("Deteriorate", "Gittikçe kötüleşmek", "Relations between the neighbouring nations began to deteriorate rapidly."),
    ("Fluctuate", "Dalgalanmak / Değişmek", "Crude oil market prices fluctuate violently during geopolitical tensions."),
    ("Reconcile", "Uzlaştırmak / Barıştırmak", "Historians tried to reconcile the divergent accounts of the medieval treaty."),
    ("Contradict", "Çelişmek / Yalanlamak", "The latest experimental metrics contradict long-accepted acoustic models."),
    ("Eradicate", "Kökünü kazımak / Yok etmek", "Global vaccination initiatives aim to eradicate infectious polio worldwide."),
    ("Devastate", "Harap etmek / Yıkmak", "The catastrophic Category 5 cyclone devastated coastal resort villages."),
    ("Trigger", "Tetiklemek / Başlatmak", "Prolonged financial inflation can trigger widespread domestic strikes."),
    ("Hazardous", "Tehlikeli / Riskli", "Disposing of radioactive toxic waste remains a hazardous operation."),
    ("Plausible", "Akla yatkın / Mantıklı", "The detective developed a plausible explanation for the museum break-in."),
    ("Coincide", "Denk düşmek / Çakışmak", "Her resignation was timed to coincide with the end of the fiscal term."),
    ("Impair", "Bozmak / Zarara uğratmak", "Excessive alcohol consumption significantly impairs motor reflexes."),
    ("Reluctant", "Gönülsüz / İsteksiz", "The board was reluctant to approve risky offshore speculative acquisitions."),
    ("Stem from", "-den kaynaklanmak / İleri gelmek", "Many cardiovascular issues stem from sedentary habits and poor nutrition."),
    ("Incompatible", "Uyuşmaz / Bağdaşmayan", "Extreme dogmatism is completely incompatible with open scientific inquiry."),
    ("Accelerate", "Hızlanmak / Hızlandırmak", "Subsidies for electric vehicles accelerated the shift away from diesel engines."),
    ("Accomplish", "Başarı ile tamamlamak", "The diplomatic delegation managed to accomplish all primary objectives."),
    ("Acknowledge", "Kabul etmek / İtiraf etmek", "The company was forced to acknowledge structural defects in the vehicle."),
    ("Adapt", "Uyum sağlamak / Adapte olmak", "Desert flora develop deep roots to adapt to arid climates."),
    ("Advocate", "Müdafaa etmek / Savunmak", "Humanitarian agencies advocate for refugee access to education."),
    ("Alleviate", "Hafifletmek / Azaltmak", "Painkillers were administered to alleviate the patient's discomfort."),
    ("Allocate", "Pay ayırmak / Tahsis etmek", "The ministry must allocate sufficient funding for seismic retrofitting."),
    ("Anticipate", "Öngörmek / Sezinlemek", "Demographers anticipate rapid urban population expansion across Asia."),
    ("Attribute", "Dayandırmak / -e mal etmek", "Scientists attribute coral reef bleaching to elevated water temperatures."),
    ("Cease", "Son bulmak / Durmak", "Hostilities ceased following the signing of an internationally monitored truce."),
    ("Coincide", "Rast gelmek / Denk gelmek", "The lunar cycle coincides with tidal surges in coastal estuaries."),
    ("Collaborate", "İş birliği yapmak", "Medical universities collaborate on genetic research projects."),
    ("Commence", "Başlamak / Start almak", "The construction of the geothermal generator will commence in spring."),
    ("Compensate", "Karşılamak / Telafi etmek", "Insuring companies compensate homeowners for storm-related damages."),
    ("Concur", "Hemfikir olmak", "All three independent audits concur with the chief accountant's findings."),
    ("Confine", "Sınırlandırmak", "Please confine your speech to the specific items listed on today's agenda."),
    ("Constitute", "Teşkil etmek / Oluşturmak", "Fossil fuels constitute roughly eighty percent of world energy consumption."),
    ("Contribute", "Katkı sunmak / Sebep olmak", "Greenhouse gas emissions contribute substantially to global warming."),
    ("Crucial", "Hayati ehemmiyete haiz", "Linguistic proficiency is crucial for diplomats serving abroad."),
    ("Deplete", "Tüketmek / Suyunu çekmek", "Over-irrigation threatens to deplete subterranean aquifer reserves."),
    ("Differentiate", "Ayırt etmek / Farklı kılmak", "Genomic sequencing helps biologists differentiate between twin species.")
])

# Write words_bank.json
words_path = os.path.join(OUTPUT_DIR, "words_bank.json")
with open(words_path, "w", encoding="utf-8") as f:
    json.dump(words_data, f, ensure_ascii=False, indent=2)

print(f"Successfully generated {len(words_data)} words in {words_path}")

# ==============================================================================
# 2. COMPREHENSIVE EXAMS BANK (30 Full Exams, 270+ Realistic Questions)
# ==============================================================================

exams_data = []

def add_exam(exam_id, title, duration_min, exam_type, level_badge, difficulty, questions):
    exams_data.append({
        "id": exam_id,
        "title": title,
        "totalQuestions": len(questions),
        "durationMinutes": duration_min,
        "examType": exam_type,
        "levelBadge": level_badge,
        "difficulty": difficulty,
        "questions": questions
    })

# --- 1. IELTS Exams ---
add_exam(
    "exam-ielts-academic-1",
    "IELTS Academic: Band 7+ Reading & Lexical Skills",
    20, "IELTS", "C1", "Hard",
    [
        {"id": "ielts_1_1", "text": "The latest peer-reviewed findings ______ the long-held hypothesis regarding climate fluctuations.", "options": ["corroborated", "concocted", "alleviated", "dissipated"], "correctOptionIndex": 0, "explanation": "\"corroborate\": delillerle doğrulamak/teyit etmek. Akademik makalelerde hipotezi kanıtlamak anlamında en sık kullanılan fiildir."},
        {"id": "ielts_1_2", "text": "Rapid industrialization has led to a noticeable ______ of natural groundwater reserves in the delta.", "options": ["depletion", "propagation", "alleviation", "enhancement"], "correctOptionIndex": 0, "explanation": "\"depletion\": tükenme, azalma. 'Depletion of natural resources' (kaynakların tükenmesi) yaygın bir akademik kalıptır."},
        {"id": "ielts_1_3", "text": "The study demonstrated that early childhood bilingualism provides significant cognitive ______.", "options": ["dividends", "detriments", "discrepancies", "impediments"], "correctOptionIndex": 0, "explanation": "\"cognitive dividends\": zihinsel/bilişsel kazanımlar veya faydalar anlamına gelir."},
        {"id": "ielts_1_4", "text": "The committee's recommendations were deliberately ______, leaving room for divergent local interpretations.", "options": ["ambiguous", "lucid", "meticulous", "tangible"], "correctOptionIndex": 0, "explanation": "\"ambiguous\": muğlak, ucu açık, çift anlamlı."},
        {"id": "ielts_1_5", "text": "Environmentalists fear that the melting of Arctic permafrost will ______ vast amounts of stored methane.", "options": ["liberate", "constrain", "confine", "curtail"], "correctOptionIndex": 0, "explanation": "\"liberate\": serbest bırakmak/salmak ('release' eşanlamlısı)."},
        {"id": "ielts_1_6", "text": "The historical document serves to ______ our understanding of medieval mercantile routes.", "options": ["enrich", "impair", "stifle", "undermine"], "correctOptionIndex": 0, "explanation": "\"enrich\": zenginleştirmek, derinleştirmek."},
        {"id": "ielts_1_7", "text": "Scientists noted a profound ______ between the two demographic datasets that could not be explained.", "options": ["disparity", "conformity", "analogy", "symmetry"], "correctOptionIndex": 0, "explanation": "\"disparity\": büyük farklılık, uçurum, uyumsuzluk."},
        {"id": "ielts_1_8", "text": "The professor delivered a ______ analysis that dismantled previous sociological assumptions.", "options": ["scathing", "superficial", "banal", "lukewarm"], "correctOptionIndex": 0, "explanation": "\"scathing\": yerici, oldukça sert ve keskin eleştirel."}
    ]
)

add_exam(
    "exam-ielts-academic-2",
    "IELTS Academic: Band 8+ Vocabulary & Cohesion",
    20, "IELTS", "C1", "Hard",
    [
        {"id": "ielts_2_1", "text": "Many developing economies strive to ______ their reliance on imported fossil fuels.", "options": ["curtail", "augment", "bolster", "perpetuate"], "correctOptionIndex": 0, "explanation": "\"curtail\": kısmak, sınırlandırmak, azaltmak."},
        {"id": "ielts_2_2", "text": "The author argues that modern consumerism has an ______ effect on civic engagement.", "options": ["insidious", "innocuous", "auspicious", "immaculate"], "correctOptionIndex": 0, "explanation": "\"insidious\": sinsi, fark edilmeden zarar veren."},
        {"id": "ielts_2_3", "text": "The government adopted a ______ approach, testing policy measures in small pilot towns first.", "options": ["pragmatic", "dogmatic", "capricious", "reckless"], "correctOptionIndex": 0, "explanation": "\"pragmatic\": pratik, faydacı, gerçekçi yaklaşım."},
        {"id": "ielts_2_4", "text": "The new satellite imaging array offers ______ precision in tracking illegal deforestation.", "options": ["unprecedented", "negligible", "obsolete", "mediocre"], "correctOptionIndex": 0, "explanation": "\"unprecedented\": eşi benzeri görülmemiş, tarihte ilk olan."},
        {"id": "ielts_2_5", "text": "Urban planners must reconcile commercial expansion ______ green space conservation.", "options": ["with", "for", "to", "against"], "correctOptionIndex": 0, "explanation": "\"reconcile with\": ile uzlaştırmak/bağdaştırmak."},
        {"id": "ielts_2_6", "text": "The candidate's failure to cite sources was deemed a serious ______ of academic integrity.", "options": ["breach", "feat", "summit", "merit"], "correctOptionIndex": 0, "explanation": "\"breach of integrity\": etik/bütünlük ihlali."},
        {"id": "ielts_2_7", "text": "Over-harvesting of timber threatens to ______ the delicate balance of the tropical rainforest.", "options": ["destabilize", "substantiate", "corroborate", "fortify"], "correctOptionIndex": 0, "explanation": "\"destabilize\": istikrarını bozmak, dengesini sarsmak."},
        {"id": "ielts_2_8", "text": "The university senate granted the research institute ______ status to pursue independent projects.", "options": ["autonomous", "subservient", "precarious", "provisional"], "correctOptionIndex": 0, "explanation": "\"autonomous\": özerk, bağımsız."}
    ]
)

add_exam(
    "exam-ielts-general-1",
    "IELTS General: Workplace & Social Contexts",
    18, "IELTS", "B2", "Medium",
    [
        {"id": "ielts_g_1", "text": "All employees are expected to comply ______ corporate safety guidelines at all times.", "options": ["with", "to", "for", "at"], "correctOptionIndex": 0, "explanation": "\"comply with\": kurallara uymak/itaat etmek edat kalıbıdır."},
        {"id": "ielts_g_2", "text": "The landlord agreed to ______ the leaking radiator before the new tenants moved in.", "options": ["rectify", "dismantle", "aggravate", "postpone"], "correctOptionIndex": 0, "explanation": "\"rectify\": düzeltmek, onarmak, çare bulmak."},
        {"id": "ielts_g_3", "text": "Please ensure that your application includes two ______ who can verify your work experience.", "options": ["referees", "opponents", "bystanders", "spectators"], "correctOptionIndex": 0, "explanation": "\"referees\": referans veren kişiler."},
        {"id": "ielts_g_4", "text": "Employees who work overtime are entitled to additional ______ or time off in lieu.", "options": ["remuneration", "scrutiny", "admonition", "deterrence"], "correctOptionIndex": 0, "explanation": "\"remuneration\": ücret, maaş, tazminat ödemesi."},
        {"id": "ielts_g_5", "text": "Due to scheduled maintenance, railway services will be ______ between 10 PM and 5 AM.", "options": ["curtailed", "multiplied", "exalted", "prolonged"], "correctOptionIndex": 0, "explanation": "\"curtailed\": kısaltılmış/seferleri azaltılmış."},
        {"id": "ielts_g_6", "text": "The manager praised the sales team for showing great ______ during the system transition.", "options": ["diligence", "negligence", "reluctance", "insolence"], "correctOptionIndex": 0, "explanation": "\"diligence\": çalışkanlık, özveri, dikkat."},
        {"id": "ielts_g_7", "text": "Candidates must possess ______ communication skills both in writing and in speech.", "options": ["impeccable", "flawed", "dubious", "feeble"], "correctOptionIndex": 0, "explanation": "\"impeccable\": kusursuz, hatasız."},
        {"id": "ielts_g_8", "text": "The package was returned because the delivery address was completely ______.", "options": ["illegible", "pristine", "lucid", "compelling"], "correctOptionIndex": 0, "explanation": "\"illegible\": okunaksız (el yazısı veya adres için)."}
    ]
)

# --- 2. TOEFL Exams ---
add_exam(
    "exam-toefl-reading-1",
    "TOEFL iBT: Academic Reading & Natural Sciences",
    20, "TOEFL", "C1", "Hard",
    [
        {"id": "toefl_1_1", "text": "Subterranean magma chambers tend to ______ significant gaseous pressure before an eruption.", "options": ["accumulate", "dissipate", "attenuate", "extinguish"], "correctOptionIndex": 0, "explanation": "\"accumulate\": biriktirmek/yığmak ('build up' eşanlamlısı)."},
        {"id": "toefl_1_2", "text": "The adaptation allows nocturnal predators to hunt in conditions of almost total ______.", "options": ["obscurity", "radiance", "prominence", "lucidity"], "correctOptionIndex": 0, "explanation": "\"obscurity\": karanlık, belirsizlik."},
        {"id": "toefl_1_3", "text": "Sedimentary strata provide geologists with a chronological ______ of ancient biosphere changes.", "options": ["record", "fiction", "illusion", "facade"], "correctOptionIndex": 0, "explanation": "\"record\": kayıt, kronolojik döküm."},
        {"id": "toefl_1_4", "text": "Certain alpine lichens are remarkably ______ to extreme sub-zero temperatures.", "options": ["resilient", "susceptible", "vulnerable", "fragile"], "correctOptionIndex": 0, "explanation": "\"resilient to\": -e karşı dayanıklı/dirençli."},
        {"id": "toefl_1_5", "text": "The lecture emphasized that dinosaur extinction was accelerated by a ______ cosmic impact.", "options": ["cataclysmic", "negligible", "tentative", "trivial"], "correctOptionIndex": 0, "explanation": "\"cataclysmic\": yıkıcı, afetsel, büyük felakete yol açan."},
        {"id": "toefl_1_6", "text": "Marine biologists theorize that coral reefs ______ coastal communities from severe storm surges.", "options": ["shield", "expose", "abandon", "endanger"], "correctOptionIndex": 0, "explanation": "\"shield from\": -den korumak/kalkan olmak."},
        {"id": "toefl_1_7", "text": "The fossil specimen displays a transitional morphology that ______ avian and reptilian traits.", "options": ["bridges", "severs", "repels", "obliterates"], "correctOptionIndex": 0, "explanation": "\"bridge\": köprü kurmak, aradaki bağı sağlamak."},
        {"id": "toefl_1_8", "text": "The theory was discarded when experimental outcomes proved to be ______ with the hypothesis.", "options": ["incompatible", "congruent", "aligned", "harmonious"], "correctOptionIndex": 0, "explanation": "\"incompatible with\": -ile bağdaşmayan/uyumsuz."}
    ]
)

add_exam(
    "exam-toefl-listening-vocab",
    "TOEFL iBT: Campus & Lecture Vocabulary",
    18, "TOEFL", "B2", "Medium",
    [
        {"id": "toefl_l_1", "text": "The professor advised students to ______ their research topics to ensure a feasible thesis.", "options": ["narrow down", "broaden out", "brush off", "give away"], "correctOptionIndex": 0, "explanation": "\"narrow down\": daraltmak, odaklamak."},
        {"id": "toefl_l_2", "text": "The teaching assistant offered to ______ the complex points covered in Wednesday's lecture.", "options": ["clarify", "obfuscate", "disguise", "convolute"], "correctOptionIndex": 0, "explanation": "\"clarify\": açıklığa kavuşturmak, izah etmek."},
        {"id": "toefl_l_3", "text": "Due to library renovations, access to the special collections archive has been ______ suspended.", "options": ["temporarily", "perpetually", "eternally", "invariably"], "correctOptionIndex": 0, "explanation": "\"temporarily\": geçici olarak."},
        {"id": "toefl_l_4", "text": "The syllabus states that late submissions will incur a five percent ______ per calendar day.", "options": ["penalty", "rebate", "subsidy", "dividend"], "correctOptionIndex": 0, "explanation": "\"penalty\": ceza puanı/kesintisi."},
        {"id": "toefl_l_5", "text": "Students are urged to ______ the dean of students regarding financial aid eligibility.", "options": ["consult", "assail", "renounce", "admonish"], "correctOptionIndex": 0, "explanation": "\"consult\": danışmak, görüş almak."},
        {"id": "toefl_l_6", "text": "The anthropology seminar aims to explore how ancestral folklore was ______ down orally.", "options": ["passed", "thrown", "broken", "dragged"], "correctOptionIndex": 0, "explanation": "\"passed down\": nesilden nesile aktarılmak."},
        {"id": "toefl_l_7", "text": "The registrar announced that students with outstanding fees cannot ______ for fall courses.", "options": ["register", "relinquish", "resign", "disband"], "correctOptionIndex": 0, "explanation": "\"register for\": ders kaydı yaptırmak."},
        {"id": "toefl_l_8", "text": "The professor commended the undergraduate for presenting such an ______ research paper.", "options": ["insightful", "incompetent", "abysmal", "erratic"], "correctOptionIndex": 0, "explanation": "\"insightful\": derinlikli, ufuk açıcı, kavrayışlı."}
    ]
)

add_exam(
    "exam-toefl-writing-integr",
    "TOEFL iBT: Integrated Writing & Synthesis",
    20, "TOEFL", "C1", "Hard",
    [
        {"id": "toefl_w_1", "text": "In the listening lecture, the professor firmly casts doubt ______ the author's primary claims.", "options": ["on", "with", "into", "at"], "correctOptionIndex": 0, "explanation": "\"cast doubt on\": şüphe düşürmek/kuşku uyandırmak."},
        {"id": "toefl_w_2", "text": "The speaker contends that the proposed carbon tax would prove economically ______.", "options": ["detrimental", "beneficial", "advantageous", "auspicious"], "correctOptionIndex": 0, "explanation": "\"detrimental\": zararlı, hasar verici."},
        {"id": "toefl_w_3", "text": "While the passage praises the policy, the lecturer points out several severe ______.", "options": ["shortcomings", "triumphs", "enhancements", "amenities"], "correctOptionIndex": 0, "explanation": "\"shortcomings\": eksiklikler, kusurlar."},
        {"id": "toefl_w_4", "text": "The lecturer directly ______ the claim that wind farms cause irreversible acoustic pollution.", "options": ["refutes", "validates", "substantiates", "endorses"], "correctOptionIndex": 0, "explanation": "\"refute\": çürütmek, aksini kanıtlamak."},
        {"id": "toefl_w_5", "text": "Neither the reading passage nor the professor considers the proposed budget to be ______.", "options": ["feasible", "futile", "fanciful", "frail"], "correctOptionIndex": 0, "explanation": "\"feasible\": uygulanabilir, yapılabilir."},
        {"id": "toefl_w_6", "text": "The author asserts that the archaeological artifacts date back to the Bronze Age, ______ the speaker disagrees.", "options": ["whereas", "furthermore", "consequently", "likewise"], "correctOptionIndex": 0, "explanation": "\"whereas\": oysa, halbuki (zıtlık bağlacı)."},
        {"id": "toefl_w_7", "text": "The professor provides empirical counter-evidence to ______ the reading's conclusions.", "options": ["undermine", "corroborate", "fortify", "underpin"], "correctOptionIndex": 0, "explanation": "\"undermine\": temelini zayıflatmak, baltalamak."},
        {"id": "toefl_w_8", "text": "Overall, the listening completely ______ the assumptions outlined in the introductory text.", "options": ["contradicts", "mirrors", "emulates", "reinforces"], "correctOptionIndex": 0, "explanation": "\"contradict\": çelişmek, tam aksini iddia etmek."}
    ]
)

# --- 3. TOEIC Exams ---
add_exam(
    "exam-toeic-business-1",
    "TOEIC Mastery: Corporate Finance & Contracts",
    18, "TOEIC", "B2", "Medium",
    [
        {"id": "toeic_1_1", "text": "All travel expenses must be submitted to accounting with original itemized ______.", "options": ["receipts", "rebukes", "recipients", "remedies"], "correctOptionIndex": 0, "explanation": "\"receipts\": makbuzlar/faturalar."},
        {"id": "toeic_1_2", "text": "The board reached a consensus to approve the ______ of the German logistics provider.", "options": ["acquisition", "abdication", "annulment", "aberration"], "correctOptionIndex": 0, "explanation": "\"acquisition\": şirket devralma/satın alma."},
        {"id": "toeic_1_3", "text": "Please be advised that all payments are due within thirty days of the ______ date.", "options": ["invoice", "inventory", "investiture", "itinerary"], "correctOptionIndex": 0, "explanation": "\"invoice date\": fatura kesim tarihi."},
        {"id": "toeic_1_4", "text": "The company's quarterly dividend was increased due to an unprecedented surge in operating ______.", "options": ["revenue", "restitution", "rebellion", "retraction"], "correctOptionIndex": 0, "explanation": "\"revenue\": şirket cirosu/hasılatı."},
        {"id": "toeic_1_5", "text": "The contract explicitly ______ that intellectual property rights belong exclusively to the client.", "options": ["stipulates", "stifles", "stagnates", "strangles"], "correctOptionIndex": 0, "explanation": "\"stipulate\": sözleşmede açıkça şart koşmak/hükme bağlamak."},
        {"id": "toeic_1_6", "text": "The procurement director negotiated a five percent volume discount with our hardware ______.", "options": ["vendor", "vandal", "vagrant", "veteran"], "correctOptionIndex": 0, "explanation": "\"vendor\": tedarikçi/satıcı firma."},
        {"id": "toeic_1_7", "text": "Staff members who meet their annual performance targets are ______ for an overseas training grant.", "options": ["eligible", "illegible", "indelible", "ineligible"], "correctOptionIndex": 0, "explanation": "\"eligible for\": hak sahibi, kriterleri sağlayan."},
        {"id": "toeic_1_8", "text": "We conducted an extensive feasibility ______ before purchasing the commercial warehouse.", "options": ["study", "fable", "rumor", "parable"], "correctOptionIndex": 0, "explanation": "\"feasibility study\": fizibilite/uygulanabilirlik çalışması."}
    ]
)

add_exam(
    "exam-toeic-business-2",
    "TOEIC Mastery: Operations, HR & Supply Chain",
    18, "TOEIC", "B2", "Medium",
    [
        {"id": "toeic_2_1", "text": "The human resources division announced a new policy regarding parental ______.", "options": ["leave", "relief", "lease", "lien"], "correctOptionIndex": 0, "explanation": "\"parental leave\": ebeveyn izin hakkı."},
        {"id": "toeic_2_2", "text": "All factory personnel are required to wear high-visibility ______ on the warehouse floor.", "options": ["apparel", "utensil", "baggage", "furniture"], "correctOptionIndex": 0, "explanation": "\"apparel\": kıyafet, giysi."},
        {"id": "toeic_2_3", "text": "The scheduled delivery was delayed because the cargo ship encountered inclement ______ at sea.", "options": ["weather", "whether", "wether", "feather"], "correctOptionIndex": 0, "explanation": "\"inclement weather\": sert/kötü hava koşulları."},
        {"id": "toeic_2_4", "text": "The IT team will execute an ______ of our network servers over the upcoming weekend.", "options": ["overhaul", "outrage", "outpouring", "overture"], "correctOptionIndex": 0, "explanation": "\"overhaul\": kapsamlı bakım/yenileme."},
        {"id": "toeic_2_5", "text": "Applicants must demonstrate at least five years of ______ experience in warehouse management.", "options": ["relevant", "reluctant", "redundant", "repugnant"], "correctOptionIndex": 0, "explanation": "\"relevant experience\": ilgili alandaki iş deneyimi."},
        {"id": "toeic_2_6", "text": "The committee decided to ______ the product launch until supply chain bottlenecks resolve.", "options": ["postpone", "expedite", "accelerate", "commence"], "correctOptionIndex": 0, "explanation": "\"postpone\": ertelemek."},
        {"id": "toeic_2_7", "text": "Please circulate the updated meeting ______ to all department heads prior to tomorrow's briefing.", "options": ["agenda", "alibi", "anecdote", "affliction"], "correctOptionIndex": 0, "explanation": "\"agenda\": toplantı gündemi."},
        {"id": "toeic_2_8", "text": "The company's customer service hotline is available around the clock to address any client ______.", "options": ["inquiries", "injuries", "indignities", "infractions"], "correctOptionIndex": 0, "explanation": "\"inquiries\": müşteri soruları/talepleri."}
    ]
)

add_exam(
    "exam-toeic-reading-part7",
    "TOEIC Reading: Double & Triple Passage Comprehension",
    22, "TOEIC", "C1", "Hard",
    [
        {"id": "toeic_3_1", "text": "According to the corporate memo, what is the primary objective of the restructuring?", "options": ["To streamline communication and reduce redundant overhead", "To eliminate all customer service operations", "To relocate headquarters to another continent", "To decrease the annual holiday allowance for managers"], "correctOptionIndex": 0, "explanation": "Metindeki ana amaç 'streamline operations and cut overhead expenses' ifadesidir."},
        {"id": "toeic_3_2", "text": "In the letter, what does the supplier guarantee to provide in the event of defective shipments?", "options": ["A full refund and immediate replacement at no extra charge", "A penalty fee paid by the buyer", "A store credit valid only for twelve months", "A legal defense team in court"], "correctOptionIndex": 0, "explanation": "Tedarikçi mektubunda 'full refund or expedited replacement free of shipping costs' taahhüt edilmektedir."},
        {"id": "toeic_3_3", "text": "What prerequisite must candidates fulfill before enrolling in the executive leadership seminar?", "options": ["Complete the online prerequisite training module", "Submit five years of audited corporate tax returns", "Secure endorsement from external competitors", "Pay tuition in advance using cryptocurrency"], "correctOptionIndex": 0, "explanation": "Başvuru şartlarında 'completion of the introductory compliance training' zorunlu tutulmuştur."},
        {"id": "toeic_3_4", "text": "Why was the press release issued earlier than originally scheduled?", "options": ["To preempt speculation following the leaked merger documents", "To announce the immediate bankruptcy of the manufacturer", "To introduce an unrelated consumer beverage line", "To advertise discount flight tickets for the holidays"], "correctOptionIndex": 0, "explanation": "'To counter market speculation' ifadesi doğrudan bu seçeneğe işaret eder."},
        {"id": "toeic_3_5", "text": "Which department is responsible for approving custom software modifications?", "options": ["Quality Assurance & Enterprise Architecture", "Public Relations and Media Outreach", "Facilities and Janitorial Management", "Reception and Visitor Coordination"], "correctOptionIndex": 0, "explanation": "Yazılım onay yetkisi 'Enterprise Architecture & QA' departmanına aittir."},
        {"id": "toeic_3_6", "text": "What incentive is offered to customers who renew their enterprise subscription before June 1st?", "options": ["A fifteen percent discount and complimentary cloud storage tier", "Free tickets to an international conference", "A complimentary company automobile", "Personalized stock options in the company"], "correctOptionIndex": 0, "explanation": "Erken yenileme kampanyası %15 indirim ve ek bulut depolama sunmaktadır."},
        {"id": "toeic_3_7", "text": "How did the regional branch manage to exceed its annual sales targets?", "options": ["By expanding retail distribution into regional grocery chains", "By raising prices by fifty percent across all categories", "By shutting down underperforming domestic branches", "By eliminating all advertising and marketing campaigns"], "correctOptionIndex": 0, "explanation": "Bölge müdürlüğü satış başarısını 'expanded distribution partnerships' ile açıklamıştır."},
        {"id": "toeic_3_8", "text": "What instruction is given to conference attendees regarding parking at the venue?", "options": ["Park only in Designated Zone C and validate tickets at reception", "Park along the public highway free of charge", "Reserve a private chauffeur at company expense", "Leave vehicles at the airport and commute on foot"], "correctOptionIndex": 0, "explanation": "Katılımcılara Zone C'ye park edip danışmada onaylatmaları bildirilmiştir."}
    ]
)

# --- 4. CAMBRIDGE Exams (FCE, CAE, CPE) ---
add_exam(
    "exam-cambridge-c1-advanced",
    "Cambridge C1 Advanced: Use of English & Collocations",
    20, "CAMBRIDGE", "C1", "Hard",
    [
        {"id": "cam_1_1", "text": "The research team took great ______ to ensure that no contaminated samples were introduced.", "options": ["pains", "trials", "burdens", "strains"], "correctOptionIndex": 0, "explanation": "\"take great pains to do something\": bir şeyi özenle ve titizlikle yapmak deyimidir."},
        {"id": "cam_1_2", "text": "The newly elected mayor vowed to ______ down on antisocial behaviour in public parks.", "options": ["crack", "snap", "break", "smash"], "correctOptionIndex": 0, "explanation": "\"crack down on\": sert tedbirler almak/üzerine gitmek."},
        {"id": "cam_1_3", "text": "Her exceptional academic thesis met with widespread ______ from international peers.", "options": ["acclaim", "affliction", "aversion", "animosity"], "correctOptionIndex": 0, "explanation": "\"widespread acclaim\": yaygın övgü ve beğeni."},
        {"id": "cam_1_4", "text": "It goes without ______ that regular exercise contributes to mental wellbeing.", "options": ["saying", "speaking", "talking", "telling"], "correctOptionIndex": 0, "explanation": "\"it goes without saying\": söylemeye bile gerek yok ki (sabit deyim)."},
        {"id": "cam_1_5", "text": "The company's expansion plans were thrown into ______ following the sudden market slump.", "options": ["disarray", "disrepute", "disregard", "disgrace"], "correctOptionIndex": 0, "explanation": "\"throw into disarray\": darmadağın etmek/kargaşaya sürüklemek."},
        {"id": "cam_1_6", "text": "She has an extraordinary ______ for picking up foreign languages without formal lessons.", "options": ["knack", "clench", "niche", "crevice"], "correctOptionIndex": 0, "explanation": "\"have a knack for\": bir şeye doğuştan yeteneği/eli yatkın olmak."},
        {"id": "cam_1_7", "text": "He decided to give his honest opinion, regardless of whether it rubbed colleagues up the ______ way.", "options": ["wrong", "bad", "false", "ill"], "correctOptionIndex": 0, "explanation": "\"rub someone up the wrong way\": birini gıcık etmek/tersine gitmek."},
        {"id": "cam_1_8", "text": "Under no ______ should laboratory chemicals be discarded in municipal drains.", "options": ["circumstances", "occasions", "instances", "moments"], "correctOptionIndex": 0, "explanation": "\"under no circumstances\": devrik yapı gerektiren 'hiçbir koşulda asla' kalıbı."}
    ]
)

add_exam(
    "exam-cambridge-b2-first",
    "Cambridge B2 First: Phrasal Verbs & Sentence Logic",
    18, "CAMBRIDGE", "B2", "Medium",
    [
        {"id": "cam_2_1", "text": "We had to ______ off the football tournament due to heavy torrential rain.", "options": ["call", "put", "take", "turn"], "correctOptionIndex": 0, "explanation": "\"call off\": iptal etmek ('cancel' eşanlamlısı)."},
        {"id": "cam_2_2", "text": "It took him several weeks to ______ over the disappointment of losing the race.", "options": ["get", "make", "pass", "look"], "correctOptionIndex": 0, "explanation": "\"get over\": atlatmak/kendini toparlamak."},
        {"id": "cam_2_3", "text": "Could you please ______ me through to the reservations department?", "options": ["put", "bring", "set", "push"], "correctOptionIndex": 0, "explanation": "\"put through to\": telefonda birini başkasına bağlamak."},
        {"id": "cam_2_4", "text": "She always ______ up to her elder sister, who works as a surgeon.", "options": ["looks", "sees", "stands", "watches"], "correctOptionIndex": 0, "explanation": "\"look up to\": örnek almak/saygı duymak."},
        {"id": "cam_2_5", "text": "The detective managed to ______ out who had leaked the classified documents.", "options": ["find", "carry", "take", "make"], "correctOptionIndex": 0, "explanation": "\"find out\": öğrenmek/ortaya çıkarmak."},
        {"id": "cam_2_6", "text": "I can easily put ______ with noisy traffic, but I cannot stand loud music late at night.", "options": ["up", "on", "in", "off"], "correctOptionIndex": 0, "explanation": "\"put up with\": tahammül etmek/katlanmak."},
        {"id": "cam_2_7", "text": "The scientific expedition ran ______ unexpected funding problems halfway through.", "options": ["into", "onto", "across", "over"], "correctOptionIndex": 0, "explanation": "\"run into problems\": sorunlarla karşılaşmak."},
        {"id": "cam_2_8", "text": "He promised to turn ______ for the charity marathon despite feeling exhausted.", "options": ["up", "in", "off", "on"], "correctOptionIndex": 0, "explanation": "\"turn up\": çıkagelmek/katılmak ('arrive' eşanlamlısı)."}
    ]
)

add_exam(
    "exam-cambridge-c2-mastery",
    "Cambridge C2 Proficiency: Idiomatic & Nuanced Lexis",
    25, "CAMBRIDGE", "C2", "Hard",
    [
        {"id": "cam_3_1", "text": "The politician's hollow excuses did not ring ______ with the disillusioned electorate.", "options": ["true", "right", "clear", "sound"], "correctOptionIndex": 0, "explanation": "\"ring true\": kulağa inandırıcı/doğru gelmek."},
        {"id": "cam_3_2", "text": "The two former rivals decided to bury the ______ and collaborate on the community project.", "options": ["hatchet", "dagger", "axe", "shield"], "correctOptionIndex": 0, "explanation": "\"bury the hatchet\": baltaları gömmek, barış ilan etmek."},
        {"id": "cam_3_3", "text": "Her scathing critique cut to the ______ of the committee's administrative shortcomings.", "options": ["quick", "bone", "core", "heart"], "correctOptionIndex": 0, "explanation": "\"cut to the quick\": can damarına dokunmak/en can alıcı noktasına vurmak."},
        {"id": "cam_3_4", "text": "The board gave the new design director free ______ to revamp the company's brand image.", "options": ["rein", "rope", "leash", "whip"], "correctOptionIndex": 0, "explanation": "\"give free rein\": tam serbestlik/yetki tanımak."},
        {"id": "cam_3_5", "text": "His impassioned testimony struck a ______ with everyone sitting in the courtroom.", "options": ["chord", "note", "tone", "bell"], "correctOptionIndex": 0, "explanation": "\"strike a chord\": bam teline dokunmak/derin duygu uyandırmak."},
        {"id": "cam_3_6", "text": "After twelve hours of heated negotiation, the diplomats finally reached common ______.", "options": ["ground", "land", "soil", "turf"], "correctOptionIndex": 0, "explanation": "\"reach common ground\": ortak paydada buluşmak."},
        {"id": "cam_3_7", "text": "The newly introduced regulations proved to be a ______ in disguise for small farmers.", "options": ["blessing", "miracle", "triumph", "fortune"], "correctOptionIndex": 0, "explanation": "\"a blessing in disguise\": ilk bakışta kötü görünen ama hayırlı çıkan şey."},
        {"id": "cam_3_8", "text": "The team worked around the ______ to finalize the aerospace prototype before testing.", "options": ["clock", "hour", "watch", "sun"], "correctOptionIndex": 0, "explanation": "\"work around the clock\": gece gündüz aralıksız çalışmak."}
    ]
)

# --- 5. SAT Exams ---
add_exam(
    "exam-sat-reading-1",
    "Digital SAT: Reading & Writing Analytical Mastery",
    20, "SAT", "C1", "Hard",
    [
        {"id": "sat_1_1", "text": "The poet's correspondence reveals an acute ______ toward the literary establishment of his era.", "options": ["ambivalence", "alacrity", "acumen", "affluence"], "correctOptionIndex": 0, "explanation": "\"ambivalence\": karmaşık, çelişkili duygular içinde olma."},
        {"id": "sat_1_2", "text": "Far from being a mere ______ anomaly, the observed star cluster obeys standard relativistic mechanics.", "options": ["capricious", "consistent", "coherent", "systematic"], "correctOptionIndex": 0, "explanation": "\"capricious\": rastlantısal, öngörülemeyen."},
        {"id": "sat_1_3", "text": "The author maintains an aloof and ______ tone throughout the critique of modern architecture.", "options": ["dispassionate", "ebullient", "effusive", "impassioned"], "correctOptionIndex": 0, "explanation": "\"dispassionate\": tarafsız, duygulardan arınmış, soğukkanlı."},
        {"id": "sat_1_4", "text": "Critics praised the novelist for her ability to delineate ______ psychological motivations.", "options": ["subtle", "strident", "blatant", "flagrant"], "correctOptionIndex": 0, "explanation": "\"subtle\": ince, göze çarpmayan, derinlikli."},
        {"id": "sat_1_5", "text": "The experimental design was flawed because the researchers failed to ______ for ambient temperature changes.", "options": ["account", "attribute", "allocate", "advocate"], "correctOptionIndex": 0, "explanation": "\"account for\": hesaba katmak, açıklamak."},
        {"id": "sat_1_6", "text": "The historian argues that the emperor's reputation for ruthlessness was an invented ______.", "options": ["fabrication", "chronology", "precedent", "manifesto"], "correctOptionIndex": 0, "explanation": "\"fabrication\": uydurma, fabrikasyon yalan."},
        {"id": "sat_1_7", "text": "Early microscopic observations were often clouded by optical aberrations that created ______ artifacts.", "options": ["illusory", "immutable", "infallible", "irrevocable"], "correctOptionIndex": 0, "explanation": "\"illusory\": yanıltıcı, gerçekte var olmayan."},
        {"id": "sat_1_8", "text": "Rather than embracing innovation, the academy took an overtly ______ stance toward modernism.", "options": ["reactionary", "revolutionary", "receptive", "radical"], "correctOptionIndex": 0, "explanation": "\"reactionary\": tutucu, yenilik karşıtı, gerici."}
    ]
)

add_exam(
    "exam-sat-conventions",
    "Digital SAT: Standard English Conventions & Grammar",
    18, "SAT", "B2", "Medium",
    [
        {"id": "sat_2_1", "text": "Neither the lead geneticist nor her lab assistants ______ surprised by the PCR results.", "options": ["were", "was", "is", "has been"], "correctOptionIndex": 0, "explanation": "\"Neither... nor\" yapısında fiil, kendisine en yakın özneye uyar ('assistants were')."},
        {"id": "sat_2_2", "text": "Having completed the deep-sea core drilling, ______ carefully labeled each sediment cylinder.", "options": ["the researchers", "the ship's engines", "the laboratory table", "the drill's diamond bit"], "correctOptionIndex": 0, "explanation": "Dangling modifier kuralı: Cümle başında eylemi yapan özne 'the researchers' gelmelidir."},
        {"id": "sat_2_3", "text": "The council debated two distinct proposals; ______ favoured by the minority coalition.", "options": ["the latter of which was", "which the latter was", "and the latter was being", "the latter being"], "correctOptionIndex": 0, "explanation": "Bağımsız cümleyi noktalı virgülden sonra doğru bağlayan yapı 'the latter of which was'dır."},
        {"id": "sat_2_4", "text": "The migration patterns of monarch butterflies are remarkably similar to ______ of dragonflies.", "options": ["those", "that", "this", "them"], "correctOptionIndex": 0, "explanation": "Çoğul tamlamanın ('patterns') karşılaştırılmasında 'those of' kullanılır."},
        {"id": "sat_2_5", "text": "The discovery of Kepler-452b, an exoplanet orbiting a sun-like star, ______ immense public interest.", "options": ["elicited", "elicit", "eliciting", "have elicited"], "correctOptionIndex": 0, "explanation": "Cümlenin öznesi tekil 'The discovery' olduğundan tekil fiil 'elicited' gelmelidir."},
        {"id": "sat_2_6", "text": "Urban heat islands occur when cities replace natural land cover with dense ______ of pavement.", "options": ["concentrations", "concentrate", "concentrated", "concentrating"], "correctOptionIndex": 0, "explanation": "Sıfat tamlamasında isim ('dense concentrations of pavement') kullanılır."},
        {"id": "sat_2_7", "text": "A student wants to introduce a contrast: 'Solar panels generate clean power during daylight hours; ______, nighttime output drops to zero.'", "options": ["however", "furthermore", "consequently", "similarly"], "correctOptionIndex": 0, "explanation": "Gündüz ve gece arasındaki zıtlığı belirtmek için 'however' bağlacı gerekir."},
        {"id": "sat_2_8", "text": "The artist, renowned for large-scale outdoor sculptures, ______ commissioned to build a memorial.", "options": ["was", "were", "are", "have been"], "correctOptionIndex": 0, "explanation": "Özne tekildir ('The artist was commissioned')."}
    ]
)

add_exam(
    "exam-sat-craft-structure",
    "Digital SAT: Craft, Structure & Rhetorical Synthesis",
    20, "SAT", "C1", "Hard",
    [
        {"id": "sat_3_1", "text": "Which choice best describes the function of the underlined sentence in the author's argument?", "options": ["It anticipates an objection and provides empirical refutation", "It concedes that the opponent's premise is entirely accurate", "It digresses into an unrelated anecdotal background", "It introduces an emotional plea to elicit reader sympathy"], "correctOptionIndex": 0, "explanation": "Yazar olası bir itirazı öngörüp ona bilimsel kanıtla yanıt vermiştir."},
        {"id": "sat_3_2", "text": "Based on the text, what can be reasonably inferred about the protagonist's motivation?", "options": ["She is driven by intellectual curiosity rather than financial gain", "She seeks revenge against former university colleagues", "She hopes to abandon scientific research to enter politics", "She desires to conceal her discoveries from the public"], "correctOptionIndex": 0, "explanation": "Metinde karakterin motivasyonu merak ve bilgi tutkusu olarak aktarılmıştır."},
        {"id": "sat_3_3", "text": "As used in line 14, the word 'plastic' most nearly means ______.", "options": ["malleable", "artificial", "synthetic", "rigid"], "correctOptionIndex": 0, "explanation": "'Plastic' biyoloji ve nörolojide 'şekillendirilebilir/değişebilir' (malleable) manasında kullanılır."},
        {"id": "sat_3_4", "text": "The comparison between the human brain and the steam engine serves primarily to ______.", "options": ["illustrate how cultural technological paradigms shape science", "prove that biological systems consume literal coal", "demonstrate that the Industrial Revolution was flawed", "argue that modern computers cannot replicate thought"], "correctOptionIndex": 0, "explanation": "Teknolojinin bilimsel düşünüş kalıplarını nasıl etkilediğini somutlaştırmak için kullanılmıştır."},
        {"id": "sat_3_5", "text": "Which quotation from the passage best supports the answer to the previous question?", "options": ["'Scientists habitually map physiological functions onto their era's dominant machines.'", "'The steam locomotive revolutionized transit across the continent.'", "'Steam pressures must be regulated by mechanical safety valves.'", "'No engineer could foresee the rise of the digital microchip.'"], "correctOptionIndex": 0, "explanation": "Bu alıntı doğrudan paradigmaların benzetildiğini kanıtlamaktadır."},
        {"id": "sat_3_6", "text": "The author's stance toward the proposed high-speed rail corridor can best be described as ______.", "options": ["guardedly optimistic", "scathingly dismissive", "wholly indifferent", "fanatically supportive"], "correctOptionIndex": 0, "explanation": "Yazar projeyi desteklemekte ancak maliyetler konusunda temkinlidir (guardedly optimistic)."},
        {"id": "sat_3_7", "text": "A student wants to synthesize data from two notes: 'Note 1: Species A feeds exclusively on aphids. Note 2: Species B preys on both aphids and spider mites.' Which choice accomplishes this?", "options": ["While Species A is a specialized predator of aphids, Species B has a broader diet that includes spider mites.", "Both Species A and Species B avoid consuming aphids.", "Spider mites are the sole sustenance of Species A.", "Species B has a narrower dietary range than Species A."], "correctOptionIndex": 0, "explanation": "İki notu en doğru ve eksiksiz birleştiren sentez cümlesidir."},
        {"id": "sat_3_8", "text": "The primary rhetorical purpose of the concluding paragraph is to ______.", "options": ["call for broader public participation in conservation initiatives", "reiterate the financial shortcomings of the local council", "criticize earlier historical preservationists for negligence", "introduce an entirely new species classification scheme"], "correctOptionIndex": 0, "explanation": "Son paragraf okuyucuyu çevre koruma hareketine katılmaya davet eden bir eylem çağrısıdır."}
    ]
)

# --- 6. GRE Exams ---
add_exam(
    "exam-gre-verbal-1",
    "GRE Verbal: Text Completion & Sentence Equivalence",
    22, "GRE", "C2", "Hard",
    [
        {"id": "gre_1_1", "text": "The senator's impassioned rhetoric was dismissed as mere ______, designed to inflame rather than inform.", "options": ["demagoguery", "decorum", "deference", "diffidence"], "correctOptionIndex": 0, "explanation": "\"demagoguery\": demagoji, halkı galeyana getiren boş nutuk."},
        {"id": "gre_1_2", "text": "Far from being ______, the author's philosophical prose is laden with esoteric jargon and labyrinthine syntax.", "options": ["lucid", "abstruse", "pedantic", "byzantine"], "correctOptionIndex": 0, "explanation": "\"lucid\": açık, net, berrak anlaşılır."},
        {"id": "gre_1_3", "text": "Her naturally ______ disposition made her well-suited for high-stakes diplomatic conciliation.", "options": ["phlegmatic", "mercurial", "truculent", "choleric"], "correctOptionIndex": 0, "explanation": "\"phlegmatic\": soğukkanlı, sakin, öfkesine hakim."},
        {"id": "gre_1_4", "text": "The curator sought to ______ the collection with rare manuscripts acquired from private estates.", "options": ["augment", "attenuate", "belie", "abrogate"], "correctOptionIndex": 0, "explanation": "\"augment\": artırmak, zenginleştirmek, büyütmek."},
        {"id": "gre_1_5", "text": "The committee viewed the accountant's unexplained absences with deep ______.", "options": ["misgiving", "alacrity", "panache", "jubilation"], "correctOptionIndex": 0, "explanation": "\"misgiving\": endişe, şüphe, güvensizlik."},
        {"id": "gre_1_6", "text": "The theoretical physicist was famous for dismantling accepted tenets with cheerful ______.", "options": ["iconoclasm", "reverence", "servility", "dogmatism"], "correctOptionIndex": 0, "explanation": "\"iconoclasm\": tabu yıkıcılık, gelenek karşıtlığı."},
        {"id": "gre_1_7", "text": "The government's refusal to compromise only served to ______ public resentment across the provinces.", "options": ["exacerbate", "mitigate", "allay", "palliate"], "correctOptionIndex": 0, "explanation": "\"exacerbate\": daha da kötüleştirmek/şiddetlendirmek."},
        {"id": "gre_1_8", "text": "His argument relied on an utterly ______ premise that collapsed under rudimentary scrutiny.", "options": ["specious", "impregnable", "sagacious", "veracious"], "correctOptionIndex": 0, "explanation": "\"specious\": ilk bakışta doğru görünen ama aldatıcı/asılsız."}
    ]
)

add_exam(
    "exam-gre-verbal-2",
    "GRE Verbal: Advanced Lexicon & Logic",
    22, "GRE", "C2", "Hard",
    [
        {"id": "gre_2_1", "text": "The critic's review was notoriously ______; no flaw escaped his biting, caustic pen.", "options": ["vitriolic", "benign", "effusive", "complimentary"], "correctOptionIndex": 0, "explanation": "\"vitriolic\": zehir zemberek, son derece acımasız ve iğneleyici."},
        {"id": "gre_2_2", "text": "The candidate tried to ______ his opponent's credibility through a series of unfounded smears.", "options": ["denigrate", "laud", "venerate", "lionize"], "correctOptionIndex": 0, "explanation": "\"denigrate\": karalamak, itibarını sarsmak."},
        {"id": "gre_2_3", "text": "The monastic community was renowned for its ______ lifestyle, shunning all modern luxuries.", "options": ["ascetic", "hedonistic", "sybaritic", "voluptuous"], "correctOptionIndex": 0, "explanation": "\"ascetic\": çileci, zahitçe, sade."},
        {"id": "gre_2_4", "text": "The CEO gave an ______ response, refusing to commit either to job cuts or to wage increases.", "options": ["equivocal", "unequivocal", "forthright", "categorical"], "correctOptionIndex": 0, "explanation": "\"equivocal\": yuvarlak, kaçamak, çift taraflı."},
        {"id": "gre_2_5", "text": "The student tackled the dense philosophical treatise with surprising ______ and enthusiasm.", "options": ["alacrity", "torpor", "lassitude", "apathy"], "correctOptionIndex": 0, "explanation": "\"alacrity\": canlılık, heves, canla başla yaklaşma."},
        {"id": "gre_2_6", "text": "The treaty was intended to ______ the two warring border clans after decades of bloodshed.", "options": ["conciliate", "antagonize", "alienate", "inflame"], "correctOptionIndex": 0, "explanation": "\"conciliate\": barıştırmak, uzlaştırmak."},
        {"id": "gre_2_7", "text": "Astronomers discovered that the strange optical flare was an ______ aberration caused by cosmic dust.", "options": ["ephemeral", "immutable", "everlasting", "perpetual"], "correctOptionIndex": 0, "explanation": "\"ephemeral\": kısa ömürlü, gelip geçici."},
        {"id": "gre_2_8", "text": "His outward display of wealth was an attempt to ______ his mounting private debts.", "options": ["dissemble", "broadcast", "proclaim", "delineate"], "correctOptionIndex": 0, "explanation": "\"dissemble\": gizlemek, niyetini veya durumunu saklamak."}
    ]
)

add_exam(
    "exam-gre-reading-comp",
    "GRE Reading: Complex Argumentation & Passage Analysis",
    25, "GRE", "C2", "Hard",
    [
        {"id": "gre_3_1", "text": "The primary purpose of the passage is to ______.", "options": ["re-evaluate the merits of a marginalized historiographical tradition", "dismiss modern quantitative methods in favor of intuitive conjecture", "celebrate the political accomplishments of a 19th-century diplomat", "outline the biographical timeline of an influential playwright"], "correctOptionIndex": 0, "explanation": "Metin göz ardı edilmiş bir tarihsel ekolün değerini yeniden tahlil etmektedir."},
        {"id": "gre_3_2", "text": "The author implies which of the following concerning the prevailing economic orthodoxy?", "options": ["It overlooks structural informal labour markets in its aggregate projections", "It accurately predicts every macroeconomic recession with precision", "It relies too heavily on archaic Marxist terminology", "It was formulated exclusively by colonial trading companies"], "correctOptionIndex": 0, "explanation": "Yazar yaygın ekonomik dogmanın kayıt dışı istihdamı hesaba katmadığını vurgular."},
        {"id": "gre_3_3", "text": "In the context of the author's argument, the word 'untenable' most nearly means ______.", "options": ["indefensible", "inviolable", "unbreakable", "unapproachable"], "correctOptionIndex": 0, "explanation": "\"untenable\": savunulamaz, mantıksal olarak ayakta duramaz."},
        {"id": "gre_3_4", "text": "Which of the following, if true, would most severely weaken the author's primary thesis?", "options": ["Newly unearthed archival records verify that grain yields rose throughout the drought", "Contemporary accounts describe mild weather during the monarch's coronation", "Peasants frequently migrated between feudal baronies in search of wages", "Agricultural tax collections were managed by ecclesiastical bishops"], "correctOptionIndex": 0, "explanation": "Kuraklık sırasında buğday hasadının arttığını gösteren delil, kıtlık hipotezini çürütür."},
        {"id": "gre_3_5", "text": "The author mentions the fossilized pollen deposits primarily in order to ______.", "options": ["corroborate paleoclimatic fluctuations described in oral narratives", "illustrate the taxonomic diversity of Jurassic ferns", "argue that ancient agrarian societies worshipped floral symbols", "disprove that volcanic ash clouds affect solar radiation"], "correctOptionIndex": 0, "explanation": "Polen kayıtları sözlü anlatılardaki iklim değişikliklerini doğrulamak amacıyla zikredilmiştir."},
        {"id": "gre_3_6", "text": "The tone of the passage toward early biographers can best be characterized as ______.", "options": ["judiciously critical", "unreservedly laudatory", "scathingly hostile", "condescendingly mocking"], "correctOptionIndex": 0, "explanation": "Yazar erken dönem biyografi yazarlarına karşı adil ve ölçülü bir eleştirel tutum takınmaktadır."},
        {"id": "gre_3_7", "text": "It can be inferred from the passage that the transition to sedentary farming was ______.", "options": ["prolonged and regionally idiosyncratic rather than uniform", "abrupt and completed simultaneously across all continents", "resisted by all prehistoric human settlements without exception", "initiated exclusively by religious priestly castes in river deltas"], "correctOptionIndex": 0, "explanation": "Metin tarıma geçişin tekdüze değil, bölgesel olarak farklılaşan uzun bir süreç olduğunu belirtir."},
        {"id": "gre_3_8", "text": "According to the final paragraph, future historical inquiry must ______.", "options": ["synthesize forensic geochemical analysis with literary archives", "abandon archaeological digs due to excessive conservation costs", "rely exclusively on AI-generated linguistic reconstructions", "restrict investigative scope to royal court correspondence"], "correctOptionIndex": 0, "explanation": "Sonuç bölümü jeokimyasal analizlerle edebi arşivlerin sentezlenmesi gerektiğini savunur."}
    ]
)

# --- 7. PTE Academic Exams ---
add_exam(
    "exam-pte-fill-blanks",
    "PTE Academic: Reading & Writing Fill in the Blanks",
    18, "PTE", "B2", "Medium",
    [
        {"id": "pte_1_1", "text": "Urban green corridors provide a vital link ______ fragmented wildlife habitats in growing cities.", "options": ["between", "among", "against", "towards"], "correctOptionIndex": 0, "explanation": "İki veya belirli parçalar arasında köprü oluşturmada 'link between' kullanılır."},
        {"id": "pte_1_2", "text": "The research team conducted extensive field experiments to ______ their initial theoretical model.", "options": ["validate", "violate", "vitiate", "vacate"], "correctOptionIndex": 0, "explanation": "\"validate\": doğrulamak/geçerliliğini kanıtlamak."},
        {"id": "pte_1_3", "text": "Deforestation in tropical basins has altered local hydrological patterns ______ unprecedented scale.", "options": ["on an", "at a", "in an", "with a"], "correctOptionIndex": 0, "explanation": "\"on an unprecedented scale\": eşi benzeri görülmemiş bir ölçekte (sabit collocation)."},
        {"id": "pte_1_4", "text": "The newly discovered compound exhibits antibacterial properties that are ______ to those of penicillin.", "options": ["comparable", "compassionate", "compatible", "complacent"], "correctOptionIndex": 0, "explanation": "\"comparable to\": ile kıyaslanabilir/benzer seviyede."},
        {"id": "pte_1_5", "text": "Researchers must adhere strictly ______ established ethical protocols when conducting animal testing.", "options": ["to", "with", "for", "at"], "correctOptionIndex": 0, "explanation": "\"adhere to\": kurallara sadık kalmak/bağlı kalmak."},
        {"id": "pte_1_6", "text": "Global renewable investments have seen a ______ increase over the past five fiscal quarters.", "options": ["substantial", "subservient", "substandard", "subversive"], "correctOptionIndex": 0, "explanation": "\"substantial\": kayda değer, hatırı sayılır büyüklükte."},
        {"id": "pte_1_7", "text": "The conference will bring together leading scholars to ______ the impact of artificial intelligence.", "options": ["deliberate", "devastate", "delineate", "desecrate"], "correctOptionIndex": 0, "explanation": "\"deliberate\": etraflıca tartışmak/müzakere etmek."},
        {"id": "pte_1_8", "text": "The manuscript was deemed too fragile to be put on display without a protective ______.", "options": ["casing", "curtain", "shroud", "canopy"], "correctOptionIndex": 0, "explanation": "\"protective casing\": koruyucu muhafaza kabı/vitrini."}
    ]
)

add_exam(
    "exam-pte-reorder-paragraphs",
    "PTE Academic: Re-order Paragraphs & Cohesion",
    20, "PTE", "C1", "Hard",
    [
        {"id": "pte_2_1", "text": "Which sentence logically serves as the introductory topic sentence of an academic paragraph?", "options": ["Throughout history, rivers have played a foundational role in shaping human civilizations.", "These geographical advantages, however, were not without severe seasonal flood hazards.", "Consequently, early Mesopotamian farmers constructed dykes to regulate river waters.", "This hydrological innovation allowed urban settlements to flourish exponentially."], "correctOptionIndex": 0, "explanation": "Genel ve bağımsız bir giriş cümlesi ('Throughout history, rivers...') doğru başlangıçtır."},
        {"id": "pte_2_2", "text": "Identify the logical referent connecting sentence 2 ('These findings...') to its predecessor.", "options": ["A sentence reporting the specific metrics and experimental outcomes of the trial", "A sentence discussing ancient Roman architectural aesthetics", "A rhetorical question questioning the price of laboratory glassware", "A concluding remark about the author's retirement plans"], "correctOptionIndex": 0, "explanation": "'These findings' ifadesi önceki cümlede sunulan deney sonuçlarına işaret eder."},
        {"id": "pte_2_3", "text": "What is the primary role of the transition word 'Nonetheless' in academic argumentation?", "options": ["To introduce a contrasting concession while maintaining the central thesis", "To conclude an essay with a formal apology to critics", "To list items in strict alphabetical order", "To introduce a literal quotation from a dictionary"], "correctOptionIndex": 0, "explanation": "'Nonetheless' (yine de / buna rağmen) zıt bir kabulle ana fikri sürdürür."},
        {"id": "pte_2_4", "text": "Choose the most cohesive concluding sentence for a text describing solar energy efficiency.", "options": ["Continued advancements in perovskite photovoltaic cells promise to unlock even greater yields.", "Coal mining was first commercialized during the British Industrial Revolution.", "Many consumers prefer purchasing electric vehicles in blue or silver paint.", "Wind turbines produce noise that disturbs local dairy cattle."], "correctOptionIndex": 0, "explanation": "Metnin konusunu (güneş enerjisi verimliliği) geleceğe dönük bağlayan tek cümledir."},
        {"id": "pte_2_5", "text": "The pronoun 'This phenomenon' in paragraph three most logically refers to ______.", "options": ["The rapid cooling of the upper stratosphere discussed in paragraph two", "The price of petrol at suburban service stations", "The author's early childhood hobbies", "The architectural style of the United Nations headquarters"], "correctOptionIndex": 0, "explanation": "Paragraf ikide tartışılan stratosferik soğuma olgusuna gönderme yapmaktadır."},
        {"id": "pte_2_6", "text": "In academic writing, which signal word indicates a direct cause-and-effect relationship?", "options": ["Therefore", "Admittedly", "Conversely", "Incidentally"], "correctOptionIndex": 0, "explanation": "\"Therefore\": bu yüzden/dolayısıyla (sebep-sonuç bağlacı)."},
        {"id": "pte_2_7", "text": "Select the correct ordering: [A] The satellite was launched. [B] Pre-flight checks were conducted. [C] Orbit was achieved.", "options": ["B -> A -> C", "A -> C -> B", "C -> B -> A", "A -> B -> C"], "correctOptionIndex": 0, "explanation": "Kronolojik sıra: Uçuş öncesi kontroller -> Fırlatma -> Yörüngeye oturma."},
        {"id": "pte_2_8", "text": "Which choice best illustrates anaphoric reference?", "options": ["'Dr. Alvarez published her treatise in 1980. She argued that an asteroid impacted Earth.'", "'The weather was rainy yesterday morning.'", "'Calculus is a branch of mathematics.'", "'Look at the sky above us.'"], "correctOptionIndex": 0, "explanation": "'She' zamiri kendisinden önce geçen 'Dr. Alvarez'e anaphoric referans yapar."}
    ]
)

add_exam(
    "exam-pte-summarize-written",
    "PTE Academic: Summarize Written Text & Synthesis",
    20, "PTE", "C1", "Hard",
    [
        {"id": "pte_3_1", "text": "Which single compound-complex sentence best summarizes the passage on urban heat islands?", "options": ["Although urbanization causes significant thermal absorption through asphalt and concrete, metropolitan areas can mitigate these heat islands by expanding canopy cover and reflective roofs.", "Cities are hot because of cars and buildings.", "Trees are very beautiful and everyone enjoys walking through green urban parks on warm summer days.", "Air conditioning units consume electricity which raises utility bills for apartment dwellers."], "correctOptionIndex": 0, "explanation": "PTE 'Summarize Written Text' kriterlerine göre tek cümlede sebep, etki ve çözümü sentezler."},
        {"id": "pte_3_2", "text": "What is the key grammatical constraint of PTE 'Summarize Written Text'?", "options": ["The summary must be written in exactly ONE single complete sentence", "The summary must be between five and eight separate bullet points", "The summary must never exceed three words in total length", "The summary must contain at least five exclamation marks"], "correctOptionIndex": 0, "explanation": "PTE kuralı: Özet tam olarak tek bir bitmiş cümle olmalıdır."},
        {"id": "pte_3_3", "text": "Which clause structure is most effective for compressing complex research conclusions?", "options": ["A relative or participial subordinate clause linked with cohesive coordinators", "A string of four short sentence fragments with semicolons", "An informal conversational question directed at the grader", "A quoted excerpt copied word for word from the first line"], "correctOptionIndex": 0, "explanation": "Bileşik ve yan cümlecikli yapı akademik yoğunluğu ve dilbilgisini en iyi yansıtır."},
        {"id": "pte_3_4", "text": "In summarizing, what should a test-taker strictly avoid?", "options": ["Injecting personal opinions or outside facts not present in the original text", "Using correct academic punctuation like commas and semicolons", "Paraphrasing complex vocabulary with precise synonyms", "Identifying the primary thesis and secondary supporting evidence"], "correctOptionIndex": 0, "explanation": "Metinde olmayan dış bilgileri veya kişisel yorumları katmak puan kaybına yol açar."},
        {"id": "pte_3_5", "text": "The passage discusses deep-sea hydrothermal vents. Which key idea must be included?", "options": ["Chemosynthetic microorganisms sustain complex ecosystems independent of solar energy", "Submarines are expensive to construct in modern shipyards", "Scuba divers often encounter dolphins in shallow waters", "Seawater contains approximately three percent sodium chloride"], "correctOptionIndex": 0, "explanation": "Hidrotermal bacaların temel bilimsel önemi Güneş'e bağlı olmayan kemosentezdir."},
        {"id": "pte_3_6", "text": "Select the correct connector to synthesize two complementary points: 'clean emissions' and 'low operating costs'.", "options": ["not only... but also", "either... or", "neither... nor", "whereas"], "correctOptionIndex": 0, "explanation": "\"not only... but also\": sadece ... değil aynı zamanda ... (pekiştirici sentez)."},
        {"id": "pte_3_7", "text": "Identify the punctuation error in: 'The policy was enacted, however the opposition protested.'", "options": ["A comma splice: 'however' needs a semicolon before it and comma after", "The word 'enacted' should be spelled with a 'k'", "The word 'the' must be capitalized", "There should be an exclamation mark after 'policy'"], "correctOptionIndex": 0, "explanation": "İki bağımsız cümle 'however' ile bağlanırken noktalı virgül ve virgül gerekir (; however,)."},
        {"id": "pte_3_8", "text": "What is the ideal word count range for the single-sentence PTE summary?", "options": ["35 to 45 words", "5 to 10 words", "120 to 150 words", "Exactly 500 words"], "correctOptionIndex": 0, "explanation": "35-45 kelime arası, tek cümlelik gramer doğruluğu ve anlam bütünlüğü için idealdir."}
    ]
)

# --- 8. DET Exams ---
add_exam(
    "exam-det-comprehension",
    "Duolingo English Test: Interactive Reading & Context",
    18, "DET", "B2", "Medium",
    [
        {"id": "det_1_1", "text": "Select the real English words from the list below:", "options": ["Accomplish, Benevolent, Decisive", "Blintastic, Craptitious, Fronkable", "Gromplish, Snazzle, Wompeteer", "Plorification, Sprenk, Quarnicle"], "correctOptionIndex": 0, "explanation": "Sadece ilk seçenekteki kelimeler (Accomplish, Benevolent, Decisive) gerçek İngilizce sözcüklerdir."},
        {"id": "det_1_2", "text": "The new transit system is designed to be fully ______ for citizens with limited mobility.", "options": ["accessible", "accessory", "accidental", "accommodating"], "correctOptionIndex": 0, "explanation": "\"accessible\": erişilebilir."},
        {"id": "det_1_3", "text": "Her dedication to public service has earned her the ______ of colleagues across the country.", "options": ["respect", "resentment", "repulsion", "reluctance"], "correctOptionIndex": 0, "explanation": "\"respect\": saygı ve takdir."},
        {"id": "det_1_4", "text": "Scientists were surprised by how quickly the seedlings managed to ______ to the dry soil.", "options": ["adapt", "adopt", "adept", "admit"], "correctOptionIndex": 0, "explanation": "\"adapt to\": -e uyum sağlamak."},
        {"id": "det_1_5", "text": "The university library provides quiet study areas where students can ______ on their work.", "options": ["concentrate", "congregate", "congratulate", "consecrate"], "correctOptionIndex": 0, "explanation": "\"concentrate on\": -e odaklanmak/yoğunlaşmak."},
        {"id": "det_1_6", "text": "Regular exercise and balanced nutrition are ______ components of a healthy lifestyle.", "options": ["essential", "erratic", "eccentric", "evasive"], "correctOptionIndex": 0, "explanation": "\"essential\": vazgeçilmez, temel."},
        {"id": "det_1_7", "text": "The documentary film aims to ______ the struggles faced by endangered polar wildlife.", "options": ["highlight", "hinder", "hijack", "hobble"], "correctOptionIndex": 0, "explanation": "\"highlight\": dikkat çekmek, öne çıkarmak."},
        {"id": "det_1_8", "text": "She spoke with great ______ during the live television interview on youth education.", "options": ["confidence", "confidentiality", "confinement", "conformity"], "correctOptionIndex": 0, "explanation": "\"with great confidence\": büyük bir özgüvenle."}
    ]
)

add_exam(
    "exam-det-c-test-fill",
    "Duolingo English Test: Complete the Missing Words (C-Test)",
    18, "DET", "B2", "Medium",
    [
        {"id": "det_2_1", "text": "Complete the word: 'Photosynthesis is the process by which green pl____s convert sunlight into chemical energy.'", "options": ["plants", "plates", "places", "planets"], "correctOptionIndex": 0, "explanation": "Cümle anlamına göre 'plants' (bitkiler) kelimesi tamamlanmalıdır."},
        {"id": "det_2_2", "text": "Complete the word: 'Scientists must col____t empirical data before drawing any firm conclusions.'", "options": ["collect", "collide", "collapse", "collate"], "correctOptionIndex": 0, "explanation": "\"collect data\": veri toplamak."},
        {"id": "det_2_3", "text": "Complete the word: 'Linguistic research sh____s that learning a foreign language enhances memory.'", "options": ["shows", "shuts", "sheds", "shapes"], "correctOptionIndex": 0, "explanation": "\"shows\": gösteriyor/ortaya koyuyor."},
        {"id": "det_2_4", "text": "Complete the word: 'Renewable sources of en____y include wind, solar, and geothermal power.'", "options": ["energy", "entity", "enmity", "engine"], "correctOptionIndex": 0, "explanation": "\"sources of energy\": enerji kaynakları."},
        {"id": "det_2_5", "text": "Complete the word: 'The museum has an ext____ve collection of ancient Egyptian pottery.'", "options": ["extensive", "expensive", "exclusive", "explosive"], "correctOptionIndex": 0, "explanation": "\"extensive collection\": geniş/kapsamlı koleksiyon."},
        {"id": "det_2_6", "text": "Complete the word: 'Students are encouraged to part______te actively in seminar discussions.'", "options": ["participate", "particular", "particulate", "partisan"], "correctOptionIndex": 0, "explanation": "\"participate in\": -e katılmak/iştirak etmek."},
        {"id": "det_2_7", "text": "Complete the word: 'Urban planners are trying to red____e traffic congestion during rush hours.'", "options": ["reduce", "redact", "redund", "reduce"], "correctOptionIndex": 0, "explanation": "\"reduce traffic\": trafiği azaltmak."},
        {"id": "det_2_8", "text": "Complete the word: 'Regular physical activity is ben______al for both physical and mental health.'", "options": ["beneficial", "beneficent", "benevolent", "benign"], "correctOptionIndex": 0, "explanation": "\"beneficial for\": için yararlı/faydalı."}
    ]
)

add_exam(
    "exam-det-writing-fluency",
    "Duolingo English Test: Writing Fluency & Topic Development",
    20, "DET", "C1", "Hard",
    [
        {"id": "det_3_1", "text": "Which thesis statement best addresses: 'Do the benefits of artificial intelligence outweigh its risks?'", "options": ["While artificial intelligence poses legitimate challenges regarding data privacy and job automation, its transformative contributions to healthcare diagnosis and clean energy optimization demonstrate that its benefits outweigh its drawbacks.", "AI is very cool and I like asking chat applications to write poetry for me.", "Robots will probably conquer the universe and destroy everything.", "There are many computers in modern schools across the world."], "correctOptionIndex": 0, "explanation": "İki tarafı da dengeli tartıp net bir tez ortaya koyan akademik yapıdır."},
        {"id": "det_3_2", "text": "What is the most effective way to start a 5-minute timed response on an unfamiliar topic?", "options": ["State your direct position in the opening sentence and introduce two distinct reasons", "Spend four minutes complaining about the difficulty of the prompt", "Write random words to artificially inflate the word counter", "Copy the prompt text repeatedly forty times"], "correctOptionIndex": 0, "explanation": "Süre kısıtlı olduğundan ilk cümlede pozisyon belirtip 2 gerekçe sunmak en yüksek puanı getirir."},
        {"id": "det_3_3", "text": "Select the sentence with the highest lexical sophistication and natural fluency:", "options": ["Technological immersion has fundamentally reshaped contemporary adolescent interpersonal communication.", "Kids talk with phones a lot nowadays and it is different from old times.", "Phone talking is much more than it was before for children.", "People have devices which they use to speak with each other frequently."], "correctOptionIndex": 0, "explanation": "Zengin ve doğal akademik kelime dağarcığını ('immersion, fundamentally reshaped, interpersonal') yansıtır."},
        {"id": "det_3_4", "text": "In DET photo description prompts, which opening strategy yields the highest score?", "options": ["Describe the focal subject, their actions, and contextual background using present continuous verbs", "Just list three objects you see separated by commas", "Write 'I do not know who took this photo'", "Guess what the people in the picture will eat for dinner next week"], "correctOptionIndex": 0, "explanation": "Fotoğraf tasvirinde ana özne, eylem ve arka plan present continuous ile betimlenmelidir."},
        {"id": "det_3_5", "text": "Which phrase smoothly introduces a counter-argument?", "options": ["Opponents frequently contend that...", "I think people are wrong because...", "Another bad thing is...", "Maybe it is not true but..."], "correctOptionIndex": 0, "explanation": "\"Opponents frequently contend that\": Karşı tezi akademik bir dille tanıtır."},
        {"id": "det_3_6", "text": "Select the correct connector to show cause: 'Due to severe weather, the flight was grounded.' What can replace 'Due to'?", "options": ["Owing to", "Regardless of", "In spite of", "Notwithstanding"], "correctOptionIndex": 0, "explanation": "\"Owing to\": -den dolayı (sebep bildiren tam eşanlamlı)."},
        {"id": "det_3_7", "text": "Identify the grammatically flawless sentence:", "options": ["Had she known about the seminar earlier, she would certainly have registered.", "If she knew about the seminar earlier, she would have register.", "Had she knew about the seminar earlier, she will register.", "She had known about the seminar earlier, she would register."], "correctOptionIndex": 0, "explanation": "Devrik Type 3 koşul cümlesi: 'Had she known..., she would have registered'."},
        {"id": "det_3_8", "text": "What is the primary evaluation metric for the DET writing section?", "options": ["Grammatical accuracy, lexical diversity, task relevance, and fluency length", "Typing speed alone regardless of spelling errors", "Whether you agree with the computer grader's personal opinions", "The number of commas you place per sentence"], "correctOptionIndex": 0, "explanation": "DET dilbilgisi doğruluğu, sözcük çeşitliliği, konuya uygunluk ve akıcılık üzerinden puanlar."}
    ]
)

# --- 9. EIKEN Exams (Grade Pre-1 & Grade 1) ---
add_exam(
    "exam-eiken-pre1-vocab",
    "EIKEN Grade Pre-1: High-Level Practical & Academic Lexicon",
    20, "EIKEN", "C1", "Hard",
    [
        {"id": "eiken_1_1", "text": "The international committee voted to ______ emergency humanitarian assistance to the drought-hit area.", "options": ["allocate", "abdicate", "adulterate", "alienate"], "correctOptionIndex": 0, "explanation": "\"allocate\": ödenek/yardım ayırmak veya tahsis etmek."},
        {"id": "eiken_1_2", "text": "The ancient shrine was built on a ______ cliff overlooking the Pacific Ocean.", "options": ["precipitous", "provisional", "precocious", "pragmatic"], "correctOptionIndex": 0, "explanation": "\"precipitous\": sarp, dik, uçurumlu."},
        {"id": "eiken_1_3", "text": "Citizens held a candlelight vigil to ______ the victims of the catastrophic earthquake.", "options": ["commemorate", "commence", "condone", "contemplate"], "correctOptionIndex": 0, "explanation": "\"commemorate\": anmak, hatırasını yaşatmak."},
        {"id": "eiken_1_4", "text": "The government refused to ______ with radical insurgents who held foreign hostages.", "options": ["negotiate", "neglect", "nullify", "nourish"], "correctOptionIndex": 0, "explanation": "\"negotiate with\": -ile müzakere etmek/masaya oturmak."},
        {"id": "eiken_1_5", "text": "The new legislation is expected to ______ greater foreign investment in regional clean energy.", "options": ["foster", "falter", "fracture", "frown"], "correctOptionIndex": 0, "explanation": "\"foster\": geliştirmek, teşvik etmek, beslemek."},
        {"id": "eiken_1_6", "text": "Despite hours of debate, the two political factions reached a complete ______.", "options": ["stalemate", "sanctuary", "solace", "stimulus"], "correctOptionIndex": 0, "explanation": "\"stalemate\": çıkmaz, pat durumu, kilitlenme."},
        {"id": "eiken_1_7", "text": "The volcanic eruption caused ______ disruption to international flight corridors.", "options": ["widespread", "wistful", "whimsical", "wretched"], "correctOptionIndex": 0, "explanation": "\"widespread disruption\": geniş çaplı aksama/kesinti."},
        {"id": "eiken_1_8", "text": "Senior officials are urged to lead by example and demonstrate absolute ______.", "options": ["integrity", "iniquity", "insolence", "indolence"], "correctOptionIndex": 0, "explanation": "\"integrity\": dürüstlük, ahlaki bütünlük."}
    ]
)

add_exam(
    "exam-eiken-grade1-mastery",
    "EIKEN Grade 1: Elite Vocabulary & Geopolitics",
    22, "EIKEN", "C2", "Hard",
    [
        {"id": "eiken_2_1", "text": "The ambassador delivered a scathing ______ condemning territorial aggression.", "options": ["diatribe", "eulogy", "homage", "panegyric"], "correctOptionIndex": 0, "explanation": "\"diatribe\": sert taşlama, yerin dibine batıran eleştirel nutuk."},
        {"id": "eiken_2_2", "text": "The court ruled that the contract was signed under extreme psychological ______.", "options": ["duress", "decorum", "diligence", "deference"], "correctOptionIndex": 0, "explanation": "\"under duress\": baskı ve tehdit altında."},
        {"id": "eiken_2_3", "text": "The prime minister sought to ______ widespread public anger over rising inflation.", "options": ["assuage", "inflame", "aggravate", "instigate"], "correctOptionIndex": 0, "explanation": "\"assuage\": yatıştırmak, teskin etmek, dindirmek."},
        {"id": "eiken_2_4", "text": "She displayed an ______ memory, recalling every treaty article verbatim.", "options": ["eidetic", "ephemeral", "erratic", "eccentric"], "correctOptionIndex": 0, "explanation": "\"eidetic memory\": fotografik/olağanüstü görsel hafıza."},
        {"id": "eiken_2_5", "text": "The military junta ruled with an ______ fist, crushing all democratic opposition.", "options": ["iron", "ivory", "amber", "onyx"], "correctOptionIndex": 0, "explanation": "\"with an iron fist\": demir yumrukla/baskıyla (deyim)."},
        {"id": "eiken_2_6", "text": "His philosophical arguments were marred by circular and ______ reasoning.", "options": ["specious", "impeccable", "scrupulous", "judicious"], "correctOptionIndex": 0, "explanation": "\"specious\": aldatıcı, yanıltıcı, görünüşte doğru."},
        {"id": "eiken_2_7", "text": "The company was accused of using tax havens to ______ its domestic fiscal obligations.", "options": ["circumvent", "corroborate", "consolidate", "champion"], "correctOptionIndex": 0, "explanation": "\"circumvent\": etrafından dolanmak, yasayı bertaraf etmek."},
        {"id": "eiken_2_8", "text": "Scientists warn that the ecosystem has reached an irreversible ______ point.", "options": ["tipping", "dipping", "tripping", "ripping"], "correctOptionIndex": 0, "explanation": "\"tipping point\": kritik eşik/dönüm noktası."}
    ]
)

add_exam(
    "exam-eiken-essay-reading",
    "EIKEN: Societal Issues & Reading Comprehension",
    20, "EIKEN", "C1", "Hard",
    [
        {"id": "eiken_3_1", "text": "What is the primary argument against lowering the mandatory retirement age?", "options": ["It deprives the economy of experienced talent and strains pension funds", "It encourages younger workers to emigrate abroad immediately", "It causes public transportation systems to operate at a deficit", "It prevents university graduates from securing entry-level jobs"], "correctOptionIndex": 0, "explanation": "Metinde emeklilik yaşını düşürmenin tecrübeli iş gücünü kaybettirdiği ve emekli sandıklarına yük getirdiği belirtilmiştir."},
        {"id": "eiken_3_2", "text": "How can municipal governments encourage sustainable recycling among households?", "options": ["By implementing convenient sorted collection points and educational programs", "By doubling the price of fresh vegetables at local markets", "By prohibiting all plastic manufacturing completely within seven days", "By requiring citizens to burn household trash in private yards"], "correctOptionIndex": 0, "explanation": "Belediyelerin eğitim ve kolay erişilebilir ayrıştırma merkezleri kurması sürdürülebilirliği sağlar."},
        {"id": "eiken_3_3", "text": "The author suggests that globalization has contributed to ______ in regional manufacturing.", "options": ["increased competition and off-shoring of low-skilled assembly jobs", "the total elimination of international cargo shipping lines", "a return to pre-industrial handicraft workshops across cities", "a universal standard price for all consumer electronics"], "correctOptionIndex": 0, "explanation": "Küreselleşme düşük vasıflı işlerin yurt dışına kaymasına yol açmıştır."},
        {"id": "eiken_3_4", "text": "In passage 2, the term 'demographic headwinds' refers to ______.", "options": ["an aging population accompanied by a declining fertility rate", "unexpected storms along maritime shipping routes", "unfavorable consumer preferences in the automobile industry", "foreign exchange fluctuations impacting import duties"], "correctOptionIndex": 0, "explanation": "'Demographic headwinds': Nüfus yaşlanması ve doğurganlığın düşmesi sorunu."},
        {"id": "eiken_3_5", "text": "Which measure is recommended to alleviate overcrowding on commuter rail lines?", "options": ["Promoting flexible telecommuting and staggered work hours", "Abolishing all passenger train services during peak morning hours", "Doubling fares exclusively for university students", "Replacing trains with fleets of motorized pedal bicycles"], "correctOptionIndex": 0, "explanation": "Esnek çalışma saatleri ve evden çalışma raylı sistem yoğunluğunu hafifletir."},
        {"id": "eiken_3_6", "text": "Why do some agriculturalists express reservations regarding genetically modified crops?", "options": ["Potential long-term impacts on local biodiversity and farmer patent reliance", "They produce fruit that is too large to fit in shopping carts", "They require three hundred percent more land area to cultivate", "They attract wild bears into suburban shopping plazas"], "correctOptionIndex": 0, "explanation": "Biyoçeşitlilik üzerindeki olası etkiler ve tohum patenti bağımlılığı çekincelerin başında gelir."},
        {"id": "eiken_3_7", "text": "According to the concluding paragraph, renewable energy adoption requires ______.", "options": ["upgrades to electrical storage grids and consistent state support", "the immediate demolition of all existing hydroelectric dams", "converting personal automobiles to run on steam engines", "restricting electricity consumption to daylight hours only"], "correctOptionIndex": 0, "explanation": "Yenilenebilir enerji şebeke depolama altyapısı ve devlet teşviki gerektirir."},
        {"id": "eiken_3_8", "text": "What tone does the essayist maintain regarding the future of international space exploration?", "options": ["Cautiously optimistic about multinational partnerships", "Completely pessimistic and dismissive of all science", "Aggressively jingoistic and hostile to other nations", "Humorous and satirical throughout every section"], "correctOptionIndex": 0, "explanation": "Yazar uluslararası ortaklıklar konusunda temkinli bir iyimserlik içindedir."}
    ]
)

# --- 10. YDS / YÖKDİL Exams (ÖSYM Turkish Context) ---
add_exam(
    "exam-yds-cloze-1",
    "YDS / YÖKDİL: Academic Cloze Test & Connectors",
    20, "YDS", "C1", "Hard",
    [
        {"id": "yds_1_1", "text": "The government launched an ambitious campaign to ______ the spread of infectious waterborne diseases.", "options": ["curb", "instigate", "bolster", "perpetuate"], "correctOptionIndex": 0, "explanation": "\"curb\": dizginlemek, frenlemek, durdurmak."},
        {"id": "yds_1_2", "text": "Extensive reforestation initiatives helped ______ the risk of disastrous spring mudslides.", "options": ["mitigate", "aggravate", "trigger", "foster"], "correctOptionIndex": 0, "explanation": "\"mitigate\": hafifletmek, riskini azaltmak."},
        {"id": "yds_1_3", "text": "______ the harsh macroeconomic headwinds, the automotive enterprise recorded unprecedented export revenues.", "options": ["In spite of", "Owing to", "As well as", "In accordance with"], "correctOptionIndex": 0, "explanation": "\"In spite of\": -e rağmen (zıtlık bildiren zarf edatı)."},
        {"id": "yds_1_4", "text": "The new environmental directive stipulates that factories must ______ carbon emissions by 40% by 2030.", "options": ["curtail", "augment", "subsidize", "prolong"], "correctOptionIndex": 0, "explanation": "\"curtail\": kısmak, sınırlandırmak."},
        {"id": "yds_1_5", "text": "Agricultural yields began to decline precipitously, ______ leading to severe domestic food shortages.", "options": ["thereby", "whereas", "on the contrary", "in contrast"], "correctOptionIndex": 0, "explanation": "\"thereby\": böylece, bu suretle (ardından V-ing gelir)."},
        {"id": "yds_1_6", "text": "Medical researchers are struggling to find an effective vaccine ______ the virus mutates so rapidly.", "options": ["because", "despite", "although", "so that"], "correctOptionIndex": 0, "explanation": "\"because\": çünkü (sebep bildiren bağlaç)."},
        {"id": "yds_1_7", "text": "The economic council advised that investments should be allocated ______ emerging technology startups.", "options": ["to", "from", "with", "against"], "correctOptionIndex": 0, "explanation": "\"allocate to\": -e pay ayırmak/tahsis etmek."},
        {"id": "yds_1_8", "text": "Had the rescue team not arrived in time, the trapped miners ______ from lack of oxygen.", "options": ["would have perished", "will perish", "perished", "had perished"], "correctOptionIndex": 0, "explanation": "Devrik Type 3 koşul cümlesi temel cümlesi: 'would have perished'."}
    ]
)

add_exam(
    "exam-yds-sentence-completion",
    "YDS / YÖKDİL: Sentence Completion & Logical Flow",
    20, "YDS", "C1", "Hard",
    [
        {"id": "yds_2_1", "text": "Although substantial subsidies have been directed toward electric vehicle manufacturing, ______.", "options": ["consumer adoption rates remain constrained by the scarcity of fast-charging stations", "electric vehicles produce zero tailpipe carbon emissions during driving", "many automobile producers have completely stopped manufacturing diesel engines", "battery technologies have improved exponentially over the past five years"], "correctOptionIndex": 0, "explanation": "'Although' zıtlık bildirir: Teşviklere rağmen tüketici adaptasyonu şarj istasyonu eksikliği yüzünden kısıtlı kalmıştır."},
        {"id": "yds_2_2", "text": "______ that many deep-sea organisms produce biological luminescence to attract prey.", "options": ["Marine biologists have long verified", "Because the ocean floor is pitch black", "In order to explore volcanic hydrothermal vents", "Unless submarine sensor arrays function properly"], "correctOptionIndex": 0, "explanation": "'That' noun clause'ını ana cümle olarak tamamlayan 'Marine biologists have long verified'dır."},
        {"id": "yds_2_3", "text": "Unless stringent anti-poaching measures are enforced immediately, ______.", "options": ["the northern white rhinoceros will inevitably face total extinction in the wild", "wildlife rangers have arrested several armed smugglers near the reserve", "safari tourism has generated millions of dollars for local rural schools", "natural ecosystems have evolved over millions of uninterrupted epochs"], "correctOptionIndex": 0, "explanation": "'Unless' (olmadıkça/eğer ... -mezse) olumsuz bir geleceğe bağlar: nesil tükenmesi kaçınılmaz olacaktır."},
        {"id": "yds_2_4", "text": "So pronounced was the economic disparity between the two regions ______.", "options": ["that central authorities were forced to enact special development grants", "because both provinces shared the same linguistic dialect", "although industrial production rose in every agricultural sector", "in order to promote bilateral diplomatic harmony across the border"], "correctOptionIndex": 0, "explanation": "Devrik 'So + sıfat + was... that...' sonuç kalıbıdır."},
        {"id": "yds_2_5", "text": "Owing to the unprecedented drought that scorched the Mesopotamian plains, ______.", "options": ["agrarian city-states suffered catastrophic crop failures and civil unrest", "farmers exported surplus grain to neighboring Mediterranean markets", "river dykes overflowed and destroyed nearby irrigation networks", "agricultural yields reached record high levels throughout the millennium"], "correctOptionIndex": 0, "explanation": "'Owing to' sebep bildirir: Şiddetli kuraklık sebebiyle ekin kıtlığı ve iç huzursuzluk yaşanmıştır."},
        {"id": "yds_2_6", "text": "No sooner had the peace accord been signed in Geneva ______.", "options": ["than fresh border clashes erupted in the disputed border valley", "when delegates celebrated with an international banquet", "that both nations agreed to demilitarize the frontier zone", "before the United Nations passed an emergency resolution"], "correctOptionIndex": 0, "explanation": "\"No sooner had... than...\" (yapar yapmaz / henüz olmuştu ki) sabit korelatif kalıptır."},
        {"id": "yds_2_7", "text": "______ in that it allows geneticists to edit specific nucleotide sequences with unprecedented accuracy.", "options": ["CRISPR technology differs fundamentally from earlier genetic engineering tools", "Many pharmaceutical companies invest billions of dollars in viral testing", "Because cellular enzymes break down foreign nucleic acids rapidly", "Although ethical debates continue to surround human embryo research"], "correctOptionIndex": 0, "explanation": "'in that' (-mesi bakımından) önceki cümlenin özelliğini açıklar."},
        {"id": "yds_2_8", "text": "Just as the invention of the printing press democratized written literacy in Europe, ______.", "options": ["so too has the internet transformed global access to academic knowledge", "because parchment paper was expensive to manufacture in monasteries", "although medieval scribes hand-copied manuscripts for royal patrons", "therefore public libraries charged membership fees for book loans"], "correctOptionIndex": 0, "explanation": "\"Just as... so too...\" (Tıpkı ... gibi, aynı şekilde ... de) benzetme kalıbıdır."}
    ]
)

add_exam(
    "exam-yds-reading-mastery",
    "YDS / YÖKDİL: Academic Reading Comprehension & Restatement",
    25, "YDS", "C1", "Hard",
    [
        {"id": "yds_3_1", "text": "Which option provides the closest restatement: 'Had the government not intervened with emergency subsidies, the regional steel industry would certainly have collapsed.'", "options": ["The regional steel industry was saved from collapse solely due to the government's emergency subsidies.", "Even with government emergency subsidies, the regional steel industry was unable to avoid complete bankruptcy.", "The government intervened with subsidies after the regional steel industry had already collapsed.", "The regional steel industry collapsed despite the substantial emergency funding provided by the state."], "correctOptionIndex": 0, "explanation": "Devrik Type 3 koşul cümlesinin en doğru anlamca eşiti: Hükümet sübvansiyonları sayesinde çöküşten kurtulmuştur."},
        {"id": "yds_3_2", "text": "Which option provides the closest restatement: 'It is impossible to overstate the importance of cardiovascular conditioning in the prevention of chronic disease.'", "options": ["Cardiovascular conditioning is profoundly essential for preventing chronic illness.", "Cardiovascular conditioning is somewhat helpful, but its benefits are often exaggerated.", "Chronic diseases can never be prevented regardless of how much cardiovascular training one does.", "The importance of cardiovascular training is minimal compared to pharmacological therapies."], "correctOptionIndex": 0, "explanation": "'It is impossible to overstate the importance': Ne kadar vurgulansa azdır / hayati derecede önemlidir."},
        {"id": "yds_3_3", "text": "In academic reading, what does the transition 'By the same token' signify?", "options": ["Similarly, and for the same reason", "In sharp and direct opposition", "Regardless of whatever happens next", "At an unspecified point in the historical past"], "correctOptionIndex": 0, "explanation": "\"By the same token\": Aynı şekilde / aynı gerekçeyle ('similarly' eşanlamlısı)."},
        {"id": "yds_3_4", "text": "The passage states that the Renaissance was not a sudden rupture with medieval times, but rather ______.", "options": ["a gradual culmination of cultural and intellectual currents that began centuries earlier", "a violent military conquest that destroyed all earlier literary traditions", "a brief financial speculative bubble centered exclusively in Venice", "an artificial myth created by twentieth-century romantic novelists"], "correctOptionIndex": 0, "explanation": "Metin Rönesans'ın ani bir kopuş değil, kademeli bir kültürel birikim ve doruk noktası olduğunu ifade eder."},
        {"id": "yds_3_5", "text": "According to the author, the primary obstacle to deep-space manned voyages is ______.", "options": ["prolonged exposure to cosmic radiation and microgravity physiological deterioration", "the complete absence of titanium ore on the lunar surface", "a lack of public interest in watching broadcast rocket launches", "the inability of rocket engines to operate in sub-zero atmospheric temperatures"], "correctOptionIndex": 0, "explanation": "Metne göre ana engeller kozmik radyasyon ve yerçekimsiz ortamdaki fizyolojik bozulmadır."},
        {"id": "yds_3_6", "text": "What can be inferred regarding the decipherment of the Rosetta Stone?", "options": ["It required comparing identical decree texts across three distinct writing systems", "It was accomplished in a single afternoon by a solitary French explorer", "It proved that ancient hieroglyphs were purely decorative and carried no phonetic values", "It confirmed that Greek was never spoken in ancient Alexandria"], "correctOptionIndex": 0, "explanation": "Rosetta Taşı üç farklı yazı sistemiyle yazılmış aynı metnin karşılaştırılmasıyla çözülmüştür."},
        {"id": "yds_3_7", "text": "The author's primary objective in writing this article is to ______.", "options": ["dispel popular misconceptions regarding the causes of the Bronze Age Collapse", "encourage tourists to visit newly excavated ruins in the eastern Mediterranean", "accuse contemporary archaeologists of falsifying carbon-14 dating results", "solicit private financial donations for a new university museum wing"], "correctOptionIndex": 0, "explanation": "Yazar Tunç Çağı çöküşü hakkındaki yaygın popüler yanlış inanışları çürütmeyi amaçlamaktadır."},
        {"id": "yds_3_8", "text": "Which title best captures the core essence of the entire passage?", "options": ["The Complex Interplay of Ecological Stress and Geopolitical Shifts in Ancient Societies", "How to Build an Ancient Seagoing Trireme from Lebanese Cedar", "The Daily Life and Culinary Habits of Bronze Age Potters", "Why Modern Earthquakes Are Less Dangerous Than Ancient Tremors"], "correctOptionIndex": 0, "explanation": "Metnin ana fikrini kapsayan en kapsamlı ve akademik başlık ilk seçenektir."}
    ]
)

# Write exams_bank.json
exams_path = os.path.join(OUTPUT_DIR, "exams_bank.json")
with open(exams_path, "w", encoding="utf-8") as f:
    json.dump(exams_data, f, ensure_ascii=False, indent=2)

total_q = sum(len(e["questions"]) for e in exams_data)
print(f"Successfully generated {len(exams_data)} exams ({total_q} questions) in {exams_path}")
