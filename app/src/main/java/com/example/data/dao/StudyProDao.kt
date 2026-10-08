package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyProDao {
    // --- USERS ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    fun countUsers(): Flow<Int>

    // --- EXAMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("SELECT * FROM exams ORDER BY id ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE isPublished = 1 ORDER BY id ASC")
    fun getPublishedExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: Int): ExamEntity?

    @Query("SELECT COUNT(*) FROM exams")
    fun countExams(): Flow<Int>

    // --- SYLLABUS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabus(item: SyllabusEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusList(items: List<SyllabusEntity>)

    @Update
    suspend fun updateSyllabus(item: SyllabusEntity)

    @Query("DELETE FROM syllabus WHERE id = :id")
    suspend fun deleteSyllabusById(id: Int)

    @Query("SELECT * FROM syllabus WHERE examId = :examId ORDER BY partName ASC, subjectName ASC")
    fun getSyllabusByExam(examId: Int): Flow<List<SyllabusEntity>>

    @Query("SELECT * FROM syllabus ORDER BY examId ASC, partName ASC")
    fun getAllSyllabus(): Flow<List<SyllabusEntity>>

    @Query("SELECT DISTINCT subjectName FROM syllabus WHERE examId = :examId")
    fun getSubjectsForExam(examId: Int): Flow<List<String>>

    // --- TESTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    @Update
    suspend fun updateTest(test: TestEntity)

    @Query("DELETE FROM tests WHERE id = :id")
    suspend fun deleteTestById(id: Int)

    @Query("SELECT * FROM tests ORDER BY id DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE examId = :examId ORDER BY id DESC")
    fun getTestsByExam(examId: Int): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE examId = :examId AND isPublished = 1 ORDER BY id DESC")
    fun getPublishedTestsByExam(examId: Int): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: Int): TestEntity?

    @Query("SELECT COUNT(*) FROM tests")
    fun countTests(): Flow<Int>

    // --- QUESTIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long>

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Int)

    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Int): QuestionEntity?

    @Query("SELECT COUNT(*) FROM questions")
    fun countQuestions(): Flow<Int>

    @Query("""
        SELECT * FROM questions 
        WHERE (:examId = 0 OR examId = :examId)
        AND (:subject = '' OR subject = :subject)
        AND (:difficulty = '' OR difficulty = :difficulty)
        AND (questionText LIKE '%' || :query || '%' OR topic LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun filterQuestions(examId: Int, query: String, subject: String, difficulty: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE (:examId = 0 OR examId = :examId) ORDER BY id ASC")
    suspend fun getQuestionsByExamSync(examId: Int): List<QuestionEntity>

    // --- TEST - QUESTION MAPPING ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestQuestion(item: TestQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestQuestions(items: List<TestQuestionEntity>)

    @Query("DELETE FROM test_questions WHERE testId = :testId AND questionId = :questionId")
    suspend fun removeQuestionFromTest(testId: Int, questionId: Int)

    @Query("DELETE FROM test_questions WHERE testId = :testId")
    suspend fun clearQuestionsFromTest(testId: Int)

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN test_questions tq ON q.id = tq.questionId
        WHERE tq.testId = :testId
        ORDER BY tq.orderIndex ASC, q.id ASC
    """)
    fun getQuestionsForTest(testId: Int): Flow<List<QuestionEntity>>

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN test_questions tq ON q.id = tq.questionId
        WHERE tq.testId = :testId
        ORDER BY tq.orderIndex ASC, q.id ASC
    """)
    suspend fun getQuestionsForTestSync(testId: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM test_questions WHERE testId = :testId")
    fun countQuestionsInTest(testId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM test_questions WHERE testId = :testId")
    suspend fun countQuestionsInTestSync(testId: Int): Int

    // --- ATTEMPTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: TestAttemptEntity): Long

    @Query("SELECT * FROM test_attempts WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getAttemptsByUser(userId: Int): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts ORDER BY submittedAt DESC")
    fun getAllAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE testId = :testId ORDER BY submittedAt DESC")
    fun getAttemptsByTest(testId: Int): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE id = :id LIMIT 1")
    suspend fun getAttemptById(id: Int): TestAttemptEntity?

    @Query("SELECT COUNT(*) FROM test_attempts")
    fun countAttempts(): Flow<Int>

    @Query("SELECT * FROM test_attempts ORDER BY finalScore DESC, percentage DESC, timeUsedSeconds ASC LIMIT :limit")
    fun getLeaderboard(limit: Int = 50): Flow<List<TestAttemptEntity>>

    // --- CURRENT AFFAIRS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAffairs(item: CurrentAffairsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAffairsList(items: List<CurrentAffairsEntity>)

    @Update
    suspend fun updateCurrentAffairs(item: CurrentAffairsEntity)

    @Query("DELETE FROM current_affairs WHERE id = :id")
    suspend fun deleteCurrentAffairsById(id: Int)

    @Query("SELECT * FROM current_affairs ORDER BY id DESC")
    fun getAllCurrentAffairs(): Flow<List<CurrentAffairsEntity>>

    @Query("SELECT * FROM current_affairs WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedCurrentAffairs(): Flow<List<CurrentAffairsEntity>>

    @Query("SELECT * FROM current_affairs WHERE (:category = '' OR category = :category) AND (isPublished = 1 OR :includeUnpublished = 1) ORDER BY id DESC")
    fun getCurrentAffairsByCategory(category: String, includeUnpublished: Boolean = false): Flow<List<CurrentAffairsEntity>>

    // --- GK ITEMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGkItem(item: GkItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGkItemsList(items: List<GkItemEntity>)

    @Update
    suspend fun updateGkItem(item: GkItemEntity)

    @Query("DELETE FROM gk_items WHERE id = :id")
    suspend fun deleteGkItemById(id: Int)

    @Query("SELECT * FROM gk_items ORDER BY id DESC")
    fun getAllGkItems(): Flow<List<GkItemEntity>>

    @Query("SELECT * FROM gk_items WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedGkItems(): Flow<List<GkItemEntity>>

    @Query("SELECT * FROM gk_items WHERE (:category = '' OR category = :category) AND (isPublished = 1 OR :includeUnpublished = 1) ORDER BY id DESC")
    fun getGkItemsByCategory(category: String, includeUnpublished: Boolean = false): Flow<List<GkItemEntity>>

    // --- STUDY MATERIALS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(item: StudyMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterialsList(items: List<StudyMaterialEntity>)

    @Update
    suspend fun updateStudyMaterial(item: StudyMaterialEntity)

    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteStudyMaterialById(id: Int)

    @Query("SELECT * FROM study_materials ORDER BY id DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE (:subject = '' OR subject = :subject) AND (isPublished = 1 OR :includeUnpublished = 1) ORDER BY id DESC")
    fun getStudyMaterialsBySubject(subject: String, includeUnpublished: Boolean = false): Flow<List<StudyMaterialEntity>>

    // --- IMAGE LIBRARY ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageLibraryItem(item: ImageLibraryEntity): Long

    @Query("DELETE FROM image_library WHERE id = :id")
    suspend fun deleteImageLibraryItemById(id: Int)

    @Query("SELECT * FROM image_library ORDER BY id DESC")
    fun getAllImageLibraryItems(): Flow<List<ImageLibraryEntity>>

    @Query("SELECT * FROM image_library WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedImageLibraryItems(): Flow<List<ImageLibraryEntity>>

    // --- SETTINGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingEntity)

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettingEntity>>
}
