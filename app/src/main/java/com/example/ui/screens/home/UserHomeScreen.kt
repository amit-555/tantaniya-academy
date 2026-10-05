package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.TestEntity
import com.example.ui.components.QuickNavTile
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    onStartTest: (Int) -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToCurrentAffairs: () -> Unit,
    onNavigateToGk: () -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val user = authState.currentUser
    val publishedTests by mainViewModel.publishedTests.collectAsState()
    val allExams by mainViewModel.allExams.collectAsState()
    val currentAffairs by mainViewModel.currentAffairsList.collectAsState()
    val gkList by mainViewModel.gkList.collectAsState()
    val selectedExamId by mainViewModel.selectedExamId.collectAsState()

    val totalTestsCount by mainViewModel.totalTestsCount.collectAsState()
    val totalQuestionsCount by mainViewModel.totalQuestionsCount.collectAsState()

    val displayedTests = remember(publishedTests, selectedExamId) {
        publishedTests.sortedBy { if (it.examId == selectedExamId) 0 else 1 }
    }

    var showExamSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "Tantaniya Academy",
                subtitle = user?.targetExam ?: "ગુજરાત પોલીસ કોન્સ્ટેબલ",
                actions = {
                    if (user?.role == "ADMIN") {
                        IconButton(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.testTag("admin_panel_top_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "એડમિન પેનલ",
                                tint = AmberWarning
                            )
                        }
                    }
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier.testTag("profile_top_icon")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name?.take(1)?.uppercase() ?: "U",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
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
            // 1. Welcome Greeting Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "નમસ્તે, ${user?.name ?: "વિદ્યાર્થી મિત્ર"}! 👋",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                    if (com.example.data.repository.AuthConfig.isPrimaryAdmin(user?.email) && user?.role == "ADMIN") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = AmberWarning,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "👑 Primary Admin",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (user?.role == "ADMIN") "રોલ: મુખ્ય પ્રબંધક (Administrator)" else "લક્ષ્ય: ${user?.targetExam ?: "LRD કોન્સ્ટેબલ"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                        fontWeight = if (user?.role == "ADMIN") FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }
                            AssistChip(
                                onClick = { showExamSelector = true },
                                label = { Text("પરીક્ષા બદલો", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        if (user?.role == "ADMIN") {
                            val isPrimary = com.example.data.repository.AuthConfig.isPrimaryAdmin(user?.email)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToAdmin,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("home_admin_panel_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPrimary) Icons.Default.VerifiedUser else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (isPrimary) AmberWarning else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPrimary) "પ્રાથમિક એડમિન પેનલ (Admin Panel)" else "એડમિન પેનલ (Admin Panel)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Dynamic Target Exams Carousel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "પરીક્ષાઓ (Target Exams)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    val pubCount = allExams.count { it.isPublished }
                    StatusBadge(
                        text = "કુલ $pubCount પરીક્ષાઓ",
                        textColor = NavyPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            item {
                val publishedExamsList = allExams.filter { it.isPublished }
                if (publishedExamsList.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("કોઈ પરીક્ષા ઉપલબ્ધ નથી")
                        }
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(publishedExamsList) { exam ->
                            val isSelected = (selectedExamId == exam.id) || (user?.targetExam != null && user.targetExam.contains(exam.title))
                            Card(
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable {
                                        authViewModel.updateTargetExam(exam.title)
                                        mainViewModel.setSelectedExam(exam.id)
                                    }
                                    .testTag("exam_card_${exam.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NavyPrimary) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatusBadge(
                                            text = exam.code,
                                            textColor = if (isSelected) NavyDark else NavyPrimary,
                                            backgroundColor = if (isSelected) AmberWarning else MaterialTheme.colorScheme.primaryContainer
                                        )
                                        if (isSelected) {
                                            StatusBadge(
                                                text = "✓ સક્રિય લક્ષ્ય",
                                                textColor = EmeraldSuccess,
                                                backgroundColor = EmeraldLight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = exam.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        color = if (isSelected) NavyDark else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = exam.description,
                                        fontSize = 11.sp,
                                        color = if (isSelected) NavyDark.copy(alpha = 0.8f) else TextSecondary,
                                        maxLines = 2,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${exam.totalMarks} ગુણ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${exam.durationMinutes} મિનિટ", fontSize = 11.sp, color = TextSecondary)
                                        Text("-${exam.negativeMarking}", fontSize = 11.sp, color = CrimsonError)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "મોક ટેસ્ટ",
                        value = publishedTests.size.toString(),
                        icon = Icons.Default.Assignment,
                        iconTint = NavyLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "પ્રશ્ન બેંક",
                        value = totalQuestionsCount.toString(),
                        icon = Icons.Default.HelpCenter,
                        iconTint = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Quick Navigation Hub
            item {
                Text(
                    text = "ઝડપી પ્રવેશ (Quick Navigation)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickNavTile(
                            title = "મોક ટેસ્ટ",
                            subtitle = "પરીક્ષા આપો",
                            icon = Icons.Default.Quiz,
                            iconBgColor = NavyPrimary,
                            onClick = onNavigateToTests,
                            modifier = Modifier.weight(1f)
                        )
                        QuickNavTile(
                            title = "પ્રશ્ન બેંક",
                            subtitle = "સંપૂર્ણ પ્રેક્ટિસ",
                            icon = Icons.Default.MenuBook,
                            iconBgColor = EmeraldSuccess,
                            onClick = onNavigateToQuestions,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickNavTile(
                            title = "કરંટ અફેર્સ",
                            subtitle = "તાજા સમાચાર",
                            icon = Icons.Default.Article,
                            iconBgColor = AmberWarning,
                            onClick = onNavigateToCurrentAffairs,
                            modifier = Modifier.weight(1f)
                        )
                        QuickNavTile(
                            title = "જનરલ નોલેજ",
                            subtitle = "GK ફેક્ટ્સ",
                            icon = Icons.Default.Lightbulb,
                            iconBgColor = Color(0xFF8B5CF6),
                            onClick = onNavigateToGk,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickNavTile(
                            title = "અભ્યાસ સામગ્રી",
                            subtitle = "શોર્ટ નોટ્સ",
                            icon = Icons.Default.LibraryBooks,
                            iconBgColor = Color(0xFF0EA5E9),
                            onClick = onNavigateToMaterials,
                            modifier = Modifier.weight(1f)
                        )
                        QuickNavTile(
                            title = "પરીક્ષા સિલેબસ",
                            subtitle = "વિષયવાર ગુણભાર",
                            icon = Icons.Default.FormatListNumbered,
                            iconBgColor = Color(0xFFEC4899),
                            onClick = onNavigateToSyllabus,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickNavTile(
                            title = "મારા પરિણામો",
                            subtitle = "પરફોર્મન્સ ટ્રેક",
                            icon = Icons.Default.TrendingUp,
                            iconBgColor = Color(0xFF059669),
                            onClick = onNavigateToResults,
                            modifier = Modifier.weight(1f)
                        )
                        QuickNavTile(
                            title = "લીડરબોર્ડ",
                            subtitle = "ટોપ રેન્કિંગ",
                            icon = Icons.Default.EmojiEvents,
                            iconBgColor = Color(0xFFF59E0B),
                            onClick = onNavigateToLeaderboard,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. Available Mock Tests Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ઉપલબ્ધ મોક ટેસ્ટ (Available Tests)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onNavigateToTests) {
                        Text("બધા જુઓ (${publishedTests.size})")
                    }
                }
            }

            if (displayedTests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("હાલમાં કોઈ મોક ટેસ્ટ પ્રકાશિત નથી.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(displayedTests) { test ->
                    HomeTestCard(
                        test = test,
                        onStartClick = { onStartTest(test.id) }
                    )
                }
            }

            // 5. Daily Current Affairs / GK Snippet
            item {
                val latestCa = currentAffairs.firstOrNull()
                val latestGk = gkList.firstOrNull()
                if (latestCa != null || latestGk != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Newspaper, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "આજનો કરંટ અફેર્સ / GK પ્રવાહ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (latestCa != null) {
                                Text(
                                    text = "• ${latestCa.headline}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = latestCa.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    maxLines = 2
                                )
                            }
                            if (latestGk != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "💡 વિશેષ ફેક્ટ: ${latestGk.oneLinerFact}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExamSelector) {
        AlertDialog(
            onDismissRequest = { showExamSelector = false },
            title = { Text("પરીક્ષા પસંદ કરો (Target Exam)") },
            text = {
                Column {
                    allExams.forEach { exam ->
                        ListItem(
                            headlineContent = { Text(exam.title, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(exam.description, maxLines = 1) },
                            modifier = Modifier
                                .clickable {
                                    authViewModel.updateTargetExam(exam.title)
                                    mainViewModel.setSelectedExam(exam.id)
                                    showExamSelector = false
                                }
                        )
                        Divider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExamSelector = false }) {
                    Text("બંધ કરો")
                }
            }
        )
    }
}

@Composable
fun HomeTestCard(
    test: TestEntity,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_card_${test.id}"),
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
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = test.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 2
                    )
                }
                StatusBadge(
                    text = "પ્રકાશિત",
                    textColor = EmeraldSuccess,
                    backgroundColor = EmeraldLight
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Test Metadata Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${test.durationMinutes} મિનિટ", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp), tint = AmberWarning)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${test.totalMarks} ગુણ", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = CrimsonError)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("-${test.negativeMarking}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("start_test_button_${test.id}"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ટેસ્ટ શરૂ કરો (Start Test)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
