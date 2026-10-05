package com.example.ui.screens.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun QuestionBankPracticeScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val questions by mainViewModel.filteredQuestions.collectAsState()
    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val filterSubject by mainViewModel.filterSubject.collectAsState()
    val filterDifficulty by mainViewModel.filterDifficulty.collectAsState()

    val subjects = listOf("બધા", "ભારતીય બંધારણ", "ગુજરાતનો ઇતિહાસ", "ગુજરાતની ભૂગોળ", "રીઝનિંગ", "ગણિત", "સામાન્ય વિજ્ઞાન", "ગુજરાતી વ્યાકરણ", "કરંટ અફેર્સ")
    val difficulties = listOf("બધા", "સરળ", "મધ્યમ", "કઠિન")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "પ્રશ્ન બેંક (Question Bank)",
                subtitle = "સ્વ-અભ્યાસ & પ્રશ્ન પ્રેક્ટિસ",
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { mainViewModel.setQuestionSearch(it) },
                placeholder = { Text("પ્રશ્ન અથવા ટોપિક શોધો...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { mainViewModel.setQuestionSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("practice_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Subject Filter Row
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(subjects) { sub ->
                    val isSelected = (sub == "બધા" && filterSubject.isBlank()) || sub == filterSubject
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (sub == "બધા") mainViewModel.setQuestionSubjectFilter("")
                            else mainViewModel.setQuestionSubjectFilter(sub)
                        },
                        label = { Text(sub, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Difficulty Filter Row
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(difficulties) { diff ->
                    val isSelected = (diff == "બધા" && filterDifficulty.isBlank()) || diff == filterDifficulty
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (diff == "બધા") mainViewModel.setQuestionDifficultyFilter("")
                            else mainViewModel.setQuestionDifficultyFilter(diff)
                        },
                        label = { Text(diff, fontSize = 11.sp) }
                    )
                }
            }

            // Questions list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "કુલ પ્રશ્નો: ${questions.size}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
                    )
                }

                if (questions.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("કોઈ પ્રશ્નો મળ્યા નથી.")
                            }
                        }
                    }
                } else {
                    itemsIndexed(questions) { index, question ->
                        PracticeQuestionCard(index = index + 1, question = question)
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeQuestionCard(
    index: Int,
    question: QuestionEntity
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isRevealed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "પ્રશ્ન #$index",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(
                        text = question.subject,
                        textColor = NavyDark,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    StatusBadge(
                        text = question.difficulty,
                        textColor = TextSecondary,
                        backgroundColor = SlateSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options
            listOf(
                "A" to question.optionA,
                "B" to question.optionB,
                "C" to question.optionC,
                "D" to question.optionD
            ).forEach { (optKey, optText) ->
                val isSelected = selectedOption == optKey
                val isCorrect = question.correctOption == optKey

                val bg = when {
                    isRevealed && isCorrect -> EmeraldLight
                    isRevealed && isSelected && !isCorrect -> CrimsonLight
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else -> SlateSurfaceVariant.copy(alpha = 0.5f)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedOption = optKey
                            isRevealed = true
                        },
                    color = bg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isRevealed && isCorrect) EmeraldSuccess
                                    else if (isRevealed && isSelected) CrimsonError
                                    else NavyPrimary
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optKey,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            modifier = Modifier.weight(1f)
                        )
                        if (isRevealed && isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isRevealed = !isRevealed }) {
                    Icon(
                        if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRevealed) "જવાબ છુપાવો" else "સાચો જવાબ જુઓ")
                }
            }

            AnimatedVisibility(visible = isRevealed && question.explanation.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SlateSurfaceVariant
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 સાચો જવાબ: વિકલ્પ ${question.correctOption}",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                        )
                    }
                }
            }
        }
    }
}
