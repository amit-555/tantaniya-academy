package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExamEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminExamManagerScreen(
    mainViewModel: MainViewModel,
    onManageSyllabusForExam: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val allExams by mainViewModel.allExams.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingExam by remember { mutableStateOf<ExamEntity?>(null) }
    var deletingExam by remember { mutableStateOf<ExamEntity?>(null) }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "પરીક્ષાઓ સંચાલન (Exam Management)",
                subtitle = "નવી સરકારી ભરતી પરીક્ષાઓ બનાવો અને સંપાદિત કરો",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_create_exam_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "નવી પરીક્ષા બનાવો")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = NavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ડાયનેમિક પરીક્ષા સિસ્ટમ", fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "તમે બનાવેલી દરેક પરીક્ષા આપમેળે વિદ્યાર્થીના ડેશબોર્ડમાં દેખાશે. દા.ત. ફોરેસ્ટ બીટ ગાર્ડ, MPHW, CCE અથવા કોઈપણ નવી પરીક્ષા બનાવી શકો છો.",
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark, lineHeight = 18.sp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "કુલ નોંધાયેલ પરીક્ષાઓ (${allExams.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (allExams.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("કોઈ પરીક્ષા ઉપલબ્ધ નથી.")
                        }
                    }
                }
            } else {
                items(allExams) { exam ->
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
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exam.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "કોડ: ${exam.code}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = NavyPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = exam.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                        maxLines = 2
                                    )
                                }

                                StatusBadge(
                                    text = if (exam.isPublished) "પ્રકાશિત" else "અપ્રકાશિત",
                                    textColor = if (exam.isPublished) EmeraldSuccess else CrimsonError,
                                    backgroundColor = if (exam.isPublished) EmeraldLight else CrimsonLight
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("કુલ ગુણ: ${exam.totalMarks}", fontSize = 12.sp, color = TextSecondary)
                                Text("•", color = TextSecondary)
                                Text("સમય: ${exam.durationMinutes} મિનિટ", fontSize = 12.sp, color = TextSecondary)
                                Text("•", color = TextSecondary)
                                Text("નેગેટિવ: -${exam.negativeMarking}", fontSize = 12.sp, color = TextSecondary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        mainViewModel.setSelectedExam(exam.id)
                                        onManageSyllabusForExam(exam.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("સિલેબસ / વિષયો", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { mainViewModel.updateExam(exam.copy(isPublished = !exam.isPublished)) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (exam.isPublished) "અપ્રકાશિત" else "પ્રકાશિત", fontSize = 11.sp)
                                }

                                IconButton(onClick = { editingExam = exam }) {
                                    Icon(Icons.Default.Edit, contentDescription = "સંપાદિત કરો", tint = NavyPrimary)
                                }

                                IconButton(onClick = { deletingExam = exam }) {
                                    Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create / Edit Exam Dialog
    if (showCreateDialog || editingExam != null) {
        val isEditing = editingExam != null
        val existing = editingExam

        var title by remember { mutableStateOf(existing?.title ?: "") }
        var code by remember { mutableStateOf(existing?.code ?: "") }
        var desc by remember { mutableStateOf(existing?.description ?: "") }
        var marksText by remember { mutableStateOf(existing?.totalMarks?.toString() ?: "150") }
        var durationText by remember { mutableStateOf(existing?.durationMinutes?.toString() ?: "150") }
        var negMarkText by remember { mutableStateOf(existing?.negativeMarking?.toString() ?: "0.25") }
        var isPublished by remember { mutableStateOf(existing?.isPublished ?: true) }

        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                editingExam = null
            },
            title = { Text(if (isEditing) "પરીક્ષા સંપાદિત કરો" else "નવી પરીક્ષા બનાવો (Create Exam)") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("પરીક્ષાનું નામ (Exam Title)") },
                            placeholder = { Text("દા.ત. ફોરેસ્ટ બીટ ગાર્ડ અથવા MPHW") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("પરીક્ષા કોડ (Exam Code)") },
                            placeholder = { Text("દા.ત. FBG, MPHW, CCE") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("વર્ણન / વિગતો (Description)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = marksText,
                                onValueChange = { marksText = it },
                                label = { Text("કુલ ગુણ") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it },
                                label = { Text("સમય (મિનિટ)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = negMarkText,
                            onValueChange = { negMarkText = it },
                            label = { Text("નેગેટિવ માર્કિંગ (દા.ત. 0.25)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(checked = isPublished, onCheckedChange = { isPublished = it })
                            Text("વિદ્યાર્થીઓ માટે તરત જ પ્રકાશિત કરો")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val marks = marksText.toIntOrNull() ?: 150
                            val dur = durationText.toIntOrNull() ?: 150
                            val neg = negMarkText.toDoubleOrNull() ?: 0.25

                            if (isEditing && existing != null) {
                                mainViewModel.updateExam(
                                    existing.copy(
                                        title = title.trim(),
                                        code = code.trim().uppercase(),
                                        description = desc.trim(),
                                        totalMarks = marks,
                                        durationMinutes = dur,
                                        negativeMarking = neg,
                                        isPublished = isPublished
                                    )
                                )
                            } else {
                                mainViewModel.createExam(
                                    title = title.trim(),
                                    code = code.trim().uppercase(),
                                    desc = desc.trim(),
                                    marks = marks,
                                    duration = dur,
                                    negMark = neg,
                                    isPublished = isPublished
                                )
                            }
                            showCreateDialog = false
                            editingExam = null
                        }
                    }
                ) {
                    Text(if (isEditing) "સાચવો" else "પરીક્ષા ઉમેરો")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    editingExam = null
                }) {
                    Text("રદ કરો")
                }
            }
        )
    }

    // Delete Confirmation
    if (deletingExam != null) {
        val ex = deletingExam!!
        AlertDialog(
            onDismissRequest = { deletingExam = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonError) },
            title = { Text("પરીક્ષા કાઢી નાખવી છે?") },
            text = { Text("શું તમે '${ex.title}' પરીક્ષા કાઢી નાખવા માંગો છો?") },
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.deleteExam(ex)
                        deletingExam = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError)
                ) {
                    Text("કાઢી નાખો")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deletingExam = null }) {
                    Text("રદ કરો")
                }
            }
        )
    }
}
