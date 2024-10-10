package com.thewhitewings.pouch.utils

import com.thewhitewings.pouch.data.mockDateTimeLocalTimezone
import com.thewhitewings.pouch.data.mockDateTimeNewYorkTimezone
import com.thewhitewings.pouch.data.mockDateTimeNewYorkTimezoneUseDayLight
import com.thewhitewings.pouch.data.mockDateTimeSingaporeTimezone
import com.thewhitewings.pouch.data.mockDateTimeUtcTimezone
import com.thewhitewings.pouch.utils.DateTimeUtils.DEFAULT_FORMAT
import com.thewhitewings.pouch.utils.DateTimeUtils.MEDIUM_LENGTH_FORMAT
import com.thewhitewings.pouch.utils.DateTimeUtils.SHORT_LENGTH_FORMAT
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateTimeUtilsTest {

    private val defaultLocale = Locale.getDefault()
    private val defaultTimeZone = TimeZone.getDefault()

    private val invalidDateTimeString = "2024 10:00:00"
    private val emptyDateTimeString = ""

    @Before
    fun setUp() {
        DateTimeUtils.isTestEnvironment = true // Skip logging during tests
    }

    @After
    fun tearDown() {
        DateTimeUtils.isTestEnvironment = false // Restore for other parts
        // Reset the locale and timezone after tests
        Locale.setDefault(defaultLocale)
        TimeZone.setDefault(defaultTimeZone)
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
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_utcToLocal_invalidDateTimeString() {
        // Given an invalid UTC date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            invalidDateTimeString
        )

        // Then the result should be an error message
        assertEquals("Error $invalidDateTimeString", result)
    }

    /**
     * Convert UTC date-time string to date-time string in Local timezone.
     * Case: UTC to Local - Empty date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_utcToLocal_emptyDateTimeString() {
        // Given an empty date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            emptyDateTimeString
        )

        // Then the result should be an error message
        assertEquals("Error $emptyDateTimeString", result)
    }

    /**
     * Convert UTC date-time string to date-time string in Local timezone.
     * Case: UTC to Local - Different time zones (New York and Singapore).
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_utcToLocal_differentTimeZones() {
        // Given a valid UTC date-time string
        // UTC time
        val utcDateTime = mockDateTimeUtcTimezone

        // Set different time zones to simulate different local zones
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
        // Expected time in New York (UTC-4 or UTC-5)
        val expectedDateTimeNewYork =
            if (!TimeZone.getTimeZone("America/New_York")
                    .useDaylightTime()
            ) mockDateTimeNewYorkTimezone
            else mockDateTimeNewYorkTimezoneUseDayLight

        // When calling the function
        val resultNewYork = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            utcDateTime
        )

        // Then the result should match the expected date-time in New York time zone
        assertEquals(expectedDateTimeNewYork, resultNewYork)


        // Set another time zone to simulate a different local zone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Singapore"))
        // Expected time in Singapore (UTC+8)
        val expectedDateTimeSingapore = mockDateTimeSingaporeTimezone

        // When calling the function again
        val resultSingapore = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.UTC_TO_LOCAL,
            utcDateTime
        )

        // Then the result should match the expected date-time in Singapore time zone
        assertEquals(expectedDateTimeSingapore, resultSingapore)
    }

    /**
     * Convert Local date-time string to date-time string in UTC timezone.
     * Case: Local to UTC - Valid date-time string.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToUtc_validDateTimeString() {
        // Given a valid local date-time string
        // Local time (UTC+3)
        val localDateTime = mockDateTimeLocalTimezone
        // Expected UTC time
        val expectedUtcDateTime = mockDateTimeUtcTimezone

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_UTC,
            localDateTime
        )

        // Then the result should match the expected UTC date-time
        assertEquals(expectedUtcDateTime, result)
    }

    /**
     * Convert Local date-time string to date-time string in UTC timezone.
     * Case: Local to UTC - Invalid date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToUtc_invalidDateTimeString() {
        // Given an invalid local date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_UTC,
            invalidDateTimeString
        )

        // Then the result should be an error message
        assertEquals("Error $invalidDateTimeString", result)
    }

    /**
     * Convert Local date-time string to date-time string in UTC timezone.
     * Case: Local to UTC - Empty date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToUtc_emptyDateTimeString() {
        // Given an empty local date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_UTC,
            emptyDateTimeString
        )

        // Then the result should be an error message
        assertEquals("Error $emptyDateTimeString", result)
    }

    /**
     * Convert Local date-time string to date-time string in UTC timezone.
     * Case: Local to UTC - Different time zones (New York and Singapore).
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToUtc_differentTimeZones() {
        // Expected UTC time
        val expectedUtcDateTime = mockDateTimeUtcTimezone

        // Set different time zones to simulate different local zones
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
        // Time in New York (UTC-4 or UTC-5)
        val dateTimeNewYork =
            if (!TimeZone.getTimeZone("America/New_York")
                    .useDaylightTime()
            ) mockDateTimeNewYorkTimezone
            else mockDateTimeNewYorkTimezoneUseDayLight

        // When calling the function
        val resultNewYork = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_UTC,
            dateTimeNewYork
        )

        // Then the result should match the expected date-time in UTC time zone
        assertEquals(expectedUtcDateTime, resultNewYork)


        // Set another time zone to simulate a different local zone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Singapore"))
        // Expected time in Singapore (UTC+8)
        val dateTimeSingapore = mockDateTimeSingaporeTimezone

        // When calling the function again
        val resultSingapore = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_UTC,
            dateTimeSingapore
        )

        // Then the result should match the expected date-time in UTC time zone
        assertEquals(expectedUtcDateTime, resultSingapore)
    }

    /**
     * Get current date-time string in UTC timezone.
     * Case: Current UTC time.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_currentUtcTime() {
        // Given the current time in UTC
        val expectedUtcDateTime =
            SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.CURRENT_UTC
        )

        // Then the result should be the current time in UTC
        assertEquals(expectedUtcDateTime, result)
    }

    /**
     * Get current date-time string in Local timezone.
     * Case: Current local time.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_currentLocalTime() {
        // Given the current local time
        val expectedLocalDateTime =
            SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }.format(Date())

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.CURRENT_LOCAL
        )

        // Then the result should be the current local time
        assertEquals(expectedLocalDateTime, result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [MEDIUM_LENGTH_FORMAT].
     * Case: Local to Local in medium length format - Valid date-time string.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalMediumLengthFormat_validDateTimeString() {
        // Given a valid local date-time string
        // Local time (UTC+3)
        val localDateTime = mockDateTimeLocalTimezone

        // Expected formatted local time in medium length format
        val expectedFormattedDateTime =
            SimpleDateFormat(DEFAULT_FORMAT).parse(localDateTime)?.let {
                SimpleDateFormat(MEDIUM_LENGTH_FORMAT, Locale.getDefault())
                    .format(it)
            }

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,
            localDateTime
        )

        // Then the result should match the expected formatted date-time
        assertEquals(expectedFormattedDateTime, result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [MEDIUM_LENGTH_FORMAT].
     * Case: Local to Local in medium length format - Invalid date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalMediumLengthFormat_invalidDateTimeString() {
        // Given an invalid local date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,
            invalidDateTimeString
        )

        // Then the result should return an error message
        assertEquals("Error $invalidDateTimeString", result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [MEDIUM_LENGTH_FORMAT].
     * Case: Local to Local in medium length format - Empty date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalMediumLengthFormat_emptyDateTimeString() {
        // Given an empty date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,
            emptyDateTimeString
        )

        // Then the result should return an error message
        assertEquals("Error $emptyDateTimeString", result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [SHORT_LENGTH_FORMAT].
     * Case: Local to Local in short length format - Valid date-time string.
     * Happy path for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalShortLengthFormat_validDateTimeString() {
        // Given a valid local date-time string
        // Local time (UTC+3)
        val localDateTime = mockDateTimeLocalTimezone

        // Expected formatted local time in short length format
        val expectedFormattedDateTime =
            SimpleDateFormat(DEFAULT_FORMAT).parse(localDateTime)?.let {
                SimpleDateFormat(SHORT_LENGTH_FORMAT, Locale.getDefault())
                    .format(it)
            }

        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
            localDateTime
        )

        // Then the result should match the expected formatted date-time
        assertEquals(expectedFormattedDateTime, result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [SHORT_LENGTH_FORMAT].
     * Case: Local to Local in short length format - Invalid date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalShortLengthFormat_invalidDateTimeString() {
        // Given an invalid local date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
            invalidDateTimeString
        )

        // Then the result should return an error message
        assertEquals("Error $invalidDateTimeString", result)
    }

    /**
     * Format Local date-time string with [DEFAULT_FORMAT] to [SHORT_LENGTH_FORMAT].
     * Case: Local to Local in short length format - Empty date-time string.
     * Error case for [DateTimeUtils.getFormattedDateTime]
     */
    @Test
    fun getFormattedDateTime_localToLocalShortLengthFormat_emptyDateTimeString() {
        // Given an empty date-time string
        // When calling the function
        val result = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
            emptyDateTimeString
        )

        // Then the result should return an error message
        assertEquals("Error $emptyDateTimeString", result)
    }
}