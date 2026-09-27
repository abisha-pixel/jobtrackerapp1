package com.example.jobtrackerapp1.presentation.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jobtrackerapp1.domain.model.JobApplication
import com.example.jobtrackerapp1.presentation.navigation.Screen
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.IndigoLight

data class CommandAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onExecute: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandPaletteDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    applications: List<JobApplication>,
    onSelectApplication: (JobApplication) -> Unit,
    onNavigate: (Screen) -> Unit,
    onAddNewApp: () -> Unit,
    onScheduleInterview: () -> Unit,
    onExportCsv: () -> Unit,
    onExportReport: () -> Unit
) {
    if (!isOpen) return

    var query by remember { mutableStateOf("") }

    val defaultActions = listOf(
        CommandAction("Add New Job Application", "Track a new applied position", Icons.Filled.Add, onAddNewApp),
        CommandAction("Schedule Upcoming Interview", "Add interview round & meeting link", Icons.Filled.VideoCameraFront, onScheduleInterview),
        CommandAction("Launch AI Career Assistant", "Analyze ATS match & craft cover letters", Icons.Filled.AutoAwesome) { onNavigate(Screen.AI_ASSISTANT) },
        CommandAction("View Kanban Pipeline", "Drag-and-drop status board", Icons.Filled.ViewColumn) { onNavigate(Screen.KANBAN) },
        CommandAction("Export Applications to CSV", "Download structured spreadsheet data", Icons.Filled.Download, onExportCsv),
        CommandAction("Generate Executive Report", "Produce analytics summary report", Icons.Filled.Assessment, onExportReport),
        CommandAction("Recruiter CRM & Contacts", "Manage recruiter directory & notes", Icons.Filled.People) { onNavigate(Screen.RECRUITERS) },
        CommandAction("System Admin Panel", "Audit logs & system user management", Icons.Filled.AdminPanelSettings) { onNavigate(Screen.ADMIN) }
    )

    val matchedApps = remember(query, applications) {
        if (query.isBlank()) emptyList()
        else applications.filter {
            it.companyName.contains(query, ignoreCase = true) ||
            it.jobTitle.contains(query, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }

    val matchedActions = remember(query) {
        if (query.isBlank()) defaultActions
        else defaultActions.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.subtitle.contains(query, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Search Field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = CyanAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Command Palette",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search jobs, jump to screen, or type a command...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    // Application Search Matches
                    if (matchedApps.isNotEmpty()) {
                        item {
                            Text(
                                text = "MATCHED APPLICATIONS (${matchedApps.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoLight,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        items(matchedApps) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectApplication(app)
                                        onDismiss()
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Work,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${app.companyName} - ${app.jobTitle}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${app.status.readableName()} • ${app.location}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        }
                    }

                    // Quick Actions
                    item {
                        Text(
                            text = "COMMANDS & NAVIGATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                        )
                    }

                    items(matchedActions) { action ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    action.onExecute()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = null,
                                    tint = IndigoLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = action.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = action.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}
