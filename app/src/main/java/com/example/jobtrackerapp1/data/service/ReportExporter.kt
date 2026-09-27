package com.example.jobtrackerapp1.data.service

import com.example.jobtrackerapp1.domain.model.DashboardStats
import com.example.jobtrackerapp1.domain.model.JobApplication
import java.text.SimpleDateFormat
import java.util.*

/**
 * 10. Analytics & Reports Exporter
 * Generates structured CSV data and formatted executive summaries
 * suitable for PDF export and clipboard sharing.
 */
object ReportExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun generateApplicationsCsv(applications: List<JobApplication>): String {
        val sb = StringBuilder()
        sb.append("Application ID,Company,Job Title,Status,Priority,Work Type,Salary Min,Salary Max,Source,Applied Date,Follow-up Date,Location,Notes\n")

        applications.forEach { app ->
            val appliedDateStr = dateFormat.format(Date(app.applicationDate))
            val followUpStr = app.followUpDate?.let { dateFormat.format(Date(it)) } ?: "N/A"
            val sanitizedNotes = app.notes.replace(",", ";").replace("\n", " ")
            val sanitizedTitle = app.jobTitle.replace(",", " ")
            val sanitizedCompany = app.companyName.replace(",", " ")
            val sanitizedLocation = app.location.replace(",", " ")

            sb.append("${app.id},\"$sanitizedCompany\",\"$sanitizedTitle\",${app.status.name},${app.priority.name},${app.workType.name},${app.salaryMin},${app.salaryMax},\"${app.source}\",$appliedDateStr,$followUpStr,\"$sanitizedLocation\",\"$sanitizedNotes\"\n")
        }

        return sb.toString()
    }

    fun generateAnalyticsSummaryReport(
        stats: DashboardStats,
        applications: List<JobApplication>,
        userEmail: String
    ): String {
        val nowStr = SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
        val sb = StringBuilder()

        sb.append("=====================================================\n")
        sb.append("          APEX JOB TRACKER - EXECUTIVE REPORT         \n")
        sb.append("=====================================================\n")
        sb.append("Generated For : $userEmail\n")
        sb.append("Report Date   : $nowStr\n\n")

        sb.append("--- KEY METRICS ---\n")
        sb.append("• Total Applications Tracked : ${stats.totalApplied}\n")
        sb.append("• Active In-Progress Pipeline : ${stats.inProgress}\n")
        sb.append("• Scheduled Interviews       : ${stats.interviewsScheduled}\n")
        sb.append("• Offers Received            : ${stats.offersReceived}\n")
        sb.append("• Rejection Count            : ${stats.rejectedCount}\n")
        sb.append("• Application Success Rate   : ${"%.1f".format(stats.successRate)}%\n")
        sb.append("• Average Recruiter Response : ${"%.1f".format(stats.averageResponseDays)} days\n\n")

        sb.append("--- STATUS BREAKDOWN ---\n")
        stats.statusBreakdown.forEach { (status, count) ->
            sb.append("• ${status.readableName().padEnd(24)}: $count\n")
        }

        sb.append("\n--- RECENT APPLICATIONS ---\n")
        applications.take(5).forEach { app ->
            val dateStr = dateFormat.format(Date(app.applicationDate))
            sb.append("• ${app.companyName} | ${app.jobTitle} [${app.status.readableName()}] - Applied: $dateStr\n")
        }

        sb.append("\n=====================================================\n")
        sb.append("End of Report. Apex Job Tracking System.\n")

        return sb.toString()
    }
}
