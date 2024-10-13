package com.thewhitewings.pouch.feature_note.data.data_source

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.thewhitewings.pouch.feature_note.data.util.Constants

private const val TAG = "DatabaseMigrations"

/**
 * Migration logic from version 1 to version 2 of the database.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // - Create a temporary table with the old schema
        val tempTableName = "${Constants.TABLE_NAME}_temp"
        db.execSQL("CREATE TABLE IF NOT EXISTS $tempTableName AS SELECT * FROM ${Constants.TABLE_NAME}")

        // - Drop the old table
        db.execSQL("DROP TABLE IF EXISTS ${Constants.TABLE_NAME}")

        // - Create the new table with the updated schema
        db.execSQL(
            """CREATE TABLE ${Constants.TABLE_NAME} (
                    ${Constants.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    ${Constants.COLUMN_NOTE_TITLE} TEXT DEFAULT '' NOT NULL, 
                    ${Constants.COLUMN_NOTE_BODY} TEXT DEFAULT '' NOT NULL, 
                    ${Constants.COLUMN_TIMESTAMP} TEXT DEFAULT CURRENT_TIMESTAMP NOT NULL
                    )"""
        )
        Log.d(TAG, "${Constants.TABLE_NAME} table is created")

        // Get column information for the old and new tables
        val oldTableColumns = getTableColumns(db, tempTableName)
        val newTableColumns = getTableColumns(db, Constants.TABLE_NAME)

        // - Handle common columns
        // Find the common columns between old and new tables
        val commonColumns = oldTableColumns.intersect(newTableColumns).toList()

        // Join common columns into a string
        val commonColumnsString = commonColumns.joinToString(",")

        // Copy common columns data from the temp table to the new table
        db.execSQL(
            "INSERT INTO ${Constants.TABLE_NAME} ($commonColumnsString) " +
                    "SELECT $commonColumnsString FROM $tempTableName"
        )

        // - Handle renamed columns (no renamed columns)

        // - Drop the temporary table
        db.execSQL("DROP TABLE IF EXISTS $tempTableName")

        // - Find removed and added columns (for debugging)
//        val removedColumns = oldTableColumns.toMutableList().apply { removeAll(newTableColumns) }
//        val addedColumns = newTableColumns.toMutableList().apply { removeAll(oldTableColumns) }
    }
}

/**
 * Migration logic from version 3 to version 1 of the database.
 */
val MIGRATION_3_1 = object : Migration(3, 1) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // - Create a temporary table with the old schema
        val tempTableName = "${Constants.TABLE_NAME}_temp"
        db.execSQL("CREATE TABLE IF NOT EXISTS $tempTableName AS SELECT * FROM ${Constants.TABLE_NAME_VERSION_3}")

        // - Drop the old table
        db.execSQL("DROP TABLE IF EXISTS ${Constants.TABLE_NAME_VERSION_3}")

        // - Create the new table with the updated schema
        db.execSQL(
            """CREATE TABLE ${Constants.TABLE_NAME} (
                    ${Constants.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    ${Constants.COLUMN_NOTE_TITLE} TEXT DEFAULT '' NOT NULL, 
                    ${Constants.COLUMN_NOTE_BODY} TEXT DEFAULT '' NOT NULL, 
                    ${Constants.COLUMN_TIMESTAMP} TEXT DEFAULT CURRENT_TIMESTAMP NOT NULL
                    )"""
        )
        Log.d(TAG, "${Constants.TABLE_NAME} table is created")

        // Get column information for the old and new tables
        val oldTableColumns = getTableColumns(db, tempTableName)
        val newTableColumns = getTableColumns(db, Constants.TABLE_NAME)

        // - Handle common columns
        // Find the common columns between old and new tables
        val commonColumns = oldTableColumns.intersect(newTableColumns).toList()

        // Join common columns into a string
        val commonColumnsString = commonColumns.joinToString(",")

        // Copy common columns data from the temp table to the new table
        if (commonColumns.isNotEmpty())
            db.execSQL(
                "INSERT INTO ${Constants.TABLE_NAME} ($commonColumnsString) " +
                        "SELECT $commonColumnsString FROM $tempTableName"
            )

        /*
        new:              common:           old:
        id                                  ID
        note_title                          NoteTitle
        note_body                           NoteBody
        timestamp                           Timestamp
         */

        // - Handle renamed columns
        // Handle renamed id column
        if (oldTableColumns.contains(Constants.COLUMN_ID_VERSION_3) &&
            newTableColumns.contains(Constants.COLUMN_ID)
        )
            db.execSQL(
                "INSERT INTO ${Constants.TABLE_NAME} (${Constants.COLUMN_ID}) " +
                        "SELECT ${Constants.COLUMN_ID_VERSION_3} FROM $tempTableName"
            )
        Log.d(TAG, "renamed id column is copied")

        // Iterate over the mapping pairs and copy data of renamed columns (except id column)
        for ((oldColumnName, newColumnName) in renamedColumnMappingsVersion3to1) {
            if (oldTableColumns.contains(oldColumnName) && newTableColumns.contains(newColumnName))
                db.execSQL(
                    "UPDATE ${Constants.TABLE_NAME} " +
                            "SET $newColumnName = $tempTableName.$oldColumnName " +
                            "FROM $tempTableName " +
                            "WHERE ${Constants.TABLE_NAME}.${Constants.COLUMN_ID} = $tempTableName.${Constants.COLUMN_ID_VERSION_3}"
                )
        }
        Log.d(TAG, "renamed columns are copied")

        // - Drop the temporary table
        db.execSQL("DROP TABLE IF EXISTS $tempTableName")

        // - Find removed and added columns (for debugging)
//        val removedColumns = oldTableColumns.toMutableList().apply { removeAll(newTableColumns) }
//        val addedColumns = newTableColumns.toMutableList().apply { removeAll(oldTableColumns) }
    }
}

/**
 * Column name mappings of renamed columns when migrating the database from version 3 to version 1.
 * Map old column name to new column name.
 *
 * **Note:** the id column [Constants.COLUMN_ID_VERSION_3] is handled manually.
 *
 * The key of the map (the first item in the Pair) represents the old column name.
 * The value of the map (the second item in the Pair) represents the new column name.
 */
val renamedColumnMappingsVersion3to1 = mapOf(
    Constants.COLUMN_NOTE_TITLE_VERSION_3 to Constants.COLUMN_NOTE_TITLE,
    Constants.COLUMN_NOTE_BODY_VERSION_3 to Constants.COLUMN_NOTE_BODY,
    Constants.COLUMN_TIMESTAMP_VERSION_3 to Constants.COLUMN_TIMESTAMP
)

/**
 * Get column names for a given table.
 *
 * Note: returned a linked hash set and not a list to improve the performance.
 * @param database The database instance from which the columns are to be retrieved.
 * @param tableName The name of the table for which the columns are to be retrieved.
 * @return A linked hash set containing the column names.
 */
private fun getTableColumns(
    database: SupportSQLiteDatabase,
    tableName: String
): LinkedHashSet<String> {
    val columns = LinkedHashSet<String>()
    val cursor = database.query("PRAGMA table_info($tableName)")
    cursor.use {
        while (cursor.moveToNext()) {
            val columnNameIndex = cursor.getColumnIndex("name")
            if (columnNameIndex != -1)
                columns.add(cursor.getString(columnNameIndex))
        }
    }
    return columns
}