package com.example.jobtrackerapp1.presentation.screens.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.ApplicationHistory
import com.example.jobtrackerapp1.domain.model.ApplicationStatus
import com.example.jobtrackerapp1.domain.model.JobApplication
import com.example.jobtrackerapp1.presentation.components.PriorityBadge
import com.example.jobtrackerapp1.presentation.components.StatusBadge
import com.example.jobtrackerapp1.presentation.components.WorkTypeBadge
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.RoseDanger
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    application: JobApplication,
    history: List<ApplicationHistory>,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateStatus: (ApplicationStatus) -> Unit,
    onScheduleInterview: () -> Unit,
    onRunAiMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    var showStatusPicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Nav Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }

            Row {
                IconButton(onClick = onRunAiMatch) {
                    Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = "AI Match", tint = CyanAccent)
                }
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = RoseDanger)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header Title Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = application.companyName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = application.jobTitle,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = application.status)
                    PriorityBadge(priority = application.priority)
                    WorkTypeBadge(workType = application.workType)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Stage Changer Trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Stage: ${application.status.readableName()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box {
                        Button(
                            onClick = { showStatusPicker = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Move Stage", fontSize = 12.sp)
                        }

                        DropdownMenu(
                            expanded = showStatusPicker,
                            onDismissRequest = { showStatusPicker = false }
                        ) {
                            ApplicationStatus.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.readableName()) },
                                    onClick = {
                                        showStatusPicker = false
                                        onUpdateStatus(st)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role & Compensation Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "POSITION DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                Spacer(modifier = Modifier.height(10.dp))

                DetailItem("Location", application.location)
                DetailItem("Salary Band", "$${application.salaryMin} - $${application.salaryMax} ${application.salaryCurrency}")
                DetailItem("Employment Type", application.employmentType.name.replace("_", " "))
                DetailItem("Source", application.source)
                DetailItem("Application Date", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(application.applicationDate)))
                if (application.nextAction.isNotBlank()) {
                    DetailItem("Next Action", application.nextAction)
                }
                if (application.jobUrl.isNotBlank()) {
                    DetailItem("Job Listing URL", application.jobUrl)
                }
            }
        }

        // Job Description
        if (application.jobDescription.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "JOB DESCRIPTION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = application.jobDescription, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }

        // Notes & Strategy
        if (application.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "PERSONAL NOTES & STRATEGY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = application.notes, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }

        // Stage Change Timeline History
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "STAGE HISTORY TIMELINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                Spacer(modifier = Modifier.height(10.dp))

                val appHistory = history.filter { it.applicationId == application.id }
                if (appHistory.isEmpty()) {
                    Text(
                        text = "Initial status set to ${application.status.readableName()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    appHistory.forEach { h ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(CyanAccent, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${h.fromStatus.readableName()} → ${h.toStatus.readableName()}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = dateFormat.format(Date(h.timestamp)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Schedule Interview Action
        Button(
            onClick = onScheduleInterview,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.VideoCameraFront, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Schedule Interview for this Role")
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Application") },
            text = { Text("Are you sure you want to remove the application for ${application.companyName}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = RoseDanger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
