package com.example.data.firestore

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.data.dao.StudyProDao
import com.example.data.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

class FirestoreSyncManager(
    private val dao: StudyProDao,
    private val context: Context
) {
    private val TAG = "FirestoreSyncManager"
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    /**
     * Start background real-time synchronization with Cloud Firestore.
     * Listens for changes from Web and other Android devices and syncs them to local Room cache.
     */
    fun startRealtimeSync(scope: CoroutineScope) {
        // 1. Sync Exams
        db.collection("exams").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Exams listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val exam = ExamEntity(
                                id = id,
                                title = doc.getString("title") ?: "",
                                code = doc.getString("code") ?: "",
                                description = doc.getString("description") ?: "",
                                totalMarks = doc.getLong("totalMarks")?.toInt() ?: 200,
                                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 180,
                                negativeMarking = doc.getDouble("negativeMarking") ?: 0.25,
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertExam(exam)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing exam doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 2. Sync Tests
        db.collection("tests").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Tests listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val test = TestEntity(
                                id = id,
                                examId = doc.getLong("examId")?.toInt() ?: 1,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 180,
                                totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 200,
                                totalMarks = doc.getLong("totalMarks")?.toInt() ?: 200,
                                negativeMarking = doc.getDouble("negativeMarking") ?: 0.25,
                                passingMarks = doc.getDouble("passingMarks") ?: 80.0,
                                instructions = doc.getString("instructions") ?: "",
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertTest(test)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing test doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 3. Sync Questions
        db.collection("questions").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Questions listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val question = QuestionEntity(
                                id = id,
                                examId = doc.getLong("examId")?.toInt() ?: 1,
                                part = doc.getString("part") ?: "ભાગ-અ (Part A)",
                                subject = doc.getString("subject") ?: "",
                                topic = doc.getString("topic") ?: "",
                                difficulty = doc.getString("difficulty") ?: "મધ્યમ",
                                questionText = doc.getString("questionText") ?: "",
                                optionA = doc.getString("optionA") ?: "",
                                optionB = doc.getString("optionB") ?: "",
                                optionC = doc.getString("optionC") ?: "",
                                optionD = doc.getString("optionD") ?: "",
                                correctOption = doc.getString("correctOption") ?: "A",
                                explanation = doc.getString("explanation") ?: "",
                                marks = doc.getDouble("marks") ?: 1.0,
                                negativeMarks = doc.getDouble("negativeMarks") ?: 0.25,
                                source = doc.getString("source") ?: "Tantaniya Academy Bank",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertQuestion(question)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing question doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 4. Sync Current Affairs
        db.collection("current_affairs").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Current Affairs listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val ca = CurrentAffairsEntity(
                                id = id,
                                dateText = doc.getString("dateText") ?: "",
                                headline = doc.getString("headline") ?: "",
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "ગુજરાત",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                importantFacts = doc.getString("importantFacts") ?: "",
                                source = doc.getString("source") ?: "Tantaniya Academy Desk",
                                relatedExam = doc.getString("relatedExam") ?: "બધી પરીક્ષાઓ",
                                relatedSubject = doc.getString("relatedSubject") ?: "કરંટ અફેર્સ",
                                mcqQuestion = doc.getString("mcqQuestion") ?: "",
                                mcqOptionA = doc.getString("mcqOptionA") ?: "",
                                mcqOptionB = doc.getString("mcqOptionB") ?: "",
                                mcqOptionC = doc.getString("mcqOptionC") ?: "",
                                mcqOptionD = doc.getString("mcqOptionD") ?: "",
                                mcqCorrect = doc.getString("mcqCorrect") ?: "",
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertCurrentAffairs(ca)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing CA doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 5. Sync General Knowledge
        db.collection("gk").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "GK listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val gk = GkItemEntity(
                                id = id,
                                category = doc.getString("category") ?: "સામાન્ય જ્ઞાન",
                                title = doc.getString("title") ?: "",
                                content = doc.getString("content") ?: "",
                                oneLinerFact = doc.getString("oneLinerFact") ?: "",
                                exam = doc.getString("exam") ?: "બધી પરીક્ષાઓ",
                                subject = doc.getString("subject") ?: "જનરલ નોલેજ",
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertGkItem(gk)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing GK doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 6. Sync Study Materials & PDFs
        db.collection("study_materials").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Study materials listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val sm = StudyMaterialEntity(
                                id = id,
                                examId = doc.getLong("examId")?.toInt() ?: 1,
                                subject = doc.getString("subject") ?: "",
                                topic = doc.getString("topic") ?: "",
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                type = doc.getString("type") ?: "નોટ્સ",
                                contentText = doc.getString("contentText") ?: "",
                                fileUrl = doc.getString("fileUrl") ?: "",
                                fileSize = doc.getString("fileSize") ?: "",
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertStudyMaterial(sm)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing study material doc: ${e.message}")
                        }
                    }
                }
            }
        }

        // 7. Sync Image Library
        db.collection("image_library").addSnapshotListener { snapshot, err ->
            if (err != null) {
                Log.w(TAG, "Image library listener error: ${err.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch(Dispatchers.IO) {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: continue
                            val img = ImageLibraryEntity(
                                id = id,
                                title = doc.getString("title") ?: "",
                                category = doc.getString("category") ?: "સામાન્ય",
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                isPublished = doc.getBoolean("isPublished") ?: true,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                            dao.insertImageLibraryItem(img)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing image library doc: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    // --- CLOUD WRITE METHODS (ADMIN & USER) ---

    suspend fun pushExam(exam: ExamEntity) {
        val data = hashMapOf(
            "id" to exam.id,
            "title" to exam.title,
            "code" to exam.code,
            "description" to exam.description,
            "totalMarks" to exam.totalMarks,
            "durationMinutes" to exam.durationMinutes,
            "negativeMarking" to exam.negativeMarking,
            "isPublished" to exam.isPublished,
            "createdAt" to exam.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("exams").document(exam.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push exam: ${e.message}")
        }
    }

    suspend fun removeExam(examId: Int) {
        try {
            db.collection("exams").document(examId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove exam: ${e.message}")
        }
    }

    suspend fun pushTest(test: TestEntity) {
        val data = hashMapOf(
            "id" to test.id,
            "examId" to test.examId,
            "title" to test.title,
            "description" to test.description,
            "durationMinutes" to test.durationMinutes,
            "totalQuestions" to test.totalQuestions,
            "totalMarks" to test.totalMarks,
            "negativeMarking" to test.negativeMarking,
            "passingMarks" to test.passingMarks,
            "instructions" to test.instructions,
            "isPublished" to test.isPublished,
            "createdAt" to test.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("tests").document(test.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push test: ${e.message}")
        }
    }

    suspend fun removeTest(testId: Int) {
        try {
            db.collection("tests").document(testId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove test: ${e.message}")
        }
    }

    suspend fun pushQuestion(q: QuestionEntity) {
        val data = hashMapOf(
            "id" to q.id,
            "examId" to q.examId,
            "part" to q.part,
            "subject" to q.subject,
            "topic" to q.topic,
            "difficulty" to q.difficulty,
            "questionText" to q.questionText,
            "optionA" to q.optionA,
            "optionB" to q.optionB,
            "optionC" to q.optionC,
            "optionD" to q.optionD,
            "correctOption" to q.correctOption,
            "explanation" to q.explanation,
            "marks" to q.marks,
            "negativeMarks" to q.negativeMarks,
            "source" to q.source,
            "createdAt" to q.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("questions").document(q.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push question: ${e.message}")
        }
    }

    suspend fun removeQuestion(qId: Int) {
        try {
            db.collection("questions").document(qId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove question: ${e.message}")
        }
    }

    suspend fun pushCurrentAffairs(ca: CurrentAffairsEntity) {
        val data = hashMapOf(
            "id" to ca.id,
            "dateText" to ca.dateText,
            "headline" to ca.headline,
            "description" to ca.description,
            "category" to ca.category,
            "imageUrl" to ca.imageUrl,
            "importantFacts" to ca.importantFacts,
            "source" to ca.source,
            "relatedExam" to ca.relatedExam,
            "relatedSubject" to ca.relatedSubject,
            "isPublished" to ca.isPublished,
            "createdAt" to ca.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("current_affairs").document(ca.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push CA: ${e.message}")
        }
    }

    suspend fun removeCurrentAffairs(caId: Int) {
        try {
            db.collection("current_affairs").document(caId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove CA: ${e.message}")
        }
    }

    suspend fun pushGkItem(gk: GkItemEntity) {
        val data = hashMapOf(
            "id" to gk.id,
            "category" to gk.category,
            "title" to gk.title,
            "content" to gk.content,
            "oneLinerFact" to gk.oneLinerFact,
            "exam" to gk.exam,
            "subject" to gk.subject,
            "isPublished" to gk.isPublished,
            "createdAt" to gk.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("gk").document(gk.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push GK: ${e.message}")
        }
    }

    suspend fun removeGkItem(gkId: Int) {
        try {
            db.collection("gk").document(gkId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove GK: ${e.message}")
        }
    }

    suspend fun pushStudyMaterial(sm: StudyMaterialEntity) {
        val data = hashMapOf(
            "id" to sm.id,
            "examId" to sm.examId,
            "subject" to sm.subject,
            "topic" to sm.topic,
            "title" to sm.title,
            "description" to sm.description,
            "type" to sm.type,
            "contentText" to sm.contentText,
            "fileUrl" to sm.fileUrl,
            "fileSize" to sm.fileSize,
            "isPublished" to sm.isPublished,
            "createdAt" to sm.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("study_materials").document(sm.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push study material: ${e.message}")
        }
    }

    suspend fun removeStudyMaterial(smId: Int) {
        try {
            db.collection("study_materials").document(smId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove study material: ${e.message}")
        }
    }

    suspend fun pushImageLibraryItem(item: ImageLibraryEntity) {
        val data = hashMapOf(
            "id" to item.id,
            "title" to item.title,
            "category" to item.category,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "isPublished" to item.isPublished,
            "createdAt" to item.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            db.collection("image_library").document(item.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push image library item: ${e.message}")
        }
    }

    suspend fun removeImageLibraryItem(itemId: Int) {
        try {
            db.collection("image_library").document(itemId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove image library item: ${e.message}")
        }
    }

    suspend fun pushTestAttempt(attempt: TestAttemptEntity) {
        val data = hashMapOf(
            "id" to attempt.id,
            "userId" to attempt.userId,
            "userName" to attempt.userName,
            "testId" to attempt.testId,
            "testTitle" to attempt.testTitle,
            "examTitle" to attempt.examTitle,
            "totalQuestions" to attempt.totalQuestions,
            "attemptedCount" to attempt.attemptedCount,
            "correctCount" to attempt.correctCount,
            "incorrectCount" to attempt.incorrectCount,
            "unattemptedCount" to attempt.unattemptedCount,
            "positiveMarks" to attempt.positiveMarks,
            "negativeMarks" to attempt.negativeMarks,
            "finalScore" to attempt.finalScore,
            "percentage" to attempt.percentage,
            "accuracy" to attempt.accuracy,
            "timeUsedSeconds" to attempt.timeUsedSeconds,
            "submittedAt" to attempt.submittedAt,
            "answersJson" to attempt.answersJson,
            "subjectPerformanceJson" to attempt.subjectPerformanceJson
        )
        try {
            db.collection("test_attempts").document(attempt.id.toString()).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push test attempt: ${e.message}")
        }
    }

    /**
     * Upload an image or PDF to Firebase Storage.
     * Falls back to a Base64 data URI if storage bucket is not configured.
     */
    suspend fun uploadMedia(uri: Uri, folder: String, isPdf: Boolean = false): String = withContext(Dispatchers.IO) {
        val extension = if (isPdf) "pdf" else "jpg"
        val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
        val filename = "${System.currentTimeMillis()}_${(1000..9999).random()}.$extension"
        val storageRef = storage.reference.child("$folder/$filename")

        try {
            storageRef.putFile(uri).await()
            return@withContext storageRef.downloadUrl.await().toString()
        } catch (storageErr: Exception) {
            Log.w(TAG, "Firebase Storage direct upload failed, creating base64 data URI: ${storageErr.message}")
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: ByteArray(0)
                inputStream?.close()
                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                return@withContext "data:$mimeType;base64,$base64"
            } catch (readErr: Exception) {
                Log.e(TAG, "Error encoding fallback data URI: ${readErr.message}")
                return@withContext ""
            }
        }
    }
}
