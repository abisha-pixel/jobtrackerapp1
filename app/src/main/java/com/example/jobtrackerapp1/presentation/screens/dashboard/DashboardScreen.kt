package com.example.jobtrackerapp1.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.ApplicationStatus
import com.example.jobtrackerapp1.domain.model.DashboardStats
import com.example.jobtrackerapp1.domain.model.JobApplication
import com.example.jobtrackerapp1.presentation.components.PriorityBadge
import com.example.jobtrackerapp1.presentation.components.StatCard
import com.example.jobtrackerapp1.presentation.components.StatusBadge
import com.example.jobtrackerapp1.presentation.navigation.Screen
import com.example.jobtrackerapp1.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    recentApplications: List<JobApplication>,
    onNavigate: (Screen) -> Unit,
    onSelectApplication: (JobApplication) -> Unit,
    onAddNewApplication: () -> Unit,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddNewApplication,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Application", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { onNavigate(Screen.KANBAN) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ViewColumn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kanban Board", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Metrics Grid (Row 1: Total Applied & In Progress)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Applied",
                    count = stats.totalApplied.toString(),
                    subtitle = "Across all platforms",
                    icon = Icons.Filled.Work,
                    accentColor = IndigoLight,
                    onClick = { onNavigate(Screen.APPLICATIONS) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "In Progress",
                    count = stats.inProgress.toString(),
                    subtitle = "Active review stages",
                    icon = Icons.Filled.TrendingUp,
                    accentColor = CyanAccent,
                    onClick = { onNavigate(Screen.KANBAN) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Metrics Grid (Row 2: Interviews Scheduled & Offers)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Interviews Scheduled",
                    count = stats.interviewsScheduled.toString(),
                    subtitle = "Upcoming rounds",
                    icon = Icons.Filled.VideoCameraFront,
                    accentColor = PurpleAccent,
                    onClick = { onNavigate(Screen.INTERVIEWS) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Offers Received",
                    count = stats.offersReceived.toString(),
                    subtitle = "${"%.0f".format(stats.successRate)}% success rate",
                    icon = Icons.Filled.EmojiEvents,
                    accentColor = EmeraldSuccess,
                    onClick = { onNavigate(Screen.APPLICATIONS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Metrics Grid (Row 3: Follow-ups due & Avg Response Time)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Follow-ups Due",
                    count = stats.followUpsDue.toString(),
                    subtitle = "Requires recruiter ping",
                    icon = Icons.Filled.Alarm,
                    accentColor = AmberWarning,
                    onClick = { onNavigate(Screen.REMINDERS) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Avg. Response Time",
                    count = "${"%.1f".format(stats.averageResponseDays)}d",
                    subtitle = "From apply to feedback",
                    icon = Icons.Filled.Speed,
                    accentColor = IndigoLight,
                    onClick = { onNavigate(Screen.ANALYTICS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Interactive Pipeline Stages Gauge Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pipeline Distribution",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { onNavigate(Screen.KANBAN) }) {
                            Text("Open Kanban →", fontSize = 12.sp, color = CyanAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Visual Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(5.dp))
                    ) {
                        val total = stats.totalApplied.coerceAtLeast(1)
                        val appliedCount = stats.statusBreakdown[ApplicationStatus.APPLIED] ?: 0
                        val interviewCount = (stats.statusBreakdown[ApplicationStatus.INTERVIEW] ?: 0) +
                                (stats.statusBreakdown[ApplicationStatus.TECHNICAL_INTERVIEW] ?: 0) +
                                (stats.statusBreakdown[ApplicationStatus.FINAL_ROUND] ?: 0) +
                                (stats.statusBreakdown[ApplicationStatus.SCREENING] ?: 0)
                        val offerCount = (stats.statusBreakdown[ApplicationStatus.OFFER] ?: 0) +
                                (stats.statusBreakdown[ApplicationStatus.ACCEPTED] ?: 0)

                        if (appliedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(appliedCount.toFloat() / total)
                                    .fillMaxHeight()
                                    .background(CyanAccent, RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                            )
                        }
                        if (interviewCount > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(interviewCount.toFloat() / total)
                                    .fillMaxHeight()
                                    .background(PurpleAccent)
                            )
                        }
                        if (offerCount > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(offerCount.toFloat() / total)
                                    .fillMaxHeight()
                                    .background(EmeraldSuccess, RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stage count chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Applied", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stats.statusBreakdown[ApplicationStatus.APPLIED] ?: 0}", fontWeight = FontWeight.Bold, color = CyanAccent)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Interviews", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stats.interviewsScheduled}", fontWeight = FontWeight.Bold, color = PurpleAccent)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Offers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stats.offersReceived}", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rejected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stats.rejectedCount}", fontWeight = FontWeight.Bold, color = RoseDanger)
                        }
                    }
                }
            }
        }

        // Recent Applications List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Applications",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { onNavigate(Screen.APPLICATIONS) }) {
                    Text("View All (${stats.totalApplied}) →", fontSize = 12.sp, color = IndigoLight)
                }
            }
        }

        // Recent Applications Cards
        items(recentApplications.take(4)) { app ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectApplication(app) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.companyName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        StatusBadge(status = app.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = app.jobTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${app.location} • Applied: ${dateFormat.format(Date(app.applicationDate))}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        PriorityBadge(priority = app.priority)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
