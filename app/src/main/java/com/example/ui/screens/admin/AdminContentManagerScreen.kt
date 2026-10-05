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
import com.example.data.entity.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudyProTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminContentManagerScreen(
    initialTab: Int = 0,
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabTitles = listOf("કરંટ અફેર્સ", "જનરલ નોલેજ (GK)", "અભ્યાસ સામગ્રી", "સિલેબસ")

    Scaffold(
        topBar = {
            StudyProTopBar(
                title = "કન્ટેન્ટ મેનેજમેન્ટ (Content Manager)",
                subtitle = "સિલેબસ, સામગ્રી અને સમાચાર વ્યવસ્થાપન",
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AdminCurrentAffairsTab(mainViewModel)
                1 -> AdminGkTab(mainViewModel)
                2 -> AdminMaterialsTab(mainViewModel)
                3 -> AdminSyllabusTab(mainViewModel)
            }
        }
    }
}

// 1. Current Affairs Tab
@Composable
fun AdminCurrentAffairsTab(mainViewModel: MainViewModel) {
    val caList by mainViewModel.currentAffairsList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("કુલ સમાચાર: ${caList.size}", fontWeight = FontWeight.Bold)
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("નવું ઉમેરો")
                    }
                }
            }

            items(caList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = item.category, textColor = NavyDark, backgroundColor = MaterialTheme.colorScheme.primaryContainer)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.dateText, fontSize = 11.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.headline, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.description, fontSize = 12.sp, color = TextSecondary, maxLines = 2)
                        }
                        IconButton(onClick = { mainViewModel.deleteCurrentAffairs(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var headline by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("૨૯ સપ્ટેમ્બર ૨૦૨૬") }
        var cat by remember { mutableStateOf("ગુજરાત") }
        var mcqQ by remember { mutableStateOf("") }
        var mcqA by remember { mutableStateOf("") }
        var mcqB by remember { mutableStateOf("") }
        var mcqC by remember { mutableStateOf("") }
        var mcqD by remember { mutableStateOf("") }
        var mcqCor by remember { mutableStateOf("A") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("નવા કરંટ અફેર્સ ઉમેરો") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = headline, onValueChange = { headline = it }, label = { Text("હેડલાઈન (Headline)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("વિગતવાર સમાચાર (Description)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("તારીખ") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("કેટેગરી") }, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("સ્વ-મૂલ્યાંકન MCQ પ્રશ્ન (વૈકલ્પિક):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        OutlinedTextField(value = mcqQ, onValueChange = { mcqQ = it }, label = { Text("પ્રશ્ન") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = mcqA, onValueChange = { mcqA = it }, label = { Text("A") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = mcqB, onValueChange = { mcqB = it }, label = { Text("B") }, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = mcqC, onValueChange = { mcqC = it }, label = { Text("C") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = mcqD, onValueChange = { mcqD = it }, label = { Text("D") }, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(value = mcqCor, onValueChange = { mcqCor = it.uppercase().take(1) }, label = { Text("સાચો વિકલ્પ (A/B/C/D)") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (headline.isNotBlank()) {
                            mainViewModel.addCurrentAffairs(
                                CurrentAffairsEntity(
                                    headline = headline,
                                    description = desc,
                                    dateText = date,
                                    category = cat,
                                    mcqQuestion = mcqQ,
                                    mcqOptionA = mcqA,
                                    mcqOptionB = mcqB,
                                    mcqOptionC = mcqC,
                                    mcqOptionD = mcqD,
                                    mcqCorrect = mcqCor
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("ઉમેરો")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("રદ કરો") }
            }
        )
    }
}

// 2. GK Tab
@Composable
fun AdminGkTab(mainViewModel: MainViewModel) {
    val gkList by mainViewModel.gkList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("કુલ GK વિષયો: ${gkList.size}", fontWeight = FontWeight.Bold)
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("નવો GK ટોપિક")
                    }
                }
            }

            items(gkList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            StatusBadge(text = item.category, textColor = Color(0xFF6D28D9), backgroundColor = Color(0xFFEDE9FE))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.oneLinerFact, fontSize = 12.sp, color = AmberWarning, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { mainViewModel.deleteGkItem(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf("બંધારણ") }
        var content by remember { mutableStateOf("") }
        var fact by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("નવો GK ટોપિક ઉમેરો") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("ટોપિક શિર્ષક (Title)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("કેટેગરી (Category)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("સંપૂર્ણ વિગત (Content)") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = fact, onValueChange = { fact = it }, label = { Text("વન-લાઇનર ફેક્ટ (Key Fact)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            mainViewModel.addGkItem(GkItemEntity(category = cat, title = title, content = content, oneLinerFact = fact))
                            showAddDialog = false
                        }
                    }
                ) { Text("સાચવો") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("રદ કરો") }
            }
        )
    }
}

