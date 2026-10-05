package com.example.ui.screens.test

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuestionStatus
import com.example.ui.viewmodel.TestEngineViewModel

// High-Contrast CBRT Dark Theme Constants (WCAG AAA compliant)
private val CbrtBackground = Color(0xFF0F172A)         // Deep slate background (#0F172A)
private val CbrtCardBackground = Color(0xFF1E293B)     // Dark slate card surface (#1E293B)
private val CbrtCardBorder = Color(0xFF334155)         // Slate 700 crisp border (#334155)
private val CbrtTextWhite = Color(0xFFFFFFFF)          // Pure bright white (#FFFFFF) for question & options
private val CbrtTextLight = Color(0xFFF1F5F9)          // Slate 100 high-contrast text (#F1F5F9)
private val CbrtTextMuted = Color(0xFF94A3B8)          // Slate 400 secondary text (#94A3B8)
private val CbrtSelectedCardBg = Color(0xFF1E3A8A).copy(alpha = 0.55f) // High-contrast selected option bg
private val CbrtSelectedBorder = Color(0xFF38BDF8)     // Vibrant sky blue selected border (#38BDF8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestEngineScreen(
    testEngineViewModel: TestEngineViewModel,
    onTestSubmitted: (Int) -> Unit,
    onExitTest: () -> Unit
) {
    val state by testEngineViewModel.uiState.collectAsState()
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var showExitWarningDialog by remember { mutableStateOf(false) }
    var showPaletteSheet by remember { mutableStateOf(false) }

    // Prevent accidental back navigation
    BackHandler {
        if (!state.isSubmitted) {
            showExitWarningDialog = true
        } else {
            onExitTest()
        }
    }

    LaunchedEffect(state.isSubmitted, state.submissionResult) {
        if (state.isSubmitted && state.submissionResult != null) {
            onTestSubmitted(state.submissionResult?.id ?: 0)
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CbrtBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF38BDF8))
        }
        return
    }

    val currentQ = state.currentQuestion

    Scaffold(
        containerColor = CbrtBackground,
        topBar = {
            // Realistic CBRT Header with Dark Console Aesthetic
            Surface(
                color = Color(0xFF0B1329),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Logo & Branding
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberWarning),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tantaniya Academy CBRT",
                                    fontWeight = FontWeight.Bold,
                                    color = CbrtTextWhite,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = state.test?.title ?: "પરીક્ષા",
                                    color = CbrtTextLight.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Right: Real-time Countdown Timer Badge with Strong Contrast
                        val isLowTime = state.remainingSeconds < 300
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isLowTime) CrimsonError else Color(0xFF1E293B),
                            border = BorderStroke(
                                1.dp,
                                if (isLowTime) Color(0xFFF87171) else Color(0xFF38BDF8).copy(alpha = 0.6f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CbrtTextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "બાકી સમય: ${state.formattedRemainingTime}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CbrtTextWhite,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Section Navigation Tabs (Subject / Part Selector)
                    if (state.sections.size > 1) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.sections) { section ->
                                val isSelected = state.selectedSection == section
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { testEngineViewModel.selectSection(section) },
                                    color = if (isSelected) AmberWarning else Color(0xFF1E293B),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AmberWarning else Color(0xFF475569)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = section,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF0F172A) else CbrtTextLight,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // CBRT Bottom Action Bar
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color(0xFF0B1329)
            ) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                    HorizontalDivider(color = CbrtCardBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button
                        OutlinedButton(
                            onClick = { testEngineViewModel.goToPreviousQuestion() },
                            enabled = state.currentQuestionIndex > 0,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = CbrtTextLight,
                                disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.4f),
                                disabledContentColor = Color(0xFF64748B)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (state.currentQuestionIndex > 0) Color(0xFF475569) else Color(0xFF334155)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("cbrt_prev_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("પાછળ (Previous)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        // Mark for Review & Next Button
                        OutlinedButton(
                            onClick = { testEngineViewModel.markForReviewAndNext() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2E1065),
                                contentColor = Color(0xFFDDD6FE)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("cbrt_review_next_button")
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("રિવ્યુ & આગળ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Save & Next Button
                        Button(
                            onClick = { testEngineViewModel.saveAndNext() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB),
                                contentColor = CbrtTextWhite
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("cbrt_save_next_button")
                        ) {
                            Text("સાચવો & આગળ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // Low Time Warning Alert Banner
            if (state.timerWarningMessage != null) {
                Surface(
                    color = Color(0xFF7F1D1D),
                    border = BorderStroke(1.dp, Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFECACA), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                state.timerWarningMessage ?: "",
                                color = Color(0xFFFECACA),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = { testEngineViewModel.dismissTimerWarning() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "બંધ કરો", tint = Color(0xFFFECACA), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // CBRT Status Counter Row & Palette Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CbrtCardBackground),
                border = BorderStroke(1.dp, CbrtCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Question Status Counts
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        CbrtBadge(count = state.answeredCount, label = "જવાબ આપેલ", color = Color(0xFF4ADE80), bgColor = Color(0xFF064E3B))
                        CbrtBadge(count = state.markedCount, label = "રિવ્યુ", color = Color(0xFFC084FC), bgColor = Color(0xFF3B0764))
                        CbrtBadge(count = state.unattemptedCount, label = "બાકી", color = Color(0xFFFCA5A5), bgColor = Color(0xFF7F1D1D))
                    }

                    // Question Palette Toggle & Submit
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showPaletteSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("open_palette_button")
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, tint = CbrtTextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("પેલેટ (${state.questions.size})", color = CbrtTextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { showSubmitConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("submit_test_button")
                        ) {
                            Text("સબમિટ", color = CbrtTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (currentQ != null) {
                // Question Header Card - Dark Card with Bright Light Gujarati Question Text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CbrtCardBackground),
                    border = BorderStroke(1.dp, CbrtCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Question Number with High-Contrast Light Blue
                            Text(
                                text = "પ્રશ્ન ક્રમાંક: ${state.currentQuestionIndex + 1} / ${state.questions.size}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA),
                                    fontSize = 15.sp
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = Color(0xFF1E3A8A),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentQ.subject,
                                        color = Color(0xFF93C5FD),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = Color(0xFF334155),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+${currentQ.marks} | -${currentQ.negativeMarks}",
                                        color = CbrtTextLight,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // GUJARATI QUESTION TEXT: Bright Pure White (#FFFFFF), SemiBold (600), High Contrast
                        Text(
                            text = currentQ.questionText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                color = CbrtTextWhite
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Options with large radio buttons and bright white option text
                val selectedOption = state.userAnswers[currentQ.id]

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CbrtRadioOption(
                        label = "A",
                        text = currentQ.optionA,
                        isSelected = selectedOption == "A",
                        onSelect = { testEngineViewModel.selectOption("A") }
                    )
                    CbrtRadioOption(
                        label = "B",
                        text = currentQ.optionB,
                        isSelected = selectedOption == "B",
                        onSelect = { testEngineViewModel.selectOption("B") }
                    )
                    CbrtRadioOption(
                        label = "C",
                        text = currentQ.optionC,
                        isSelected = selectedOption == "C",
                        onSelect = { testEngineViewModel.selectOption("C") }
                    )
                    CbrtRadioOption(
                        label = "D",
                        text = currentQ.optionD,
                        isSelected = selectedOption == "D",
                        onSelect = { testEngineViewModel.selectOption("D") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clear Response & Review quick toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isMarked = state.markedForReview.contains(currentQ.id)
                    TextButton(onClick = { testEngineViewModel.toggleMarkForReview() }) {
                        Icon(
                            if (isMarked) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isMarked) Color(0xFFA78BFA) else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMarked) "સમીક્ષા માટે ચિહ્નિત છે" else "સમીક્ષા માટે ચિહ્નિત કરો",
                            color = if (isMarked) Color(0xFFA78BFA) else Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    if (selectedOption != null) {
                        TextButton(
                            onClick = { testEngineViewModel.clearAnswer() },
                            modifier = Modifier.testTag("clear_response_button")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "જવાબ સાફ કરો (Clear Response)",
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Question Palette Drawer / Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = Color(0xFF1E293B),
            contentColor = CbrtTextLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "પ્રશ્ન પેલેટ (Question Palette)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CbrtTextWhite)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "કોઈપણ પ્રશ્ન પર સીધા જવા માટે નંબર પર ક્લિક કરો.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accessible Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CbrtLegendItem(color = EmeraldSuccess, label = "🟢 જવાબ આપેલ")
                    CbrtLegendItem(color = CrimsonError, label = "🔴 જોયેલ-બાકી")
                    CbrtLegendItem(color = Color(0xFFA78BFA), label = "🟣 રિવ્યુ")
                    CbrtLegendItem(color = Color(0xFF64748B), label = "⚪ જોયેલ નથી")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                ) {
                    itemsIndexed(state.questions) { index, q ->
                        val status = state.getStatusForQuestion(q.id)
                        val isCurrent = index == state.currentQuestionIndex

                        val (bgColor, textColor, borderStroke) = when (status) {
                            QuestionStatus.ANSWERED -> Triple(EmeraldSuccess, Color.White, null)
                            QuestionStatus.ANSWERED_AND_MARKED -> Triple(Color(0xFF7C3AED), Color.White, BorderStroke(2.dp, EmeraldSuccess))
                            QuestionStatus.MARKED_FOR_REVIEW -> Triple(Color(0xFF7C3AED), Color.White, null)
                            QuestionStatus.NOT_ANSWERED -> Triple(CrimsonError, Color.White, null)
                            QuestionStatus.NOT_VISITED -> Triple(Color(0xFF334155), CbrtTextLight, BorderStroke(1.dp, Color(0xFF64748B)))
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .then(
                                    if (borderStroke != null) Modifier.border(borderStroke.width, borderStroke.brush, RoundedCornerShape(8.dp))
                                    else if (isCurrent) Modifier.border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                                    else Modifier
                                )
                                .clickable {
                                    testEngineViewModel.goToQuestion(index)
                                    showPaletteSheet = false
                                }
                                .testTag("palette_cell_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            containerColor = Color(0xFF1E293B),
            titleContentColor = CbrtTextWhite,
            textContentColor = CbrtTextLight,
            icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = Color(0xFF38BDF8)) },
            title = { Text("ટેસ્ટ સબમિટ કરવો છે?", fontWeight = FontWeight.Bold, color = CbrtTextWhite) },
            text = {
                Column {
                    Text("પરીક્ષા સબમિટ કરતાં પહેલાં તમારી સમીક્ષા:", color = CbrtTextLight)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• કુલ પ્રશ્નો: ${state.questions.size}", color = CbrtTextLight)
                    Text("• ઉત્તર આપેલા: ${state.answeredCount}", color = Color(0xFF4ADE80), fontWeight = FontWeight.SemiBold)
                    Text("• સમીક્ષા માટે રાખેલા: ${state.markedCount}", color = Color(0xFFC084FC), fontWeight = FontWeight.SemiBold)
                    Text("• ઉત્તર આપવાના બાકી: ${state.unattemptedCount}", color = Color(0xFFFCA5A5), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("સબમિટ કર્યા પછી તમારું પરિણામ અને સમજૂતી તરત જ તૈયાર થશે.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        testEngineViewModel.submitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_submit_button")
                ) {
                    Text("હા, સબમિટ કરો", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSubmitConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CbrtTextLight),
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) {
                    Text("ના, ચાલુ રાખો")
                }
            }
        )
    }

    // Exit Warning Dialog
    if (showExitWarningDialog) {
        AlertDialog(
            onDismissRequest = { showExitWarningDialog = false },
            containerColor = Color(0xFF1E293B),
            titleContentColor = CbrtTextWhite,
            textContentColor = CbrtTextLight,
            title = { Text("ટેસ્ટમાંથી બહાર નીકળવું છે?", fontWeight = FontWeight.Bold, color = CbrtTextWhite) },
            text = {
                Text(
                    "જો તમે અત્યારે બહાર નીકળશો તો તમે આપેલા જવાબો સાચવી શકાશે નહીં. શું તમે ખરેખર બહાર જવા માંગો છો?",
                    color = CbrtTextLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitWarningDialog = false
                        onExitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError, contentColor = Color.White)
                ) {
                    Text("બહાર નીકળો", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitWarningDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CbrtTextLight),
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) {
                    Text("ચાલુ રાખો")
                }
            }
        )
    }
}

/**
 * High-Contrast CBRT Option Card
 *
 * Keeps the dark card aesthetic while ensuring:
 * 1. Bright pure white (#FFFFFF) option text (Gujarati is sharp and fully legible).
 * 2. High contrast option label badge (A/B/C/D).
 * 3. Clearly distinguished selected state with vibrant sky blue border and glowing container.
 * 4. Zero black/dark-on-dark inherited styles.
 */
@Composable
fun CbrtRadioOption(
    label: String,
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val cardBg = if (isSelected) CbrtSelectedCardBg else CbrtCardBackground
    val borderStroke = if (isSelected) {
        BorderStroke(2.dp, CbrtSelectedBorder)
    } else {
        BorderStroke(1.dp, CbrtCardBorder)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .border(borderStroke.width, borderStroke.brush, RoundedCornerShape(12.dp))
            .testTag("cbrt_option_${label}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF38BDF8),
                    unselectedColor = Color(0xFF94A3B8)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    color = CbrtTextWhite,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = CbrtTextWhite // Always bright pure white #FFFFFF for maximum contrast!
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CbrtBadge(count: Int, label: String, color: Color, bgColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$count", fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CbrtLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = CbrtTextLight,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
