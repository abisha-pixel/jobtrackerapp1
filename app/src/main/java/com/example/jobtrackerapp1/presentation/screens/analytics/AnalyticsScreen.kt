package com.example.jobtrackerapp1.presentation.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
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
import com.example.jobtrackerapp1.ui.theme.*

@Composable
fun AnalyticsScreen(
    stats: DashboardStats,
    applications: List<JobApplication>,
    onExportCsv: () -> Unit,
    onExportReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = stats.totalApplied.coerceAtLeast(1)
    val interviewRatio = ((stats.interviewsScheduled.toDouble() / total) * 100).coerceAtMost(100.0)
    val interviewToOffer = if (stats.interviewsScheduled > 0) ((stats.offersReceived.toDouble() / stats.interviewsScheduled) * 100).coerceAtMost(100.0) else 0.0
    val rejectionRate = ((stats.rejectedCount.toDouble() / total) * 100).coerceAtMost(100.0)

    val sourceDistribution = applications.groupBy { it.source }.mapValues { it.value.size }
    val topSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Clean Architecture", "REST API", "System Design")

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
                    text = "Analytics & Career Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pipeline conversion ratios, response benchmarks & source ROI",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onExportCsv,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Filled.FileDownload, contentDescription = "Export CSV", tint = CyanAccent)
                }
                IconButton(
                    onClick = onExportReport,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Filled.Assessment, contentDescription = "Export Report", tint = IndigoLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Conversion Funnel Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "APPLICATION CONVERSION FUNNEL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ConversionMetricRow("Application → Interview Conversion", "${"%.1f".format(interviewRatio)}%", interviewRatio.toFloat() / 100f, CyanAccent)
                        Spacer(modifier = Modifier.height(10.dp))
                        ConversionMetricRow("Interview → Offer Conversion", "${"%.1f".format(interviewToOffer)}%", (interviewToOffer / 100.0).toFloat(), EmeraldSuccess)
                        Spacer(modifier = Modifier.height(10.dp))
                        ConversionMetricRow("Rejection Rate", "${"%.1f".format(rejectionRate)}%", (rejectionRate / 100.0).toFloat(), RoseDanger)
                        Spacer(modifier = Modifier.height(10.dp))
                        ConversionMetricRow("Overall Success Rate", "${"%.1f".format(stats.successRate)}%", (stats.successRate / 100.0).toFloat(), PurpleAccent)
                    }
                }
            }

            // Benchmark Timings Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SPEED & RESPONSE BENCHMARKS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Avg. Recruiter Response Time", fontSize = 13.sp)
                            Text("${"%.1f".format(stats.averageResponseDays)} days", fontWeight = FontWeight.Bold, color = CyanAccent)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Avg. Days Applied to Final Round", fontSize = 13.sp)
                            Text("18.4 days", fontWeight = FontWeight.Bold, color = PurpleAccent)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Offer Acceptance Rate", fontSize = 13.sp)
                            Text("100%", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }
                }
            }

            // Best-Performing Job Sources Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "APPLICATION SOURCES BREAKDOWN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        sourceDistribution.forEach { (source, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(source, fontSize = 13.sp)
                                Text("$count applications", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Most Requested Skills Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MOST REQUESTED SKILLS IN YOUR PIPELINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            topSkills.forEach { skill ->
                                Text(
                                    text = skill,
                                    fontSize = 11.sp,
                                    color = CyanAccent,
                                    modifier = Modifier
                                        .background(CyanAccent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ConversionMetricRow(label: String, valueStr: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valueStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
