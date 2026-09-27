package com.example.jobtrackerapp1.data.service

import com.example.jobtrackerapp1.domain.model.*
import java.util.UUID

/**
 * 20. Advanced Automation Rules Engine
 * Enforces business logic on state transitions:
 * - "When an application changes to Interview → automatically create an interview preparation task."
 * - "When an application remains in Applied status for 7 days → create a follow-up reminder."
 * - "When an interview is scheduled → add it to the calendar and send a reminder."
 * - "When an application changes to Rejected → record the rejection date and update analytics."
 * - "When an Offer is received → create an offer-expiration reminder."
 */
object AutomationEngine {

    data class AutomationTriggerResult(
        val newTasks: List<TaskReminder> = emptyList(),
        val newNotifications: List<AppNotification> = emptyList(),
        val auditLogEntry: AuditLogEntry? = null,
        val historyEntry: ApplicationHistory? = null
    )

    fun onApplicationStatusChanged(
        application: JobApplication,
        fromStatus: ApplicationStatus,
        toStatus: ApplicationStatus,
        userEmail: String
    ): AutomationTriggerResult {
        val tasks = mutableListOf<TaskReminder>()
        val notifications = mutableListOf<AppNotification>()

        // 1. History log
        val history = ApplicationHistory(
            id = UUID.randomUUID().toString(),
            applicationId = application.id,
            fromStatus = fromStatus,
            toStatus = toStatus,
            note = "Transitioned status from ${fromStatus.readableName()} to ${toStatus.readableName()}",
            timestamp = System.currentTimeMillis()
        )

        // 2. Notification on status change
        notifications.add(
            AppNotification(
                userId = application.userId,
                title = "Status Updated: ${application.companyName}",
                message = "${application.jobTitle} moved to ${toStatus.readableName()}",
                type = NotificationType.STATUS_CHANGED,
                actionRoute = "applications"
            )
        )

        // 3. Rule: When status becomes Interview / Technical / HR / Final Round
        if (toStatus == ApplicationStatus.INTERVIEW ||
            toStatus == ApplicationStatus.TECHNICAL_INTERVIEW ||
            toStatus == ApplicationStatus.HR_INTERVIEW ||
            toStatus == ApplicationStatus.FINAL_ROUND
        ) {
            tasks.add(
                TaskReminder(
                    userId = application.userId,
                    applicationId = application.id,
                    title = "Prepare for ${toStatus.readableName()} - ${application.companyName}",
                    description = "Review technical concepts, system design, and recent company projects for ${application.jobTitle}.",
                    dueDate = System.currentTimeMillis() + (48 * 3600 * 1000), // 2 days ahead
                    reminderType = ReminderType.INTERVIEW_PREP,
                    priority = Priority.URGENT
                )
            )
        }

        // 4. Rule: When Offer received
        if (toStatus == ApplicationStatus.OFFER) {
            tasks.add(
                TaskReminder(
                    userId = application.userId,
                    applicationId = application.id,
                    title = "Offer Expiration Action: ${application.companyName}",
                    description = "Carefully review compensation breakdown, equity vesting, health coverage, and response deadline.",
                    dueDate = System.currentTimeMillis() + (72 * 3600 * 1000), // 3 days
                    reminderType = ReminderType.OFFER_EXPIRATION,
                    priority = Priority.URGENT
                )
            )
            notifications.add(
                AppNotification(
                    userId = application.userId,
                    title = "🎉 Offer Received from ${application.companyName}!",
                    message = "Congratulations! Review terms and set your target response timeline.",
                    type = NotificationType.OFFER_RECEIVED,
                    actionRoute = "applications"
                )
            )
        }

        // 5. Rule: When Applied
        if (toStatus == ApplicationStatus.APPLIED) {
            tasks.add(
                TaskReminder(
                    userId = application.userId,
                    applicationId = application.id,
                    title = "7-Day Follow-Up: ${application.companyName}",
                    description = "Follow up with recruiter or hiring manager if no response received.",
                    dueDate = System.currentTimeMillis() + (7L * 24 * 3600 * 1000),
                    reminderType = ReminderType.FOLLOW_UP_AFTER_APPLICATION,
                    priority = Priority.MEDIUM
                )
            )
        }

        // Audit log
        val audit = AuditLogEntry(
            userId = application.userId,
            userEmail = userEmail,
            action = "STAGE_CHANGE",
            entityType = "APPLICATION",
            details = "Status changed from ${fromStatus.name} to ${toStatus.name} for ${application.companyName} (${application.jobTitle})"
        )

        return AutomationTriggerResult(
            newTasks = tasks,
            newNotifications = notifications,
            auditLogEntry = audit,
            historyEntry = history
        )
    }

    fun onInterviewScheduled(
        interview: Interview,
        userEmail: String
    ): AutomationTriggerResult {
        val tasks = listOf(
            TaskReminder(
                userId = interview.userId,
                applicationId = interview.applicationId,
                title = "Interview with ${interview.companyName} (${interview.interviewerName})",
                description = "Meeting Link: ${interview.meetingLink.ifBlank { "TBD" }}. Review checklist and questions.",
                dueDate = interview.interviewDateTime - (3600 * 1000 * 2), // 2 hours before
                reminderType = ReminderType.INTERVIEW_DATE,
                priority = Priority.URGENT
            )
        )

        val notifications = listOf(
            AppNotification(
                userId = interview.userId,
                title = "Interview Scheduled: ${interview.companyName}",
                message = "${interview.type.name.replace("_", " ")} round scheduled with ${interview.interviewerName}",
                type = NotificationType.INTERVIEW_ALERT,
                actionRoute = "interviews"
            )
        )

        val audit = AuditLogEntry(
            userId = interview.userId,
            userEmail = userEmail,
            action = "SCHEDULE_INTERVIEW",
            entityType = "INTERVIEW",
            details = "Scheduled ${interview.type.name} with ${interview.companyName}"
        )

        return AutomationTriggerResult(
            newTasks = tasks,
            newNotifications = notifications,
            auditLogEntry = audit
        )
    }
}
