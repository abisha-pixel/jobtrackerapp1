package com.example.jobtrackerapp1.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobtrackerapp1.data.repository.JobTrackerRepository
import com.example.jobtrackerapp1.data.service.AiCareerAssistantService
import com.example.jobtrackerapp1.domain.model.*
import com.example.jobtrackerapp1.presentation.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JobTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JobTrackerRepository(application)

    // Current Navigation State
    val currentScreen = MutableStateFlow(Screen.DASHBOARD)

    // User & RBAC
    val currentUser = repository.currentUser

    // Data Streams from Repository
    val applications = repository.applications
    val interviews = repository.interviews
    val contacts = repository.contacts
    val resumes = repository.resumes
    val coverLetters = repository.coverLetters
    val tasks = repository.tasks
    val notifications = repository.notifications
    val jobListings = repository.jobListings
    val auditLogs = repository.auditLogs
    val history = repository.history
    val dashboardStats = repository.dashboardStats

    // Search and Filters for Applications
    val searchQuery = MutableStateFlow("")
    val filterStatus = MutableStateFlow<ApplicationStatus?>(null)
    val filterPriority = MutableStateFlow<Priority?>(null)
    val filterWorkType = MutableStateFlow<WorkType?>(null)

    // Filtered Applications Derived Flow
    val filteredApplications = combine(
        applications,
        searchQuery,
        filterStatus,
        filterPriority,
        filterWorkType
    ) { apps, query, status, priority, workType ->
        apps.filter { app ->
            val matchesQuery = query.isBlank() ||
                    app.companyName.contains(query, ignoreCase = true) ||
                    app.jobTitle.contains(query, ignoreCase = true) ||
                    app.location.contains(query, ignoreCase = true) ||
                    app.tags.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = status == null || app.status == status
            val matchesPriority = priority == null || app.priority == priority
            val matchesWorkType = workType == null || app.workType == workType

            matchesQuery && matchesStatus && matchesPriority && matchesWorkType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Application for Detail Modal / Screen
    val selectedApplicationId = MutableStateFlow<String?>(null)
    val selectedApplication = combine(applications, selectedApplicationId) { apps, id ->
        apps.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Dialog & Sheet states
    val isAddEditAppDialogOpen = MutableStateFlow(false)
    val editingApplication = MutableStateFlow<JobApplication?>(null)

    val isAddInterviewDialogOpen = MutableStateFlow(false)
    val isAddContactDialogOpen = MutableStateFlow(false)
    val isAddReminderDialogOpen = MutableStateFlow(false)
    val isAddResumeDialogOpen = MutableStateFlow(false)
    val isCommandPaletteOpen = MutableStateFlow(false)
    val isFilterSheetOpen = MutableStateFlow(false)

    // Theme & Notification Preferences
    val isDarkMode = MutableStateFlow(true)
    val emailNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled = MutableStateFlow(true)

    // Feedback Toast / Snackbar State
    val toastMessage = MutableSharedFlow<String>()

    // AI Analysis State
    val aiAnalysisState = MutableStateFlow<AiAnalysisResult?>(null)
    val isAiAnalyzing = MutableStateFlow(false)

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun showToast(msg: String) {
        viewModelScope.launch { toastMessage.emit(msg) }
    }

    // Role Switching
    fun switchRole(role: UserRole) {
        repository.switchRole(role)
        showToast("Switched active view to ${role.displayName()}")
    }

    fun updateProfile(user: User) {
        repository.updateProfile(user)
        showToast("Profile updated successfully")
    }

    // Application Actions
    fun openAddApplicationDialog() {
        editingApplication.value = null
        isAddEditAppDialogOpen.value = true
    }

    fun openEditApplicationDialog(app: JobApplication) {
        editingApplication.value = app
        isAddEditAppDialogOpen.value = true
    }

    fun saveApplication(app: JobApplication) {
        // Duplicate check
        val duplicate = AiCareerAssistantService.detectDuplicateJob(
            newCompany = app.companyName,
            newTitle = app.jobTitle,
            existingApplications = applications.value.filter { it.id != app.id }
        )
        if (duplicate != null) {
            showToast("Notice: Similar active application found at ${duplicate.companyName}")
        }

        repository.saveApplication(app)
        isAddEditAppDialogOpen.value = false
        showToast("Application for ${app.companyName} saved")
    }

    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus) {
        repository.updateApplicationStatus(appId, newStatus)
        showToast("Moved to ${newStatus.readableName()}")
    }

    fun deleteApplication(appId: String) {
        repository.deleteApplication(appId)
        if (selectedApplicationId.value == appId) {
            selectedApplicationId.value = null
        }
        showToast("Application deleted")
    }

    // Interview Actions
    fun saveInterview(interview: Interview) {
        repository.saveInterview(interview)
        isAddInterviewDialogOpen.value = false
        showToast("Interview scheduled for ${interview.companyName}")
    }

    fun toggleChecklistItem(interviewId: String, index: Int) {
        repository.toggleChecklistItem(interviewId, index)
    }

    fun deleteInterview(interviewId: String) {
        repository.deleteInterview(interviewId)
        showToast("Interview removed")
    }

    // Contact Actions
    fun saveContact(contact: RecruiterContact) {
        repository.saveContact(contact)
        isAddContactDialogOpen.value = false
        showToast("Contact ${contact.name} saved")
    }

    fun deleteContact(contactId: String) {
        repository.deleteContact(contactId)
        showToast("Contact deleted")
    }

    // Task & Reminder Actions
    fun saveTask(task: TaskReminder) {
        repository.saveTask(task)
        isAddReminderDialogOpen.value = false
        showToast("Reminder created")
    }

    fun toggleTaskCompleted(taskId: String) {
        repository.toggleTaskCompleted(taskId)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
        showToast("Task removed")
    }

    // Notification Actions
    fun markNotificationRead(notifId: String) {
        repository.markNotificationRead(notifId)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
        showToast("All notifications marked as read")
    }

    // Resume Actions
    fun saveResume(resume: ResumeVersion) {
        repository.saveResume(resume)
        isAddResumeDialogOpen.value = false
        showToast("Resume version '${resume.versionName}' saved")
    }

    fun deleteResume(resumeId: String) {
        repository.deleteResume(resumeId)
        showToast("Resume version deleted")
    }

    fun saveCoverLetter(coverLetter: CoverLetter) {
        repository.saveCoverLetter(coverLetter)
        showToast("Cover letter saved")
    }

    // Job Search & Saved Jobs Actions
    fun toggleSaveJobListing(jobId: String) {
        repository.toggleSaveJobListing(jobId)
        showToast("Updated saved status")
    }

    fun convertJobListingToApplication(listing: JobListing) {
        repository.convertJobListingToApplication(listing)
        showToast("Converted '${listing.title}' into an active application!")
    }

    // AI Career Assistant Analysis
    fun runAiAnalysis(jobTitle: String, company: String, jobDescription: String) {
        isAiAnalyzing.value = true
        viewModelScope.launch {
            val user = currentUser.value
            val defaultResume = resumes.value.firstOrNull { it.isDefault } ?: resumes.value.firstOrNull()
            val resumeText = defaultResume?.resumeSummary ?: user.experience

            val result = AiCareerAssistantService.analyzeResumeMatch(
                resumeText = resumeText,
                userSkills = user.skills,
                jobTitle = jobTitle,
                company = company,
                jobDescription = jobDescription
            )
            aiAnalysisState.value = result
            isAiAnalyzing.value = false
            showToast("AI Career Analysis complete: ${result.matchScorePercent}% Match")
        }
    }

    // Reports Export
    fun exportCsv(): String {
        val csv = repository.exportApplicationsCsv()
        showToast("Applications exported to CSV format")
        return csv
    }

    fun exportAnalyticsReport(): String {
        val report = repository.exportAnalyticsReport()
        showToast("Analytics Report generated successfully")
        return report
    }
}
