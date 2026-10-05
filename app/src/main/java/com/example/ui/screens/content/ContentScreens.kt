package com.example.ui.screens.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.data.entity.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

// --- CURRENT AFFAIRS SCREEN ---
@Composable
fun CurrentAffairsScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val caList by mainViewModel.currentAffairsList.collectAsState()
    val selectedCat by mainViewModel.caCategory.collectAsState()

    val categories = listOf("બધા", "ગુજરાત", "ભારત", "વિજ્ઞાન", "રમતગમત", "યોજનાઓ", "વિશ્વ", "અર્થતંત્ર")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "કરંટ અફેર્સ (Current Affairs)",
                subtitle = "દૈનિક મહત્વપૂર્ણ પ્રવાહો & પ્રશ્નો",
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
            // Category Filter Row
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = (cat == "બધા" && selectedCat.isBlank()) || cat == selectedCat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (cat == "બધા") mainViewModel.setCaCategory("")
                            else mainViewModel.setCaCategory(cat)
                        },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (caList.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("આ કેટેગરીમાં હાલ કોઈ સમાચાર નથી.")
                            }
                        }
                    }
                } else {
                    items(caList) { item ->
                        CurrentAffairsCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentAffairsCard(item: CurrentAffairsEntity) {
    var showMcq by remember { mutableStateOf(false) }
    var userSelectedOption by remember { mutableStateOf<String?>(null) }

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
                StatusBadge(
                    text = item.category,
                    textColor = NavyPrimary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = item.dateText,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.headline,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
            )

            if (item.mcqQuestion.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showMcq = !showMcq },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (showMcq) Icons.Default.ExpandLess else Icons.Default.Quiz,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (showMcq) "સ્વ-મૂલ્યાંકન પ્રશ્ન છુપાવો" else "આ સમાચાર પરથી પૂછાતો પ્રશ્ન જુઓ")
                }

                AnimatedVisibility(visible = showMcq) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .background(SlateSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "પ્રશ્ન: ${item.mcqQuestion}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val options = listOf(
                            "A" to item.mcqOptionA,
                            "B" to item.mcqOptionB,
                            "C" to item.mcqOptionC,
                            "D" to item.mcqOptionD
                        ).filter { it.second.isNotBlank() }

                        options.forEach { (lbl, optText) ->
                            val isChosen = userSelectedOption == lbl
                            val isCorrect = item.mcqCorrect == lbl
                            val showCorrection = userSelectedOption != null

                            val bg = when {
                                showCorrection && isCorrect -> EmeraldLight
                                showCorrection && isChosen && !isCorrect -> CrimsonLight
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { userSelectedOption = lbl },
                                color = bg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$lbl. $optText",
                                        fontSize = 13.sp,
                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showCorrection && isCorrect) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- GK SCREEN ---
@Composable
fun GkScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val gkList by mainViewModel.gkList.collectAsState()
    val selectedCat by mainViewModel.gkCategory.collectAsState()

    val categories = listOf("બધા", "બંધારણ", "ગુજરાત ઇતિહાસ", "ગુજરાત ભૂગોળ", "સામાન્ય વિજ્ઞાન", "સંસ્કૃતિ")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "જનરલ નોલેજ (GK)",
                subtitle = "ગુજરાત અને ભારતીય પરિપ્રેક્ષ્ય",
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
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = (cat == "બધા" && selectedCat.isBlank()) || cat == selectedCat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (cat == "બધા") mainViewModel.setGkCategory("")
                            else mainViewModel.setGkCategory(cat)
                        },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(gkList) { item ->
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
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                StatusBadge(
                                    text = item.category,
                                    textColor = Color(0xFF6D28D9),
                                    backgroundColor = Color(0xFFEDE9FE)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, lineHeight = 22.sp)
                            )
                            if (item.oneLinerFact.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberLight.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.oneLinerFact,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF78350F)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SYLLABUS SCREEN ---
@Composable
fun SyllabusScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val currentSyllabus by mainViewModel.currentSyllabus.collectAsState()
    val allExams by mainViewModel.allExams.collectAsState()
    val selectedExamId by mainViewModel.selectedExamId.collectAsState()

    val parts = remember(currentSyllabus) {
        currentSyllabus.groupBy { it.partName }
    }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "પરીક્ષા સિલેબસ (Syllabus)",
                subtitle = "સંપૂર્ણ અભ્યાસક્રમ અને ગુણભાર",
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ગુજરાત પોલીસ કોન્સ્ટેબલ પરીક્ષા માળખું",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyDark)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• કુલ ગુણ: 200 ગુણ | કુલ સમય: 3 કલાક (180 મિનિટ)\n• નેગેટિવ માર્કિંગ: 0.25 ગુણ પ્રતિ ખોટો જવાબ\n• પાસિંગ માર્ક્સ: લઘુત્તમ 40% (80 ગુણ)",
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark, lineHeight = 20.sp)
                        )
                    }
                }
            }

            parts.forEach { (partName, itemsList) ->
                item {
                    Text(
                        text = partName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                items(itemsList) { syllabusItem ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = syllabusItem.subjectName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = syllabusItem.topicName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            StatusBadge(
                                text = "${syllabusItem.weightageMarks} ગુણ",
                                textColor = EmeraldSuccess,
                                backgroundColor = EmeraldLight
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- STUDY MATERIAL SCREEN ---
@Composable
fun StudyMaterialScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val materials by mainViewModel.studyMaterials.collectAsState()
    val selectedSub by mainViewModel.materialSubject.collectAsState()

    val subjects = listOf("બધા", "ભારતીય બંધારણ", "રીઝનિંગ", "ગણિત", "ગુજરાત ઇતિહાસ", "વિજ્ઞાન")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "અભ્યાસ સામગ્રી (Study Material)",
                subtitle = "શોર્ટ નોટ્સ & અગત્યના મુદ્દાઓ",
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
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { sub ->
                    val isSelected = (sub == "બધા" && selectedSub.isBlank()) || sub == selectedSub
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (sub == "બધા") mainViewModel.setMaterialSubject("")
                            else mainViewModel.setMaterialSubject(sub)
                        },
                        label = { Text(sub, fontSize = 12.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(materials) { mat ->
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
                                StatusBadge(
                                    text = mat.subject,
                                    textColor = NavyDark,
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                StatusBadge(
                                    text = mat.type,
                                    textColor = AmberWarning,
                                    backgroundColor = AmberLight
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = mat.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mat.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = SlateSurfaceVariant
                            ) {
                                Text(
                                    text = mat.contentText,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, lineHeight = 20.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- LEADERBOARD SCREEN ---
@Composable
fun LeaderboardScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val leaderboard by mainViewModel.leaderboard.collectAsState()

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "લીડરબોર્ડ (Leaderboard)",
                subtitle = "ટોપ રેન્કિંગ વિદ્યાર્થીઓ",
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("સ્પર્ધાત્મક રેન્કિંગ", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("મોક ટેસ્ટમાં મેળવેલ સર્વોચ્ચ ગુણ અને ચોકસાઈના આધારે રેન્ક", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            if (leaderboard.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("હાલમાં કોઈ રેકોર્ડ નથી. મોક ટેસ્ટ આપીને પ્રથમ રેન્ક મેળવો!")
                        }
                    }
                }
            } else {
                itemsIndexed(leaderboard) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rank = index + 1
                            val rankColor = when (rank) {
                                1 -> Color(0xFFEAB308) // Gold
                                2 -> Color(0xFF94A3B8) // Silver
                                3 -> Color(0xFFD97706) // Bronze
                                else -> SlateOutline
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(rankColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$rank",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rank <= 3) rankColor else TextPrimary,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = item.testTitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${item.finalScore} ગુણ",
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${item.percentage}% | ${item.accuracy}% ચોકસાઈ",
                                    fontSize = 11.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
