package com.example.jobtrackerapp1.presentation.screens.applications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jobtrackerapp1.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditApplicationDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    existingApp: JobApplication?,
    userId: String,
    onSave: (JobApplication) -> Unit
) {
    if (!isOpen) return

    var companyName by remember { mutableStateOf(existingApp?.companyName ?: "") }
    var jobTitle by remember { mutableStateOf(existingApp?.jobTitle ?: "") }
    var location by remember { mutableStateOf(existingApp?.location ?: "Remote") }
    var salaryMin by remember { mutableStateOf(existingApp?.salaryMin?.toString() ?: "120000") }
    var salaryMax by remember { mutableStateOf(existingApp?.salaryMax?.toString() ?: "160000") }
    var jobUrl by remember { mutableStateOf(existingApp?.jobUrl ?: "") }
    var jobDescription by remember { mutableStateOf(existingApp?.jobDescription ?: "") }
    var source by remember { mutableStateOf(existingApp?.source ?: "LinkedIn") }
    var notes by remember { mutableStateOf(existingApp?.notes ?: "") }
    var tagsStr by remember { mutableStateOf(existingApp?.tags?.joinToString(", ") ?: "Engineering, Tier 1") }

    var selectedStatus by remember { mutableStateOf(existingApp?.status ?: ApplicationStatus.APPLIED) }
    var selectedPriority by remember { mutableStateOf(existingApp?.priority ?: Priority.MEDIUM) }
    var selectedWorkType by remember { mutableStateOf(existingApp?.workType ?: WorkType.REMOTE) }

    var companyError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (existingApp == null) "New Job Application" else "Edit Job Application",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Fill in role specifications, compensation, and stage",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Company Name
                OutlinedTextField(
                    value = companyName,
                    onValueChange = {
                        companyName = it
                        companyError = it.isBlank()
                    },
                    label = { Text("Company Name *") },
                    isError = companyError,
                    supportingText = { if (companyError) Text("Company name is required") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Job Title
                OutlinedTextField(
                    value = jobTitle,
                    onValueChange = {
                        jobTitle = it
                        titleError = it.isBlank()
                    },
                    label = { Text("Job Title *") },
                    isError = titleError,
                    supportingText = { if (titleError) Text("Job title is required") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location & Work Type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Work Type Dropdown
                    var workTypeExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { workTypeExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(selectedWorkType.name)
                        }
                        DropdownMenu(
                            expanded = workTypeExpanded,
                            onDismissRequest = { workTypeExpanded = false }
                        ) {
                            WorkType.values().forEach { wt ->
                                DropdownMenuItem(
                                    text = { Text(wt.name) },
                                    onClick = {
                                        selectedWorkType = wt
                                        workTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Salary Range (Min - Max)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = salaryMin,
                        onValueChange = { salaryMin = it },
                        label = { Text("Min Salary ($)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = salaryMax,
                        onValueChange = { salaryMax = it },
                        label = { Text("Max Salary ($)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status & Priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var statusExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1.2f)) {
                        OutlinedButton(
                            onClick = { statusExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(selectedStatus.readableName(), fontSize = 12.sp)
                        }
                        DropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            ApplicationStatus.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.readableName()) },
                                    onClick = {
                                        selectedStatus = st
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    var priorityExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(0.8f)) {
                        OutlinedButton(
                            onClick = { priorityExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(selectedPriority.name, fontSize = 12.sp)
                        }
                        DropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            Priority.values().forEach { pr ->
                                DropdownMenuItem(
                                    text = { Text(pr.name) },
                                    onClick = {
                                        selectedPriority = pr
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Job URL & Source
                OutlinedTextField(
                    value = jobUrl,
                    onValueChange = { jobUrl = it },
                    label = { Text("Job Listing URL") },
                    placeholder = { Text("https://careers.company.com/...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Source (LinkedIn, Referral, Indeed...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                OutlinedTextField(
                    value = tagsStr,
                    onValueChange = { tagsStr = it },
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Job Description
                OutlinedTextField(
                    value = jobDescription,
                    onValueChange = { jobDescription = it },
                    label = { Text("Job Description") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes & Strategy") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (companyName.isBlank()) {
                                companyError = true
                                return@Button
                            }
                            if (jobTitle.isBlank()) {
                                titleError = true
                                return@Button
                            }

                            val cleanTags = tagsStr.split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }

                            val appToSave = existingApp?.copy(
                                companyName = companyName.trim(),
                                jobTitle = jobTitle.trim(),
                                location = location.trim(),
                                salaryMin = salaryMin.toIntOrNull() ?: 0,
                                salaryMax = salaryMax.toIntOrNull() ?: 0,
                                workType = selectedWorkType,
                                status = selectedStatus,
                                priority = selectedPriority,
                                jobUrl = jobUrl.trim(),
                                source = source.trim(),
                                tags = cleanTags,
                                jobDescription = jobDescription.trim(),
                                notes = notes.trim(),
                                updatedAt = System.currentTimeMillis()
                            ) ?: JobApplication(
                                userId = userId,
                                companyName = companyName.trim(),
                                jobTitle = jobTitle.trim(),
                                location = location.trim(),
                                salaryMin = salaryMin.toIntOrNull() ?: 0,
                                salaryMax = salaryMax.toIntOrNull() ?: 0,
                                workType = selectedWorkType,
                                status = selectedStatus,
                                priority = selectedPriority,
                                jobUrl = jobUrl.trim(),
                                source = source.trim(),
                                tags = cleanTags,
                                jobDescription = jobDescription.trim(),
                                notes = notes.trim()
                            )

                            onSave(appToSave)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (existingApp == null) "Create Application" else "Save Changes")
                    }
                }
            }
        }
    }
}
