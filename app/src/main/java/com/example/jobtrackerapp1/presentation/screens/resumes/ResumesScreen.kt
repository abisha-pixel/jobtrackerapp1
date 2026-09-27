package com.example.jobtrackerapp1.presentation.screens.resumes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jobtrackerapp1.domain.model.CoverLetter
import com.example.jobtrackerapp1.domain.model.ResumeVersion
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.EmeraldSuccess
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResumesScreen(
    resumes: List<ResumeVersion>,
    coverLetters: List<CoverLetter>,
    userId: String,
    onSaveResume: (ResumeVersion) -> Unit,
    onDeleteResume: (String) -> Unit,
    onSaveCoverLetter: (CoverLetter) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var selectedTab by remember { mutableStateOf(0) }
    var showAddResumeDialog by remember { mutableStateOf(false) }
    var showAddCoverLetterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Resume & Asset Vault",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage targeted resume versions, extracted skills & cover letters",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (selectedTab == 0) showAddResumeDialog = true else showAddCoverLetterDialog = true
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (selectedTab == 0) "Add Resume" else "New Letter")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row (Resumes vs Cover Letters)
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Resumes (${resumes.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Cover Letters (${coverLetters.size})") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // Resumes Tab
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resumes, key = { it.id }) { resume ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Filled.Description, contentDescription = null, tint = IndigoLight)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = resume.versionName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (resume.isDefault) {
                                    Text(
                                        text = "DEFAULT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess,
                                        modifier = Modifier
                                            .background(EmeraldSuccess.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Target: ${resume.targetRole} • File: ${resume.fileName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (resume.resumeSummary.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = resume.resumeSummary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            if (resume.skillsExtracted.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    resume.skillsExtracted.take(5).forEach { skill ->
                                        Text(
                                            text = skill,
                                            fontSize = 10.sp,
                                            color = CyanAccent,
                                            modifier = Modifier
                                                .background(CyanAccent.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Uploaded ${dateFormat.format(Date(resume.uploadedAt))}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                IconButton(onClick = { onDeleteResume(resume.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Cover Letters Tab
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(coverLetters, key = { it.id }) { letter ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = letter.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target: ${letter.targetCompany}",
                                fontSize = 12.sp,
                                color = IndigoLight
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = letter.content,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddResumeDialog) {
        var versionName by remember { mutableStateOf("") }
        var targetRole by remember { mutableStateOf("") }
        var skills by remember { mutableStateOf("Kotlin, Compose, Coroutines, Architecture") }
        var summary by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddResumeDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Add Resume Version", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetRole,
                        onValueChange = { targetRole = it },
                        label = { Text("Target Role *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Extracted Skills (comma-separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Summary") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddResumeDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (versionName.isNotBlank() && targetRole.isNotBlank()) {
                                    val newRes = ResumeVersion(
                                        userId = userId,
                                        versionName = versionName.trim(),
                                        targetRole = targetRole.trim(),
                                        fileName = "${versionName.replace(" ", "_")}.pdf",
                                        skillsExtracted = skills.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                        resumeSummary = summary.trim()
                                    )
                                    onSaveResume(newRes)
                                    showAddResumeDialog = false
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
