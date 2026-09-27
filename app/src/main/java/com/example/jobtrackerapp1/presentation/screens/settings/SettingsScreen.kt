package com.example.jobtrackerapp1.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.User
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary

@Composable
fun SettingsScreen(
    user: User,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    emailNotifEnabled: Boolean,
    onToggleEmailNotif: (Boolean) -> Unit,
    pushNotifEnabled: Boolean,
    onTogglePushNotif: (Boolean) -> Unit,
    onUpdateProfile: (User) -> Unit,
    onExportCsv: () -> Unit,
    onExportReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var location by remember { mutableStateOf(user.location) }
    var portfolio by remember { mutableStateOf(user.portfolioUrl) }
    var linkedin by remember { mutableStateOf(user.linkedInUrl) }
    var github by remember { mutableStateOf(user.gitHubUrl) }
    var skillsStr by remember { mutableStateOf(user.skills.joinToString(", ")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = IndigoLight)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Settings & Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Preferences, candidate profile & system notifications",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "CANDIDATE PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = skillsStr,
                    onValueChange = { skillsStr = it },
                    label = { Text("Skills (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = portfolio,
                    onValueChange = { portfolio = it },
                    label = { Text("Portfolio Website") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = linkedin,
                    onValueChange = { linkedin = it },
                    label = { Text("LinkedIn URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = github,
                    onValueChange = { github = it },
                    label = { Text("GitHub URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val cleanSkills = skillsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        val updated = user.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            location = location.trim(),
                            portfolioUrl = portfolio.trim(),
                            linkedInUrl = linkedin.trim(),
                            gitHubUrl = github.trim(),
                            skills = cleanSkills
                        )
                        onUpdateProfile(updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Save Profile Changes")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preferences Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "DISPLAY & NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Dark Mode Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode Theme", fontSize = 14.sp)
                    Switch(checked = isDarkMode, onCheckedChange = onToggleDarkMode)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Push Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push Alerts (Interviews & Deadlines)", fontSize = 14.sp)
                    Switch(checked = pushNotifEnabled, onCheckedChange = onTogglePushNotif)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Email Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Digest & Follow-up Reminders", fontSize = 14.sp)
                    Switch(checked = emailNotifEnabled, onCheckedChange = onToggleEmailNotif)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data Export Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "DATA PORTABILITY & EXPORT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onExportCsv,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Export CSV")
                    }

                    OutlinedButton(
                        onClick = onExportReport,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Executive Report")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
