package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.ReadingDao
import com.vocabmaster.app.data.local.ReadingPassageEntity
import com.vocabmaster.app.domain.models.ReadingPassage
import com.vocabmaster.app.domain.repository.ReadingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealReadingRepositoryImpl(
    private val readingDao: ReadingDao
) : ReadingRepository {

    // A small seed set of short passages for our MVP; the full passage bank is added later.
    private val seedPassages = listOf(
        ReadingPassageEntity(
            id = "1",
            title = "The Rise of Remote Work",
            bodyText = "Over the past decade, remote work has transformed from a rare perk into a " +
                "mainstream way of working. Companies that once required employees to be in the " +
                "office five days a week now offer flexible arrangements, allowing people to work " +
                "from home, co-working spaces, or even while traveling. This shift has been driven " +
                "by advances in communication technology, changing employee expectations, and, more " +
                "recently, global events that made remote work a necessity rather than a choice."
        ),
        ReadingPassageEntity(
            id = "2",
            title = "Renewable Energy on the Rise",
            bodyText = "Renewable energy sources such as solar and wind power are becoming " +
                "increasingly cost-competitive with fossil fuels. As technology improves and " +
                "production scales up, the price of solar panels and wind turbines continues to " +
                "fall. Many countries have set ambitious targets to reduce their reliance on coal " +
                "and oil, investing heavily in clean energy infrastructure to combat climate change " +
                "and secure long-term energy independence."
        ),
        ReadingPassageEntity(
            id = "3",
            title = "The Science of Sleep",
            bodyText = "Sleep plays a crucial role in memory consolidation, emotional regulation, " +
                "and overall physical health. Researchers have found that adults who consistently " +
                "sleep fewer than six hours a night are at higher risk of chronic conditions such " +
                "as obesity and cardiovascular disease. Despite this evidence, many people continue " +
                "to sacrifice sleep in favor of work or entertainment, underestimating its long-term " +
                "impact on well-being."
        )
    )

    override suspend fun getPassages(): List<ReadingPassage> = withContext(Dispatchers.IO) {
        if (readingDao.getAllPassages().isEmpty()) {
            readingDao.insertPassages(seedPassages)
        }
        readingDao.getAllPassages().map { it.toDomain() }
    }

    private fun ReadingPassageEntity.toDomain() = ReadingPassage(
        id = id,
        title = title,
        bodyText = bodyText
    )
}
