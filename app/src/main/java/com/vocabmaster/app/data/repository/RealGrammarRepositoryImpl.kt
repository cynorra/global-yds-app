package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.GrammarDao
import com.vocabmaster.app.data.local.GrammarTopicEntity
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealGrammarRepositoryImpl(
    private val grammarDao: GrammarDao
) : GrammarRepository {

    // A small seed set of common YDS grammar topics for our MVP; the full topic bank is added later.
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
        )
    )

    override suspend fun getTopics(): List<GrammarTopic> = withContext(Dispatchers.IO) {
        if (grammarDao.getAllTopics().isEmpty()) {
            grammarDao.insertTopics(seedTopics)
        }
        grammarDao.getAllTopics().map { it.toDomain() }
    }

    private fun GrammarTopicEntity.toDomain() = GrammarTopic(
        id = id,
        title = title,
        explanation = explanation,
        exampleSentence = exampleSentence
    )
}
