package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.GrammarDao
import com.vocabmaster.app.data.local.GrammarTopicEntity
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealGrammarRepositoryImpl(
    private val grammarDao: GrammarDao
) : GrammarRepository {

    // A YDS-focused grammar topic bank covering the structures most frequently tested.
    private val seedTopics = listOf(
        GrammarTopicEntity(
            id = "1",
            title = "Present Simple vs Present Continuous",
            explanation = "Present Simple alışkanlıkları ve genel doğruları anlatır (I work every day). Present Continuous ise şu an olan bir eylemi anlatır (I am working now).",
            exampleSentence = "She usually walks to work, but today she is driving."
        ),
        GrammarTopicEntity(
            id = "2",
            title = "Conditionals (If Clauses)",
            explanation = "Type 1 gerçek olası durumlar (If it rains, I will stay home), Type 2 hayali/olası olmayan durumlar (If I were rich, I would travel), Type 3 geçmişte gerçekleşmemiş durumlar (If I had studied, I would have passed) için kullanılır.",
            exampleSentence = "If I had known, I would have come earlier."
        ),
        GrammarTopicEntity(
            id = "3",
            title = "Passive Voice",
            explanation = "Eylemi yapan değil, eylemden etkilenen öznenin önemli olduğu cümlelerde kullanılır: be + past participle.",
            exampleSentence = "The report was written by the manager."
        ),
        GrammarTopicEntity(
            id = "4",
            title = "Relative Clauses",
            explanation = "Who, which, that, whose gibi bağlaçlarla bir ismi tanımlayan yan cümleler kurulur.",
            exampleSentence = "The book that you gave me was excellent."
        ),
        GrammarTopicEntity(
            id = "5",
            title = "Modals of Deduction",
            explanation = "Must, might, could, can't gibi modallar geçmiş veya şimdiki zamanda çıkarım yapmak için kullanılır.",
            exampleSentence = "He must have left already; his car is gone."
        ),
        GrammarTopicEntity(
            id = "6",
            title = "Present Perfect vs Past Simple",
            explanation = "Present Perfect, geçmişte başlayıp etkisi süren veya zamanı belirsiz olaylar için kullanılır (I have visited Paris). Past Simple ise zamanı belli, tamamlanmış olaylar içindir (I visited Paris in 2019).",
            exampleSentence = "I have already finished my homework, but I finished it late last night."
        ),
        GrammarTopicEntity(
            id = "7",
            title = "Present Perfect Continuous",
            explanation = "Geçmişte başlayıp hâlâ devam eden veya yakın zamanda biten, süresi vurgulanan eylemler için kullanılır: have/has been + V-ing.",
            exampleSentence = "They have been working on this project for three months."
        ),
        GrammarTopicEntity(
            id = "8",
            title = "Past Perfect",
            explanation = "Geçmişteki iki olaydan daha önce gerçekleşenini anlatmak için kullanılır: had + past participle.",
            exampleSentence = "By the time she arrived, the meeting had already started."
        ),
        GrammarTopicEntity(
            id = "9",
            title = "Future Forms (will / going to / present continuous)",
            explanation = "'Will' anlık kararlar ve tahminler, 'going to' önceden planlanmış niyetler, 'present continuous' ise ayarlanmış gelecek planları için kullanılır.",
            exampleSentence = "I'm meeting my dentist at 5 p.m. tomorrow; I think it will take an hour."
        ),
        GrammarTopicEntity(
            id = "10",
            title = "Reported Speech (Indirect Speech)",
            explanation = "Birinin söylediklerini aktarırken zamanlar genellikle bir adım geriye kayar (says → said; is → was; will → would).",
            exampleSentence = "She said that she was tired and would go home early."
        ),
        GrammarTopicEntity(
            id = "11",
            title = "Gerunds and Infinitives",
            explanation = "Bazı fiillerden sonra -ing (enjoy doing), bazılarından sonra to + fiil (decide to go) kullanılır; bu kalıplar ezbere bilinmelidir.",
            exampleSentence = "She enjoys reading novels but decided to watch a movie tonight."
        ),
        GrammarTopicEntity(
            id = "12",
            title = "Comparatives and Superlatives",
            explanation = "Kısa sıfatlarda -er/-est, uzun sıfatlarda more/most kullanılır; düzensiz biçimler (good-better-best) ayrıca öğrenilmelidir.",
            exampleSentence = "This exam is more difficult than the last one, but it's not the most difficult I've seen."
        ),
        GrammarTopicEntity(
            id = "13",
            title = "Adjective Order",
            explanation = "Birden fazla sıfat kullanıldığında sıralama genellikle şöyledir: görüş, boyut, yaş, şekil, renk, köken, malzeme (a beautiful small old round black Italian leather bag).",
            exampleSentence = "She bought a beautiful small wooden table."
        ),
        GrammarTopicEntity(
            id = "14",
            title = "Articles (a / an / the)",
            explanation = "'A/an' ilk kez bahsedilen, belirsiz tekil isimler için; 'the' hem konuşan hem dinleyenin bildiği belirli isimler için kullanılır.",
            exampleSentence = "I saw a cat in the garden; the cat was sleeping under a tree."
        ),
        GrammarTopicEntity(
            id = "15",
            title = "Question Tags",
            explanation = "Cümle olumluysa etiket olumsuz, cümle olumsuzsa etiket olumlu olur: You are coming, aren't you?",
            exampleSentence = "You haven't seen this movie before, have you?"
        ),
        GrammarTopicEntity(
            id = "16",
            title = "Wish / If Only",
            explanation = "Şimdiki zamana dair pişmanlık için wish + past simple, geçmişe dair pişmanlık için wish + past perfect kullanılır.",
            exampleSentence = "I wish I had studied harder for the exam."
        ),
        GrammarTopicEntity(
            id = "17",
            title = "Causative Form (have/get something done)",
            explanation = "Bir işi başkasına yaptırdığımızı anlatmak için kullanılır: have/get + nesne + past participle.",
            exampleSentence = "I had my car repaired last weekend."
        ),
        GrammarTopicEntity(
            id = "18",
            title = "Modals of Obligation and Permission",
            explanation = "Must/have to zorunluluk, should/ought to tavsiye, can/may izin anlamı taşır; must not ile don't have to arasındaki fark sık sorulur.",
            exampleSentence = "You must wear a seatbelt, but you don't have to drive if you're tired."
        ),
        GrammarTopicEntity(
            id = "19",
            title = "Inversion",
            explanation = "Vurgu amacıyla olumsuz zarflar (never, rarely, not only) cümle başına geldiğinde özne-yüklem sırası devrik olur.",
            exampleSentence = "Never have I seen such a beautiful sunset."
        ),
        GrammarTopicEntity(
            id = "20",
            title = "Noun Clauses",
            explanation = "That, what, whether, if gibi bağlaçlarla kurulan ve cümlede özne/nesne görevi gören yan cümlelerdir.",
            exampleSentence = "What she said surprised everyone in the room."
        )
    )

    override suspend fun getTopics(): Resource<List<GrammarTopic>> = withContext(Dispatchers.IO) {
        try {
            if (grammarDao.getAllTopics().isEmpty()) {
                grammarDao.insertTopics(seedTopics)
            }
            Resource.Success(grammarDao.getAllTopics().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private fun GrammarTopicEntity.toDomain() = GrammarTopic(
        id = id,
        title = title,
        explanation = explanation,
        exampleSentence = exampleSentence
    )
}
