package com.thewhitewings.pouch.utils;

import com.thewhitewings.pouch.ui.NoteFragment;

public enum DateTimeFormatType {

    /**
     * Date and time from UTC to Local Time Zone for Retrieving.
     * {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    UTC_TO_LOCAL,

    /**
     * Date and time from Local Time Zone to UTC for Storing in Database.
     * {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    LOCAL_TO_UTC,

    /**
     * Current date and time in Local Time Zone for Storing in Notes List.
     * {@link DateTimeUtils#DEFAULT_FORMAT}
     * "yyyy-MM-dd HH:mm:ss" = 2024-01-02 19:16:19
     */
    CURRENT_LOCAL,

    /**
     * Formatted Date in Local Time Zone for Retrieving in {@link NoteFragment}.
     * {@link DateTimeUtils#MEDIUM_LENGTH_FORMAT}
     * "MMM d, yyyy" = Feb 4, 2024
     */
    LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,

    /**
     * Format timestamp to short length format.
     * {@link DateTimeUtils#SHORT_LENGTH_FORMAT}
     * "MMM d" = Feb 4
     */
    LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT
}