package com.thewhitewings.pouch.utils;

import static com.thewhitewings.pouch.utils.DateTimeFormatType.*;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    private static final String TAG = "DateTimeUtils";

    /**
     * Default format for date and time.
     * yyyy-MM-dd HH:mm:ss = 2024-01-02 19:16:19
     */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * Medium length format for date and time.
     * "MMM d, yyyy" = Feb 4, 2024
     */
    public static final String MEDIUM_LENGTH_FORMAT = "MMM d, yyyy";
    /**
     * Short length format for date and time.
     * "MMM d" = Feb 4
     */
    public static final String SHORT_LENGTH_FORMAT = "MMM d";


    /**
     * Get formatted date and time based on the format type. The Basic format is {@link #DEFAULT_FORMAT} <p>
     * Note: Date and time are stored in the Database in UTC, and in Notes List in Local Time Zone.
     *
     * @param formatType {@link DateTimeFormatType} type of formatting to apply.
     * @param dateTime Optional. Date/time string to format.
     * @return Formatted date/time string.
     */
    public static String getFormattedDateTime(DateTimeFormatType formatType, String dateTime) {
        SimpleDateFormat sdFormat = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        Date date;
        String formattedDateTime;

        switch (formatType) {
            case UTC_TO_LOCAL:
                try {
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getDefault());
                    formattedDateTime = sdFormat.format(date);
                    return formattedDateTime;
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date " + UTC_TO_LOCAL, e);
                    return "Error " + dateTime;
                }

            case LOCAL_TO_UTC:
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    formattedDateTime = sdFormat.format(date);
                    return formattedDateTime;
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date " + LOCAL_TO_UTC, e);
                    return "Error " + dateTime;
                }

            case CURRENT_LOCAL:
                date = new Date();
                return sdFormat.format(date);

            case LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT:
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.applyPattern(MEDIUM_LENGTH_FORMAT);
                    formattedDateTime = sdFormat.format(date);
                    return formattedDateTime;
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date " +  LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT, e);
                    return "Error " + dateTime;
                }

            case LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT:
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.applyPattern(SHORT_LENGTH_FORMAT);
                    formattedDateTime = sdFormat.format(date);
                    return formattedDateTime;
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date " +  LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT, e);
                    return "Error " + dateTime;
                }

            default:
                throw new IllegalArgumentException("Unknown format type: " + formatType);
        }
    }
}

