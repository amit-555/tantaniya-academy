package com.example.ui.screens.admin

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeneratedMcq
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAiGeneratorScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val isGenerating by mainViewModel.isAiGenerating.collectAsState()
    val statusMessage by mainViewModel.aiStatusMessage.collectAsState()
    val generatedQuestions by mainViewModel.aiGeneratedQuestions.collectAsState()
    val allTests by mainViewModel.allTests.collectAsState()
    val geminiApiKey by mainViewModel.geminiApiKey.collectAsState()

    var subject by remember { mutableStateOf("ભારતીય બંધારણ") }
    var topic by remember { mutableStateOf("મૂળભૂત અધિકારો અને ફરજો") }
    var difficulty by remember { mutableStateOf("મધ્યમ") }
    var requestedCountText by remember { mutableStateOf("5") }
    var referenceText by remember { mutableStateOf("") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingQuestion by remember { mutableStateOf<GeneratedMcq?>(null) }

    var showTargetTestDialog by remember { mutableStateOf(false) }
    var selectedTargetTestId by remember { mutableStateOf<Int?>(null) }
    var successToast by remember { mutableStateOf<String?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
        }
    }

    val subjects = listOf("ભારતીય બંધારણ", "ગુજરાતનો ઇતિહાસ", "ગુજરાતની ભૂગોળ", "રીઝનિંગ", "ગણિત", "સામાન્ય વિજ્ઞાન", "ગુજરાતી વ્યાકરણ", "કરંટ અફેર્સ")
    val difficulties = listOf("સરળ", "મધ્યમ", "કઠિન")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "AI પ્રશ્ન નિર્માતા (AI Question Generator)",
                subtitle = "Gemini AI આધારિત ગુજરાતી MCQ નિર્માણ",
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "પ્રશ્ન નિર્માણ પરિમાણો (Generation Parameters)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Subject Selector Dropdown / Row
                        Text("વિષય પસંદ કરો (Subject):", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("બંધારણ", "ઇતિહાસ", "ભૂગોળ", "વિજ્ઞાન", "ગણિત").forEach { subItem ->
                                FilterChip(
                                    selected = subject.contains(subItem),
                                    onClick = {
                                        subject = when (subItem) {
                                            "બંધારણ" -> "ભારતીય બંધારણ"
                                            "ઇતિહાસ" -> "ગુજરાતનો ઇતિહાસ"
                                            "ભૂગોળ" -> "ગુજરાતની ભૂગોળ"
                                            "વિજ્ઞાન" -> "સામાન્ય વિજ્ઞાન"
                                            else -> "ગણિત અને રીઝનિંગ"
                                        }
                                    },
                                    label = { Text(subItem, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("ચોક્કસ ટોપિક (Topic Name)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_topic_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = requestedCountText,
                                onValueChange = { requestedCountText = it },
                                label = { Text("પ્રશ્નોની સંખ્યા (Count)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_count_input"),
                                singleLine = true
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text("મુશ્કેલી સ્તર:", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    difficulties.forEach { diff ->
                                        FilterChip(
                                            selected = difficulty == diff,
                                            onClick = { difficulty = diff },
                                            label = { Text(diff, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Camera / Image to MCQ Section
                        Text("ફોટો અથવા દસ્તાવેજમાંથી પ્રશ્નો (Photo to MCQ):", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_camera_capture_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("કેમેરા ફોટો લો", fontSize = 12.sp)
                            }

                            if (capturedBitmap != null) {
                                OutlinedButton(
                                    onClick = { capturedBitmap = null },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonError)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("હટાવો", fontSize = 11.sp)
                                }
                            }
                        }

                        if (capturedBitmap != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(EmeraldLight, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                Image(
                                    bitmap = capturedBitmap!!.asImageBitmap(),
                                    contentDescription = "કેમેરા ઇમેજ",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("ફોટો જોડાયેલ છે! AI આ ફોટામાંથી પ્રશ્નો બનાવશે.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF065F46)))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = referenceText,
                            onValueChange = { referenceText = it },
                            label = { Text("વધારાનું સંદર્ભ લખાણ (Optional Reference Text)") },
                            placeholder = { Text("કોઈ પુસ્તકનો ફકરો અથવા મહત્વના મુદ્દા અહીં પેસ્ટ કરો...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val count = requestedCountText.toIntOrNull() ?: 5
                                mainViewModel.generateAiQuestions(
                                    subject = subject,
                                    topic = topic,
                                    difficulty = difficulty,
                                    count = count,
                                    imageBitmap = capturedBitmap,
                                    extractedText = referenceText.ifBlank { null }
                                )
                            },
                            enabled = !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_ai_questions_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("AI પ્રશ્નો તૈયાર કરી રહ્યું છે...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI પ્રશ્નો બનાવો (Generate MCQs)", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statusMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (statusMessage!!.contains("ભૂલ")) CrimsonError else EmeraldSuccess
                                )
                            )
                        }
                    }
                }
            }

            // Generated Questions Preview List
            if (generatedQuestions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "તૈયાર થયેલ પ્રશ્નોની સમીક્ષા (${generatedQuestions.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        val selectedCount = generatedQuestions.count { it.isSelected }
                        StatusBadge(
                            text = "પસંદ કરેલ: $selectedCount / ${generatedQuestions.size}",
                            textColor = NavyPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

                // Batch Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                mainViewModel.saveGeneratedToQuestionBank { count ->
                                    successToast = "$count પ્રશ્નો પ્રશ્ન બેંકમાં ઉમેરાઈ ગયા!"
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_to_question_bank_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("પ્રશ્ન બેંકમાં સેવ કરો", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showTargetTestDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_to_test_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AddToQueue, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ટેસ્ટમાં ઉમેરો", fontSize = 12.sp)
                        }
                    }
                }

                itemsIndexed(generatedQuestions) { index, item ->
                    GeneratedMcqPreviewCard(
                        index = index + 1,
                        mcq = item,
                        onToggleSelect = { mainViewModel.toggleGeneratedSelection(index) },
                        onEdit = {
                            editingIndex = index
                            editingQuestion = item.copy()
                        },
                        onDelete = { mainViewModel.deleteGeneratedQuestion(index) }
                    )
                }
            }
        }
    }

    // Edit Generated Question Dialog
    if (editingQuestion != null && editingIndex != null) {
        val q = editingQuestion!!
        var qText by remember { mutableStateOf(q.questionText) }
        var optA by remember { mutableStateOf(q.optionA) }
        var optB by remember { mutableStateOf(q.optionB) }
        var optC by remember { mutableStateOf(q.optionC) }
        var optD by remember { mutableStateOf(q.optionD) }
        var correct by remember { mutableStateOf(q.correctOption) }
        var expl by remember { mutableStateOf(q.explanation) }

        AlertDialog(
            onDismissRequest = {
                editingIndex = null
                editingQuestion = null
            },
            title = { Text("પ્રશ્ન સંપાદિત કરો (Edit Question)") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = qText,
                            onValueChange = { qText = it },
                            label = { Text("પ્રશ્ન લખાણ") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("વિકલ્પ A") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("વિકલ્પ B") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("વિકલ્પ C") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("વિકલ્પ D") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = correct,
                            onValueChange = { correct = it.uppercase().take(1) },
                            label = { Text("સાચો વિકલ્પ (A/B/C/D)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = expl,
                            onValueChange = { expl = it },
                            label = { Text("સમજૂતી (Explanation)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = q.copy(
                            questionText = qText,
                            optionA = optA,
                            optionB = optB,
                            optionC = optC,
                            optionD = optD,
                            correctOption = correct,
                            explanation = expl
                        )
                        mainViewModel.updateGeneratedQuestion(editingIndex!!, updated)
                        editingIndex = null
                        editingQuestion = null
                    }
                ) {
                    Text("સાચવો (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    editingIndex = null
                    editingQuestion = null
                }) {
                    Text("રદ કરો")
                }
            }
        )
    }

    // Target Test Selection Dialog
    if (showTargetTestDialog) {
        AlertDialog(
            onDismissRequest = { showTargetTestDialog = false },
            title = { Text("ટેસ્ટ પસંદ કરો જેમાં પ્રશ્નો ઉમેરવા છે") },
            text = {
                Column {
                    if (allTests.isEmpty()) {
                        Text("હજી સુધી કોઈ ટેસ્ટ બનેલો નથી. પહેલાં ટેસ્ટ મેનેજરમાંથી નવો ટેસ્ટ બનાવો.")
                    } else {
                        allTests.forEach { test ->
                            ListItem(
                                headlineContent = { Text(test.title, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("${test.durationMinutes} મિનિટ | ${test.totalMarks} ગુણ") },
                                trailingContent = {
                                    Button(
                                        onClick = {
                                            mainViewModel.saveGeneratedToQuestionBank(targetTestId = test.id) { count ->
                                                showTargetTestDialog = false
                                                successToast = "$count પ્રશ્નો '${test.title}' ટેસ્ટમાં ઉમેરાયા!"
                                            }
                                        }
                                    ) {
                                        Text("ઉમેરો")
                                    }
                                }
                            )
                            Divider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTargetTestDialog = false }) {
                    Text("બંધ કરો")
                }
            }
        )
    }

    if (successToast != null) {
        AlertDialog(
            onDismissRequest = { successToast = null },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess) },
            title = { Text("સફળતા!") },
            text = { Text(successToast ?: "") },
            confirmButton = {
                Button(onClick = { successToast = null }) {
                    Text("ઠીક છે")
                }
            }
        )
    }
}

@Composable
fun GeneratedMcqPreviewCard(
    index: Int,
    mcq: GeneratedMcq,
    onToggleSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mcq.isSelected) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = mcq.isSelected,
                        onCheckedChange = { onToggleSelect() }
                    )
                    Text(
                        text = "પ્રશ્ન #$index",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "સંપાદિત કરો", modifier = Modifier.size(18.dp), tint = NavyPrimary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", modifier = Modifier.size(18.dp), tint = CrimsonError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mcq.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("A. ${mcq.optionA}", fontSize = 12.sp, color = if (mcq.correctOption == "A") EmeraldSuccess else TextPrimary, fontWeight = if (mcq.correctOption == "A") FontWeight.Bold else FontWeight.Normal)
                Text("B. ${mcq.optionB}", fontSize = 12.sp, color = if (mcq.correctOption == "B") EmeraldSuccess else TextPrimary, fontWeight = if (mcq.correctOption == "B") FontWeight.Bold else FontWeight.Normal)
                Text("C. ${mcq.optionC}", fontSize = 12.sp, color = if (mcq.correctOption == "C") EmeraldSuccess else TextPrimary, fontWeight = if (mcq.correctOption == "C") FontWeight.Bold else FontWeight.Normal)
                Text("D. ${mcq.optionD}", fontSize = 12.sp, color = if (mcq.correctOption == "D") EmeraldSuccess else TextPrimary, fontWeight = if (mcq.correctOption == "D") FontWeight.Bold else FontWeight.Normal)
            }

            if (mcq.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = SlateSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ${mcq.explanation}",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
