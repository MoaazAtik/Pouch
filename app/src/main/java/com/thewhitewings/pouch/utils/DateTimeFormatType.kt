package com.thewhitewings.pouch.utils

/**
 * Enum class for DateTime format types.
 * It is used to format date and time for different time zones and format text lengths.
 */
enum class DateTimeFormatType {

    /**
     * Date and time from UTC to Local Time Zone for Retrieving.
     *
     * It is formatted as: [DateTimeUtils.DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    UTC_TO_LOCAL,

    /**
     * Date and time from Local Time Zone to UTC.
     *
     * It is formatted as: [DateTimeUtils.DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    LOCAL_TO_UTC,

    /**
     * Current date and time in UTC for Storing in Database.
     *
     * It is formatted as: [DateTimeUtils.DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 16:16:19
     */
    CURRENT_UTC,

    /**
     * Current date and time in Local Time Zone.
     *
     * It is formatted as: [DateTimeUtils.DEFAULT_FORMAT]
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    CURRENT_LOCAL,

    /**
     * Formatted Date in Local Time Zone for displaying in Note Screen.
     *
     * It is formatted as: [DateTimeUtils.MEDIUM_LENGTH_FORMAT]
     * "MMM d, yyyy" = Feb 4, 2024
     */
    LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,

    /**
     * Formatted Date in Local Time Zone for displaying in Notes list Screen.
     *
     * It is formatted as: [DateTimeUtils.SHORT_LENGTH_FORMAT]
     * "MMM d" = Feb 4
     */
    LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT
}