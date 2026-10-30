package com.vocabmaster.app.domain.models

enum class ExamType(
    val code: String,
    val displayName: String,
    val flag: String,
    val description: String,
    val defaultDurationMinutes: Int
) {
    ALL("ALL", "Tüm Sınavlar", "🌍", "Tüm genel ve akademik sınavlar", 15),
    TOEIC("TOEIC", "TOEIC", "🇯🇵", "Business & Daily Workplace English", 20),
    IELTS("IELTS", "IELTS", "🇬🇧", "Academic & General Training", 20),
    TOEFL("TOEFL", "TOEFL iBT", "🇺🇸", "University & Academic English", 18),
    CAMBRIDGE("CAMBRIDGE", "Cambridge", "🇪🇺", "B2 First & C1 Advanced English", 15),
    SAT("SAT", "SAT / ACT", "🎓", "Reading, Writing & Logic", 15),
    PTE("PTE", "PTE Academic", "⚡", "Fast AI-Scored University Test", 12),
    DET("DET", "Duolingo Test", "🦉", "Adaptive Online University Test", 10),
    GRE("GRE", "GRE / GMAT", "🏛️", "Advanced Verbal & Vocabulary", 15),
    EIKEN("EIKEN", "EIKEN", "🌸", "Japan Standard English Test", 15),
    YDS("YDS", "YDS / YÖKDİL", "🇹🇷", "Türkiye Akademik & Memuriyet", 15);

    companion object {
        fun fromCode(code: String?): ExamType {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ALL
        }
    }
}

