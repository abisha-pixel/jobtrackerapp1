package com.example.jobtrackerapp1.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val category: String = "Core"
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Overview"),
    APPLICATIONS("applications", "Applications", Icons.Filled.Work, Icons.Outlined.Work, "Tracking"),
    KANBAN("kanban", "Kanban Pipeline", Icons.Filled.ViewColumn, Icons.Outlined.ViewColumn, "Tracking"),
    JOB_SEARCH("job_search", "Job Search & Saved", Icons.Filled.Search, Icons.Outlined.Search, "Discovery"),
    INTERVIEWS("interviews", "Interviews", Icons.Filled.VideoCameraFront, Icons.Outlined.VideoCameraFront, "Engagements"),
    CALENDAR("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "Engagements"),
    RECRUITERS("recruiters", "Recruiters & CRM", Icons.Filled.People, Icons.Outlined.People, "Network"),
    RESUMES("resumes", "Resumes & Letters", Icons.Filled.Description, Icons.Outlined.Description, "Assets"),
    REMINDERS("reminders", "Tasks & Reminders", Icons.Filled.Alarm, Icons.Outlined.Alarm, "Productivity"),
    ANALYTICS("analytics", "Analytics & Reports", Icons.Filled.BarChart, Icons.Outlined.BarChart, "Insights"),
    AI_ASSISTANT("ai_assistant", "AI Career Coach", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "Intelligence"),
    ADMIN("admin", "Admin Control Panel", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "Management"),
    SETTINGS("settings", "Settings & Profile", Icons.Filled.Settings, Icons.Outlined.Settings, "Account"),
    AUTH("auth", "Authentication", Icons.Filled.Lock, Icons.Outlined.Lock, "Account")
}
