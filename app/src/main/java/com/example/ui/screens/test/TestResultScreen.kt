package com.example.ui.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.QuestionEntity
import com.example.data.entity.TestAttemptEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    attempt: TestAttemptEntity,
    questions: List<QuestionEntity>,
    onRetakeTest: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "CORRECT", "INCORRECT", "UNATTEMPTED"

    val userAnswersMap = remember(attempt.answersJson) {
        val map = mutableMapOf<Int, String>()
        try {
            val json = JSONObject(attempt.answersJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.toInt()] = json.getString(k)
            }
        } catch (_: Exception) {}
        map
    }

    val filteredQuestions = remember(selectedFilter, questions, userAnswersMap) {
        when (selectedFilter) {
            "CORRECT" -> questions.filter { q ->
                val selected = userAnswersMap[q.id]
                selected != null && selected.equals(q.correctOption, ignoreCase = true)
            }
            "INCORRECT" -> questions.filter { q ->
                val selected = userAnswersMap[q.id]
                selected != null && !selected.equals(q.correctOption, ignoreCase = true)
            }
            "UNATTEMPTED" -> questions.filter { q ->
                !userAnswersMap.containsKey(q.id)
            }
            else -> questions
        }
    }

    val timeFormatted = remember(attempt.timeUsedSeconds) {
        val min = attempt.timeUsedSeconds / 60
        val sec = attempt.timeUsedSeconds % 60
        "${min} મિનિટ ${sec} સેકન્ડ"
    }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "ટેસ્ટ પરિણામ & વિશ્લેષણ",
                subtitle = attempt.testTitle,
                showBackButton = true,
                onBackClick = onNavigateToHome
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_home_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("હોમ પેજ")
                    }
                    Button(
                        onClick = { onRetakeTest(attempt.testId) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_retake_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ફરીથી આપો")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Scorecard Hero
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "કુલ સ્કોર (Final Score)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${attempt.finalScore} / ${attempt.totalQuestions}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 36.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusBadge(
                                text = "ટકાવારી: ${attempt.percentage}%",
                                textColor = Color.White,
                                backgroundColor = if (attempt.percentage >= 40.0) EmeraldSuccess else CrimsonError
                            )
                            StatusBadge(
                                text = "ચોકસાઈ: ${attempt.accuracy}%",
                                textColor = NavyDark,
                                backgroundColor = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid (Correct, Incorrect, Unattempted, Time)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${attempt.correctCount}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                )
                                Text("સાચા (+${attempt.positiveMarks})", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${attempt.incorrectCount}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonError
                                    )
                                )
                                Text("ખોટા (-${attempt.negativeMarks})", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${attempt.unattemptedCount}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                )
                                Text("બાકી", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = timeFormatted,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                )
                                Text("સમય", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                            }
                        }
                    }
                }
            }

            // 2. Question Filter Tabs
            item {
                Text(
                    text = "પ્રશ્નવાર વિશ્લેષણ (Question Analysis)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("બધા (${questions.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "CORRECT",
                        onClick = { selectedFilter = "CORRECT" },
                        label = { Text("સાચા (${attempt.correctCount})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "INCORRECT",
                        onClick = { selectedFilter = "INCORRECT" },
                        label = { Text("ખોટા (${attempt.incorrectCount})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "UNATTEMPTED",
                        onClick = { selectedFilter = "UNATTEMPTED" },
                        label = { Text("બાકી (${attempt.unattemptedCount})", fontSize = 11.sp) }
                    )
                }
            }

            // 3. Question Items with Detailed Explanations
            if (filteredQuestions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("આ કેટેગરીમાં કોઈ પ્રશ્નો નથી.")
                    }
                }
            } else {
                itemsIndexed(filteredQuestions) { idx, q ->
                    val userChoice = userAnswersMap[q.id]
                    val isCorrect = userChoice != null && userChoice.equals(q.correctOption, ignoreCase = true)
                    val isUnattempted = userChoice == null

                    QuestionReviewCard(
                        index = idx + 1,
                        question = q,
                        userChoice = userChoice,
                        isCorrect = isCorrect,
                        isUnattempted = isUnattempted
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionReviewCard(
    index: Int,
    question: QuestionEntity,
    userChoice: String?,
    isCorrect: Boolean,
    isUnattempted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Result Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "પ્રશ્ન $index",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                if (isUnattempted) {
                    StatusBadge(
                        text = "જવાબ નથી આપ્યો (0 ગુણ)",
                        textColor = TextSecondary,
                        backgroundColor = SlateSurfaceVariant
                    )
                } else if (isCorrect) {
                    StatusBadge(
                        text = "સાચો જવાબ (+${question.marks} ગુણ)",
                        textColor = EmeraldSuccess,
                        backgroundColor = EmeraldLight
                    )
                } else {
                    StatusBadge(
                        text = "ખોટો જવાબ (-${question.negativeMarks} ગુણ)",
                        textColor = CrimsonError,
                        backgroundColor = CrimsonLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options display
            ReviewOptionRow(label = "A", text = question.optionA, correct = question.correctOption == "A", selected = userChoice == "A")
            Spacer(modifier = Modifier.height(6.dp))
            ReviewOptionRow(label = "B", text = question.optionB, correct = question.correctOption == "B", selected = userChoice == "B")
            Spacer(modifier = Modifier.height(6.dp))
            ReviewOptionRow(label = "C", text = question.optionC, correct = question.correctOption == "C", selected = userChoice == "C")
            Spacer(modifier = Modifier.height(6.dp))
            ReviewOptionRow(label = "D", text = question.optionD, correct = question.correctOption == "D", selected = userChoice == "D")

            // Explanation box in Gujarati
            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("સમજૂતી (Explanation):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewOptionRow(
    label: String,
    text: String,
    correct: Boolean,
    selected: Boolean
) {
    val borderColor = when {
        correct -> EmeraldSuccess
        selected -> CrimsonError
        else -> SlateOutline.copy(alpha = 0.5f)
    }

    val bgColor = when {
        correct -> EmeraldLight.copy(alpha = 0.6f)
        selected -> CrimsonLight.copy(alpha = 0.6f)
        else -> Color.Transparent
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (correct) EmeraldSuccess else if (selected) CrimsonError else SlateSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (correct || selected) Color.White else TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                modifier = Modifier.weight(1f)
            )
            if (correct) {
                Icon(Icons.Default.CheckCircle, contentDescription = "સાચો વિકલ્પ", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
            } else if (selected) {
                Icon(Icons.Default.Cancel, contentDescription = "તમારો ખોટો વિકલ્પ", tint = CrimsonError, modifier = Modifier.size(18.dp))
            }
        }
    }
}
