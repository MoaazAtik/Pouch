package com.thewhitewings.pouch.data

data class Note(
    val id: Int,
    val noteTitle: String,
    val noteBody: String,
    val timestamp: String
){

    override fun toString(): String {
        return "Note{" +
                "id=" + id +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteBody='" + noteBody + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}'
    }

    /**
     * Checks if two notes are equal based on their content.
     *
     * @param note The note to compare with.
     * @return True if the notes are equal, false otherwise.
     */
    fun equalContent(note: Note): Boolean {
        return id == note.id &&
                noteTitle == note.noteTitle &&
                noteBody == note.noteBody &&
                timestamp == note.timestamp
    }
}
