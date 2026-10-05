package com.example.ui.screens.test

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
import com.example.data.entity.TestAttemptEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.screens.home.HomeTestCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TestListScreen(
    mainViewModel: MainViewModel,
    onStartTest: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val publishedTests by mainViewModel.publishedTests.collectAsState()

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "મોક ટેસ્ટ (Mock Tests)",
                subtitle = "સંપૂર્ણ પરીક્ષા પેપર્સ",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "ઉપલબ્ધ મોક ટેસ્ટની યાદી (${publishedTests.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (publishedTests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("કોઈ મોક ટેસ્ટ પ્રકાશિત નથી.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(publishedTests) { test ->
                    HomeTestCard(
                        test = test,
                        onStartClick = { onStartTest(test.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TestHistoryScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    onViewAttempt: (TestAttemptEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val userId = authState.currentUser?.id ?: 1
    val attempts by mainViewModel.getUserAttempts(userId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "મારા પરિણામો (My Results)",
                subtitle = "આપેલા તમામ મોક ટેસ્ટનો ઇતિહાસ",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "કુલ આપેલી પરીક્ષાઓ: ${attempts.size}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (attempts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("હજી સુધી તમે કોઈ મોક ટેસ્ટ આપ્યો નથી.", style = MaterialTheme.typography.bodyMedium)
                            Text("મોક ટેસ્ટ આપીને તમારી તૈયારી ચકાસો!", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }
                }
            } else {
                items(attempts) { attempt ->
                    AttemptHistoryCard(
                        attempt = attempt,
                        onClick = { onViewAttempt(attempt) }
                    )
                }
            }
        }
    }
}

@Composable
fun AttemptHistoryCard(
    attempt: TestAttemptEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attempt_history_card_${attempt.id}"),
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
                        text = attempt.testTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    val dateFormatted = remember(attempt.submittedAt) {
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy, hh:mm a", java.util.Locale.getDefault())
                        sdf.format(java.util.Date(attempt.submittedAt))
                    }
                    Text(
                        text = "તારીખ: $dateFormatted",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }

                StatusBadge(
                    text = "${attempt.percentage}%",
                    textColor = if (attempt.percentage >= 40.0) EmeraldSuccess else CrimsonError,
                    backgroundColor = if (attempt.percentage >= 40.0) EmeraldLight else CrimsonLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${attempt.finalScore} / ${attempt.totalQuestions}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("કુલ સ્કોર", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${attempt.correctCount}", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                    Text("સાચા", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${attempt.incorrectCount}", fontWeight = FontWeight.Bold, color = CrimsonError)
                    Text("ખોટા", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${attempt.accuracy}%", fontWeight = FontWeight.Bold, color = AmberWarning)
                    Text("ચોકસાઈ", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("વિગતવાર વિશ્લેષણ જુઓ (View Analysis)")
            }
        }
    }
}
