package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExamEntity
import com.example.data.entity.QuestionEntity
import com.example.data.entity.TestEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTestManagerScreen(
    mainViewModel: MainViewModel,
    onPreviewTest: (Int) -> Unit = {},
    onBackClick: () -> Unit
) {
    val allTests by mainViewModel.allTests.collectAsState()
    val allExams by mainViewModel.allExams.collectAsState()
    val allQuestions by mainViewModel.filteredQuestions.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedManageTest by remember { mutableStateOf<TestEntity?>(null) }
    var showAddFromBankModal by remember { mutableStateOf(false) }
    var showCreateQuestionInsideTestModal by remember { mutableStateOf(false) }
    var selectedQuestionIdsToAdd by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var validationWarningMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "ટેસ્ટ બિલ્ડર (Complete Test Builder)",
                subtitle = "સંપૂર્ણ મોક ટેસ્ટ નિર્માણ & પ્રશ્ન વિતરણ",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_create_test_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "નવો ટેસ્ટ બનાવો")
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
                            Icon(Icons.Default.Tune, contentDescription = null, tint = NavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ટેસ્ટ નિર્માણ & પ્રશ્ન વિતરણ", fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "તમે ૨૦૦ પ્રશ્નો/૨૦૦ ગુણનો કોન્સ્ટેબલ ટેસ્ટ, ૧૫૦ ગુણનો ફોરેસ્ટ ગાર્ડ ટેસ્ટ અથવા ૧૦૦ ગુણનો MPHW ટેસ્ટ બનાવીને પ્રશ્ન બેંકમાંથી અથવા સીધા પ્રશ્નો ઉમેરી શકો છો.",
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark, lineHeight = 18.sp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "કુલ મોક ટેસ્ટ: ${allTests.size}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (allTests.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("કોઈ ટેસ્ટ ઉપલબ્ધ નથી. નીચે આપેલા '+' બટનથી નવો ટેસ્ટ બનાવો.")
                        }
                    }
                }
            } else {
                items(allTests) { test ->
                    val examName = remember(test.examId, allExams) {
                        allExams.find { it.id == test.examId }?.title ?: "પરીક્ષા #${test.examId}"
                    }
                    val testQuestionsFlow = remember(test.id) { mainViewModel.getQuestionsForTest(test.id) }
                    val currentQuestionsCount by testQuestionsFlow.collectAsState(initial = emptyList())
                    val currentCount = currentQuestionsCount.size

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
                                    Text(test.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = examName,
                                        style = MaterialTheme.typography.labelSmall.copy(color = NavyPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(test.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), maxLines = 2)
                                }
                                StatusBadge(
                                    text = if (test.isPublished) "પ્રકાશિત" else "ડ્રાફ્ટ",
                                    textColor = if (test.isPublished) EmeraldSuccess else CrimsonError,
                                    backgroundColor = if (test.isPublished) EmeraldLight else CrimsonLight
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Progress counter (Added vs Required)
                            val isComplete = currentCount >= test.totalQuestions
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isComplete) EmeraldLight else AmberLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "પ્રશ્નો ઉમેરાયા: $currentCount / ${test.totalQuestions}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isComplete) Color(0xFF065F46) else Color(0xFF78350F)
                                    )
                                    Text(
                                        text = if (isComplete) "ટેસ્ટ તૈયાર છે!" else "${test.totalQuestions - currentCount} બાકી",
                                        fontSize = 11.sp,
                                        color = if (isComplete) Color(0xFF065F46) else Color(0xFF78350F)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("સમય: ${test.durationMinutes} મિ.", fontSize = 12.sp, color = TextSecondary)
                                Text("•", color = TextSecondary)
                                Text("કુલ ગુણ: ${test.totalMarks}", fontSize = 12.sp, color = TextSecondary)
                                Text("•", color = TextSecondary)
                                Text("નેગેટિવ: -${test.negativeMarking}", fontSize = 12.sp, color = TextSecondary)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { selectedManageTest = test },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("પ્રશ્નો ઉમેરો / સંચાલન", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { onPreviewTest(test.id) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("પ્રિવ્યુ", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (!test.isPublished && currentCount < test.totalQuestions) {
                                            validationWarningMessage = "${test.totalQuestions} પ્રશ્નો જરૂરી છે. હાલમાં માત્ર $currentCount પ્રશ્નો ઉમેરાયા છે. શું તમે આ અપૂર્ણ ટેસ્ટને પ્રકાશિત કરવા માંગો છો?"
                                        } else {
                                            mainViewModel.updateTest(test.copy(isPublished = !test.isPublished))
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (test.isPublished) "અપ્રકાશિત" else "પ્રકાશિત", fontSize = 11.sp)
                                }

                                IconButton(onClick = { mainViewModel.deleteTest(test.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Complete Test Creator Dialog
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var selectedExamId by remember { mutableStateOf(allExams.firstOrNull()?.id ?: 1) }
        var desc by remember { mutableStateOf("") }
        var durationText by remember { mutableStateOf("180") }
        var questionsText by remember { mutableStateOf("200") }
        var marksText by remember { mutableStateOf("200") }
        var negMarkingText by remember { mutableStateOf("0.25") }
        var passingMarksText by remember { mutableStateOf("80.0") }
        var instructions by remember { mutableStateOf("૧. દરેક સાચા જવાબ પર નિયત ગુણ મળશે.\n૨. ખોટા જવાબ પર નેગેટિવ માર્કિંગ કપાશે.\n૩. સમય પૂરો થતાં ટેસ્ટ ઓટો-સબમિટ થશે.") }
        var isPublished by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("સંપૂર્ણ મોક ટેસ્ટ બનાવો (Complete Test Builder)") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("ટેસ્ટનું નામ (Test Title)") },
                            placeholder = { Text("દા.ત. ગુજરાત પોલીસ કોન્સ્ટેબલ ફુલ મોક ટેસ્ટ ૦૨") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("સંબંધિત પરીક્ષા પસંદ કરો (Exam):", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(allExams) { exam ->
                                FilterChip(
                                    selected = selectedExamId == exam.id,
                                    onClick = { selectedExamId = exam.id },
                                    label = { Text(exam.title, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("ટેસ્ટ વિગત / વર્ણન") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = questionsText,
                                onValueChange = {
                                    questionsText = it
                                    marksText = it // Auto calculate marks matching questions
                                },
                                label = { Text("કુલ પ્રશ્નો (Questions)") },
                                placeholder = { Text("દા.ત. 200 / 150 / 100") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = marksText,
                                onValueChange = { marksText = it },
                                label = { Text("કુલ ગુણ (Total Marks)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it },
                                label = { Text("સમય (મિનિટ)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = negMarkingText,
                                onValueChange = { negMarkingText = it },
                                label = { Text("નેગેટિવ માર્કિંગ") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passingMarksText,
                            onValueChange = { passingMarksText = it },
                            label = { Text("પાસિંગ ગુણ (Passing Marks)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("પરીક્ષા સૂચનાઓ (Instructions)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(checked = isPublished, onCheckedChange = { isPublished = it })
                            Text("તરત જ પ્રકાશિત કરો (Publish immediately)")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            mainViewModel.createCompleteTest(
                                examId = selectedExamId,
                                title = title.trim(),
                                desc = desc.trim(),
                                duration = durationText.toIntOrNull() ?: 180,
                                totalQuestions = questionsText.toIntOrNull() ?: 200,
                                totalMarks = marksText.toIntOrNull() ?: 200,
                                negativeMarking = negMarkingText.toDoubleOrNull() ?: 0.25,
                                passingMarks = passingMarksText.toDoubleOrNull() ?: 80.0,
                                instructions = instructions.trim(),
                                isPublished = isPublished
                            )
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("ટેસ્ટ સાચવો")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("રદ કરો") }
            }
        )
    }

    // Manage Test Questions Modal
    if (selectedManageTest != null) {
        val test = selectedManageTest!!
        val testQuestions by mainViewModel.getQuestionsForTest(test.id).collectAsState(initial = emptyList())
        val isComplete = testQuestions.size >= test.totalQuestions

        AlertDialog(
            onDismissRequest = { selectedManageTest = null },
            title = {
                Column {
                    Text(test.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = "પ્રશ્નો: ${testQuestions.size} / ${test.totalQuestions}",
                            textColor = if (isComplete) EmeraldSuccess else AmberWarning,
                            backgroundColor = if (isComplete) EmeraldLight else AmberLight
                        )
                        Text(
                            text = if (isComplete) "✓ ટેસ્ટ પરિપૂર્ણ છે" else "${test.totalQuestions - testQuestions.size} પ્રશ્નો બાકી",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Question Distribution Info & Auto-Populate Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "વિષયવાર પ્રશ્ન વિતરણ (Question Distribution):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "રીઝનિંગ: ૩૦ | ગણિત: ૩૦ | બંધારણ: ૩૦ | ગુજરાત ઇતિહાસ-ભૂગોળ: ૪૦ | વિજ્ઞાન: ૨૦ | કરંટ: ૨૦ | GK: ૩૦ (કુલ ૨૦૦ પ્રશ્નો)",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    mainViewModel.populateDistributedQuestionsForTest(test.id, test.examId) { count ->
                                        validationWarningMessage = "$count પ્રશ્નો સફળતાપૂર્વક ટેસ્ટમાં ઉમેરાયા!"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("વિષયવાર ૨૦૦ પ્રશ્નો ઓટો ભરો (Fill 200 Questions)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Action Buttons: Add from Bank & Create Directly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedQuestionIdsToAdd = emptySet()
                                showAddFromBankModal = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LibraryAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("પ્રશ્ન બેંકમાંથી", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showCreateQuestionInsideTestModal = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("નવો પ્રશ્ન રચો", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                        if (testQuestions.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("હજી આ ટેસ્ટમાં કોઈ પ્રશ્નો ઉમેરાયા નથી. ઉપરના બટનો દ્વારા ઉમેરો.")
                                }
                            }
                        } else {
                            itemsIndexed(testQuestions) { idx, q ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${idx + 1}.", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(q.questionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
                                            Text("${q.subject} | સાચો: ${q.correctOption}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                        IconButton(onClick = { mainViewModel.removeQuestionFromTest(test.id, q.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "હટાવો", tint = CrimsonError, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedManageTest = null }) {
                    Text("સંપન્ન (Done)")
                }
            }
        )
    }

    // Add multiple questions from Question Bank
    if (showAddFromBankModal && selectedManageTest != null) {
        val test = selectedManageTest!!
        AlertDialog(
            onDismissRequest = { showAddFromBankModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("પ્રશ્ન બેંકમાંથી પસંદ કરો")
                    Text("પસંદ: ${selectedQuestionIdsToAdd.size}", fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 380.dp)) {
                    items(allQuestions) { q ->
                        val isChecked = selectedQuestionIdsToAdd.contains(q.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedQuestionIdsToAdd = if (isChecked) {
                                        selectedQuestionIdsToAdd - q.id
                                    } else {
                                        selectedQuestionIdsToAdd + q.id
                                    }
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedQuestionIdsToAdd = if (checked) {
                                        selectedQuestionIdsToAdd + q.id
                                    } else {
                                        selectedQuestionIdsToAdd - q.id
                                    }
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(q.questionText, fontSize = 13.sp, maxLines = 2, fontWeight = FontWeight.Medium)
                                Text("${q.subject} | ${q.difficulty}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Divider()
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.batchAddQuestionsToTest(test.id, selectedQuestionIdsToAdd.toList())
                        showAddFromBankModal = false
                    },
                    enabled = selectedQuestionIdsToAdd.isNotEmpty()
                ) {
                    Text("પસંદ કરેલા ઉમેરો (${selectedQuestionIdsToAdd.size})")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFromBankModal = false }) { Text("રદ કરો") }
            }
        )
    }

    // Create New Question Directly inside Test Builder Modal
    if (showCreateQuestionInsideTestModal && selectedManageTest != null) {
        val test = selectedManageTest!!
        var qText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf("A") }
        var sub by remember { mutableStateOf("જનરલ નોલેજ") }
        var expl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateQuestionInsideTestModal = false },
            title = { Text("સીધો ટેસ્ટમાં પ્રશ્ન ઉમેરો (Create Question)") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("પ્રશ્ન લખાણ") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("વિકલ્પ A") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("વિકલ્પ B") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("વિકલ્પ C") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("વિકલ્પ D") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = correct, onValueChange = { correct = it.uppercase().take(1) }, label = { Text("સાચો વિકલ્પ (A/B/C/D)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("વિષય (Subject)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = expl, onValueChange = { expl = it }, label = { Text("સમજૂતી (Explanation)") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isNotBlank() && optA.isNotBlank()) {
                            mainViewModel.addQuestionManually(
                                QuestionEntity(
                                    examId = test.examId,
                                    questionText = qText.trim(),
                                    optionA = optA.trim(),
                                    optionB = optB.trim(),
                                    optionC = optC.trim(),
                                    optionD = optD.trim(),
                                    correctOption = correct.ifBlank { "A" },
                                    subject = sub.trim(),
                                    topic = "ટેસ્ટ વિશેષ",
                                    explanation = expl.trim()
                                ),
                                testId = test.id
                            )
                            showCreateQuestionInsideTestModal = false
                        }
                    }
                ) {
                    Text("ટેસ્ટમાં ઉમેરો")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateQuestionInsideTestModal = false }) { Text("રદ કરો") }
            }
        )
    }

    // Validation Warning Dialog
    if (validationWarningMessage != null) {
        AlertDialog(
            onDismissRequest = { validationWarningMessage = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning) },
            title = { Text("પ્રશ્ન સંખ્યા અધૂરી છે") },
            text = { Text(validationWarningMessage ?: "") },
            confirmButton = {
                Button(
                    onClick = {
                        validationWarningMessage = null
                    }
                ) {
                    Text("સમજ્યા (પ્રશ્નો ઉમેરો)")
                }
            }
        )
    }
}
