package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun AdminQuestionBankScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val questions by mainViewModel.filteredQuestions.collectAsState()
    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val filterSubject by mainViewModel.filterSubject.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<QuestionEntity?>(null) }

    val subjects = listOf("બધા", "ભારતીય બંધારણ", "ગુજરાતનો ઇતિહાસ", "ગુજરાતની ભૂગોળ", "રીઝનિંગ", "ગણિત", "સામાન્ય વિજ્ઞાન", "ગુજરાતી વ્યાકરણ", "કરંટ અફેર્સ")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "પ્રશ્ન બેંક સંચાલન (Question Bank)",
                subtitle = "પ્રશ્નો ઉમેરો, સંપાદિત કરો અને શોધો",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("admin_add_question_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "નવો પ્રશ્ન ઉમેરો")
            }
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
                placeholder = { Text("પ્રશ્ન લખાણ અથવા ટોપિક શોધો...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("admin_question_search_input"),
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "કુલ પ્રશ્નો: ${questions.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
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
                    itemsIndexed(questions) { index, q ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#${index + 1}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        StatusBadge(text = q.subject, textColor = NavyDark, backgroundColor = MaterialTheme.colorScheme.primaryContainer)
                                        StatusBadge(text = q.difficulty, textColor = TextSecondary, backgroundColor = SlateSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(q.questionText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("A: ${q.optionA}", fontSize = 12.sp, color = if (q.correctOption == "A") EmeraldSuccess else TextSecondary, fontWeight = if (q.correctOption == "A") FontWeight.Bold else FontWeight.Normal)
                                    Text("B: ${q.optionB}", fontSize = 12.sp, color = if (q.correctOption == "B") EmeraldSuccess else TextSecondary, fontWeight = if (q.correctOption == "B") FontWeight.Bold else FontWeight.Normal)
                                    Text("C: ${q.optionC}", fontSize = 12.sp, color = if (q.correctOption == "C") EmeraldSuccess else TextSecondary, fontWeight = if (q.correctOption == "C") FontWeight.Bold else FontWeight.Normal)
                                    Text("D: ${q.optionD}", fontSize = 12.sp, color = if (q.correctOption == "D") EmeraldSuccess else TextSecondary, fontWeight = if (q.correctOption == "D") FontWeight.Bold else FontWeight.Normal)
                                }

                                if (q.explanation.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("💡 ${q.explanation}", fontSize = 11.sp, color = TextSecondary)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { editingQuestion = q }) {
                                        Icon(Icons.Default.Edit, contentDescription = "સંપાદિત કરો", tint = NavyPrimary)
                                    }
                                    IconButton(onClick = { mainViewModel.deleteQuestion(q.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Question Dialog
    if (showAddDialog || editingQuestion != null) {
        val isEditing = editingQuestion != null
        val existing = editingQuestion

        var qText by remember { mutableStateOf(existing?.questionText ?: "") }
        var optA by remember { mutableStateOf(existing?.optionA ?: "") }
        var optB by remember { mutableStateOf(existing?.optionB ?: "") }
        var optC by remember { mutableStateOf(existing?.optionC ?: "") }
        var optD by remember { mutableStateOf(existing?.optionD ?: "") }
        var correct by remember { mutableStateOf(existing?.correctOption ?: "A") }
        var sub by remember { mutableStateOf(existing?.subject ?: "ભારતીય બંધારણ") }
        var topic by remember { mutableStateOf(existing?.topic ?: "મૂળભૂત અધિકારો") }
        var diff by remember { mutableStateOf(existing?.difficulty ?: "મધ્યમ") }
        var expl by remember { mutableStateOf(existing?.explanation ?: "") }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingQuestion = null
            },
            title = { Text(if (isEditing) "પ્રશ્ન સંપાદિત કરો" else "નવો પ્રશ્ન ઉમેરો (Add Question)") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("પ્રશ્ન લખાણ (Question)") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("વિકલ્પ A") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("વિકલ્પ B") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("વિકલ્પ C") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("વિકલ્પ D") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = correct, onValueChange = { correct = it.uppercase().take(1) }, label = { Text("સાચો વિકલ્પ (A / B / C / D)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("વિષય (Subject)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("ટોપિક (Topic)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = expl, onValueChange = { expl = it }, label = { Text("સમજૂતી (Explanation)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isNotBlank() && optA.isNotBlank()) {
                            if (isEditing && existing != null) {
                                mainViewModel.updateQuestion(
                                    existing.copy(
                                        questionText = qText,
                                        optionA = optA,
                                        optionB = optB,
                                        optionC = optC,
                                        optionD = optD,
                                        correctOption = correct,
                                        subject = sub,
                                        topic = topic,
                                        explanation = expl
                                    )
                                )
                            } else {
                                mainViewModel.addQuestionManually(
                                    QuestionEntity(
                                        questionText = qText,
                                        optionA = optA,
                                        optionB = optB,
                                        optionC = optC,
                                        optionD = optD,
                                        correctOption = correct,
                                        subject = sub,
                                        topic = topic,
                                        difficulty = diff,
                                        explanation = expl
                                    )
                                )
                            }
                            showAddDialog = false
                            editingQuestion = null
                        }
                    }
                ) {
                    Text("સાચવો (Save)")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        editingQuestion = null
                    }
                ) {
                    Text("રદ કરો")
                }
            }
        )
    }
}
