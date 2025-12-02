package ca.unb.mobiledev.cookiestepper.db

import android.content.Context
import androidx.room.Database
import kotlin.jvm.Volatile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.unb.mobiledev.cookiestepper.dao.FoodLogDao
import ca.unb.mobiledev.cookiestepper.dao.StepDao
import ca.unb.mobiledev.cookiestepper.dao.UserProfileDao
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry
import ca.unb.mobiledev.cookiestepper.entities.StepData
import ca.unb.mobiledev.cookiestepper.entities.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [StepData::class, UserProfile::class, FoodLogEntry::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //abstract getter for the data access object
    abstract fun stepDao(): StepDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun foodLogDao(): FoodLogDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {

        // Method to run when the database is first created
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    database.stepDao()
                    database.userProfileDao()
                    database.foodLogDao()
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "step_counter_database"
                ).addCallback(AppDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

