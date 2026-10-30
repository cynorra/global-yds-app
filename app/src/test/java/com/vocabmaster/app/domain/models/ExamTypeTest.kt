package com.vocabmaster.app.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExamTypeTest {

    @Test
    fun `all 10 exam types and ALL have non-empty codes, flags and names`() {
        val allTypes = ExamType.values()
        assertEquals(11, allTypes.size)

        for (type in allTypes) {
            assertNotNull(type.code)
            assertNotNull(type.displayName)
            assertNotNull(type.flag)
            assertNotNull(type.description)
            assert(type.defaultDurationMinutes > 0)
        }
    }

    @Test
    fun `specific exam codes match expected values`() {
        assertEquals("TOEIC", ExamType.TOEIC.code)
        assertEquals("IELTS", ExamType.IELTS.code)
        assertEquals("TOEFL", ExamType.TOEFL.code)
        assertEquals("CAMBRIDGE", ExamType.CAMBRIDGE.code)
        assertEquals("SAT", ExamType.SAT.code)
        assertEquals("PTE", ExamType.PTE.code)
        assertEquals("DET", ExamType.DET.code)
        assertEquals("EIKEN", ExamType.EIKEN.code)
        assertEquals("GRE", ExamType.GRE.code)
        assertEquals("YDS", ExamType.YDS.code)
        assertEquals("ALL", ExamType.ALL.code)
    }
}

