package com.example.jobtrackerapp1.presentation.screens.interviews

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
import com.example.jobtrackerapp1.domain.model.*
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.EmeraldSuccess
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary
import com.example.jobtrackerapp1.ui.theme.RoseDanger
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InterviewsScreen(
    interviews: List<Interview>,
    applications: List<JobApplication>,
    userId: String,
    onSaveInterview: (Interview) -> Unit,
    onToggleChecklistItem: (String, Int) -> Unit,
    onDeleteInterview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE, MMM dd • hh:mm a", Locale.getDefault())
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Interview Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Track upcoming rounds, checklists, questions & results",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (interviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.VideoCameraFront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No interviews scheduled yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Text("Schedule an Interview")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(interviews, key = { it.id }) { interview ->
                    InterviewCardItem(
                        interview = interview,
                        dateFormat = dateFormat,
                        onToggleChecklist = { idx -> onToggleChecklistItem(interview.id, idx) },
                        onDelete = { onDeleteInterview(interview.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        ScheduleInterviewDialog(
            isOpen = showAddDialog,
            onDismiss = { showAddDialog = false },
            applications = applications,
            userId = userId,
            onSave = {
                onSaveInterview(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun InterviewCardItem(
    interview: Interview,
    dateFormat: SimpleDateFormat,
    onToggleChecklist: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = interview.companyName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${interview.jobTitle} • ${interview.type.name.replace("_", " ")}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = RoseDanger.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date & Meeting Link Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dateFormat.format(Date(interview.interviewDateTime)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (interview.meetingLink.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = interview.meetingLink, fontSize = 11.sp, color = CyanAccent)
                }
            }

            if (interview.interviewerName.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Interviewer: ${interview.interviewerName} (${interview.interviewerTitle.ifBlank { "Hiring Team" }})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Preparation Checklist
            if (interview.preparationChecklist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Preparation Checklist:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                interview.preparationChecklist.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChecklist(idx) }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onToggleChecklist(idx) },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.text,
                            fontSize = 12.sp,
                            color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleInterviewDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    applications: List<JobApplication>,
    userId: String,
    onSave: (Interview) -> Unit
) {
    if (!isOpen) return

    var selectedApp by remember { mutableStateOf(applications.firstOrNull()) }
    var interviewerName by remember { mutableStateOf("") }
    var meetingLink by remember { mutableStateOf("https://meet.google.com/interview-room") }
    var selectedType by remember { mutableStateOf(InterviewType.TECHNICAL) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Schedule Interview", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Select Application
                Text(text = "Target Application: ${selectedApp?.companyName ?: "None"}", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = interviewerName,
                    onValueChange = { interviewerName = it },
                    label = { Text("Interviewer Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link / Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Interview Type Picker
                var typeExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { typeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedType.name.replace("_", " "))
                    }
                    DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        InterviewType.values().forEach { itType ->
                            DropdownMenuItem(
                                text = { Text(itType.name.replace("_", " ")) },
                                onClick = {
                                    selectedType = itType
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val interview = Interview(
                                applicationId = selectedApp?.id ?: UUID.randomUUID().toString(),
                                userId = userId,
                                companyName = selectedApp?.companyName ?: "Company",
                                jobTitle = selectedApp?.jobTitle ?: "Engineer",
                                interviewDateTime = System.currentTimeMillis() + (48 * 3600 * 1000),
                                type = selectedType,
                                interviewerName = interviewerName.ifBlank { "Hiring Team" },
                                meetingLink = meetingLink,
                                preparationChecklist = listOf(
                                    ChecklistItem(text = "Review role specifications & system design", isCompleted = false),
                                    ChecklistItem(text = "Test camera, microphone, and coding workspace", isCompleted = false),
                                    ChecklistItem(text = "Prepare 3 thoughtful questions for interviewer", isCompleted = false)
                                )
                            )
                            onSave(interview)
                        }
                    ) {
                        Text("Schedule")
                    }
                }
            }
        }
    }
}
