package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.ai.GeneratedMcq
import com.example.data.entity.*
import com.example.data.repository.StudyProRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: StudyProRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : ViewModel() {

    // --- EXAMS ---
    val allExams: StateFlow<List<ExamEntity>> = repository.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedExams: StateFlow<List<ExamEntity>> = repository.getPublishedExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedExamId = MutableStateFlow(1)
    val selectedExamId: StateFlow<Int> = _selectedExamId.asStateFlow()

    val selectedExam: StateFlow<ExamEntity?> = combine(_selectedExamId, allExams) { id, exams ->
        exams.find { it.id == id } ?: exams.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- TESTS ---
    val allTests: StateFlow<List<TestEntity>> = repository.getAllTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedTests: StateFlow<List<TestEntity>> = repository.getPublishedTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically filtered tests for current exam selected on user dashboard
    val publishedTestsForSelectedExam: StateFlow<List<TestEntity>> = combine(
        _selectedExamId,
        publishedTests
    ) { examId, tests ->
        val filtered = tests.filter { it.examId == examId }
        if (filtered.isEmpty() && tests.isNotEmpty() && examId == 1) tests else filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- QUESTION BANK ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterExamId = MutableStateFlow(0)
    val filterExamId: StateFlow<Int> = _filterExamId.asStateFlow()

    private val _filterSubject = MutableStateFlow("")
    val filterSubject: StateFlow<String> = _filterSubject.asStateFlow()

    private val _filterDifficulty = MutableStateFlow("")
    val filterDifficulty: StateFlow<String> = _filterDifficulty.asStateFlow()

    val filteredQuestions: StateFlow<List<QuestionEntity>> = combine(
        _filterExamId,
        _searchQuery,
        _filterSubject,
        _filterDifficulty
    ) { examId, q, sub, diff ->
        QueryFilter(examId, q, sub, diff)
    }.flatMapLatest { filter ->
        repository.filterQuestions(filter.examId, filter.q, filter.sub, filter.diff)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class QueryFilter(val examId: Int, val q: String, val sub: String, val diff: String)

    // --- SYLLABUS ---
    val currentSyllabus: StateFlow<List<SyllabusEntity>> = _selectedExamId
        .flatMapLatest { examId -> repository.getSyllabusByExam(examId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- CURRENT AFFAIRS CMS ---
    private val _caCategory = MutableStateFlow("")
    val caCategory: StateFlow<String> = _caCategory.asStateFlow()

    // Users see published current affairs
    val currentAffairsList: StateFlow<List<CurrentAffairsEntity>> = _caCategory
        .flatMapLatest { cat ->
            if (cat.isBlank()) repository.getPublishedCurrentAffairs()
            else repository.getCurrentAffairsByCategory(cat, includeUnpublished = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin sees all current affairs including drafts
    val allCurrentAffairsAdmin: StateFlow<List<CurrentAffairsEntity>> = repository.getAllCurrentAffairs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- GK ITEMS CMS ---
    private val _gkCategory = MutableStateFlow("")
    val gkCategory: StateFlow<String> = _gkCategory.asStateFlow()

    val gkList: StateFlow<List<GkItemEntity>> = _gkCategory
        .flatMapLatest { cat ->
            if (cat.isBlank()) repository.getPublishedGkItems()
            else repository.getGkItemsByCategory(cat, includeUnpublished = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGkItemsAdmin: StateFlow<List<GkItemEntity>> = repository.getAllGkItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- STUDY MATERIALS ---
    private val _materialSubject = MutableStateFlow("")
    val materialSubject: StateFlow<String> = _materialSubject.asStateFlow()

    val studyMaterials: StateFlow<List<StudyMaterialEntity>> = _materialSubject
        .flatMapLatest { sub ->
            if (sub.isBlank()) repository.getPublishedStudyMaterials()
            else repository.getStudyMaterialsBySubject(sub, includeUnpublished = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyMaterialsAdmin: StateFlow<List<StudyMaterialEntity>> = repository.getAllStudyMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- LEADERBOARD & ATTEMPTS ---
    val leaderboard: StateFlow<List<TestAttemptEntity>> = repository.getLeaderboard(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<TestAttemptEntity>> = repository.getAllAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- USERS (FOR ADMIN) ---
    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- METRICS FOR ADMIN DASHBOARD ---
    val totalExamsCount: StateFlow<Int> = repository.countExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalUsersCount: StateFlow<Int> = repository.countUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTestsCount: StateFlow<Int> = repository.countTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalQuestionsCount: StateFlow<Int> = repository.countQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalAttemptsCount: StateFlow<Int> = repository.countAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User's own attempts
    fun getUserAttempts(userId: Int): Flow<List<TestAttemptEntity>> = repository.getAttemptsByUser(userId)
    suspend fun getAttemptById(id: Int): TestAttemptEntity? = repository.getAttemptById(id)
    suspend fun getQuestionsForTestSync(testId: Int): List<QuestionEntity> = repository.getQuestionsForTestSync(testId)
    suspend fun countQuestionsInTestSync(testId: Int): Int = repository.countQuestionsInTestSync(testId)

    // AI Generation State
    private val _aiGeneratedQuestions = MutableStateFlow<List<GeneratedMcq>>(emptyList())
    val aiGeneratedQuestions: StateFlow<List<GeneratedMcq>> = _aiGeneratedQuestions.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow<String?>(null)
    val aiStatusMessage: StateFlow<String?> = _aiStatusMessage.asStateFlow()

    private val _geminiApiKey = MutableStateFlow("")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    init {
        viewModelScope.launch {
            val key = repository.getSetting("gemini_api_key") ?: ""
            _geminiApiKey.value = key
        }
    }

    fun setGeminiApiKey(key: String) {
        _geminiApiKey.value = key
        viewModelScope.launch {
            repository.setSetting("gemini_api_key", key)
        }
    }

    fun setSelectedExam(id: Int) {
        _selectedExamId.value = id
    }

    fun setQuestionSearch(query: String) {
        _searchQuery.value = query
    }

    fun setQuestionExamFilter(examId: Int) {
        _filterExamId.value = examId
    }

    fun setQuestionSubjectFilter(subject: String) {
        _filterSubject.value = subject
    }

    fun setQuestionDifficultyFilter(diff: String) {
        _filterDifficulty.value = diff
    }

    fun setCaCategory(cat: String) {
        _caCategory.value = cat
    }

    fun setGkCategory(cat: String) {
        _gkCategory.value = cat
    }

    fun setMaterialSubject(sub: String) {
        _materialSubject.value = sub
    }

    // --- AI GENERATION ACTIONS ---
    fun generateAiQuestions(
        examTitle: String = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
        partName: String = "ભાગ-બ (Part B)",
        subject: String,
        topic: String,
        subtopic: String = "",
        difficulty: String,
        count: Int,
        imageBitmap: Bitmap? = null,
        extractedText: String? = null
    ) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            _aiStatusMessage.value = "AI પ્રશ્નો તૈયાર કરી રહ્યું છે..."
            val result = aiService.generateQuestions(
                apiKey = _geminiApiKey.value,
                examTitle = examTitle,
                partName = partName,
                subject = subject,
                topic = topic,
                subtopic = subtopic,
                difficulty = difficulty,
                count = count,
                imageBitmap = imageBitmap,
                extractedText = extractedText
            )
            _isAiGenerating.value = false
            result.onSuccess { list ->
                if (list.isEmpty()) {
                    _aiStatusMessage.value = "આ Topic માટે પૂરતી માહિતી મળી નથી. કૃપા કરીને યોગ્ય Study Material અપલોડ કરો."
                } else {
                    _aiGeneratedQuestions.value = list
                    _aiStatusMessage.value = "સફળતા! '${topic}' ટોપિકના ${list.size} ચોક્કસ પ્રશ્નો સફળતાપૂર્વક તૈયાર થયા."
                }
            }.onFailure { err ->
                _aiStatusMessage.value = "પ્રશ્નો બનાવવામાં સમસ્યા આવી. કૃપા કરીને ફરી પ્રયાસ કરો."
            }
        }
    }

    fun toggleGeneratedSelection(index: Int) {
        val current = _aiGeneratedQuestions.value.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            current[index] = item.copy(isSelected = !item.isSelected)
            _aiGeneratedQuestions.value = current
        }
    }

    fun updateGeneratedQuestion(index: Int, updated: GeneratedMcq) {
        val current = _aiGeneratedQuestions.value.toMutableList()
        if (index in current.indices) {
            current[index] = updated
            _aiGeneratedQuestions.value = current
        }
    }

    fun deleteGeneratedQuestion(index: Int) {
        val current = _aiGeneratedQuestions.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _aiGeneratedQuestions.value = current
        }
    }

    fun saveGeneratedToQuestionBank(targetTestId: Int? = null, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val selected = _aiGeneratedQuestions.value.filter { it.isSelected }
            if (selected.isEmpty()) return@launch

            val entities = selected.map { it.toQuestionEntity(examId = _selectedExamId.value) }
            val ids = repository.insertQuestions(entities)

            if (targetTestId != null) {
                ids.forEachIndexed { idx, qId ->
                    repository.addQuestionToTest(targetTestId, qId.toInt(), idx)
                }
            }
            _aiGeneratedQuestions.value = emptyList()
            _aiStatusMessage.value = "${selected.size} પ્રશ્નો સફળતાપૂર્વક સેવ કરવામાં આવ્યા!"
            onComplete(selected.size)
        }
    }

    // --- DYNAMIC EXAMS CRUD ---
    fun createExam(
        title: String,
        code: String,
        desc: String,
        marks: Int = 200,
        duration: Int = 180,
        negMark: Double = 0.25,
        isPublished: Boolean = true
    ) {
        viewModelScope.launch {
            repository.insertExam(
                ExamEntity(
                    title = title.trim(),
                    code = code.trim().uppercase(),
                    description = desc.trim(),
                    totalMarks = marks,
                    durationMinutes = duration,
                    negativeMarking = negMark,
                    isPublished = isPublished
                )
            )
        }
    }

    fun updateExam(exam: ExamEntity) {
        viewModelScope.launch { repository.updateExam(exam) }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }

    // --- COMPLETE TEST BUILDER CRUD ---
    fun createCompleteTest(
        examId: Int,
        title: String,
        desc: String,
        duration: Int,
        totalQuestions: Int,
        totalMarks: Int,
        negativeMarking: Double,
        passingMarks: Double,
        instructions: String,
        isPublished: Boolean
    ) {
        viewModelScope.launch {
            repository.insertTest(
                TestEntity(
                    examId = examId,
                    title = title.trim(),
                    description = desc.trim(),
                    durationMinutes = duration,
                    totalQuestions = totalQuestions,
                    totalMarks = totalMarks,
                    negativeMarking = negativeMarking,
                    passingMarks = passingMarks,
                    instructions = instructions.ifBlank { "૧. દરેક સાચા જવાબ માટે નિયત ગુણ મળશે.\n૨. દરેક ખોટા જવાબ માટે નેગેટિવ માર્કિંગ કપાશે." },
                    isPublished = isPublished
                )
            )
        }
    }

    fun updateTest(test: TestEntity) {
        viewModelScope.launch { repository.updateTest(test) }
    }

    fun deleteTest(testId: Int) {
        viewModelScope.launch { repository.deleteTest(testId) }
    }

    // --- QUESTIONS & TEST-QUESTION MAPPING ---
    fun addQuestionManually(q: QuestionEntity, testId: Int? = null) {
        viewModelScope.launch {
            val id = repository.insertQuestion(q)
            if (testId != null) {
                repository.addQuestionToTest(testId, id.toInt())
            }
        }
    }

    fun updateQuestion(q: QuestionEntity) {
        viewModelScope.launch { repository.updateQuestion(q) }
    }

    fun deleteQuestion(qId: Int) {
        viewModelScope.launch { repository.deleteQuestion(qId) }
    }

    fun addQuestionToTest(testId: Int, questionId: Int) {
        viewModelScope.launch { repository.addQuestionToTest(testId, questionId) }
    }

    fun batchAddQuestionsToTest(testId: Int, questionIds: List<Int>) {
        viewModelScope.launch {
            questionIds.forEachIndexed { idx, qId ->
                repository.addQuestionToTest(testId, qId, idx)
            }
        }
    }

    fun removeQuestionFromTest(testId: Int, questionId: Int) {
        viewModelScope.launch { repository.removeQuestionFromTest(testId, questionId) }
    }

    fun populateDistributedQuestionsForTest(testId: Int, examId: Int, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val existing = repository.getQuestionsForTestSync(testId)
            val existingIds = existing.map { it.id }.toSet()
            val available = repository.getQuestionsByExamSync(examId)
            val pool = if (available.isNotEmpty()) available else repository.getQuestionsByExamSync(1)

            var added = 0
            pool.forEachIndexed { index, q ->
                if (!existingIds.contains(q.id)) {
                    repository.addQuestionToTest(testId, q.id, existing.size + index)
                    added++
                }
            }
            onComplete(added)
        }
    }

    fun getQuestionsForTest(testId: Int): Flow<List<QuestionEntity>> = repository.getQuestionsForTest(testId)

    // --- CURRENT AFFAIRS CMS CRUD ---
    fun addCurrentAffairs(item: CurrentAffairsEntity) {
        viewModelScope.launch { repository.insertCurrentAffairs(item) }
    }

    fun updateCurrentAffairs(item: CurrentAffairsEntity) {
        viewModelScope.launch { repository.updateCurrentAffairs(item) }
    }

    fun deleteCurrentAffairs(id: Int) {
        viewModelScope.launch { repository.deleteCurrentAffairs(id) }
    }

    // --- GK ITEMS CMS CRUD ---
    fun addGkItem(item: GkItemEntity) {
        viewModelScope.launch { repository.insertGkItem(item) }
    }

    fun updateGkItem(item: GkItemEntity) {
        viewModelScope.launch { repository.updateGkItem(item) }
    }

    fun deleteGkItem(id: Int) {
        viewModelScope.launch { repository.deleteGkItem(id) }
    }

    // --- STUDY MATERIAL CMS CRUD ---
    fun addStudyMaterial(item: StudyMaterialEntity) {
        viewModelScope.launch { repository.insertStudyMaterial(item) }
    }

    fun updateStudyMaterial(item: StudyMaterialEntity) {
        viewModelScope.launch { repository.updateStudyMaterial(item) }
    }

    fun deleteStudyMaterial(id: Int) {
        viewModelScope.launch { repository.deleteStudyMaterial(id) }
    }

    // --- SYLLABUS CMS CRUD ---
    fun addSyllabus(item: SyllabusEntity) {
        viewModelScope.launch { repository.insertSyllabus(item) }
    }

    fun updateSyllabus(item: SyllabusEntity) {
        viewModelScope.launch { repository.updateSyllabus(item) }
    }

    fun deleteSyllabus(id: Int) {
        viewModelScope.launch { repository.deleteSyllabus(id) }
    }

    // --- USER MANAGEMENT ---
    fun toggleUserStatus(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user.copy(isActive = !user.isActive))
        }
    }

    fun toggleUserRole(user: UserEntity) {
        viewModelScope.launch {
            val newRole = if (user.role == "ADMIN") "USER" else "ADMIN"
            repository.updateUser(user.copy(role = newRole))
        }
    }
}
