package com.thewhitewings.pouch.utils;

import com.thewhitewings.pouch.ui.NoteFragment;

/**
 * Enum class for DateTime format types.
 * It is used to format date and time for different time zones and format text lengths.
 */
public enum DateTimeFormatType {

    /**
     * Date and time from UTC to Local Time Zone for Retrieving.
     * <p>
     * It is formatted as: {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     * </p>
     */
    UTC_TO_LOCAL,

    /**
     * Date and time from Local Time Zone to UTC for Storing in Database.
     * <p>
     * It is formatted as: {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     * </p>
     */
    LOCAL_TO_UTC,

    /**
     * Current date and time in Local Time Zone.
     * <p>
     * It is formatted as: {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     * </p>
     */
    CURRENT_LOCAL,

    /**
     * Formatted Date in Local Time Zone for displaying in {@link NoteFragment}.
     * <p>
     * It is formatted as: {@link DateTimeUtils#MEDIUM_LENGTH_FORMAT}
     * "MMM d, yyyy" = Feb 4, 2024
     * </p>
     */
    LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,

    /**
     * Formatted Date in Local Time Zone for displaying in the main activity.
     * <p>
     * It is formatted as: {@link DateTimeUtils#SHORT_LENGTH_FORMAT}
     * "MMM d" = Feb 4
     * </p>
     */
    LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT
}