package com.thewhitewings.pouch.data

/*
Important Note: timestamp format in these notes doesn't represent the actual timestamp format of notes in the database.
Refer to the default value of the timestamp property in the Note class.
 */
val mockNote1 = Note(
    1,
    "Apple",
    "Content for Apple",
    (System.currentTimeMillis() - 1000).toString() // Oldest
)

val mockNote2 = Note(
    2,
    "Banana",
    "Content for Banana",
    System.currentTimeMillis().toString() // Newest
)

val mockNote3 = Note(
    3,
    "Cherry",
    "Content for Cherry",
    (System.currentTimeMillis() - 500).toString()
)

val azSortedNotes = listOf(mockNote1, mockNote2, mockNote3)
val zaSortedNotes = azSortedNotes.reversed()
val oldestFirstNotes = listOf(mockNote1, mockNote3, mockNote2)
val newestFirstNotes = oldestFirstNotes.reversed()

/**
 * Timestamp formatted like [DatetimeUtils.DEFAULT_FORMAT] which represents the actual timestamp format of notes in the database.
 */
const val mockTimestamp1 = "2024-01-02 19:16:19"