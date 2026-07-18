package com.vacation.tripinmind.data

import com.vacation.tripinmind.data.local.Converters
import com.vacation.tripinmind.data.local.model.Day
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `toDayList handles null, blank, and corrupt json gracefully`() {
        assertTrue(converters.toDayList(null).isEmpty())
        assertTrue(converters.toDayList("").isEmpty())
        assertTrue(converters.toDayList("   ").isEmpty())
        assertTrue(converters.toDayList("corrupted_json_string").isEmpty())
        assertTrue(converters.toDayList("{invalid_format}").isEmpty())
    }

    @Test
    fun `toStringList handles null, blank, and corrupt json gracefully`() {
        assertTrue(converters.toStringList(null).isEmpty())
        assertTrue(converters.toStringList("").isEmpty())
        assertTrue(converters.toStringList("   ").isEmpty())
        assertTrue(converters.toStringList("corrupted_json_string").isEmpty())
        assertTrue(converters.toStringList("{invalid_format}").isEmpty())
    }

    @Test
    fun `fromDayList and toDayList serialization roundtrip works`() {
        val days = listOf(Day(nameDay = "Jour 1", additionalInfo = "Info"))
        val json = converters.fromDayList(days)
        val result = converters.toDayList(json)

        assertEquals(days, result)
    }

    @Test
    fun `fromStringList and toStringList serialization roundtrip works`() {
        val strings = listOf("idée 1", "idée 2")
        val json = converters.fromStringList(strings)
        val result = converters.toStringList(json)

        assertEquals(strings, result)
    }
}