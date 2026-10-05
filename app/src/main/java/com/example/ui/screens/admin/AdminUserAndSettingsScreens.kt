package com.example.ui.screens.admin

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
import com.example.data.entity.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

// --- USER MANAGEMENT SCREEN ---
@Composable
fun AdminUserManagementScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val users by mainViewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "યુઝર મેનેજમેન્ટ (User Management)",
                subtitle = "નોંધાયેલા તમામ વિદ્યાર્થીઓનું નિયંત્રણ",
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("નામ અથવા ઈમેઈલથી શોધો...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "કુલ વપરાશકર્તાઓ: ${users.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
                    )
                }

                items(filteredUsers) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(user.email, fontSize = 12.sp, color = TextSecondary)
                                    Text("લક્ષ્ય: ${user.targetExam}", fontSize = 11.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(
                                        text = if (user.role == "ADMIN") "ADMIN" else "USER",
                                        textColor = if (user.role == "ADMIN") NavyDark else TextSecondary,
                                        backgroundColor = if (user.role == "ADMIN") AmberLight else SlateSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusBadge(
                                        text = if (user.isActive) "સક્રિય" else "નિષ્ક્રિય",
                                        textColor = if (user.isActive) EmeraldSuccess else CrimsonError,
                                        backgroundColor = if (user.isActive) EmeraldLight else CrimsonLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { mainViewModel.toggleUserStatus(user) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (user.isActive) "નિષ્ક્રિય કરો" else "સક્રિય કરો", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { mainViewModel.toggleUserRole(user) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (user.role == "ADMIN") "USER બનાવો" else "ADMIN બનાવો", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SETTINGS SCREEN ---
@Composable
fun AdminSettingsScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val currentApiKey by mainViewModel.geminiApiKey.collectAsState()
    var inputKey by remember(currentApiKey) { mutableStateOf(currentApiKey) }
    var showSavedMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "સિસ્ટમ સેટિંગ્સ & કી (Settings)",
                subtitle = "API કી અને પ્લેટફોર્મ રૂપરેખા",
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
            // Gemini API Key Config Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini API કી રૂપરેખા (Gemini AI Key)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "AI પ્રશ્ન નિર્માતા અને Image-to-MCQ ફીચર ચલાવવા માટે Gemini API કી જરૂરી છે. તમે આ કી Google AI Studio ના Secrets પેનલમાંથી મેળવી શકો છો અથવા નીચે સીધી દાખલ કરી શકો છો:",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = inputKey,
                            onValueChange = { inputKey = it },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("AIzaSy...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gemini_api_key_input"),
                            singleLine = true,
                            trailingIcon = {
                                if (inputKey.isNotBlank()) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                mainViewModel.setGeminiApiKey(inputKey.trim())
                                showSavedMessage = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_api_key_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("API કી સાચવો (Save Key)", fontWeight = FontWeight.Bold)
                        }

                        if (showSavedMessage) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "API કી સફળતાપૂર્વક સાચવવામાં આવી!",
                                color = EmeraldSuccess,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Offline Backup Engine Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = NavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ઓફલાઇન સુરક્ષિત બેકઅપ એન્જિન", fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "જો ઈન્ટરનેટ ઉપલબ્ધ ન હોય અથવા કી દાખલ કરેલ ન હોય, તો પણ Tantaniya Academy નું ઇન્ટિગ્રેટેડ પ્રશ્ન એન્જિન બંધારણ, ઇતિહાસ, ભૂગોળ અને ગણિતના અસલ અનન્ય પ્રશ્નો આપમેળે બનાવી શકે છે.",
                            style = MaterialTheme.typography.bodySmall.copy(color = NavyDark, lineHeight = 18.sp)
                        )
                    }
                }
            }

            // About Tantaniya Academy
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tantaniya Academy વિશે (About)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("આવૃત્તિ: 1.0.0 (Production Ready)", fontSize = 12.sp, color = TextSecondary)
                        Text("પ્લેટફોર્મ: ગુજરાત સ્પર્ધાત્મક પરીક્ષા પોર્ટલ (LRD, CCE, PSI, વનરક્ષક)", fontSize = 12.sp, color = TextSecondary)
                        Text("ડેટાબેઝ: Room Local Persistence", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
