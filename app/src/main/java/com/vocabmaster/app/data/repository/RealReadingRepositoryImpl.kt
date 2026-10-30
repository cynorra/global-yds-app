package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.ReadingDao
import com.vocabmaster.app.data.local.ReadingPassageEntity
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.ReadingPassage
import com.vocabmaster.app.domain.repository.ReadingRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RealReadingRepositoryImpl(
    private val readingDao: ReadingDao,
    private val firestore: FirebaseFirestore
) : ReadingRepository {

    // One-time seed for the shared Firestore passage bank, used only the very first time the
    // `reading_passages` collection is empty. After that the bank lives entirely in Firestore -
    // add/edit/remove passages from the Firebase console, no app release needed. Passages are
    // always English (this is an English reading exercise), so there is no translation step,
    // unlike the vocabulary/grammar banks.
    private val initialSeedPassages = listOf(
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
        ),
        ReadingPassageEntity(
            id = "4",
            title = "The History of the Internet",
            bodyText = "The internet began as a small research project in the late 1960s, designed " +
                "to let a handful of university computers exchange data over a single network. For " +
                "decades it remained the domain of scientists and the military, largely unknown to " +
                "the public. It was only with the invention of the World Wide Web in the early 1990s " +
                "that ordinary people gained an easy way to browse and publish information, sparking " +
                "the explosive growth that turned the internet into the backbone of modern life."
        ),
        ReadingPassageEntity(
            id = "5",
            title = "Artificial Intelligence and Everyday Life",
            bodyText = "Artificial intelligence, once confined to research labs and science fiction, " +
                "now quietly shapes many everyday decisions. It recommends what to watch, filters " +
                "spam from inboxes, and helps translate languages in real time. While these tools " +
                "bring undeniable convenience, they also raise questions about privacy, bias, and how " +
                "much decision-making society is willing to hand over to algorithms that few people " +
                "fully understand."
        ),
        ReadingPassageEntity(
            id = "6",
            title = "Climate Change and Biodiversity",
            bodyText = "Rising global temperatures are altering habitats faster than many species can " +
                "adapt. Coral reefs are bleaching as oceans warm, migratory birds are arriving at " +
                "breeding grounds out of sync with food availability, and mountain species are being " +
                "pushed toward summits with nowhere left to climb. Scientists warn that without " +
                "significant reductions in greenhouse gas emissions, the current rate of biodiversity " +
                "loss could rival the mass extinctions of the distant past."
        ),
        ReadingPassageEntity(
            id = "7",
            title = "The Importance of Bilingual Education",
            bodyText = "Children who grow up learning two languages often develop stronger problem-" +
                "solving and multitasking skills than their monolingual peers, according to decades " +
                "of research in cognitive science. Beyond the practical advantage of speaking another " +
                "language, bilingual education has been linked to greater cultural awareness and, in " +
                "some studies, a delayed onset of age-related cognitive decline later in life."
        ),
        ReadingPassageEntity(
            id = "8",
            title = "Space Exploration: Past and Future",
            bodyText = "Six decades after the first satellite reached orbit, space exploration has " +
                "entered a new era driven largely by private companies rather than governments alone. " +
                "Reusable rockets have dramatically cut the cost of reaching space, opening the door " +
                "to ambitious plans for lunar bases and, eventually, crewed missions to Mars. Critics " +
                "argue the money would be better spent on Earth, while supporters see space technology " +
                "as a driver of scientific progress with benefits that eventually reach everyone."
        ),
        ReadingPassageEntity(
            id = "9",
            title = "The Psychology of Habit Formation",
            bodyText = "Habits form through a simple neurological loop: a cue triggers a routine, " +
                "which is followed by a reward, and repetition gradually makes the behavior automatic. " +
                "This is why breaking a bad habit is so difficult - the brain has been trained to " +
                "expect the reward. Psychologists suggest that the most effective way to change a " +
                "habit is not to eliminate the cue, but to replace the routine while keeping the " +
                "reward the same."
        )
    )

    override suspend fun getPassages(): Resource<List<ReadingPassage>> = withContext(Dispatchers.IO) {
        try {
            ensurePassagesCached()
            Resource.Success(readingDao.getAllPassages().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    // Firestore's `reading_passages` collection is the single source of truth. Only passages
    // missing locally are downloaded, so an admin can add new passages in Firestore at any time
    // without an app update.
    private suspend fun ensurePassagesCached() {
        val masterPassages = loadOrSeedMasterBank()
        if (masterPassages.isEmpty()) return // offline on first run with no local cache yet

        val existingIds = readingDao.getAllPassages().map { it.id }.toSet()
        val missingPassages = masterPassages.filterNot { it.id in existingIds }
        if (missingPassages.isEmpty()) return

        readingDao.insertPassages(missingPassages)
    }

    private suspend fun loadOrSeedMasterBank(): List<ReadingPassageEntity> {
        val masterCollection = firestore.collection(MASTER_COLLECTION)
        return try {
            val existing = masterCollection.get().await().documents.mapNotNull { it.toObject(ReadingPassageEntity::class.java) }
            existing.ifEmpty { seedMasterBank(masterCollection) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun seedMasterBank(
        masterCollection: com.google.firebase.firestore.CollectionReference
    ): List<ReadingPassageEntity> {
        try {
            initialSeedPassages.forEach { passage ->
                masterCollection.document(passage.id).set(
                    mapOf("id" to passage.id, "title" to passage.title, "bodyText" to passage.bodyText)
                ).await()
            }
        } catch (e: Exception) {
            // Offline on very first run: the app still works this session with the seed list
            // in memory; another device (or this one later) will perform the seed write.
        }
        return initialSeedPassages
    }

    private fun ReadingPassageEntity.toDomain(): ReadingPassage {
        val (exam, level) = when (id) {
            "1" -> "TOEIC" to "B2"
            "2" -> "IELTS" to "C1"
            "3" -> "TOEFL" to "C1"
            "4" -> "CAMBRIDGE" to "B2"
            "5" -> "SAT" to "C1"
            "6" -> "IELTS" to "C1"
            "7" -> "PTE" to "B2"
            "8" -> "YDS" to "B2-C1"
            else -> "ALL" to "B2"
        }
        return ReadingPassage(
            id = id,
            title = title,
            bodyText = bodyText,
            targetExam = exam,
            levelBadge = level
        )
    }

    companion object {
        private const val MASTER_COLLECTION = "reading_passages"
    }
}
