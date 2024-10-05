package com.thewhitewings.pouch.data

/*
Important Note: timestamp format in these notes doesn't represent the actual timestamp format of notes in the database.
Refer to the default value of the timestamp property in the Note class.
 */
val note1 = Note(
    1,
    "Apple",
    "Content for Apple",
    (System.currentTimeMillis() - 1000).toString() // Oldest
)

val note2 = Note(
    2,
    "Banana",
    "Content for Banana",
    System.currentTimeMillis().toString() // Newest
)

val note3 = Note(
    3,
    "Cherry",
    "Content for Cherry",
    (System.currentTimeMillis() - 500).toString()
)

val azSortedNotes = listOf(note1, note2, note3)
val zaSortedNotes = azSortedNotes.reversed()
val oldestFirstNotes = listOf(note1, note3, note2)
val newestFirstNotes = oldestFirstNotes.reversed()