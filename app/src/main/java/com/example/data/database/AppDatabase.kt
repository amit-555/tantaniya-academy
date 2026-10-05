package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.StudyProDao
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ExamEntity::class,
        SyllabusEntity::class,
        TestEntity::class,
        QuestionEntity::class,
        TestQuestionEntity::class,
        TestAttemptEntity::class,
        CurrentAffairsEntity::class,
        GkItemEntity::class,
        StudyMaterialEntity::class,
        AppSettingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyProDao(): StudyProDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studypro_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseInitialData.prepopulate(database.studyProDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Also check if users or exams exist in case of initial empty DB
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        try {
                            val user = database.studyProDao().getUserById(1)
                            if (user == null) {
                                DatabaseInitialData.prepopulate(database.studyProDao())
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }
}
