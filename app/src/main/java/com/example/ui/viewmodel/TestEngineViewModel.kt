package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.QuestionEntity
import com.example.data.entity.TestAttemptEntity
import com.example.data.entity.TestEntity
import com.example.data.repository.StudyProRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class QuestionStatus {
    NOT_VISITED,
    NOT_ANSWERED,
    ANSWERED,
    MARKED_FOR_REVIEW,
    ANSWERED_AND_MARKED
}

data class TestEngineUiState(
    val test: TestEntity? = null,
    val questions: List<QuestionEntity> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, String> = emptyMap(), // questionId -> "A"|"B"|"C"|"D"
    val markedForReview: Set<Int> = emptySet(), // questionIds
    val visitedQuestions: Set<Int> = emptySet(),
    val selectedSection: String = "બધા વિભાગો",
    val remainingSeconds: Long = 0,
    val totalTimeSeconds: Long = 0,
    val isTimerRunning: Boolean = false,
    val timerWarningMessage: String? = null,
    val isSubmitted: Boolean = false,
    val submissionResult: TestAttemptEntity? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val currentQuestion: QuestionEntity?
        get() = questions.getOrNull(currentQuestionIndex)

    val answeredCount: Int
        get() = userAnswers.size

    val markedCount: Int
        get() = markedForReview.size

    val answeredAndMarkedCount: Int
        get() = userAnswers.keys.count { markedForReview.contains(it) }

    val unattemptedCount: Int
        get() = (questions.size - userAnswers.size).coerceAtLeast(0)

    val notVisitedCount: Int
        get() = (questions.size - visitedQuestions.size).coerceAtLeast(0)

    val sections: List<String>
        get() {
            val list = mutableListOf("બધા વિભાગો")
            val distinctSubjects = questions.map { it.subject }.distinct()
            list.addAll(distinctSubjects)
            return list
        }

    fun getStatusForQuestion(qId: Int): QuestionStatus {
        val isAnswered = userAnswers.containsKey(qId)
        val isMarked = markedForReview.contains(qId)
        val isVisited = visitedQuestions.contains(qId)

        return when {
            isAnswered && isMarked -> QuestionStatus.ANSWERED_AND_MARKED
            isAnswered -> QuestionStatus.ANSWERED
            isMarked -> QuestionStatus.MARKED_FOR_REVIEW
            isVisited -> QuestionStatus.NOT_ANSWERED
            else -> QuestionStatus.NOT_VISITED
        }
    }

    val formattedRemainingTime: String
        get() {
            val hours = remainingSeconds / 3600
            val minutes = (remainingSeconds % 3600) / 60
            val seconds = remainingSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
}

class TestEngineViewModel(
    private val repository: StudyProRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestEngineUiState())
    val uiState: StateFlow<TestEngineUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var warned10Min = false
    private var warned5Min = false
    private var warned1Min = false

    fun loadTest(testId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val test = repository.getTestById(testId)
                if (test == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "ટેસ્ટ મળ્યો નથી.")
                    return@launch
                }
                var questions = repository.getQuestionsForTestSync(testId)
                if (questions.isEmpty()) {
                    questions = repository.getQuestionsForTestSync(1)
                }

                val durationSec = (test.durationMinutes * 60).toLong().coerceAtLeast(60L)
                val initialVisited = if (questions.isNotEmpty()) setOf(questions[0].id) else emptySet()

                warned10Min = false
                warned5Min = false
                warned1Min = false

                _uiState.value = TestEngineUiState(
                    test = test,
                    questions = questions,
                    remainingSeconds = durationSec,
                    totalTimeSeconds = durationSec,
                    visitedQuestions = initialVisited,
                    isLoading = false
                )

                startTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTimerRunning = true)
            while (_uiState.value.remainingSeconds > 0 && !_uiState.value.isSubmitted) {
                delay(1000)
                val newRemaining = _uiState.value.remainingSeconds - 1
                var warning: String? = null

                if (newRemaining in 590..600 && !warned10Min) {
                    warned10Min = true
                    warning = "ચેતવણી: માત્ર 10 મિનિટ બાકી છે!"
                } else if (newRemaining in 290..300 && !warned5Min) {
                    warned5Min = true
                    warning = "ધ્યાન આપો: માત્ર 5 મિનિટ બાકી છે!"
                } else if (newRemaining in 55..60 && !warned1Min) {
                    warned1Min = true
                    warning = "અંતિમ ચેતવણી: માત્ર 1 મિનિટ બાકી છે! જવાબો તપાસી લો."
                }

                _uiState.value = _uiState.value.copy(
                    remainingSeconds = newRemaining,
                    timerWarningMessage = warning ?: _uiState.value.timerWarningMessage
                )
            }
            if (_uiState.value.remainingSeconds <= 0 && !_uiState.value.isSubmitted) {
                submitTest(auto = true)
            }
        }
    }

    fun dismissTimerWarning() {
        _uiState.value = _uiState.value.copy(timerWarningMessage = null)
    }

    fun selectOption(option: String) {
        val q = _uiState.value.currentQuestion ?: return
        val currentAnswers = _uiState.value.userAnswers.toMutableMap()
        currentAnswers[q.id] = option
        _uiState.value = _uiState.value.copy(userAnswers = currentAnswers)
    }

    fun clearAnswer() {
        val q = _uiState.value.currentQuestion ?: return
        val currentAnswers = _uiState.value.userAnswers.toMutableMap()
        currentAnswers.remove(q.id)
        _uiState.value = _uiState.value.copy(userAnswers = currentAnswers)
    }

    fun saveAndNext() {
        goToNextQuestion()
    }

    fun markForReviewAndNext() {
        val q = _uiState.value.currentQuestion ?: return
        val currentMarked = _uiState.value.markedForReview.toMutableSet()
        currentMarked.add(q.id)
        _uiState.value = _uiState.value.copy(markedForReview = currentMarked)
        goToNextQuestion()
    }

    fun toggleMarkForReview() {
        val q = _uiState.value.currentQuestion ?: return
        val currentMarked = _uiState.value.markedForReview.toMutableSet()
        if (currentMarked.contains(q.id)) {
            currentMarked.remove(q.id)
        } else {
            currentMarked.add(q.id)
        }
        _uiState.value = _uiState.value.copy(markedForReview = currentMarked)
    }

    fun selectSection(sectionName: String) {
        _uiState.value = _uiState.value.copy(selectedSection = sectionName)
        if (sectionName != "બધા વિભાગો") {
            val firstInSec = _uiState.value.questions.indexOfFirst { it.subject == sectionName }
            if (firstInSec >= 0) {
                goToQuestion(firstInSec)
            }
        }
    }

    fun goToNextQuestion() {
        val nextIdx = _uiState.value.currentQuestionIndex + 1
        if (nextIdx < _uiState.value.questions.size) {
            goToQuestion(nextIdx)
        }
    }

    fun goToPreviousQuestion() {
        val prevIdx = _uiState.value.currentQuestionIndex - 1
        if (prevIdx >= 0) {
            goToQuestion(prevIdx)
        }
    }

    fun goToQuestion(index: Int) {
        if (index in _uiState.value.questions.indices) {
            val q = _uiState.value.questions[index]
            val visited = _uiState.value.visitedQuestions + q.id
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = index,
                visitedQuestions = visited
            )
        }
    }

    fun submitTest(userId: Int = 1, userName: String = "વિદ્યાર્થી", auto: Boolean = false) {
        if (_uiState.value.isSubmitted) return
        timerJob?.cancel()

        viewModelScope.launch {
            val state = _uiState.value
            val test = state.test ?: return@launch
            val questions = state.questions

            var correctCount = 0
            var incorrectCount = 0
            val answersJsonObj = JSONObject()
            val subjectPerformanceObj = JSONObject()

            // Subject wise stats map
            // subject -> [total, correct, incorrect]
            val subjectStats = mutableMapOf<String, IntArray>()

            val negPerWrong = test.negativeMarking

            for (q in questions) {
                val stats = subjectStats.getOrPut(q.subject) { IntArray(3) }
                stats[0]++ // total in subject

                val selected = state.userAnswers[q.id]
                if (selected != null) {
                    answersJsonObj.put(q.id.toString(), selected)
                    val isCorrect = selected.equals(q.correctOption, ignoreCase = true)
                    if (isCorrect) {
                        correctCount++
                        stats[1]++ // correct in subject
                    } else {
                        incorrectCount++
                        stats[2]++ // incorrect in subject
                    }
                }
            }

            for ((sub, arr) in subjectStats) {
                val subObj = JSONObject()
                subObj.put("total", arr[0])
                subObj.put("correct", arr[1])
                subObj.put("incorrect", arr[2])
                subjectPerformanceObj.put(sub, subObj)
            }

            val attempted = correctCount + incorrectCount
            val unattempted = questions.size - attempted

            val positiveMarks = correctCount * 1.0
            val negativeMarks = incorrectCount * negPerWrong
            val finalScore = (positiveMarks - negativeMarks).coerceAtLeast(0.0)
            val percentage = if (questions.isNotEmpty()) {
                (finalScore / (questions.size * 1.0)) * 100.0
            } else 0.0

            val accuracy = if (attempted > 0) {
                (correctCount.toDouble() / attempted.toDouble()) * 100.0
            } else 0.0

            val timeUsed = state.totalTimeSeconds - state.remainingSeconds

            val attemptEntity = TestAttemptEntity(
                userId = userId,
                userName = userName,
                testId = test.id,
                testTitle = test.title,
                examTitle = "પરીક્ષા",
                totalQuestions = questions.size,
                attemptedCount = attempted,
                correctCount = correctCount,
                incorrectCount = incorrectCount,
                unattemptedCount = unattempted,
                positiveMarks = Math.round(positiveMarks * 100.0) / 100.0,
                negativeMarks = Math.round(negativeMarks * 100.0) / 100.0,
                finalScore = Math.round(finalScore * 100.0) / 100.0,
                percentage = Math.round(percentage * 100.0) / 100.0,
                accuracy = Math.round(accuracy * 100.0) / 100.0,
                timeUsedSeconds = timeUsed,
                answersJson = answersJsonObj.toString(),
                subjectPerformanceJson = subjectPerformanceObj.toString()
            )

            val attemptId = repository.saveAttempt(attemptEntity)
            val savedWithId = attemptEntity.copy(id = attemptId.toInt())

            _uiState.value = _uiState.value.copy(
                isSubmitted = true,
                submissionResult = savedWithId
            )
        }
    }
}
