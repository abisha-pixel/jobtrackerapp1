package com.example.jobtrackerapp1.presentation.screens.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.jobtrackerapp1.domain.model.ApplicationStatus
import com.example.jobtrackerapp1.domain.model.JobApplication
import com.example.jobtrackerapp1.presentation.components.PriorityBadge
import com.example.jobtrackerapp1.presentation.components.StatusBadge
import com.example.jobtrackerapp1.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class KanbanColumn(
    val title: String,
    val statuses: List<ApplicationStatus>,
    val headerColor: Color
)

@Composable
fun KanbanPipelineScreen(
    applications: List<JobApplication>,
    onSelectApplication: (JobApplication) -> Unit,
    onMoveApplicationStatus: (String, ApplicationStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = listOf(
        KanbanColumn("1. Discovery", listOf(ApplicationStatus.WISHLIST, ApplicationStatus.PREPARING), StatusDiscovery),
        KanbanColumn("2. Applied", listOf(ApplicationStatus.APPLIED, ApplicationStatus.APPLICATION_VIEWED, ApplicationStatus.RECRUITER_CONTACTED), StatusActive),
        KanbanColumn("3. Interviewing", listOf(ApplicationStatus.SCREENING, ApplicationStatus.INTERVIEW, ApplicationStatus.TECHNICAL_INTERVIEW, ApplicationStatus.HR_INTERVIEW, ApplicationStatus.FINAL_ROUND), StatusInterview),
        KanbanColumn("4. Offer & Decision", listOf(ApplicationStatus.OFFER, ApplicationStatus.ACCEPTED), StatusOffer),
        KanbanColumn("5. Archived", listOf(ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN), StatusArchived)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Kanban Pipeline Board",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Track progression across pipeline stages with automated stage triggers",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal scrolling columns
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            columns.forEachIndexed { colIndex, col ->
                val colApps = applications.filter { it.status in col.statuses }

                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Column Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(col.headerColor, RoundedCornerShape(5.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = col.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Counter badge
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = colApps.size.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = col.headerColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Cards in Column
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(colApps, key = { it.id }) { app ->
                                KanbanCardItem(
                                    application = app,
                                    canMoveLeft = colIndex > 0,
                                    canMoveRight = colIndex < columns.size - 1,
                                    onMoveLeft = {
                                        val prevCol = columns[colIndex - 1]
                                        onMoveApplicationStatus(app.id, prevCol.statuses.first())
                                    },
                                    onMoveRight = {
                                        val nextCol = columns[colIndex + 1]
                                        onMoveApplicationStatus(app.id, nextCol.statuses.first())
                                    },
                                    onClick = { onSelectApplication(app) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanCardItem(
    application: JobApplication,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Company & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = application.companyName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PriorityBadge(priority = application.priority)
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = application.jobTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stage badge
            StatusBadge(status = application.status)

            Spacer(modifier = Modifier.height(8.dp))

            // Salary & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (application.salaryMax > 0) {
                    Text(
                        text = "$${application.salaryMin / 1000}k - $${application.salaryMax / 1000}k",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                }
                Text(
                    text = application.location,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Next Action & Follow-up
            if (application.nextAction.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Flag,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = application.nextAction,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(4.dp))

            // Stage shift controls (Left / Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canMoveLeft) {
                    IconButton(onClick = onMoveLeft, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Move Left", tint = IndigoLight)
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }

                Text(
                    text = "Applied ${dateFormat.format(Date(application.applicationDate))}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (canMoveRight) {
                    IconButton(onClick = onMoveRight, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "Move Right", tint = IndigoLight)
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
