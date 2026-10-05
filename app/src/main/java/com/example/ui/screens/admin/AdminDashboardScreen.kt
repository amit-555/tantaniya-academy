package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminDashboardScreen(
    mainViewModel: MainViewModel,
    onNavigateToExams: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToAiGenerator: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToCurrentAffairs: () -> Unit,
    onNavigateToGk: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExitAdmin: () -> Unit
) {
    val totalUsers by mainViewModel.totalUsersCount.collectAsState()
    val totalTests by mainViewModel.totalTestsCount.collectAsState()
    val totalQuestions by mainViewModel.totalQuestionsCount.collectAsState()
    val totalAttempts by mainViewModel.totalAttemptsCount.collectAsState()

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "એડમિન કંટ્રોલ પેનલ (Admin Panel)",
                subtitle = "Tantaniya Academy પરીક્ષા સંચાલન",
                showBackButton = true,
                onBackClick = onExitAdmin,
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "સેટિંગ્સ", tint = Color.White)
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
            // Admin Hero Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(AmberWarning),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = NavyDark)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("મુખ્ય એડમિનિસ્ટ્રેટર પેનલ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("સંપૂર્ણ નિયંત્રણ અને ડેટાબેઝ સંચાલન", color = AmberWarning, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                }
                            }
                            StatusBadge(text = "👑 Administrator", textColor = NavyDark, backgroundColor = AmberWarning)
                        }
                    }
                }
            }

            // Overview Metric Cards
            item {
                Text(
                    text = "સિસ્ટમ આંકડા (Overview Metrics)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "કુલ વિદ્યાર્થીઓ",
                        value = totalUsers.toString(),
                        icon = Icons.Default.People,
                        iconTint = NavyLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "કુલ મોક ટેસ્ટ",
                        value = totalTests.toString(),
                        icon = Icons.Default.Quiz,
                        iconTint = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "પ્રશ્ન બેંક",
                        value = totalQuestions.toString(),
                        icon = Icons.Default.HelpCenter,
                        iconTint = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "પરીક્ષા પ્રયાસો",
                        value = totalAttempts.toString(),
                        icon = Icons.Default.BarChart,
                        iconTint = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Core Admin Modules
            item {
                Text(
                    text = "મુખ્ય એડમિન મોડ્યુલ્સ (Core Modules)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // 1. Dynamic Exam Management Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToExams() }
                        .testTag("admin_exams_hero_tile"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberWarning),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = NavyDark)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "🏛️ પરીક્ષાઓ સંચાલન (Dynamic Exam Manager)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                "નવી સરકારી ભરતી પરીક્ષાઓ બનાવો: ફોરેસ્ટ ગાર્ડ, MPHW, CCE વગેરે",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // 2. AI Question Generator Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAiGenerator() }
                        .testTag("admin_ai_generator_tile"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "🤖 AI પ્રશ્ન નિર્માતા (AI MCQ Generator)",
                                fontWeight = FontWeight.Bold,
                                color = NavyDark,
                                fontSize = 15.sp
                            )
                            Text(
                                "ટોપિક, દસ્તાવેજ અથવા કેમેરા ફોટોમાંથી ગુજરાતી પ્રશ્નો બનાવો",
                                fontSize = 12.sp,
                                color = NavyDark.copy(alpha = 0.8f)
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NavyDark)
                    }
                }
            }

            // Menu Items Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMenuRow(
                        icon1 = Icons.Default.School,
                        title1 = "પરીક્ષાઓ (Exams)",
                        subtitle1 = "LRD, Forest, MPHW",
                        onClick1 = onNavigateToExams,
                        icon2 = Icons.Default.Assignment,
                        title2 = "ટેસ્ટ મેનેજમેન્ટ",
                        subtitle2 = "૨૦૦ પ્રશ્નોના મોક ટેસ્ટ",
                        onClick2 = onNavigateToTests
                    )

                    AdminMenuRow(
                        icon1 = Icons.Default.MenuBook,
                        title1 = "પ્રશ્ન બેંક",
                        subtitle1 = "પ્રશ્નો ઉમેરો / ફિલ્ટર કરો",
                        onClick1 = onNavigateToQuestions,
                        icon2 = Icons.Default.FormatListNumbered,
                        title2 = "સિલેબસ મેનેજર",
                        subtitle2 = "વિષયો અને ગુણભાર",
                        onClick2 = onNavigateToSyllabus
                    )

                    AdminMenuRow(
                        icon1 = Icons.Default.LibraryBooks,
                        title1 = "અભ્યાસ સામગ્રી",
                        subtitle1 = "નોટ્સ & ટ્રીક્સ",
                        onClick1 = onNavigateToMaterials,
                        icon2 = Icons.Default.Article,
                        title2 = "કરંટ અફેર્સ",
                        subtitle2 = "દૈનિક સમાચાર ઉમેરો",
                        onClick2 = onNavigateToCurrentAffairs
                    )

                    AdminMenuRow(
                        icon1 = Icons.Default.Lightbulb,
                        title1 = "જનરલ નોલેજ (GK)",
                        subtitle1 = "GK ફેક્ટ્સ મેનેજ કરો",
                        onClick1 = onNavigateToGk,
                        icon2 = Icons.Default.Group,
                        title2 = "યુઝર મેનેજમેન્ટ",
                        subtitle2 = "વિદ્યાર્થી એકાઉન્ટ્સ",
                        onClick2 = onNavigateToUsers
                    )

                    AdminMenuRow(
                        icon1 = Icons.Default.Settings,
                        title1 = "સિસ્ટમ સેટિંગ્સ",
                        subtitle1 = "Gemini API & રૂપરેખા",
                        onClick1 = onNavigateToSettings,
                        icon2 = Icons.Default.AutoAwesome,
                        title2 = "AI પ્રશ્ન નિર્માતા",
                        subtitle2 = "ઓટો પ્રશ્ન નિર્માણ",
                        onClick2 = onNavigateToAiGenerator
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMenuRow(
    icon1: ImageVector,
    title1: String,
    subtitle1: String,
    onClick1: () -> Unit,
    icon2: ImageVector,
    title2: String,
    subtitle2: String,
    onClick2: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdminMenuCard(icon = icon1, title = title1, subtitle = subtitle1, onClick = onClick1, modifier = Modifier.weight(1f))
        AdminMenuCard(icon = icon2, title = title2, subtitle = subtitle2, onClick = onClick2, modifier = Modifier.weight(1f))
    }
}

@Composable
fun AdminMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("admin_menu_${title}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
        }
    }
}
