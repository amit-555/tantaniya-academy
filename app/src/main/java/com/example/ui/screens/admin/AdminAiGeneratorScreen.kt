package com.example.ui.screens.admin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    val isGenerating by mainViewModel.isAiGenerating.collectAsState()
    val statusMessage by mainViewModel.aiStatusMessage.collectAsState()
    val generatedQuestions by mainViewModel.aiGeneratedQuestions.collectAsState()
    val allTests by mainViewModel.allTests.collectAsState()
    val geminiApiKey by mainViewModel.geminiApiKey.collectAsState()

    var examTitle by remember { mutableStateOf("ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)") }
    var partName by remember { mutableStateOf("ભાગ-બ (Part B)") }
    var subject by remember { mutableStateOf("ભારતીય બંધારણ") }
    var topic by remember { mutableStateOf("મૂળભૂત અધિકારો") }
    var subtopic by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("મધ્યમ") }
    var requestedCountText by remember { mutableStateOf("10") }
    var referenceText by remember { mutableStateOf("") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraErrorMessage by remember { mutableStateOf<String?>(null) }

    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingQuestion by remember { mutableStateOf<GeneratedMcq?>(null) }

    var showTargetTestDialog by remember { mutableStateOf(false) }
    var selectedTargetTestId by remember { mutableStateOf<Int?>(null) }
    var successToast by remember { mutableStateOf<String?>(null) }

    // Safe Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            cameraErrorMessage = null
        }
    }

    // Runtime Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (_: Exception) {
                cameraErrorMessage = "કેમેરા ઉપલબ્ધ નથી અથવા ખોલવામાં સમસ્યા આવી."
            }
        } else {
            cameraErrorMessage = "કેમેરાની પરવાનગી જરૂરી છે. કૃપા કરીને Settings માં Camera Permission ચાલુ કરો."
        }
    }

    // Safe Gallery Picker fallback
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    capturedBitmap = bitmap
                    cameraErrorMessage = null
                }
            } catch (_: Exception) {
                cameraErrorMessage = "ઇમેજ લોડ કરવામાં સમસ્યા આવી."
            }
        }
    }

    val exams = listOf("ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)", "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક)", "MPHW")
    val parts = listOf("ભાગ-અ (Part A)", "ભાગ-બ (Part B)")
    val subjects = listOf("ભારતીય બંધારણ", "ગુજરાતની ભૂગોળ", "ગુજરાતનો ઇતિહાસ", "સામાન્ય વિજ્ઞાન", "રીઝનિંગ", "ગણિત")
    val difficulties = listOf("સરળ", "મધ્યમ", "કઠિન")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "AI પ્રશ્ન નિર્માતા (AI MCQ Generator)",
                subtitle = "Exam → Part → Subject → Topic મુજબ સચોટ પ્રશ્નો",
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
                            text = "પ્રશ્ન નિર્માણ પરિમાણો (Generation Hierarchy)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. Exam Selector
                        Text("૧. લક્ષ્ય પરીક્ષા (Exam):", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            exams.forEach { examItem ->
                                FilterChip(
                                    selected = examTitle == examItem,
                                    onClick = { examTitle = examItem },
                                    label = { Text(if (examItem.contains("કોન્સ્ટેબલ")) "કોન્સ્ટેબલ" else if (examItem.contains("ફોરેસ્ટ")) "ફોરેસ્ટ" else "MPHW", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Part Selector
                        Text("૨. વિભાગ (Part):", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            parts.forEach { p ->
                                FilterChip(
                                    selected = partName == p,
                                    onClick = { partName = p },
                                    label = { Text(p, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. Subject Selector
                        Text("૩. વિષય (Subject):", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            subjects.take(4).forEach { subItem ->
                                FilterChip(
                                    selected = subject == subItem,
                                    onClick = {
                                        subject = subItem
                                        topic = when (subItem) {
                                            "ભારતીય બંધારણ" -> "મૂળભૂત અધિકારો"
                                            "ગુજરાતની ભૂગોળ" -> "નદીઓ અને બંધો"
                                            "ગુજરાતનો ઇતિહાસ" -> "સોલંકી વંશ અને વાવ"
                                            "સામાન્ય વિજ્ઞાન" -> "વિટામિન અને માનવ શરીર"
                                            else -> "શ્રેણી અને કોડિંગ"
                                        }
                                    },
                                    label = { Text(subItem.take(6), fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Strict Topic Input
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("૪. ચોક્કસ ટોપિક (Strict Topic Name)*") },
                            placeholder = { Text("દા.ત. મૂળભૂત અધિકારો અથવા નદીઓ") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_topic_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5. Optional Subtopic Input
                        OutlinedTextField(
                            value = subtopic,
                            onValueChange = { subtopic = it },
                            label = { Text("સબ-ટોપિક (ઓપ્શનલ Subtopic)") },
                            placeholder = { Text("દા.ત. અનુચ્છેદ ૧૨ થી ૩૫ અથવા સરદાર સરોવર") },
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // 6. Source Material Section (Camera & Gallery)
                        Text("સ્ત્રોત સામગ્રી (Source Material - Optional):", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "* નોંધ: અપલોડ કરેલ સામગ્રીમાંથી ફક્ત ઉપર પસંદ કરેલ ટોપિક ($topic) ના જ પ્રશ્નો બનશે.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    cameraErrorMessage = null
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        try {
                                            cameraLauncher.launch(null)
                                        } catch (_: Exception) {
                                            cameraErrorMessage = "કેમેરા ઉપલબ્ધ નથી અથવા ખોલવામાં સમસ્યા આવી."
                                        }
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_camera_capture_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("કેમેરા ફોટો", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    cameraErrorMessage = null
                                    try {
                                        galleryLauncher.launch("image/*")
                                    } catch (_: Exception) {
                                        cameraErrorMessage = "ગેલેરી ખોલવામાં સમસ્યા આવી."
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ગેલેરીમાંથી લો", fontSize = 12.sp)
                            }

                            if (capturedBitmap != null) {
                                OutlinedButton(
                                    onClick = { capturedBitmap = null },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonError)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Camera Error Alert Message
                        cameraErrorMessage?.let { errMsg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errMsg,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { cameraErrorMessage = null },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "બંધ કરો", modifier = Modifier.size(14.dp))
                                    }
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
                                Text(
                                    "ઇમેજ જોડાયેલ છે! AI આ સામગ્રીમાંથી માત્ર '$topic' ના જ પ્રશ્નો બનાવશે.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF065F46))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = referenceText,
                            onValueChange = { referenceText = it },
                            label = { Text("વધારાનું સંદર્ભ લખાણ (Optional Reference Text)") },
                            placeholder = { Text("પુસ્તકનો ફકરો અથવા અભ્યાસ નોંધ અહીં પેસ્ટ કરો...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Configuration Summary Card before generation
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "જનરેશન પૂર્વાવલોકન (Configuration Summary):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• પરીક્ષા: $examTitle", fontSize = 11.sp)
                                Text("• વિભાગ: $partName | વિષય: $subject", fontSize = 11.sp)
                                Text(
                                    text = "• મુખ્ય ટોપિક: $topic",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (subtopic.isNotBlank()) {
                                    Text("• સબ-ટોપિક: $subtopic", fontSize = 11.sp)
                                }
                                Text("• પ્રશ્નોની સંખ્યા: ${requestedCountText.toIntOrNull() ?: 10}", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val count = requestedCountText.toIntOrNull() ?: 10
                                mainViewModel.generateAiQuestions(
                                    examTitle = examTitle,
                                    partName = partName,
                                    subject = subject,
                                    topic = topic,
                                    subtopic = subtopic,
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
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("પ્રશ્નો જનરેટ થઈ રહ્યા છે...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ટોપિક આધારિત પ્રશ્નો જનરેટ કરો")
                            }
                        }

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statusMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (statusMessage!!.contains("ભૂલ") || statusMessage!!.contains("સમસ્યા")) CrimsonError else EmeraldSuccess
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
