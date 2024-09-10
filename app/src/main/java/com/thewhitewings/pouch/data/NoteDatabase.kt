package com.thewhitewings.pouch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thewhitewings.pouch.utils.Constants.CREATIVE_AND_BOM_DATABASE_VERSION
import com.thewhitewings.pouch.utils.Constants.CREATIVE_DATABASE_NAME

@Database(
    entities = [Note::class],
    version = CREATIVE_AND_BOM_DATABASE_VERSION,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var CreativeDatabase: NoteDatabase? = null

        @Volatile
        private var BomDatabase: NoteDatabase? = null

        fun getDatabase(
            context: Context,
            databaseName: String
        ): NoteDatabase { // database name for a specific zone

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