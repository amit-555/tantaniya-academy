package com.example.data.repository

import android.net.Uri
import com.example.data.dao.StudyProDao
import com.example.data.entity.*
import com.example.data.firestore.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow

object AuthConfig {
    const val PRIMARY_ADMIN_EMAIL = "gangalamit005@gmail.com"
    const val LEGACY_ADMIN_EMAIL = "admin@studypro.in"

    fun isPrimaryAdmin(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return email.trim().equals(PRIMARY_ADMIN_EMAIL, ignoreCase = true)
    }

    fun isAuthorizedAdmin(user: UserEntity?): Boolean {
        if (user == null || !user.isActive) return false
        return user.role.equals("ADMIN", ignoreCase = true)
    }
}

class StudyProRepository(
    private val dao: StudyProDao,
    val syncManager: FirestoreSyncManager? = null
) {

    /**
     * Guarantees that the Primary Admin account exists and is assigned role = "ADMIN".
     * Called on application launch and auth initialization.
     */
    suspend fun ensurePrimaryAdminConfigured() {
        try {
            val primaryEmail = AuthConfig.PRIMARY_ADMIN_EMAIL.lowercase()
            val existing = dao.getUserByEmail(primaryEmail)
            if (existing == null) {
                dao.insertUser(
                    UserEntity(
                        name = "Amit Gangal (Primary Admin)",
                        email = primaryEmail,
                        password = "admin",
                        role = "ADMIN",
                        targetExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)"
                    )
                )
            } else if (existing.role != "ADMIN") {
                dao.updateUser(existing.copy(role = "ADMIN"))
            }
        } catch (_: Exception) {}
    }

    // Users
    suspend fun registerUser(name: String, email: String, pass: String, exam: String): Result<UserEntity> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            val existing = dao.getUserByEmail(normalizedEmail)
            if (existing != null) {
                Result.failure(Exception("આ ઈમેઈલ પર એકાઉન્ટ પહેલેથી જ નોંધાયેલું છે!"))
            } else {
                val assignedRole = if (normalizedEmail == AuthConfig.PRIMARY_ADMIN_EMAIL.lowercase()) "ADMIN" else "USER"
                val newUser = UserEntity(
                    name = name.trim(),
                    email = normalizedEmail,
                    password = pass,
                    role = assignedRole,
                    targetExam = exam.ifBlank { "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)" }
                )
                val id = dao.insertUser(newUser)
                Result.success(newUser.copy(id = id.toInt()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<UserEntity> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            val user = dao.getUserByEmail(normalizedEmail)
            if (user == null) {
                if (normalizedEmail == AuthConfig.PRIMARY_ADMIN_EMAIL.lowercase()) {
                    val newAdmin = UserEntity(
                        name = "Amit Gangal (Primary Admin)",
                        email = normalizedEmail,
                        password = pass,
                        role = "ADMIN",
                        targetExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)"
                    )
                    val id = dao.insertUser(newAdmin)
                    Result.success(newAdmin.copy(id = id.toInt()))
                } else {
                    Result.failure(Exception("વપરાશકર્તા મળ્યા નથી! કૃપા કરીને સાચો ઈમેઈલ નાખો."))
                }
            } else if (user.password != pass) {
                Result.failure(Exception("પાસવર્ડ ખોટો છે!"))
            } else if (!user.isActive) {
                Result.failure(Exception("આ એકાઉન્ટ નિષ્ક્રિય કરેલ છે. એડમિનનો સંપર્ક કરો."))
            } else {
                if (normalizedEmail == AuthConfig.PRIMARY_ADMIN_EMAIL.lowercase() && user.role != "ADMIN") {
                    val elevated = user.copy(role = "ADMIN")
                    dao.updateUser(elevated)
                    Result.success(elevated)
                } else {
                    Result.success(user)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun getUserById(id: Int) = dao.getUserById(id)
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    fun countUsers(): Flow<Int> = dao.countUsers()

    // Exams
    fun getAllExams(): Flow<List<ExamEntity>> = dao.getAllExams()
    fun getPublishedExams(): Flow<List<ExamEntity>> = dao.getPublishedExams()
    suspend fun getExamById(id: Int) = dao.getExamById(id)

    suspend fun insertExam(exam: ExamEntity): Long {
        val id = dao.insertExam(exam)
        syncManager?.pushExam(exam.copy(id = if (exam.id == 0) id.toInt() else exam.id))
        return id
    }

    suspend fun updateExam(exam: ExamEntity) {
        dao.updateExam(exam)
        syncManager?.pushExam(exam)
    }

    suspend fun deleteExam(exam: ExamEntity) {
        dao.deleteExam(exam)
        syncManager?.removeExam(exam.id)
    }

    fun countExams(): Flow<Int> = dao.countExams()

    // Syllabus
    fun getSyllabusByExam(examId: Int): Flow<List<SyllabusEntity>> = dao.getSyllabusByExam(examId)
    fun getAllSyllabus(): Flow<List<SyllabusEntity>> = dao.getAllSyllabus()
    fun getSubjectsForExam(examId: Int): Flow<List<String>> = dao.getSubjectsForExam(examId)
    suspend fun insertSyllabus(item: SyllabusEntity) = dao.insertSyllabus(item)
    suspend fun updateSyllabus(item: SyllabusEntity) = dao.updateSyllabus(item)
    suspend fun deleteSyllabus(id: Int) = dao.deleteSyllabusById(id)

    // Tests
    fun getAllTests(): Flow<List<TestEntity>> = dao.getAllTests()
    fun getPublishedTests(): Flow<List<TestEntity>> = dao.getPublishedTests()
    fun getTestsByExam(examId: Int): Flow<List<TestEntity>> = dao.getTestsByExam(examId)
    fun getPublishedTestsByExam(examId: Int): Flow<List<TestEntity>> = dao.getPublishedTestsByExam(examId)
    suspend fun getTestById(id: Int) = dao.getTestById(id)

    suspend fun insertTest(test: TestEntity): Long {
        val id = dao.insertTest(test)
        syncManager?.pushTest(test.copy(id = if (test.id == 0) id.toInt() else test.id))
        return id
    }

    suspend fun updateTest(test: TestEntity) {
        dao.updateTest(test)
        syncManager?.pushTest(test)
    }

    suspend fun deleteTest(id: Int) {
        dao.deleteTestById(id)
        syncManager?.removeTest(id)
    }

    fun countTests(): Flow<Int> = dao.countTests()

    // Questions
    fun getAllQuestions(): Flow<List<QuestionEntity>> = dao.getAllQuestions()
    suspend fun getQuestionsByExamSync(examId: Int) = dao.getQuestionsByExamSync(examId)
    fun filterQuestions(examId: Int = 0, q: String = "", sub: String = "", diff: String = ""): Flow<List<QuestionEntity>> =
        dao.filterQuestions(examId, q, sub, diff)
    suspend fun getQuestionById(id: Int) = dao.getQuestionById(id)

    suspend fun insertQuestion(q: QuestionEntity): Long {
        val id = dao.insertQuestion(q)
        syncManager?.pushQuestion(q.copy(id = if (q.id == 0) id.toInt() else q.id))
        return id
    }

    suspend fun insertQuestions(list: List<QuestionEntity>): List<Long> {
        val ids = dao.insertQuestions(list)
        for (i in list.indices) {
            val q = list[i]
            val generatedId = if (q.id == 0) ids[i].toInt() else q.id
            syncManager?.pushQuestion(q.copy(id = generatedId))
        }
        return ids
    }

    suspend fun updateQuestion(q: QuestionEntity) {
        dao.updateQuestion(q)
        syncManager?.pushQuestion(q)
    }

    suspend fun deleteQuestion(id: Int) {
        dao.deleteQuestionById(id)
        syncManager?.removeQuestion(id)
    }

    fun countQuestions(): Flow<Int> = dao.countQuestions()

    // Test-Question Mapping
    fun getQuestionsForTest(testId: Int): Flow<List<QuestionEntity>> = dao.getQuestionsForTest(testId)
    suspend fun getQuestionsForTestSync(testId: Int) = dao.getQuestionsForTestSync(testId)
    suspend fun addQuestionToTest(testId: Int, questionId: Int, order: Int = 0) =
        dao.insertTestQuestion(TestQuestionEntity(testId = testId, questionId = questionId, orderIndex = order))
    suspend fun removeQuestionFromTest(testId: Int, questionId: Int) =
        dao.removeQuestionFromTest(testId, questionId)
    suspend fun clearQuestionsFromTest(testId: Int) = dao.clearQuestionsFromTest(testId)
    fun countQuestionsInTest(testId: Int) = dao.countQuestionsInTest(testId)
    suspend fun countQuestionsInTestSync(testId: Int) = dao.countQuestionsInTestSync(testId)

    // Attempts & Results
    suspend fun saveAttempt(attempt: TestAttemptEntity): Long {
        val id = dao.insertAttempt(attempt)
        syncManager?.pushTestAttempt(attempt.copy(id = if (attempt.id == 0) id.toInt() else attempt.id))
        return id
    }

    fun getAttemptsByUser(userId: Int): Flow<List<TestAttemptEntity>> = dao.getAttemptsByUser(userId)
    fun getAllAttempts(): Flow<List<TestAttemptEntity>> = dao.getAllAttempts()
    fun getAttemptsByTest(testId: Int): Flow<List<TestAttemptEntity>> = dao.getAttemptsByTest(testId)
    suspend fun getAttemptById(id: Int) = dao.getAttemptById(id)
    fun countAttempts(): Flow<Int> = dao.countAttempts()
    fun getLeaderboard(limit: Int = 50): Flow<List<TestAttemptEntity>> = dao.getLeaderboard(limit)

    // Current Affairs
    fun getAllCurrentAffairs(): Flow<List<CurrentAffairsEntity>> = dao.getAllCurrentAffairs()
    fun getPublishedCurrentAffairs(): Flow<List<CurrentAffairsEntity>> = dao.getPublishedCurrentAffairs()
    fun getCurrentAffairsByCategory(category: String, includeUnpublished: Boolean = false): Flow<List<CurrentAffairsEntity>> =
        dao.getCurrentAffairsByCategory(category, includeUnpublished)

    suspend fun insertCurrentAffairs(item: CurrentAffairsEntity): Long {
        val id = dao.insertCurrentAffairs(item)
        syncManager?.pushCurrentAffairs(item.copy(id = if (item.id == 0) id.toInt() else item.id))
        return id
    }

    suspend fun updateCurrentAffairs(item: CurrentAffairsEntity) {
        dao.updateCurrentAffairs(item)
        syncManager?.pushCurrentAffairs(item)
    }

    suspend fun deleteCurrentAffairs(id: Int) {
        dao.deleteCurrentAffairsById(id)
        syncManager?.removeCurrentAffairs(id)
    }

    // GK Items
    fun getAllGkItems(): Flow<List<GkItemEntity>> = dao.getAllGkItems()
    fun getPublishedGkItems(): Flow<List<GkItemEntity>> = dao.getPublishedGkItems()
    fun getGkItemsByCategory(cat: String, includeUnpublished: Boolean = false): Flow<List<GkItemEntity>> =
        dao.getGkItemsByCategory(cat, includeUnpublished)

    suspend fun insertGkItem(item: GkItemEntity): Long {
        val id = dao.insertGkItem(item)
        syncManager?.pushGkItem(item.copy(id = if (item.id == 0) id.toInt() else item.id))
        return id
    }

    suspend fun updateGkItem(item: GkItemEntity) {
        dao.updateGkItem(item)
        syncManager?.pushGkItem(item)
    }

    suspend fun deleteGkItem(id: Int) {
        dao.deleteGkItemById(id)
        syncManager?.removeGkItem(id)
    }

    // Study Materials
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>> = dao.getAllStudyMaterials()
    fun getPublishedStudyMaterials(): Flow<List<StudyMaterialEntity>> = dao.getPublishedStudyMaterials()
    fun getStudyMaterialsBySubject(sub: String, includeUnpublished: Boolean = false): Flow<List<StudyMaterialEntity>> =
        dao.getStudyMaterialsBySubject(sub, includeUnpublished)

    suspend fun insertStudyMaterial(item: StudyMaterialEntity): Long {
        val id = dao.insertStudyMaterial(item)
        syncManager?.pushStudyMaterial(item.copy(id = if (item.id == 0) id.toInt() else item.id))
        return id
    }

    suspend fun updateStudyMaterial(item: StudyMaterialEntity) {
        dao.updateStudyMaterial(item)
        syncManager?.pushStudyMaterial(item)
    }

    suspend fun deleteStudyMaterial(id: Int) {
        dao.deleteStudyMaterialById(id)
        syncManager?.removeStudyMaterial(id)
    }

    // Image Library
    fun getAllImageLibraryItems(): Flow<List<ImageLibraryEntity>> = dao.getAllImageLibraryItems()
    fun getPublishedImageLibraryItems(): Flow<List<ImageLibraryEntity>> = dao.getPublishedImageLibraryItems()

    suspend fun insertImageLibraryItem(item: ImageLibraryEntity): Long {
        val id = dao.insertImageLibraryItem(item)
        syncManager?.pushImageLibraryItem(item.copy(id = if (item.id == 0) id.toInt() else item.id))
        return id
    }

    suspend fun deleteImageLibraryItem(id: Int) {
        dao.deleteImageLibraryItemById(id)
        syncManager?.removeImageLibraryItem(id)
    }

    // Media Upload
    suspend fun uploadMedia(uri: Uri, folder: String, isPdf: Boolean = false): String {
        return syncManager?.uploadMedia(uri, folder, isPdf) ?: ""
    }

    // Settings
    suspend fun setSetting(key: String, value: String) = dao.setSetting(AppSettingEntity(key, value))
    suspend fun getSetting(key: String) = dao.getSetting(key)
    fun getAllSettings() = dao.getAllSettings()
}
