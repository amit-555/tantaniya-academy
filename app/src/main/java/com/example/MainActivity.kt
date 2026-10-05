package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.entity.QuestionEntity
import com.example.data.entity.TestAttemptEntity
import com.example.data.repository.StudyProRepository
import com.example.ui.screens.admin.*
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.ProfileDialog
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.content.*
import com.example.ui.screens.home.UserHomeScreen
import com.example.ui.screens.test.*
import com.example.ui.theme.StudyProTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.TestEngineViewModel
import kotlinx.coroutines.launch

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
    object TestsList : Screen()
    data class TestEngine(val testId: Int) : Screen()
    data class TestResult(val attemptId: Int, val testId: Int) : Screen()
    object TestHistory : Screen()
    object CurrentAffairs : Screen()
    object Gk : Screen()
    object StudyMaterials : Screen()
    object Syllabus : Screen()
    object Leaderboard : Screen()
    object QuestionPractice : Screen()

    // Admin Screens
    object AdminDashboard : Screen()
    object AdminExams : Screen()
    object AdminTests : Screen()
    object AdminQuestions : Screen()
    object AdminAiGenerator : Screen()
    data class AdminContent(val initialTab: Int = 0) : Screen()
    object AdminUsers : Screen()
    object AdminSettings : Screen()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = StudyProRepository(database.studyProDao())

        val authViewModel = AuthViewModel(repository)
        val mainViewModel = MainViewModel(repository)
        val testEngineViewModel = TestEngineViewModel(repository)

        setContent {
            StudyProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by authViewModel.uiState.collectAsState()
                    var screenStack by remember { mutableStateOf<List<Screen>>(listOf(Screen.Home)) }
                    var showProfileDialog by remember { mutableStateOf(false) }

                    val currentScreen = screenStack.lastOrNull() ?: Screen.Home

                    fun navigateTo(screen: Screen) {
                        screenStack = screenStack + screen
                    }

                    fun navigateBack(): Boolean {
                        return if (screenStack.size > 1) {
                            screenStack = screenStack.dropLast(1)
                            true
                        } else false
                    }

                    fun navigateAndClearToHome() {
                        screenStack = listOf(Screen.Home)
                    }

                    // Auto switch to login if user logged out
                    LaunchedEffect(authState.currentUser) {
                        if (authState.currentUser == null) {
                            screenStack = listOf(Screen.Login)
                        } else if (screenStack.size == 1 && screenStack.first() is Screen.Login) {
                            screenStack = listOf(Screen.Home)
                        }
                    }

                    // Handle Back Navigation
                    if (currentScreen !is Screen.Home && currentScreen !is Screen.Login && currentScreen !is Screen.TestEngine) {
                        BackHandler {
                            navigateBack()
                        }
                    }

                    when (val screen = currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onNavigateToRegister = { navigateTo(Screen.Register) },
                                onLoginSuccess = { navigateAndClearToHome() }
                            )
                        }

                        is Screen.Register -> {
                            RegisterScreen(
                                authViewModel = authViewModel,
                                onNavigateToLogin = { navigateBack() },
                                onRegisterSuccess = { navigateAndClearToHome() }
                            )
                        }

                        is Screen.Home -> {
                            UserHomeScreen(
                                authViewModel = authViewModel,
                                mainViewModel = mainViewModel,
                                onStartTest = { testId ->
                                    testEngineViewModel.loadTest(testId)
                                    navigateTo(Screen.TestEngine(testId))
                                },
                                onNavigateToTests = { navigateTo(Screen.TestsList) },
                                onNavigateToQuestions = { navigateTo(Screen.QuestionPractice) },
                                onNavigateToCurrentAffairs = { navigateTo(Screen.CurrentAffairs) },
                                onNavigateToGk = { navigateTo(Screen.Gk) },
                                onNavigateToMaterials = { navigateTo(Screen.StudyMaterials) },
                                onNavigateToSyllabus = { navigateTo(Screen.Syllabus) },
                                onNavigateToResults = { navigateTo(Screen.TestHistory) },
                                onNavigateToLeaderboard = { navigateTo(Screen.Leaderboard) },
                                onNavigateToAdmin = { navigateTo(Screen.AdminDashboard) },
                                onOpenProfile = { showProfileDialog = true }
                            )
                        }

                        is Screen.TestsList -> {
                            TestListScreen(
                                mainViewModel = mainViewModel,
                                onStartTest = { testId ->
                                    testEngineViewModel.loadTest(testId)
                                    navigateTo(Screen.TestEngine(testId))
                                },
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.TestEngine -> {
                            TestEngineScreen(
                                testEngineViewModel = testEngineViewModel,
                                onTestSubmitted = { attemptId ->
                                    navigateTo(Screen.TestResult(attemptId, screen.testId))
                                },
                                onExitTest = { navigateAndClearToHome() }
                            )
                        }

                        is Screen.TestResult -> {
                            var attempt by remember { mutableStateOf<TestAttemptEntity?>(null) }
                            var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
                            var isLoadingResult by remember { mutableStateOf(true) }

                            LaunchedEffect(screen.attemptId) {
                                isLoadingResult = true
                                val att = mainViewModel.getAttemptById(screen.attemptId)
                                val qList = mainViewModel.getQuestionsForTestSync(screen.testId)
                                attempt = att
                                questions = if (qList.isNotEmpty()) qList else mainViewModel.getQuestionsForTestSync(1)
                                isLoadingResult = false
                            }

                            if (isLoadingResult || attempt == null) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                TestResultScreen(
                                    attempt = attempt!!,
                                    questions = questions,
                                    onRetakeTest = { testId ->
                                        testEngineViewModel.loadTest(testId)
                                        navigateTo(Screen.TestEngine(testId))
                                    },
                                    onNavigateToHome = { navigateAndClearToHome() },
                                    onNavigateToHistory = { navigateTo(Screen.TestHistory) }
                                )
                            }
                        }

                        is Screen.TestHistory -> {
                            TestHistoryScreen(
                                authViewModel = authViewModel,
                                mainViewModel = mainViewModel,
                                onViewAttempt = { att ->
                                    navigateTo(Screen.TestResult(att.id, att.testId))
                                },
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.CurrentAffairs -> {
                            CurrentAffairsScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.Gk -> {
                            GkScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.StudyMaterials -> {
                            StudyMaterialScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.Syllabus -> {
                            SyllabusScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.Leaderboard -> {
                            LeaderboardScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        is Screen.QuestionPractice -> {
                            QuestionBankPracticeScreen(
                                mainViewModel = mainViewModel,
                                onBackClick = { navigateBack() }
                            )
                        }

                        // --- ADMIN SCREENS (SECURED WITH ROLE-BASED ACCESS CONTROL) ---
                        is Screen.AdminDashboard,
                        is Screen.AdminExams,
                        is Screen.AdminTests,
                        is Screen.AdminQuestions,
                        is Screen.AdminAiGenerator,
                        is Screen.AdminContent,
                        is Screen.AdminUsers,
                        is Screen.AdminSettings -> {
                            val user = authState.currentUser
                            val isAdmin = user != null && user.role.equals("ADMIN", ignoreCase = true)
                            if (!isAdmin) {
                                // Access Denied: Normal users are strictly forbidden from viewing Admin CMS
                                androidx.compose.foundation.layout.Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = com.example.ui.theme.CrimsonError,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "એક્સેસ પ્રતિબંધિત (Access Denied)",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                            color = com.example.ui.theme.CrimsonError
                                        )
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "આ પેનલ ફક્ત અધિકૃત એડમિનિસ્ટ્રેટર માટે જ સુરક્ષિત છે. તમારું એકાઉન્ટ (${user?.email ?: "અજ્ઞાત"}) એડમિન રોલ ધરાવતું નથી.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                                    androidx.compose.material3.Button(
                                        onClick = { navigateAndClearToHome() },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.NavyPrimary)
                                    ) {
                                        Text("હોમ સ્ક્રીન પર પાછા ફરો")
                                    }
                                }
                            } else {
                                when (screen) {
                                    is Screen.AdminDashboard -> {
                                        AdminDashboardScreen(
                                            mainViewModel = mainViewModel,
                                            onNavigateToExams = { navigateTo(Screen.AdminExams) },
                                            onNavigateToTests = { navigateTo(Screen.AdminTests) },
                                            onNavigateToQuestions = { navigateTo(Screen.AdminQuestions) },
                                            onNavigateToAiGenerator = { navigateTo(Screen.AdminAiGenerator) },
                                            onNavigateToSyllabus = { navigateTo(Screen.AdminContent(3)) },
                                            onNavigateToMaterials = { navigateTo(Screen.AdminContent(2)) },
                                            onNavigateToCurrentAffairs = { navigateTo(Screen.AdminContent(0)) },
                                            onNavigateToGk = { navigateTo(Screen.AdminContent(1)) },
                                            onNavigateToUsers = { navigateTo(Screen.AdminUsers) },
                                            onNavigateToSettings = { navigateTo(Screen.AdminSettings) },
                                            onExitAdmin = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminExams -> {
                                        AdminExamManagerScreen(
                                            mainViewModel = mainViewModel,
                                            onManageSyllabusForExam = { examId ->
                                                mainViewModel.setSelectedExam(examId)
                                                navigateTo(Screen.AdminContent(3))
                                            },
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminTests -> {
                                        AdminTestManagerScreen(
                                            mainViewModel = mainViewModel,
                                            onPreviewTest = { testId ->
                                                testEngineViewModel.loadTest(testId)
                                                navigateTo(Screen.TestEngine(testId))
                                            },
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminQuestions -> {
                                        AdminQuestionBankScreen(
                                            mainViewModel = mainViewModel,
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminAiGenerator -> {
                                        AdminAiGeneratorScreen(
                                            mainViewModel = mainViewModel,
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminContent -> {
                                        AdminContentManagerScreen(
                                            initialTab = screen.initialTab,
                                            mainViewModel = mainViewModel,
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminUsers -> {
                                        AdminUserManagementScreen(
                                            mainViewModel = mainViewModel,
                                            onBackClick = { navigateBack() }
                                        )
                                    }

                                    is Screen.AdminSettings -> {
                                        AdminSettingsScreen(
                                            mainViewModel = mainViewModel,
                                            onBackClick = { navigateBack() }
                                        )
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }

                    // Profile & Safe Admin switcher dialog
                    if (showProfileDialog) {
                        ProfileDialog(
                            authViewModel = authViewModel,
                            onDismiss = { showProfileDialog = false }
                        )
                    }
                }
            }
        }
    }
}
