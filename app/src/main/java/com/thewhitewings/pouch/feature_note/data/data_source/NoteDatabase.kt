package com.thewhitewings.pouch.feature_note.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.data.util.Constants.CREATIVE_AND_BOM_DATABASE_VERSION
import com.thewhitewings.pouch.feature_note.data.util.Constants.CREATIVE_DATABASE_NAME

/**
 * Room database for the app.
 */
@Database(
    entities = [Note::class],
    version = CREATIVE_AND_BOM_DATABASE_VERSION,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    /**
     * Get the DAO instance for the database.
     */
    abstract fun noteDao(): NoteDao

    companion object {

        /**
         * Singleton instance of the Creative Zone database.
         */
        @Volatile
        private var CreativeDatabase: NoteDatabase? = null

        /**
         * Singleton instance of the Box of Mysteries (BOM) database.
         */
        @Volatile
        private var BomDatabase: NoteDatabase? = null

        /**
         * Get the singleton instance of the database.
         * @param context      The application context.
         * @param databaseName The name of the database to open based on the current Zone.
         * @return The singleton instance of the database that is related to the current Zone.
         */
        fun getDatabase(
            context: Context,
            databaseName: String
        ): NoteDatabase {

            var currentDatabase: NoteDatabase? =
                if (databaseName == CREATIVE_DATABASE_NAME) CreativeDatabase else BomDatabase

            return currentDatabase ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    databaseName
                )
                    .addMigrations(MIGRATION_3_1, MIGRATION_1_2)
                    .build()
                    .also { currentDatabase = it }
            }
        }
    }
}