package com.thewhitewings.pouch.feature_note.util

/**
 * Default format of date and time.
 *
 * yyyy-MM-dd HH:mm:ss = 2024-01-02 19:16:19
 */
const val DEFAULT_FORMAT: String = "yyyy-MM-dd HH:mm:ss"

/**
 * Medium length format for date and time.
 *
 * "MMM d, yyyy" = Feb 4, 2024
 */
const val MEDIUM_LENGTH_FORMAT: String = "MMM d, yyyy"

/**
 * Short length format for date and time.
 *
 * "MMM d" = Feb 4
 */
const val SHORT_LENGTH_FORMAT: String = "MMM d"


/**
 * Enum class for DateTime format types.
 * It is used to modify the format and/or time zone of the date and time.
 */
enum class DateTimeFormatType {

    /**
     * Date and time from UTC to Local Time Zone for Retrieving.
     *
     * It is formatted as: [DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    UTC_TO_LOCAL,

    /**
     * Date and time from Local Time Zone to UTC.
     *
     * It is formatted as: [DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    LOCAL_TO_UTC,

    /**
     * Current date and time in UTC for Storing in Database.
     *
     * It is formatted as: [DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 16:16:19
     */
    CURRENT_UTC,

    /**
     * Current date and time in Local Time Zone.
     *
     * It is formatted as: [DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    CURRENT_LOCAL,

    /**
     * Formatted Date in Local Time Zone for displaying in [AddEditNoteScreen].
     *
     * It is formatted as: [MEDIUM_LENGTH_FORMAT]
     * "MMM d, yyyy" = Feb 4, 2024
     */
    LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,

    /**
     * Formatted Date in Local Time Zone for displaying in [NotesScreen].
     *
     * It is formatted as: [SHORT_LENGTH_FORMAT]
     * "MMM d" = Feb 4
     */
    LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT
}