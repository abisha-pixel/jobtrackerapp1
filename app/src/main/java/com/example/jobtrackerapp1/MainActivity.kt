package com.example.jobtrackerapp1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobtrackerapp1.presentation.components.*
import com.example.jobtrackerapp1.presentation.navigation.Screen
import com.example.jobtrackerapp1.presentation.screens.admin.AdminPanelScreen
import com.example.jobtrackerapp1.presentation.screens.ai.AiCareerAssistantScreen
import com.example.jobtrackerapp1.presentation.screens.analytics.AnalyticsScreen
import com.example.jobtrackerapp1.presentation.screens.applications.AddEditApplicationDialog
import com.example.jobtrackerapp1.presentation.screens.applications.ApplicationDetailScreen
import com.example.jobtrackerapp1.presentation.screens.applications.ApplicationsScreen
import com.example.jobtrackerapp1.presentation.screens.contacts.ContactsScreen
import com.example.jobtrackerapp1.presentation.screens.dashboard.DashboardScreen
import com.example.jobtrackerapp1.presentation.screens.interviews.CalendarScreen
import com.example.jobtrackerapp1.presentation.screens.interviews.InterviewsScreen
import com.example.jobtrackerapp1.presentation.screens.jobsearch.JobSearchScreen
import com.example.jobtrackerapp1.presentation.screens.kanban.KanbanPipelineScreen
import com.example.jobtrackerapp1.presentation.screens.reminders.RemindersScreen
import com.example.jobtrackerapp1.presentation.screens.resumes.ResumesScreen
import com.example.jobtrackerapp1.presentation.screens.settings.SettingsScreen
import com.example.jobtrackerapp1.presentation.viewmodel.JobTrackerViewModel
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.JobTrackerApp1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: JobTrackerViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            JobTrackerApp1Theme(darkTheme = isDarkMode) {
                JobTrackerAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobTrackerAppContent(viewModel: JobTrackerViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val filteredApplications by viewModel.filteredApplications.collectAsState()
    val selectedAppId by viewModel.selectedApplicationId.collectAsState()
    val selectedApp by viewModel.selectedApplication.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val interviews by viewModel.interviews.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val resumes by viewModel.resumes.collectAsState()
    val coverLetters by viewModel.coverLetters.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val jobListings by viewModel.jobListings.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val history by viewModel.history.collectAsState()

    val isAddEditAppDialogOpen by viewModel.isAddEditAppDialogOpen.collectAsState()
    val editingApp by viewModel.editingApplication.collectAsState()
    val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
    var isNotificationCenterOpen by remember { mutableStateOf(false) }

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val emailNotif by viewModel.emailNotificationsEnabled.collectAsState()
    val pushNotif by viewModel.pushNotificationsEnabled.collectAsState()
    val aiAnalysisState by viewModel.aiAnalysisState.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()

    val unreadNotifCount = remember(notifications) { notifications.count { !it.isRead } }

    // Listen to Toast / Snackbars from ViewModel
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle Back Press
    BackHandler(enabled = selectedAppId != null || currentScreen != Screen.DASHBOARD) {
        if (selectedAppId != null) {
            viewModel.selectedApplicationId.value = null
        } else if (currentScreen != Screen.DASHBOARD) {
            viewModel.navigateTo(Screen.DASHBOARD)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                currentScreen = currentScreen,
                user = currentUser,
                onNavigate = { screen ->
                    viewModel.selectedApplicationId.value = null
                    viewModel.navigateTo(screen)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "APEX",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentScreen.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = CyanAccent
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Open Sidebar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.isCommandPaletteOpen.value = true }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Command Palette", tint = IndigoLight)
                        }
                        IconButton(onClick = { viewModel.isDarkMode.value = !isDarkMode }) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val bottomNavItems = listOf(
                        Screen.DASHBOARD,
                        Screen.APPLICATIONS,
                        Screen.KANBAN,
                        Screen.INTERVIEWS,
                        Screen.AI_ASSISTANT
                    )

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentScreen == screen && selectedAppId == null
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectedApplicationId.value = null
                                viewModel.navigateTo(screen)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IndigoLight,
                                indicatorColor = IndigoLight.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header Banner with Logo & Background (Shown on top of dashboard)
                if (currentScreen == Screen.DASHBOARD && selectedAppId == null) {
                    AppHeaderBanner(
                        user = currentUser,
                        unreadNotifCount = unreadNotifCount,
                        onOpenCommandPalette = { viewModel.isCommandPaletteOpen.value = true },
                        onOpenNotifications = { isNotificationCenterOpen = true },
                        onSwitchRole = { viewModel.switchRole(it) }
                    )
                }

                // Screen Router
                Box(modifier = Modifier.weight(1f)) {
                    if (selectedApp != null) {
                        ApplicationDetailScreen(
                            application = selectedApp!!,
                            history = history,
                            onBack = { viewModel.selectedApplicationId.value = null },
                            onEdit = { viewModel.openEditApplicationDialog(selectedApp!!) },
                            onDelete = { viewModel.deleteApplication(selectedApp!!.id) },
                            onUpdateStatus = { newStatus -> viewModel.updateApplicationStatus(selectedApp!!.id, newStatus) },
                            onScheduleInterview = { viewModel.navigateTo(Screen.INTERVIEWS) },
                            onRunAiMatch = {
                                viewModel.navigateTo(Screen.AI_ASSISTANT)
                                viewModel.runAiAnalysis(selectedApp!!.jobTitle, selectedApp!!.companyName, selectedApp!!.jobDescription)
                            }
                        )
                    } else {
                        when (currentScreen) {
                            Screen.DASHBOARD -> {
                                DashboardScreen(
                                    stats = stats,
                                    recentApplications = applications,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onSelectApplication = { app -> viewModel.selectedApplicationId.value = app.id },
                                    onAddNewApplication = { viewModel.openAddApplicationDialog() },
                                    onExportCsv = { copyToClipboard(context, viewModel.exportCsv(), "CSV Export") }
                                )
                            }
                            Screen.APPLICATIONS -> {
                                ApplicationsScreen(
                                    applications = filteredApplications,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { viewModel.searchQuery.value = it },
                                    selectedStatusFilter = filterStatus,
                                    onStatusFilterChange = { viewModel.filterStatus.value = it },
                                    onSelectApplication = { app -> viewModel.selectedApplicationId.value = app.id },
                                    onAddNewApplication = { viewModel.openAddApplicationDialog() },
                                    onExportCsv = { copyToClipboard(context, viewModel.exportCsv(), "CSV Export") }
                                )
                            }
                            Screen.KANBAN -> {
                                KanbanPipelineScreen(
                                    applications = applications,
                                    onSelectApplication = { app -> viewModel.selectedApplicationId.value = app.id },
                                    onMoveApplicationStatus = { appId, newStatus -> viewModel.updateApplicationStatus(appId, newStatus) }
                                )
                            }
                            Screen.INTERVIEWS -> {
                                InterviewsScreen(
                                    interviews = interviews,
                                    applications = applications,
                                    userId = currentUser.id,
                                    onSaveInterview = { viewModel.saveInterview(it) },
                                    onToggleChecklistItem = { intId, idx -> viewModel.toggleChecklistItem(intId, idx) },
                                    onDeleteInterview = { viewModel.deleteInterview(it) }
                                )
                            }
                            Screen.CALENDAR -> {
                                CalendarScreen(
                                    interviews = interviews,
                                    tasks = tasks
                                )
                            }
                            Screen.JOB_SEARCH -> {
                                JobSearchScreen(
                                    jobListings = jobListings,
                                    onToggleSave = { viewModel.toggleSaveJobListing(it) },
                                    onConvertJobToApplication = { viewModel.convertJobListingToApplication(it) }
                                )
                            }
                            Screen.RESUMES -> {
                                ResumesScreen(
                                    resumes = resumes,
                                    coverLetters = coverLetters,
                                    userId = currentUser.id,
                                    onSaveResume = { viewModel.saveResume(it) },
                                    onDeleteResume = { viewModel.deleteResume(it) },
                                    onSaveCoverLetter = { viewModel.saveCoverLetter(it) }
                                )
                            }
                            Screen.REMINDERS -> {
                                RemindersScreen(
                                    tasks = tasks,
                                    userId = currentUser.id,
                                    onSaveTask = { viewModel.saveTask(it) },
                                    onToggleCompleted = { viewModel.toggleTaskCompleted(it) },
                                    onDeleteTask = { viewModel.deleteTask(it) }
                                )
                            }
                            Screen.RECRUITERS -> {
                                ContactsScreen(
                                    contacts = contacts,
                                    userId = currentUser.id,
                                    onSaveContact = { viewModel.saveContact(it) },
                                    onDeleteContact = { viewModel.deleteContact(it) }
                                )
                            }
                            Screen.ANALYTICS -> {
                                AnalyticsScreen(
                                    stats = stats,
                                    applications = applications,
                                    onExportCsv = { copyToClipboard(context, viewModel.exportCsv(), "Applications CSV") },
                                    onExportReport = { copyToClipboard(context, viewModel.exportAnalyticsReport(), "Executive Report") }
                                )
                            }
                            Screen.AI_ASSISTANT -> {
                                AiCareerAssistantScreen(
                                    analysisResult = aiAnalysisState,
                                    isAnalyzing = isAiAnalyzing,
                                    onRunAnalysis = { title, comp, desc -> viewModel.runAiAnalysis(title, comp, desc) }
                                )
                            }
                            Screen.ADMIN -> {
                                AdminPanelScreen(
                                    auditLogs = auditLogs,
                                    currentRole = currentUser.role,
                                    onSwitchRole = { viewModel.switchRole(it) }
                                )
                            }
                            Screen.SETTINGS, Screen.AUTH -> {
                                SettingsScreen(
                                    user = currentUser,
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { viewModel.isDarkMode.value = it },
                                    emailNotifEnabled = emailNotif,
                                    onToggleEmailNotif = { viewModel.emailNotificationsEnabled.value = it },
                                    pushNotifEnabled = pushNotif,
                                    onTogglePushNotif = { viewModel.pushNotificationsEnabled.value = it },
                                    onUpdateProfile = { viewModel.updateProfile(it) },
                                    onExportCsv = { copyToClipboard(context, viewModel.exportCsv(), "Applications CSV") },
                                    onExportReport = { copyToClipboard(context, viewModel.exportAnalyticsReport(), "Executive Report") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    AddEditApplicationDialog(
        isOpen = isAddEditAppDialogOpen,
        onDismiss = { viewModel.isAddEditAppDialogOpen.value = false },
        existingApp = editingApp,
        userId = currentUser.id,
        onSave = { viewModel.saveApplication(it) }
    )

    CommandPaletteDialog(
        isOpen = isCommandPaletteOpen,
        onDismiss = { viewModel.isCommandPaletteOpen.value = false },
        applications = applications,
        onSelectApplication = { app ->
            viewModel.selectedApplicationId.value = app.id
            viewModel.navigateTo(Screen.APPLICATIONS)
        },
        onNavigate = { screen ->
            viewModel.selectedApplicationId.value = null
            viewModel.navigateTo(screen)
        },
        onAddNewApp = { viewModel.openAddApplicationDialog() },
        onScheduleInterview = { viewModel.navigateTo(Screen.INTERVIEWS) },
        onExportCsv = { copyToClipboard(context, viewModel.exportCsv(), "Applications CSV") },
        onExportReport = { copyToClipboard(context, viewModel.exportAnalyticsReport(), "Executive Report") }
    )

    NotificationCenterDialog(
        isOpen = isNotificationCenterOpen,
        onDismiss = { isNotificationCenterOpen = false },
        notifications = notifications,
        onMarkAllRead = { viewModel.markAllNotificationsRead() },
        onNotificationClick = { notif ->
            viewModel.markNotificationRead(notif.id)
            notif.actionRoute?.let { route ->
                when (route) {
                    "interviews" -> viewModel.navigateTo(Screen.INTERVIEWS)
                    "applications" -> viewModel.navigateTo(Screen.APPLICATIONS)
                    "kanban" -> viewModel.navigateTo(Screen.KANBAN)
                    else -> {}
                }
            }
        }
    )
}

private fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}