// 3. Study Materials Tab
@Composable
fun AdminMaterialsTab(mainViewModel: MainViewModel) {
    val materials by mainViewModel.studyMaterials.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("કુલ સામગ્રી: ${materials.size}", fontWeight = FontWeight.Bold)
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("સામગ્રી ઉમેરો")
                    }
                }
            }

            items(materials) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = item.subject, textColor = NavyDark, backgroundColor = MaterialTheme.colorScheme.primaryContainer)
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(text = item.type, textColor = AmberWarning, backgroundColor = AmberLight)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.description, fontSize = 12.sp, color = TextSecondary)
                        }
                        IconButton(onClick = { mainViewModel.deleteStudyMaterial(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var sub by remember { mutableStateOf("ભારતીય બંધારણ") }
        var topic by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("નોટ્સ") }
        var content by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("નવી અભ્યાસ સામગ્રી ઉમેરો") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("શિક્ષક / શિર્ષક") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("વિષય") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("ટોપિક") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("પ્રકાર (નોટ્સ/શોર્ટ ટ્રીક્સ/PDF)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("ટૂંકું વર્ણન") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("સંપૂર્ણ નોટ્સ લખાણ") }, modifier = Modifier.fillMaxWidth(), maxLines = 5)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            mainViewModel.addStudyMaterial(
                                StudyMaterialEntity(
                                    title = title,
                                    subject = sub,
                                    topic = topic,
                                    type = type,
                                    description = desc,
                                    contentText = content
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) { Text("સાચવો") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("રદ કરો") }
            }
        )
    }
}

// 4. Syllabus Tab
@Composable
fun AdminSyllabusTab(mainViewModel: MainViewModel) {
    val syllabus by mainViewModel.currentSyllabus.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("કુલ સિલેબસ મુદ્દાઓ: ${syllabus.size}", fontWeight = FontWeight.Bold)
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("મુદ્દો ઉમેરો")
                    }
                }
            }

            items(syllabus) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.partName, fontSize = 11.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                            Text(item.subjectName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.topicName, fontSize = 12.sp, color = TextSecondary)
                        }
                        StatusBadge(text = "${item.weightageMarks} ગુણ", textColor = EmeraldSuccess, backgroundColor = EmeraldLight)
                        IconButton(onClick = { mainViewModel.deleteSyllabus(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "કાઢી નાખો", tint = CrimsonError)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var part by remember { mutableStateOf("ભાગ-અ (Part A)") }
        var sub by remember { mutableStateOf("રીઝનિંગ") }
        var topic by remember { mutableStateOf("") }
        var marks by remember { mutableStateOf("25") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("સિલેબસમાં નવો મુદ્દો ઉમેરો") },
            text = {
                Column {
                    OutlinedTextField(value = part, onValueChange = { part = it }, label = { Text("ભાગનું નામ (દા.ત. ભાગ-અ / ભાગ-બ)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("વિષય (Subject)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("ટોપિકનું નામ (Topic)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = marks, onValueChange = { marks = it }, label = { Text("ગુણભાર (Weightage Marks)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sub.isNotBlank()) {
                            mainViewModel.addSyllabus(
                                SyllabusEntity(
                                    examId = 1,
                                    partName = part,
                                    subjectName = sub,
                                    topicName = topic,
                                    weightageMarks = marks.toIntOrNull() ?: 20
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) { Text("સાચવો") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("રદ કરો") }
            }
        )
    }
}
