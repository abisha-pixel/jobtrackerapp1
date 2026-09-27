package com.example.jobtrackerapp1.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.jobtrackerapp1.data.local.DatabaseHelper
import com.example.jobtrackerapp1.data.local.SeedData
import com.example.jobtrackerapp1.data.service.AutomationEngine
import com.example.jobtrackerapp1.data.service.ReportExporter
import com.example.jobtrackerapp1.domain.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class JobTrackerRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current logged-in user state
    private val _currentUser = MutableStateFlow(SeedData.defaultCandidate)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Reactive StateFlows for UI
    private val _applications = MutableStateFlow<List<JobApplication>>(emptyList())
    val applications: StateFlow<List<JobApplication>> = _applications.asStateFlow()

    private val _interviews = MutableStateFlow<List<Interview>>(emptyList())
    val interviews: StateFlow<List<Interview>> = _interviews.asStateFlow()

    private val _contacts = MutableStateFlow<List<RecruiterContact>>(emptyList())
    val contacts: StateFlow<List<RecruiterContact>> = _contacts.asStateFlow()

    private val _resumes = MutableStateFlow<List<ResumeVersion>>(emptyList())
    val resumes: StateFlow<List<ResumeVersion>> = _resumes.asStateFlow()

    private val _coverLetters = MutableStateFlow<List<CoverLetter>>(emptyList())
    val coverLetters: StateFlow<List<CoverLetter>> = _coverLetters.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskReminder>>(emptyList())
    val tasks: StateFlow<List<TaskReminder>> = _tasks.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _jobListings = MutableStateFlow<List<JobListing>>(emptyList())
    val jobListings: StateFlow<List<JobListing>> = _jobListings.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    private val _history = MutableStateFlow<List<ApplicationHistory>>(emptyList())
    val history: StateFlow<List<ApplicationHistory>> = _history.asStateFlow()

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        scope.launch {
            seedDatabaseIfEmpty()
            refreshAll()
        }
    }

    private fun seedDatabaseIfEmpty() {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            // Insert Users
            listOf(SeedData.defaultCandidate, SeedData.defaultRecruiter, SeedData.defaultHiringManager, SeedData.defaultAdmin).forEach { u ->
                insertUserInDb(db, u)
            }
            // Insert Resumes
            SeedData.sampleResumes.forEach { r -> insertResumeInDb(db, r) }
            // Insert Cover Letters
            SeedData.sampleCoverLetters.forEach { cl -> insertCoverLetterInDb(db, cl) }
            // Insert Contacts
            SeedData.sampleContacts.forEach { c -> insertContactInDb(db, c) }
            // Insert Applications
            SeedData.sampleApplications.forEach { a -> insertApplicationInDb(db, a) }
            // Insert Interviews
            SeedData.sampleInterviews.forEach { i -> insertInterviewInDb(db, i) }
            // Insert Reminders
            SeedData.sampleReminders.forEach { r -> insertReminderInDb(db, r) }
            // Insert Notifications
            SeedData.sampleNotifications.forEach { n -> insertNotificationInDb(db, n) }
            // Insert Job Listings
            SeedData.sampleJobListings.forEach { j -> insertJobListingInDb(db, j) }
            // Insert Audit Logs
            SeedData.sampleAuditLogs.forEach { log -> insertAuditLogInDb(db, log) }
        }
    }

    private fun refreshAll() {
        val db = dbHelper.readableDatabase
        val currentUserId = _currentUser.value.id

        // Load applications
        val apps = mutableListOf<JobApplication>()
        val appCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_APPLICATIONS} ORDER BY application_date DESC", null)
        while (appCursor.moveToNext()) {
            apps.add(cursorToApplication(appCursor))
        }
        appCursor.close()
        _applications.value = apps

        // Load interviews
        val ints = mutableListOf<Interview>()
        val intCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_INTERVIEWS} ORDER BY interview_date_time ASC", null)
        while (intCursor.moveToNext()) {
            ints.add(cursorToInterview(intCursor))
        }
        intCursor.close()
        _interviews.value = ints

        // Load contacts
        val conts = mutableListOf<RecruiterContact>()
        val cCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_CONTACTS} ORDER BY created_at DESC", null)
        while (cCursor.moveToNext()) {
            conts.add(cursorToContact(cCursor))
        }
        cCursor.close()
        _contacts.value = conts

        // Load resumes
        val resList = mutableListOf<ResumeVersion>()
        val resCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_RESUMES} ORDER BY uploaded_at DESC", null)
        while (resCursor.moveToNext()) {
            resList.add(cursorToResume(resCursor))
        }
        resCursor.close()
        _resumes.value = resList

        // Load cover letters
        val clList = mutableListOf<CoverLetter>()
        val clCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_COVER_LETTERS} ORDER BY created_at DESC", null)
        while (clCursor.moveToNext()) {
            clList.add(cursorToCoverLetter(clCursor))
        }
        clCursor.close()
        _coverLetters.value = clList

        // Load tasks
        val taskList = mutableListOf<TaskReminder>()
        val tCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TASKS_REMINDERS} ORDER BY due_date ASC", null)
        while (tCursor.moveToNext()) {
            taskList.add(cursorToTask(tCursor))
        }
        tCursor.close()
        _tasks.value = taskList

        // Load notifications
        val notifList = mutableListOf<AppNotification>()
        val nCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NOTIFICATIONS} ORDER BY timestamp DESC", null)
        while (nCursor.moveToNext()) {
            notifList.add(cursorToNotification(nCursor))
        }
        nCursor.close()
        _notifications.value = notifList

        // Load job listings
        val jobList = mutableListOf<JobListing>()
        val jCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_JOB_LISTINGS} ORDER BY id ASC", null)
        while (jCursor.moveToNext()) {
            jobList.add(cursorToJobListing(jCursor))
        }
        jCursor.close()
        _jobListings.value = jobList

        // Load audit logs
        val auditList = mutableListOf<AuditLogEntry>()
        val aCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_AUDIT_LOGS} ORDER BY timestamp DESC", null)
        while (aCursor.moveToNext()) {
            auditList.add(cursorToAuditLog(aCursor))
        }
        aCursor.close()
        _auditLogs.value = auditList

        // Load history
        val histList = mutableListOf<ApplicationHistory>()
        val hCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_APPLICATION_HISTORY} ORDER BY timestamp DESC", null)
        while (hCursor.moveToNext()) {
            histList.add(cursorToHistory(hCursor))
        }
        hCursor.close()
        _history.value = histList

        // Calculate Dashboard Stats
        computeDashboardStats(apps, ints, taskList)
    }

    private fun computeDashboardStats(
        apps: List<JobApplication>,
        interviews: List<Interview>,
        tasks: List<TaskReminder>
    ) {
        val total = apps.size
        val inProgress = apps.count { it.status != ApplicationStatus.REJECTED && it.status != ApplicationStatus.WITHDRAWN && it.status != ApplicationStatus.OFFER && it.status != ApplicationStatus.ACCEPTED }
        val scheduled = interviews.count { it.result == InterviewResult.PENDING }
        val offers = apps.count { it.status == ApplicationStatus.OFFER || it.status == ApplicationStatus.ACCEPTED }
        val rejected = apps.count { it.status == ApplicationStatus.REJECTED }
        val saved = _jobListings.value.count { it.isSaved }
        val followUpsDue = tasks.count { !it.isCompleted && it.dueDate <= System.currentTimeMillis() + (24 * 3600 * 1000) }

        val successRate = if (total > 0) ((offers.toDouble() / total.toDouble()) * 100) else 0.0
        val avgDays = 4.5

        val statusMap = apps.groupBy { it.status }.mapValues { it.value.size }

        _dashboardStats.value = DashboardStats(
            totalApplied = total,
            inProgress = inProgress,
            interviewsScheduled = scheduled,
            offersReceived = offers,
            rejectedCount = rejected,
            savedCount = saved,
            followUpsDue = followUpsDue,
            successRate = successRate,
            averageResponseDays = avgDays,
            statusBreakdown = statusMap
        )
    }

    // --- APPLICATION OPERATIONS ---

    fun saveApplication(application: JobApplication) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val existing = _applications.value.firstOrNull { it.id == application.id }

            if (existing == null) {
                insertApplicationInDb(db, application)
                // Log audit
                val audit = AuditLogEntry(
                    userId = _currentUser.value.id,
                    userEmail = _currentUser.value.email,
                    action = "CREATE_APPLICATION",
                    entityType = "APPLICATION",
                    details = "Created application for ${application.companyName} (${application.jobTitle})"
                )
                insertAuditLogInDb(db, audit)
            } else {
                updateApplicationInDb(db, application)

                // If status changed, run automation engine!
                if (existing.status != application.status) {
                    val automation = AutomationEngine.onApplicationStatusChanged(
                        application = application,
                        fromStatus = existing.status,
                        toStatus = application.status,
                        userEmail = _currentUser.value.email
                    )
                    automation.newTasks.forEach { insertReminderInDb(db, it) }
                    automation.newNotifications.forEach { insertNotificationInDb(db, it) }
                    automation.historyEntry?.let { insertHistoryInDb(db, it) }
                    automation.auditLogEntry?.let { insertAuditLogInDb(db, it) }
                }
            }
            refreshAll()
        }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: ApplicationStatus) {
        scope.launch {
            val app = _applications.value.firstOrNull { it.id == applicationId } ?: return@launch
            val updated = app.copy(status = newStatus, updatedAt = System.currentTimeMillis())
            saveApplication(updated)
        }
    }

    fun deleteApplication(applicationId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            db.delete(DatabaseHelper.TABLE_APPLICATIONS, "id = ?", arrayOf(applicationId))
            val audit = AuditLogEntry(
                userId = _currentUser.value.id,
                userEmail = _currentUser.value.email,
                action = "DELETE_APPLICATION",
                entityType = "APPLICATION",
                details = "Deleted application $applicationId"
            )
            insertAuditLogInDb(db, audit)
            refreshAll()
        }
    }

    // --- INTERVIEWS ---

    fun saveInterview(interview: Interview) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val existing = _interviews.value.firstOrNull { it.id == interview.id }
            if (existing == null) {
                insertInterviewInDb(db, interview)
                val automation = AutomationEngine.onInterviewScheduled(interview, _currentUser.value.email)
                automation.newTasks.forEach { insertReminderInDb(db, it) }
                automation.newNotifications.forEach { insertNotificationInDb(db, it) }
                automation.auditLogEntry?.let { insertAuditLogInDb(db, it) }
            } else {
                updateInterviewInDb(db, interview)
            }
            refreshAll()
        }
    }

    fun deleteInterview(interviewId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            db.delete(DatabaseHelper.TABLE_INTERVIEWS, "id = ?", arrayOf(interviewId))
            refreshAll()
        }
    }

    fun toggleChecklistItem(interviewId: String, itemIndex: Int) {
        scope.launch {
            val interview = _interviews.value.firstOrNull { it.id == interviewId } ?: return@launch
            val updatedChecklist = interview.preparationChecklist.mapIndexed { idx, item ->
                if (idx == itemIndex) item.copy(isCompleted = !item.isCompleted) else item
            }
            saveInterview(interview.copy(preparationChecklist = updatedChecklist))
        }
    }

    // --- CONTACTS ---

    fun saveContact(contact: RecruiterContact) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val existing = _contacts.value.firstOrNull { it.id == contact.id }
            if (existing == null) {
                insertContactInDb(db, contact)
            } else {
                updateContactInDb(db, contact)
            }
            refreshAll()
        }
    }

    fun deleteContact(contactId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            db.delete(DatabaseHelper.TABLE_CONTACTS, "id = ?", arrayOf(contactId))
            refreshAll()
        }
    }

    // --- REMINDERS & TASKS ---

    fun saveTask(task: TaskReminder) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val existing = _tasks.value.firstOrNull { it.id == task.id }
            if (existing == null) {
                insertReminderInDb(db, task)
            } else {
                updateReminderInDb(db, task)
            }
            refreshAll()
        }
    }

    fun toggleTaskCompleted(taskId: String) {
        scope.launch {
            val task = _tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            saveTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(taskId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            db.delete(DatabaseHelper.TABLE_TASKS_REMINDERS, "id = ?", arrayOf(taskId))
            refreshAll()
        }
    }

    // --- NOTIFICATIONS ---

    fun markNotificationRead(notifId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val cv = ContentValues().apply { put("is_read", 1) }
            db.update(DatabaseHelper.TABLE_NOTIFICATIONS, cv, "id = ?", arrayOf(notifId))
            refreshAll()
        }
    }

    fun markAllNotificationsRead() {
        scope.launch {
            val db = dbHelper.writableDatabase
            val cv = ContentValues().apply { put("is_read", 1) }
            db.update(DatabaseHelper.TABLE_NOTIFICATIONS, cv, "user_id = ?", arrayOf(_currentUser.value.id))
            refreshAll()
        }
    }

    // --- RESUMES & COVER LETTERS ---

    fun saveResume(resume: ResumeVersion) {
        scope.launch {
            val db = dbHelper.writableDatabase
            insertResumeInDb(db, resume)
            refreshAll()
        }
    }

    fun deleteResume(resumeId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            db.delete(DatabaseHelper.TABLE_RESUMES, "id = ?", arrayOf(resumeId))
            refreshAll()
        }
    }

    fun saveCoverLetter(coverLetter: CoverLetter) {
        scope.launch {
            val db = dbHelper.writableDatabase
            insertCoverLetterInDb(db, coverLetter)
            refreshAll()
        }
    }

    // --- JOB SEARCH & SAVED JOBS ---

    fun toggleSaveJobListing(jobId: String) {
        scope.launch {
            val db = dbHelper.writableDatabase
            val item = _jobListings.value.firstOrNull { it.id == jobId } ?: return@launch
            val newSavedState = if (item.isSaved) 0 else 1
            val cv = ContentValues().apply { put("is_saved", newSavedState) }
            db.update(DatabaseHelper.TABLE_JOB_LISTINGS, cv, "id = ?", arrayOf(jobId))
            refreshAll()
        }
    }

    fun convertJobListingToApplication(listing: JobListing) {
        val newApp = JobApplication(
            id = UUID.randomUUID().toString(),
            userId = _currentUser.value.id,
            companyName = listing.company,
            jobTitle = listing.title,
            jobDescription = listing.description,
            jobUrl = listing.sourceUrl,
            location = listing.location,
            workType = listing.workType,
            employmentType = listing.employmentType,
            applicationDate = System.currentTimeMillis(),
            source = listing.sourceName,
            status = ApplicationStatus.APPLIED,
            notes = "Imported from ${listing.sourceName} feed."
        )
        saveApplication(newApp)
    }

    // --- USER & ROLE SWITCHING (RBAC) ---

    fun switchRole(newRole: UserRole) {
        val target = when (newRole) {
            UserRole.CANDIDATE -> SeedData.defaultCandidate
            UserRole.RECRUITER -> SeedData.defaultRecruiter
            UserRole.HIRING_MANAGER -> SeedData.defaultHiringManager
            UserRole.ADMIN -> SeedData.defaultAdmin
        }
        _currentUser.value = target
        scope.launch {
            val db = dbHelper.writableDatabase
            val audit = AuditLogEntry(
                userId = target.id,
                userEmail = target.email,
                action = "ROLE_SWITCH",
                entityType = "USER",
                details = "Switched active view to role: ${newRole.name}"
            )
            insertAuditLogInDb(db, audit)
            refreshAll()
        }
    }

    fun updateProfile(updated: User) {
        _currentUser.value = updated
        scope.launch {
            val db = dbHelper.writableDatabase
            val cv = ContentValues().apply {
                put("full_name", updated.fullName)
                put("phone", updated.phone)
                put("location", updated.location)
                put("skills", updated.skills.joinToString(","))
                put("experience", updated.experience)
                put("education", updated.education)
                put("portfolio_url", updated.portfolioUrl)
                put("linkedin_url", updated.linkedInUrl)
                put("github_url", updated.gitHubUrl)
            }
            db.update(DatabaseHelper.TABLE_USERS, cv, "id = ?", arrayOf(updated.id))
            refreshAll()
        }
    }

    // Export helpers
    fun exportApplicationsCsv(): String = ReportExporter.generateApplicationsCsv(_applications.value)
    fun exportAnalyticsReport(): String = ReportExporter.generateAnalyticsSummaryReport(
        _dashboardStats.value,
        _applications.value,
        _currentUser.value.email
    )

    // --- DB HELPER METHODS ---

    private fun insertUserInDb(db: SQLiteDatabase, u: User) {
        val cv = ContentValues().apply {
            put("id", u.id)
            put("email", u.email)
            put("full_name", u.fullName)
            put("photo_url", u.photoUrl)
            put("phone", u.phone)
            put("location", u.location)
            put("skills", u.skills.joinToString(","))
            put("experience", u.experience)
            put("education", u.education)
            put("portfolio_url", u.portfolioUrl)
            put("linkedin_url", u.linkedInUrl)
            put("github_url", u.gitHubUrl)
            put("resume_id", u.resumeId)
            put("role", u.role.name)
            put("created_at", u.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertApplicationInDb(db: SQLiteDatabase, a: JobApplication) {
        val cv = ContentValues().apply {
            put("id", a.id)
            put("user_id", a.userId)
            put("company_name", a.companyName)
            put("job_title", a.jobTitle)
            put("job_description", a.jobDescription)
            put("job_url", a.jobUrl)
            put("location", a.location)
            put("work_type", a.workType.name)
            put("salary_min", a.salaryMin)
            put("salary_max", a.salaryMax)
            put("salary_currency", a.salaryCurrency)
            put("employment_type", a.employmentType.name)
            put("application_date", a.applicationDate)
            put("application_deadline", a.applicationDeadline)
            put("recruiter_id", a.recruiterId)
            put("resume_version_id", a.resumeVersionId)
            put("cover_letter_id", a.coverLetterId)
            put("source", a.source)
            put("priority", a.priority.name)
            put("notes", a.notes)
            put("tags", a.tags.joinToString(","))
            put("status", a.status.name)
            put("company_logo", a.companyLogo)
            put("next_action", a.nextAction)
            put("follow_up_date", a.followUpDate)
            put("updated_at", a.updatedAt)
            put("created_at", a.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_APPLICATIONS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun updateApplicationInDb(db: SQLiteDatabase, a: JobApplication) {
        val cv = ContentValues().apply {
            put("company_name", a.companyName)
            put("job_title", a.jobTitle)
            put("job_description", a.jobDescription)
            put("job_url", a.jobUrl)
            put("location", a.location)
            put("work_type", a.workType.name)
            put("salary_min", a.salaryMin)
            put("salary_max", a.salaryMax)
            put("employment_type", a.employmentType.name)
            put("source", a.source)
            put("priority", a.priority.name)
            put("notes", a.notes)
            put("tags", a.tags.joinToString(","))
            put("status", a.status.name)
            put("next_action", a.nextAction)
            put("follow_up_date", a.followUpDate)
            put("updated_at", a.updatedAt)
        }
        db.update(DatabaseHelper.TABLE_APPLICATIONS, cv, "id = ?", arrayOf(a.id))
    }

    private fun insertInterviewInDb(db: SQLiteDatabase, i: Interview) {
        val checklistStr = i.preparationChecklist.joinToString(";") { "${it.text}|${it.isCompleted}" }
        val cv = ContentValues().apply {
            put("id", i.id)
            put("application_id", i.applicationId)
            put("user_id", i.userId)
            put("company_name", i.companyName)
            put("job_title", i.jobTitle)
            put("interview_date_time", i.interviewDateTime)
            put("type", i.type.name)
            put("interviewer_name", i.interviewerName)
            put("interviewer_title", i.interviewerTitle)
            put("meeting_link", i.meetingLink)
            put("location", i.location)
            put("notes", i.notes)
            put("checklist_json", checklistStr)
            put("questions_json", i.questions.joinToString(";;"))
            put("feedback", i.feedback)
            put("rating", i.rating)
            put("result", i.result.name)
            put("created_at", i.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_INTERVIEWS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun updateInterviewInDb(db: SQLiteDatabase, i: Interview) {
        val checklistStr = i.preparationChecklist.joinToString(";") { "${it.text}|${it.isCompleted}" }
        val cv = ContentValues().apply {
            put("interview_date_time", i.interviewDateTime)
            put("type", i.type.name)
            put("interviewer_name", i.interviewerName)
            put("interviewer_title", i.interviewerTitle)
            put("meeting_link", i.meetingLink)
            put("location", i.location)
            put("notes", i.notes)
            put("checklist_json", checklistStr)
            put("questions_json", i.questions.joinToString(";;"))
            put("feedback", i.feedback)
            put("rating", i.rating)
            put("result", i.result.name)
        }
        db.update(DatabaseHelper.TABLE_INTERVIEWS, cv, "id = ?", arrayOf(i.id))
    }

    private fun insertContactInDb(db: SQLiteDatabase, c: RecruiterContact) {
        val cv = ContentValues().apply {
            put("id", c.id)
            put("user_id", c.userId)
            put("name", c.name)
            put("company", c.company)
            put("job_title", c.jobTitle)
            put("email", c.email)
            put("phone", c.phone)
            put("linkedin_url", c.linkedInUrl)
            put("notes", c.notes)
            put("associated_app_ids", c.associatedApplicationIds.joinToString(","))
            put("created_at", c.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_CONTACTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun updateContactInDb(db: SQLiteDatabase, c: RecruiterContact) {
        val cv = ContentValues().apply {
            put("name", c.name)
            put("company", c.company)
            put("job_title", c.jobTitle)
            put("email", c.email)
            put("phone", c.phone)
            put("linkedin_url", c.linkedInUrl)
            put("notes", c.notes)
        }
        db.update(DatabaseHelper.TABLE_CONTACTS, cv, "id = ?", arrayOf(c.id))
    }

    private fun insertResumeInDb(db: SQLiteDatabase, r: ResumeVersion) {
        val cv = ContentValues().apply {
            put("id", r.id)
            put("user_id", r.userId)
            put("version_name", r.versionName)
            put("target_role", r.targetRole)
            put("file_name", r.fileName)
            put("skills_extracted", r.skillsExtracted.joinToString(","))
            put("resume_summary", r.resumeSummary)
            put("uploaded_at", r.uploadedAt)
            put("is_default", if (r.isDefault) 1 else 0)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_RESUMES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertCoverLetterInDb(db: SQLiteDatabase, cl: CoverLetter) {
        val cv = ContentValues().apply {
            put("id", cl.id)
            put("user_id", cl.userId)
            put("title", cl.title)
            put("target_company", cl.targetCompany)
            put("content", cl.content)
            put("created_at", cl.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_COVER_LETTERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertReminderInDb(db: SQLiteDatabase, r: TaskReminder) {
        val cv = ContentValues().apply {
            put("id", r.id)
            put("user_id", r.userId)
            put("application_id", r.applicationId)
            put("title", r.title)
            put("description", r.description)
            put("due_date", r.dueDate)
            put("reminder_type", r.reminderType.name)
            put("is_completed", if (r.isCompleted) 1 else 0)
            put("priority", r.priority.name)
            put("created_at", r.createdAt)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_TASKS_REMINDERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun updateReminderInDb(db: SQLiteDatabase, r: TaskReminder) {
        val cv = ContentValues().apply {
            put("title", r.title)
            put("description", r.description)
            put("due_date", r.dueDate)
            put("is_completed", if (r.isCompleted) 1 else 0)
            put("priority", r.priority.name)
        }
        db.update(DatabaseHelper.TABLE_TASKS_REMINDERS, cv, "id = ?", arrayOf(r.id))
    }

    private fun insertNotificationInDb(db: SQLiteDatabase, n: AppNotification) {
        val cv = ContentValues().apply {
            put("id", n.id)
            put("user_id", n.userId)
            put("title", n.title)
            put("message", n.message)
            put("type", n.type.name)
            put("is_read", if (n.isRead) 1 else 0)
            put("action_route", n.actionRoute)
            put("timestamp", n.timestamp)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_NOTIFICATIONS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertJobListingInDb(db: SQLiteDatabase, j: JobListing) {
        val cv = ContentValues().apply {
            put("id", j.id)
            put("title", j.title)
            put("company", j.company)
            put("location", j.location)
            put("salary", j.salary)
            put("work_type", j.workType.name)
            put("employment_type", j.employmentType.name)
            put("skills_required", j.skillsRequired.joinToString(","))
            put("description", j.description)
            put("source_url", j.sourceUrl)
            put("source_name", j.sourceName)
            put("is_saved", if (j.isSaved) 1 else 0)
            put("posted_date", j.postedDate)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_JOB_LISTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertAuditLogInDb(db: SQLiteDatabase, log: AuditLogEntry) {
        val cv = ContentValues().apply {
            put("id", log.id)
            put("user_id", log.userId)
            put("user_email", log.userEmail)
            put("action", log.action)
            put("entity_type", log.entityType)
            put("details", log.details)
            put("timestamp", log.timestamp)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_AUDIT_LOGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertHistoryInDb(db: SQLiteDatabase, h: ApplicationHistory) {
        val cv = ContentValues().apply {
            put("id", h.id)
            put("application_id", h.applicationId)
            put("from_status", h.fromStatus.name)
            put("to_status", h.toStatus.name)
            put("note", h.note)
            put("timestamp", h.timestamp)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_APPLICATION_HISTORY, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // --- CURSOR MAPPERS ---

    private fun cursorToApplication(c: Cursor): JobApplication {
        val tagsStr = c.getString(c.getColumnIndexOrThrow("tags")) ?: ""
        return JobApplication(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            companyName = c.getString(c.getColumnIndexOrThrow("company_name")),
            jobTitle = c.getString(c.getColumnIndexOrThrow("job_title")),
            jobDescription = c.getString(c.getColumnIndexOrThrow("job_description")) ?: "",
            jobUrl = c.getString(c.getColumnIndexOrThrow("job_url")) ?: "",
            location = c.getString(c.getColumnIndexOrThrow("location")) ?: "",
            workType = runCatching { WorkType.valueOf(c.getString(c.getColumnIndexOrThrow("work_type"))) }.getOrDefault(WorkType.REMOTE),
            salaryMin = c.getInt(c.getColumnIndexOrThrow("salary_min")),
            salaryMax = c.getInt(c.getColumnIndexOrThrow("salary_max")),
            salaryCurrency = c.getString(c.getColumnIndexOrThrow("salary_currency")) ?: "USD",
            employmentType = runCatching { EmploymentType.valueOf(c.getString(c.getColumnIndexOrThrow("employment_type"))) }.getOrDefault(EmploymentType.FULL_TIME),
            applicationDate = c.getLong(c.getColumnIndexOrThrow("application_date")),
            applicationDeadline = if (c.isNull(c.getColumnIndexOrThrow("application_deadline"))) null else c.getLong(c.getColumnIndexOrThrow("application_deadline")),
            recruiterId = c.getString(c.getColumnIndexOrThrow("recruiter_id")),
            resumeVersionId = c.getString(c.getColumnIndexOrThrow("resume_version_id")),
            coverLetterId = c.getString(c.getColumnIndexOrThrow("cover_letter_id")),
            source = c.getString(c.getColumnIndexOrThrow("source")) ?: "LinkedIn",
            priority = runCatching { Priority.valueOf(c.getString(c.getColumnIndexOrThrow("priority"))) }.getOrDefault(Priority.MEDIUM),
            notes = c.getString(c.getColumnIndexOrThrow("notes")) ?: "",
            tags = if (tagsStr.isBlank()) emptyList() else tagsStr.split(","),
            status = runCatching { ApplicationStatus.valueOf(c.getString(c.getColumnIndexOrThrow("status"))) }.getOrDefault(ApplicationStatus.APPLIED),
            companyLogo = c.getString(c.getColumnIndexOrThrow("company_logo")) ?: "",
            nextAction = c.getString(c.getColumnIndexOrThrow("next_action")) ?: "",
            followUpDate = if (c.isNull(c.getColumnIndexOrThrow("follow_up_date"))) null else c.getLong(c.getColumnIndexOrThrow("follow_up_date")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
        )
    }

    private fun cursorToInterview(c: Cursor): Interview {
        val checkStr = c.getString(c.getColumnIndexOrThrow("checklist_json")) ?: ""
        val checklist = if (checkStr.isBlank()) emptyList() else checkStr.split(";").mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size >= 2) ChecklistItem(text = parts[0], isCompleted = parts[1].toBoolean()) else null
        }
        val qStr = c.getString(c.getColumnIndexOrThrow("questions_json")) ?: ""
        val questions = if (qStr.isBlank()) emptyList() else qStr.split(";;")

        return Interview(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            applicationId = c.getString(c.getColumnIndexOrThrow("application_id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            companyName = c.getString(c.getColumnIndexOrThrow("company_name")) ?: "",
            jobTitle = c.getString(c.getColumnIndexOrThrow("job_title")) ?: "",
            interviewDateTime = c.getLong(c.getColumnIndexOrThrow("interview_date_time")),
            type = runCatching { InterviewType.valueOf(c.getString(c.getColumnIndexOrThrow("type"))) }.getOrDefault(InterviewType.TECHNICAL),
            interviewerName = c.getString(c.getColumnIndexOrThrow("interviewer_name")) ?: "",
            interviewerTitle = c.getString(c.getColumnIndexOrThrow("interviewer_title")) ?: "",
            meetingLink = c.getString(c.getColumnIndexOrThrow("meeting_link")) ?: "",
            location = c.getString(c.getColumnIndexOrThrow("location")) ?: "",
            notes = c.getString(c.getColumnIndexOrThrow("notes")) ?: "",
            preparationChecklist = checklist,
            questions = questions,
            feedback = c.getString(c.getColumnIndexOrThrow("feedback")) ?: "",
            rating = c.getInt(c.getColumnIndexOrThrow("rating")),
            result = runCatching { InterviewResult.valueOf(c.getString(c.getColumnIndexOrThrow("result"))) }.getOrDefault(InterviewResult.PENDING),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
        )
    }

    private fun cursorToContact(c: Cursor): RecruiterContact {
        val appsStr = c.getString(c.getColumnIndexOrThrow("associated_app_ids")) ?: ""
        return RecruiterContact(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            name = c.getString(c.getColumnIndexOrThrow("name")),
            company = c.getString(c.getColumnIndexOrThrow("company")),
            jobTitle = c.getString(c.getColumnIndexOrThrow("job_title")) ?: "",
            email = c.getString(c.getColumnIndexOrThrow("email")) ?: "",
            phone = c.getString(c.getColumnIndexOrThrow("phone")) ?: "",
            linkedInUrl = c.getString(c.getColumnIndexOrThrow("linkedin_url")) ?: "",
            notes = c.getString(c.getColumnIndexOrThrow("notes")) ?: "",
            associatedApplicationIds = if (appsStr.isBlank()) emptyList() else appsStr.split(","),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
        )
    }

    private fun cursorToResume(c: Cursor): ResumeVersion {
        val sStr = c.getString(c.getColumnIndexOrThrow("skills_extracted")) ?: ""
        return ResumeVersion(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            versionName = c.getString(c.getColumnIndexOrThrow("version_name")),
            targetRole = c.getString(c.getColumnIndexOrThrow("target_role")),
            fileName = c.getString(c.getColumnIndexOrThrow("file_name")),
            skillsExtracted = if (sStr.isBlank()) emptyList() else sStr.split(","),
            resumeSummary = c.getString(c.getColumnIndexOrThrow("resume_summary")) ?: "",
            uploadedAt = c.getLong(c.getColumnIndexOrThrow("uploaded_at")),
            isDefault = c.getInt(c.getColumnIndexOrThrow("is_default")) == 1
        )
    }

    private fun cursorToCoverLetter(c: Cursor): CoverLetter {
        return CoverLetter(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            targetCompany = c.getString(c.getColumnIndexOrThrow("target_company")),
            content = c.getString(c.getColumnIndexOrThrow("content")),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
        )
    }

    private fun cursorToTask(c: Cursor): TaskReminder {
        return TaskReminder(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            applicationId = c.getString(c.getColumnIndexOrThrow("application_id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            description = c.getString(c.getColumnIndexOrThrow("description")) ?: "",
            dueDate = c.getLong(c.getColumnIndexOrThrow("due_date")),
            reminderType = runCatching { ReminderType.valueOf(c.getString(c.getColumnIndexOrThrow("reminder_type"))) }.getOrDefault(ReminderType.CUSTOM),
            isCompleted = c.getInt(c.getColumnIndexOrThrow("is_completed")) == 1,
            priority = runCatching { Priority.valueOf(c.getString(c.getColumnIndexOrThrow("priority"))) }.getOrDefault(Priority.MEDIUM),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
        )
    }

    private fun cursorToNotification(c: Cursor): AppNotification {
        return AppNotification(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            message = c.getString(c.getColumnIndexOrThrow("message")),
            type = runCatching { NotificationType.valueOf(c.getString(c.getColumnIndexOrThrow("type"))) }.getOrDefault(NotificationType.SYSTEM_INFO),
            isRead = c.getInt(c.getColumnIndexOrThrow("is_read")) == 1,
            actionRoute = c.getString(c.getColumnIndexOrThrow("action_route")),
            timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"))
        )
    }

    private fun cursorToJobListing(c: Cursor): JobListing {
        val sStr = c.getString(c.getColumnIndexOrThrow("skills_required")) ?: ""
        return JobListing(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            company = c.getString(c.getColumnIndexOrThrow("company")),
            location = c.getString(c.getColumnIndexOrThrow("location")),
            salary = c.getString(c.getColumnIndexOrThrow("salary")) ?: "",
            workType = runCatching { WorkType.valueOf(c.getString(c.getColumnIndexOrThrow("work_type"))) }.getOrDefault(WorkType.REMOTE),
            employmentType = runCatching { EmploymentType.valueOf(c.getString(c.getColumnIndexOrThrow("employment_type"))) }.getOrDefault(EmploymentType.FULL_TIME),
            skillsRequired = if (sStr.isBlank()) emptyList() else sStr.split(","),
            description = c.getString(c.getColumnIndexOrThrow("description")) ?: "",
            sourceUrl = c.getString(c.getColumnIndexOrThrow("source_url")),
            sourceName = c.getString(c.getColumnIndexOrThrow("source_name")) ?: "Web",
            isSaved = c.getInt(c.getColumnIndexOrThrow("is_saved")) == 1,
            postedDate = c.getString(c.getColumnIndexOrThrow("posted_date")) ?: "Recent"
        )
    }

    private fun cursorToAuditLog(c: Cursor): AuditLogEntry {
        return AuditLogEntry(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
            userEmail = c.getString(c.getColumnIndexOrThrow("user_email")),
            action = c.getString(c.getColumnIndexOrThrow("action")),
            entityType = c.getString(c.getColumnIndexOrThrow("entity_type")),
            details = c.getString(c.getColumnIndexOrThrow("details")),
            timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"))
        )
    }

    private fun cursorToHistory(c: Cursor): ApplicationHistory {
        return ApplicationHistory(
            id = c.getString(c.getColumnIndexOrThrow("id")),
            applicationId = c.getString(c.getColumnIndexOrThrow("application_id")),
            fromStatus = runCatching { ApplicationStatus.valueOf(c.getString(c.getColumnIndexOrThrow("from_status"))) }.getOrDefault(ApplicationStatus.APPLIED),
            toStatus = runCatching { ApplicationStatus.valueOf(c.getString(c.getColumnIndexOrThrow("to_status"))) }.getOrDefault(ApplicationStatus.APPLIED),
            note = c.getString(c.getColumnIndexOrThrow("note")) ?: "",
            timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"))
        )
    }
}
