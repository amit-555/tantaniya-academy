package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "USER", // "ADMIN" or "USER"
    val targetExam: String = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val code: String,
    val description: String,
    val totalMarks: Int = 200,
    val durationMinutes: Int = 180,
    val negativeMarking: Double = 0.25,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "syllabus")
data class SyllabusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int,
    val partName: String, // e.g. "ભાગ-અ (Part A)", "ભાગ-બ (Part B)"
    val subjectName: String,
    val topicName: String,
    val weightageMarks: Int = 10
)

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int,
    val title: String,
    val description: String,
    val durationMinutes: Int = 180,
    val totalQuestions: Int = 200,
    val totalMarks: Int = 200,
    val negativeMarking: Double = 0.25,
    val passingMarks: Double = 80.0,
    val instructions: String = "૧. દરેક સાચા જવાબ માટે નિયત ગુણ મળશે.\n૨. દરેક ખોટા જવાબ માટે નેગેટિવ માર્કિંગ કપાશે.\n૩. સમય પૂરો થતાં ટેસ્ટ આપોઆપ સબમિટ થઈ જશે.",
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int = 1,
    val part: String = "ભાગ-અ (Part A)",
    val subject: String,
    val topic: String,
    val difficulty: String = "મધ્યમ", // "સરળ", "મધ્યમ", "કઠિન"
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String = "",
    val marks: Double = 1.0,
    val negativeMarks: Double = 0.25,
    val source: String = "Tantaniya Academy Bank",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "test_questions",
    indices = [Index(value = ["testId", "questionId"], unique = true)]
)
data class TestQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: Int,
    val questionId: Int,
    val orderIndex: Int = 0
)

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val testId: Int,
    val testTitle: String,
    val examTitle: String = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unattemptedCount: Int,
    val positiveMarks: Double,
    val negativeMarks: Double,
    val finalScore: Double,
    val percentage: Double,
    val accuracy: Double,
    val timeUsedSeconds: Long,
    val submittedAt: Long = System.currentTimeMillis(),
    val answersJson: String = "{}", // JSON string mapping questionId -> selectedOption
    val subjectPerformanceJson: String = "{}" // JSON string mapping subject -> "correct/total"
)

@Entity(tableName = "current_affairs")
data class CurrentAffairsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateText: String,
    val headline: String,
    val description: String,
    val category: String, // ગુજરાત, ભારત, વિશ્વ, વિજ્ઞાન, ટેકનોલોજી, રમતગમત, યોજનાઓ, અર્થતંત્ર, વગેરે
    val importantFacts: String = "",
    val source: String = "Tantaniya Academy Desk",
    val relatedExam: String = "બધી પરીક્ષાઓ",
    val relatedSubject: String = "કરંટ અફેર્સ",
    val mcqQuestion: String = "",
    val mcqOptionA: String = "",
    val mcqOptionB: String = "",
    val mcqOptionC: String = "",
    val mcqOptionD: String = "",
    val mcqCorrect: String = "",
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "gk_items")
data class GkItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // ગુજરાત GK, ભારત GK, ઇતિહાસ, ભૂગોળ, બંધારણ, વિજ્ઞાન, સંસ્કૃતિ, પર્યાવરણ
    val title: String,
    val content: String,
    val oneLinerFact: String,
    val question: String = "",
    val answer: String = "",
    val explanation: String = "",
    val exam: String = "બધી પરીક્ષાઓ",
    val subject: String = "જનરલ નોલેજ",
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int = 1,
    val subject: String,
    val topic: String,
    val title: String,
    val description: String,
    val type: String = "નોટ્સ", // "નોટ્સ", "PDF", "શોર્ટ ટ્રીક્સ"
    val contentText: String,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
