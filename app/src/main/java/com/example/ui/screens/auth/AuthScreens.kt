package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("student@studypro.in") }
    var password by remember { mutableStateOf("user") }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(46.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tantaniya Academy",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "ગુજરાત સરકારી પરીક્ષા તૈયારી મંચ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ખાતામાં પ્રવેશ કરો (Login)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ઈમેઈલ સરનામું (Email)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("પાસવર્ડ (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        singleLine = true
                    )

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { authViewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "લોગિન કરો (Login)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Login buttons
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                email = "gangalamit005@gmail.com"
                                password = "admin"
                                authViewModel.login(email, password)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("primary_admin_quick_login"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("મુખ્ય એડમિનિસ્ટ્રેટર લૉગિન (Admin)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    email = "admin@studypro.in"
                                    password = "admin"
                                    authViewModel.login(email, password)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("demo_admin_login_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("એડમિન ડેમો", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    email = "student@studypro.in"
                                    password = "user"
                                    authViewModel.login(email, password)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("demo_student_login_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("વિદ્યાર્થી ડેમો", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.testTag("go_to_register_button")
            ) {
                Text("નવું એકાઉન્ટ બનાવવા અહીં ક્લિક કરો (Register)")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var targetExam by remember { mutableStateOf("ગુજરાત પોલીસ કોન્સ્ટેબલ") }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            onRegisterSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "નવું એકાઉન્ટ બનાવો",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Tantaniya Academy પર આપનું સ્વાગત છે",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("પૂરું નામ (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ઈમેઈલ (Email)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("પાસવર્ડ (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetExam,
                        onValueChange = { targetExam = it },
                        label = { Text("તૈયારી કરતા હોવ તે પરીક્ષા (Target Exam)") },
                        leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_exam_input"),
                        singleLine = true
                    )

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { authViewModel.register(name, email, password, targetExam) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "નોંધણી પૂર્ણ કરો (Register)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.testTag("go_to_login_button")
            ) {
                Text("પહેલેથી એકાઉન્ટ છે? લોગિન કરો")
            }
        }
    }
}

@Composable
fun ProfileDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val user = state.currentUser ?: return
    var adminCodeInput by remember { mutableStateOf("") }
    var showAdminCodeDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = user.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = if (user.role == "ADMIN") "એડમિન રોલ (Administrator)" else "સામાન્ય વિદ્યાર્થી (Student)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (user.role == "ADMIN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "ઈમેઈલ: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "પરીક્ષા: ${user.targetExam}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(14.dp))

                // Secure Role Information & Verification Card
                val isPrimaryAdmin = com.example.data.repository.AuthConfig.isPrimaryAdmin(user.email)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.role == "ADMIN") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.role == "ADMIN") Icons.Default.VerifiedUser else Icons.Default.School,
                                contentDescription = null,
                                tint = if (user.role == "ADMIN") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isPrimaryAdmin && user.role == "ADMIN" -> "👑 મુખ્ય એડમિનિસ્ટ્રેટર (Primary Admin)"
                                    user.role == "ADMIN" -> "🛡️ સિસ્ટમ એડમિનિસ્ટ્રેટર (Admin)"
                                    else -> "🎓 વિદ્યાર્થી એકાઉન્ટ (Student)"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when {
                                isPrimaryAdmin && user.role == "ADMIN" ->
                                    "તમારું એકાઉન્ટ સિસ્ટમના અધિકૃત First & Primary Admin તરીકે સુરક્ષિત રીતે કન્ફિગર થયેલું છે. તમે તમામ પરીક્ષાઓ, ટેસ્ટ, પ્રશ્નો અને CMS કન્ટેન્ટ મેનેજ કરી શકો છો."
                                isPrimaryAdmin && user.role != "ADMIN" ->
                                    "તમારું એકાઉન્ટ Primary Admin છે પરંતુ હાલમાં વિદ્યાર્થી પ્રિવ્યૂ મોડમાં છે."
                                user.role == "ADMIN" ->
                                    "તમારું એકાઉન્ટ એડમિન તરીકે ચકાસાયેલ છે. તમારી પાસે એડમિન CMS એક્સેસ છે."
                                else ->
                                    "આ એક સામાન્ય વિદ્યાર્થી એકાઉન્ટ છે. એડમિન CMS અને ડેશબોર્ડ ફક્ત અધિકૃત એડમિનિસ્ટ્રેટર માટે જ સુરક્ષિત છે."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp)
                        )

                        if (isPrimaryAdmin) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { authViewModel.toggleAdminPreviewMode() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (user.role == "ADMIN") "વિદ્યાર્થી પ્રિવ્યૂ મોડ સ્વિચ કરો"
                                    else "પાછા એડમિન મોડ પર સ્વિચ કરો",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    authViewModel.logout()
                    onDismiss()
                }
            ) {
                Text("લોગ આઉટ કરો", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("બંધ કરો")
            }
        }
    )
}
