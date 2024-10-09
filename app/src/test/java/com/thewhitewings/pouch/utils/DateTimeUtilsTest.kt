package com.thewhitewings.pouch.utils

import com.thewhitewings.pouch.data.mockDateTimeLocalTimezone
import com.thewhitewings.pouch.data.mockDateTimeUtcTimezone
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DateTimeUtilsTest {

    @Before
    fun setUp() {
        DateTimeUtils.isTestEnvironment = true // Skip logging during tests
    }

    @After
    fun tearDown() {
        DateTimeUtils.isTestEnvironment = false // Restore for other parts
    }

    /**
     * Convert UTC date-time string to date-time string in Local timezone.
     * Case: UTC to Local - Valid date-time string.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_utcToLocal_validDateTimeString() {
        // Given a valid UTC date-time string
        // UTC time
        val utcDateTime = mockDateTimeUtcTimezone
        // Expected local time (UTC+3)
        val expectedLocalDateTime = mockDateTimeLocalTimezone

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            utcDateTime
        )

        // Then the result should match the expected local date-time
        assertEquals(expectedLocalDateTime, result)
    }

    /**
     * Convert UTC date-time string to date-time string in Local timezone.
     * Case: UTC to Local - Invalid date-time string.
     * for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_utcToLocal_invalidDateTimeString() {
        // Given an invalid UTC date-time string
        val invalidUtcDateTime = "invalid-datetime-string"

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            invalidUtcDateTime
        )

        // Then the result should return an error message including the invalid date-time string
        assertEquals("Error $invalidUtcDateTime", result)
    }

}