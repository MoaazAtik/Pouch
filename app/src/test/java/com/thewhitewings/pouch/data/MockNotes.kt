package com.thewhitewings.pouch.data

import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils

/**
 * Note with title and body.
 * Id and timestamp are auto-generated.
 */
val mockNoteWithTitleAndBody = Note(
    noteTitle = "Mock Title",
    noteBody = "Mock Body"
)

/**
 * Note with timestamp converted from UTC to Local timezone and with [DateTimeUtils.DEFAULT_FORMAT].
 * It represents the note that will be retrieved from the repository.
 */
val mockNoteFromRepository = mockNoteWithTitleAndBody.copy(
    timestamp = DateTimeUtils.getFormattedDateTime(
        DateTimeFormatType.UTC_TO_LOCAL,
        mockNoteWithTitleAndBody.timestamp
    )
)

/**
 * Note with timestamp of the current time in UTC timezone and with [DateTimeUtils.DEFAULT_FORMAT].
 * It represents the note that will be passed to the Dao.
 */
val mockNoteToDao = mockNoteWithTitleAndBody.copy(
    timestamp = DateTimeUtils.getFormattedDateTime(
        DateTimeFormatType.CURRENT_UTC,
        mockNoteWithTitleAndBody.timestamp
    )
)

/*
Note: timestamps in these notes are in UTC timezone.
They are formatted like [DatetimeUtils.DEFAULT_FORMAT] which represents the actual timestamp format of notes in the database.
Refer to the default value of the timestamp property in the Note class.
 */
val mockNote1 = Note(
    1,
    "Apple",
    "Content for Apple",
    "2024-01-02 19:16:19" // Oldest
)

val mockNote2 = Note(
    2,
    "Banana",
    "Content for Banana" // Newest
)

val mockNote3 = Note(
    3,
    "Cherry",
    "Content for Cherry",
    "2024-01-03 19:16:20"
)

/**
 * Notes in UTC timezone and with [DateTimeUtils.DEFAULT_FORMAT]
 */
val azSortedNotesUtc = listOf(mockNote1, mockNote2, mockNote3)
val zaSortedNotesUtc = azSortedNotesUtc.reversed()
val oldestFirstNotesUtc = listOf(mockNote1, mockNote3, mockNote2)
val newestFirstNotesUtc = oldestFirstNotesUtc.reversed()

/**
 * Notes in Local timezone and with [DateTimeUtils.DEFAULT_FORMAT]
 */
val azSortedNotesLocal = azSortedNotesUtc.map { note ->
    note.copy(
        timestamp = DateTimeUtils.getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, note.timestamp)
    )
}
val zaSortedNotesLocal = azSortedNotesLocal.reversed()
val oldestFirstNotesLocal = oldestFirstNotesUtc.map { note ->
    note.copy(
        timestamp = DateTimeUtils.getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, note.timestamp)
    )
}
val newestFirstNotesLocal = oldestFirstNotesLocal.reversed()


/**
 * Mock date-time string in UTC timezone.
 * It's formatted like [DatetimeUtils.DEFAULT_FORMAT] which represents the actual timestamp format of notes in the database.
 * It's related to [mockDateTimeLocalTimezone]
 */
const val mockDateTimeUtcTimezone = "2024-01-02 10:00:00"
/**
 * Mock date-time string in Local timezone (Europe/Istanbul) which is UTC+3.
 * It's formatted like [DatetimeUtils.DEFAULT_FORMAT] which represents the timestamp format we get from [OfflineNotesRepository].
 * It's related to [mockDateTimeUtcTimezone]
 */
const val mockDateTimeLocalTimezone = "2024-01-02 13:00:00"