package com.thewhitewings.pouch.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val TAG = "DateTimeUtils"

/**
 * Utility object for formatting date and time.
 */
object DateTimeUtils {

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
     * Get formatted date and time based on the provided [DateTimeFormatType].
     *
     * The Default format is [DEFAULT_FORMAT].
     *
     * **Note:**
     * Date and time are stored in the Database in UTC, and presented to the UI in the local time zone.
     *
     * @param formatType [DateTimeFormatType] type of formatting to apply.
     * @param dateTime   (Optional) Date/time string to format.
     * @return Formatted date/time string.
     */
    fun getFormattedDateTime(formatType: DateTimeFormatType, dateTime: String = ""): String {
        val sdFormat = SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault())
        val date: Date?
        val formattedDateTime: String

        when (formatType) {
            DateTimeFormatType.UTC_TO_LOCAL -> {
                try {
                    sdFormat.timeZone = TimeZone.getTimeZone("UTC")
                    date = sdFormat.parse(dateTime)
                    sdFormat.timeZone = TimeZone.getDefault()
                    formattedDateTime = date?.let { sdFormat.format(it) }.toString()
                    return formattedDateTime
                } catch (e: ParseException) {
                    Log.e(TAG, "Error parsing date ${DateTimeFormatType.UTC_TO_LOCAL}", e)
                    return "Error $dateTime"
                }
            }

            DateTimeFormatType.LOCAL_TO_UTC -> {
                try {
                    date = sdFormat.parse(dateTime)
                    sdFormat.timeZone = TimeZone.getTimeZone("UTC")
                    formattedDateTime = date?.let { sdFormat.format(it) }.toString()
                    return formattedDateTime
                } catch (e: ParseException) {
                    Log.e(TAG, "Error parsing date ${DateTimeFormatType.LOCAL_TO_UTC}", e)
                    return "Error $dateTime"
                }
            }

            DateTimeFormatType.CURRENT_UTC -> {
                date = Date()
                sdFormat.timeZone = TimeZone.getTimeZone("UTC")
                formattedDateTime = sdFormat.format(date)
                return formattedDateTime
            }

            DateTimeFormatType.CURRENT_LOCAL -> {
                date = Date()
                return sdFormat.format(date)
            }

            DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT -> {
                try {
                    date = sdFormat.parse(dateTime)
                    sdFormat.applyPattern(MEDIUM_LENGTH_FORMAT)
                    formattedDateTime = date?.let { sdFormat.format(it) }.toString()
                    return formattedDateTime
                } catch (e: ParseException) {
                    Log.e(
                        TAG,
                        "Error parsing date ${DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT}",
                        e
                    )
                    return "Error $dateTime"
                }
            }

            DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT -> {
                try {
                    date = sdFormat.parse(dateTime)
                    sdFormat.applyPattern(SHORT_LENGTH_FORMAT)
                    formattedDateTime = date?.let { sdFormat.format(it) }.toString()
                    return formattedDateTime
                } catch (e: ParseException) {
                    Log.e(
                        TAG,
                        "Error parsing date ${DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT}",
                        e
                    )
                    return "Error $dateTime"
                }
            }

        }
    }
}

