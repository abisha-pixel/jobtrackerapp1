package com.example.jobtrackerapp1.presentation.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.AiAnalysisResult
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.EmeraldSuccess
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary

@Composable
fun AiCareerAssistantScreen(
    analysisResult: AiAnalysisResult?,
    isAnalyzing: Boolean,
    onRunAnalysis: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var company by remember { mutableStateOf("Stripe") }
    var jobTitle by remember { mutableStateOf("Staff Mobile Platform Engineer") }
    var jobDescription by remember {
        mutableStateOf("We are looking for a Staff Mobile Engineer with deep Kotlin, Jetpack Compose, Coroutines, and Clean Architecture experience to scale our payment SDKs and ensure high reliability.")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = CyanAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "AI Career Intelligence Assistant",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ATS Scanner, Match Scoring, Tailored Cover Letters & Interview Simulation",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TARGET ROLE SPECIFICATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoLight
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("Company") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Job Title") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = jobDescription,
                    onValueChange = { jobDescription = it },
                    label = { Text("Job Description (or key requirements)") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onRunAnalysis(jobTitle, company, jobDescription) },
                    enabled = !isAnalyzing && company.isNotBlank() && jobTitle.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Profile Match...")
                    } else {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run AI Match & ATS Scan")
                    }
                }
            }
        }

        // Analysis Results
        if (analysisResult != null) {
            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ATS MATCH SCORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${analysisResult.matchScorePercent}% Match",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (analysisResult.matchScorePercent >= 75) EmeraldSuccess else CyanAccent
                            )
                        }

                        LinearProgressIndicator(
                            progress = { analysisResult.matchScorePercent / 100f },
                            modifier = Modifier
                                .width(120.dp)
                                .height(10.dp),
                            color = if (analysisResult.matchScorePercent >= 75) EmeraldSuccess else CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysisResult.atsSummary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Matched & Missing Keywords Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "KEYWORD ANALYSIS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Matched Keywords in Your Profile:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        analysisResult.matchedKeywords.forEach { kw ->
                            Text(
                                text = "✓ $kw",
                                fontSize = 11.sp,
                                color = EmeraldSuccess,
                                modifier = Modifier
                                    .background(EmeraldSuccess.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Recommended Keywords to Add:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        analysisResult.missingKeywords.forEach { kw ->
                            Text(
                                text = "+ $kw",
                                fontSize = 11.sp,
                                color = CyanAccent,
                                modifier = Modifier
                                    .background(CyanAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Improvement Tips Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "RESUME IMPROVEMENT SUGGESTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    analysisResult.resumeImprovementTips.forEach { tip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text("• ", color = CyanAccent, fontWeight = FontWeight.Bold)
                            Text(text = tip, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tailored Cover Letter Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GENERATED TAILORED COVER LETTER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysisResult.generatedCoverLetter,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Follow-up Email Draft Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PERSONALIZED FOLLOW-UP EMAIL DRAFT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysisResult.followUpEmailDraft,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
