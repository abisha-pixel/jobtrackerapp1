package com.example.jobtrackerapp1.domain.model

import java.util.UUID

/**
 * 1. User Roles
 */
enum class UserRole {
    CANDIDATE,
    RECRUITER,
    HIRING_MANAGER,
    ADMIN;

    fun displayName(): String = when (this) {
        CANDIDATE -> "Candidate / Job Seeker"
        RECRUITER -> "Recruiter"
        HIRING_MANAGER -> "Hiring Manager"
        ADMIN -> "System Administrator"
    }
}

/**
 * User Profile
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val fullName: String,
    val photoUrl: String = "",
    val phone: String = "",
    val location: String = "",
    val skills: List<String> = emptyList(),
    val experience: String = "",
    val education: String = "",
    val portfolioUrl: String = "",
    val linkedInUrl: String = "",
    val gitHubUrl: String = "",
    val resumeId: String? = null,
    val role: UserRole = UserRole.CANDIDATE,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 3. Application Statuses (14 Stages)
 */
enum class ApplicationStatus(val stageIndex: Int, val group: String) {
    WISHLIST(0, "Discovery"),
    PREPARING(1, "Discovery"),
    APPLIED(2, "Active"),
    APPLICATION_VIEWED(3, "Active"),
    RECRUITER_CONTACTED(4, "Active"),
    SCREENING(5, "Interviewing"),
    INTERVIEW(6, "Interviewing"),
    TECHNICAL_INTERVIEW(7, "Interviewing"),
    HR_INTERVIEW(8, "Interviewing"),
    FINAL_ROUND(9, "Interviewing"),
    OFFER(10, "Decision"),
    ACCEPTED(11, "Decision"),
    REJECTED(12, "Archived"),
    WITHDRAWN(13, "Archived");

    fun readableName(): String = name.replace("_", " ").lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class WorkType {
    REMOTE, HYBRID, ON_SITE
}

enum class EmploymentType {
    FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE
}

/**
 * Job Application Entity
 */
data class JobApplication(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val companyName: String,
    val jobTitle: String,
    val jobDescription: String = "",
    val jobUrl: String = "",
    val location: String = "",
    val workType: WorkType = WorkType.REMOTE,
    val salaryMin: Int = 0,
    val salaryMax: Int = 0,
    val salaryCurrency: String = "USD",
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val applicationDate: Long = System.currentTimeMillis(),
    val applicationDeadline: Long? = null,
    val recruiterId: String? = null,
    val resumeVersionId: String? = null,
    val coverLetterId: String? = null,
    val source: String = "LinkedIn",
    val priority: Priority = Priority.MEDIUM,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    val status: ApplicationStatus = ApplicationStatus.APPLIED,
    val companyLogo: String = "",
    val nextAction: String = "Follow up with recruiter",
    val followUpDate: Long? = System.currentTimeMillis() + (7L * 24 * 3600 * 1000),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Stage Change History
 */
data class ApplicationHistory(
    val id: String = UUID.randomUUID().toString(),
    val applicationId: String,
    val fromStatus: ApplicationStatus,
    val toStatus: ApplicationStatus,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 5. Interview Management
 */
enum class InterviewType {
    PHONE_SCREENING,
    TECHNICAL,
    HR,
    BEHAVIORAL,
    SYSTEM_DESIGN,
    FINAL_ROUND,
    TAKE_HOME_REVIEW
}

enum class InterviewResult {
    PENDING,
    PASSED,
    NEEDS_REVIEW,
    REJECTED,
    RESCHEDULED
}

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false
)

data class Interview(
    val id: String = UUID.randomUUID().toString(),
    val applicationId: String,
    val userId: String,
    val companyName: String = "",
    val jobTitle: String = "",
    val interviewDateTime: Long,
    val type: InterviewType = InterviewType.TECHNICAL,
    val interviewerName: String = "",
    val interviewerTitle: String = "",
    val meetingLink: String = "",
    val location: String = "",
    val notes: String = "",
    val preparationChecklist: List<ChecklistItem> = emptyList(),
    val questions: List<String> = emptyList(),
    val feedback: String = "",
    val rating: Int = 0, // 1 to 5
    val result: InterviewResult = InterviewResult.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 12. Contact & Recruiter Management
 */
data class RecruiterContact(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val company: String,
    val jobTitle: String = "Technical Recruiter",
    val email: String = "",
    val phone: String = "",
    val linkedInUrl: String = "",
    val notes: String = "",
    val associatedApplicationIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 13. Communication Tracking
 */
enum class CommunicationChannel {
    EMAIL, PHONE_CALL, LINKEDIN_MESSAGE, RECRUITER_CHAT, INTERVIEW_NOTE, OFFER_LETTER
}

data class CommunicationLog(
    val id: String = UUID.randomUUID().toString(),
    val applicationId: String,
    val contactId: String? = null,
    val channel: CommunicationChannel = CommunicationChannel.EMAIL,
    val subject: String,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 7. Resume Management & Cover Letters
 */
data class ResumeVersion(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val versionName: String,
    val targetRole: String,
    val fileName: String,
    val skillsExtracted: List<String> = emptyList(),
    val resumeSummary: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)

data class CoverLetter(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val targetCompany: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 6. Follow-Up & Reminder System
 */
enum class ReminderType {
    FOLLOW_UP_AFTER_APPLICATION,
    RECRUITER_RESPONSE,
    INTERVIEW_PREP,
    INTERVIEW_DATE,
    APPLICATION_DEADLINE,
    OFFER_EXPIRATION,
    CUSTOM
}

data class TaskReminder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val applicationId: String? = null,
    val title: String,
    val description: String = "",
    val dueDate: Long,
    val reminderType: ReminderType = ReminderType.CUSTOM,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 14. Notifications
 */
enum class NotificationType {
    INTERVIEW_ALERT,
    FOLLOW_UP_ALERT,
    DEADLINE_WARNING,
    OFFER_RECEIVED,
    STATUS_CHANGED,
    SYSTEM_INFO
}

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.SYSTEM_INFO,
    val isRead: Boolean = false,
    val actionRoute: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 8. Job Search & Import
 */
data class JobListing(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val workType: WorkType,
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val skillsRequired: List<String>,
    val description: String,
    val sourceUrl: String,
    val sourceName: String = "RemoteOK",
    val isSaved: Boolean = false,
    val postedDate: String = "2 days ago"
)

/**
 * 15. Admin Audit Log
 */
data class AuditLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userEmail: String,
    val action: String,
    val entityType: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 2. Dashboard Statistics & Analytics
 */
data class DashboardStats(
    val totalApplied: Int = 0,
    val inProgress: Int = 0,
    val interviewsScheduled: Int = 0,
    val offersReceived: Int = 0,
    val rejectedCount: Int = 0,
    val savedCount: Int = 0,
    val followUpsDue: Int = 0,
    val successRate: Double = 0.0,
    val averageResponseDays: Double = 0.0,
    val weeklyTrends: Map<String, Int> = emptyMap(),
    val statusBreakdown: Map<ApplicationStatus, Int> = emptyMap()
)

/**
 * 11. AI Career Analysis Result
 */
data class AiAnalysisResult(
    val matchScorePercent: Int,
    val atsSummary: String,
    val matchedKeywords: List<String>,
    val missingKeywords: List<String>,
    val resumeImprovementTips: List<String>,
    val interviewQuestions: List<String>,
    val generatedCoverLetter: String,
    val followUpEmailDraft: String
)
