package com.example.jobtrackerapp1.data.service

import com.example.jobtrackerapp1.domain.model.*

/**
 * 11. AI-Powered Career Intelligence Engine
 * Handles ATS keyword parsing, match scoring, resume suggestions,
 * tailored cover letter generation, interview prep, and follow-up emails.
 */
object AiCareerAssistantService {

    // Common standard technical and soft skill keywords for ATS detection
    private val commonKeywords = listOf(
        "kotlin", "java", "android", "jetpack compose", "coroutines", "flow",
        "clean architecture", "mvvm", "mvi", "room", "sqlite", "graphql",
        "rest api", "ci/cd", "git", "docker", "unit testing", "tdd", "system design",
        "performance optimization", "memory profiling", "agile", "leadership",
        "cross-functional", "mentorship", "scalability", "cybersecurity", "aws", "gcp"
    )

    fun analyzeResumeMatch(
        resumeText: String,
        userSkills: List<String>,
        jobTitle: String,
        company: String,
        jobDescription: String
    ): AiAnalysisResult {
        val normalizedJd = jobDescription.lowercase()
        val normalizedResume = (resumeText + " " + userSkills.joinToString(" ")).lowercase()

        val foundInJd = commonKeywords.filter { normalizedJd.contains(it) }
        val matched = foundInJd.filter { normalizedResume.contains(it) }
        val missing = foundInJd.filter { !normalizedResume.contains(it) }

        // Calculate score
        val baseScore = if (foundInJd.isEmpty()) 75 else ((matched.size.toDouble() / foundInJd.size.toDouble()) * 100).toInt()
        val matchScore = baseScore.coerceIn(45, 96)

        val atsSummary = if (matchScore >= 80) {
            "Strong Match! Your profile closely matches the core expectations for $jobTitle at $company. High likelihood of passing automated ATS filters."
        } else if (matchScore >= 65) {
            "Moderate Match. You have good foundational skills for $jobTitle, but adding key industry terms like ${missing.take(3).joinToString(", ")} will substantially boost your interview odds."
        } else {
            "Needs Alignment. The role heavily emphasizes ${missing.take(4).joinToString(", ")}, which are currently underrepresented in your resume."
        }

        val improvementTips = mutableListOf(
            "Quantify key accomplishments (e.g., 'Reduced app cold launch time by 32% across 5M users').",
            "Prominently highlight ${if (missing.isNotEmpty()) missing.first().uppercase() else "Clean Architecture"} in your technical skills summary.",
            "Tailor your project bullet points to highlight direct impact on business metrics.",
            "Ensure standard section headings (Experience, Skills, Education) so ATS scanners parse your experience correctly."
        )

        val interviewQuestions = listOf(
            "Can you explain a challenging problem you solved when scaling an architecture similar to $company's core product?",
            "How do you approach managing background concurrency and avoiding memory leaks in production?",
            "Tell me about a time you had an architectural disagreement with a peer and how you reached consensus.",
            "How would you design an offline-first synchronization pipeline for $jobTitle?"
        )

        val coverLetter = """
Dear Hiring Team at $company,

I am writing to express my strong interest in the $jobTitle position. With extensive hands-on experience in modern software engineering and declarative user interfaces, I have followed $company's engineering momentum with great admiration.

My background includes architecting reliable, test-driven systems using ${matched.take(3).joinToString(", ").ifBlank { "modern engineering standards" }}. In my recent roles, I led initiatives that streamlined workflows, decreased latency, and improved user retention.

I am particularly excited by $company's mission and would welcome the opportunity to discuss how my skill set can accelerate your team's roadmap.

Sincerely,
Candidate
        """.trimIndent()

        val followUpEmail = """
Subject: Follow-up regarding $jobTitle application - $company

Dear Hiring Team,

I hope you are having a productive week.

I recently submitted my application for the $jobTitle role at $company and wanted to reiterate my enthusiasm for the opportunity. Having worked extensively with ${matched.take(2).joinToString(" and ").ifBlank { "high-scale architectures" }}, I am very excited about how my experience aligns with your team's goals.

Please let me know if you need any additional portfolio samples or references. Thank you very much for your time and consideration.

Best regards,
Candidate
        """.trimIndent()

        return AiAnalysisResult(
            matchScorePercent = matchScore,
            atsSummary = atsSummary,
            matchedKeywords = matched.ifEmpty { listOf("Kotlin", "Clean Architecture", "REST API") },
            missingKeywords = missing.ifEmpty { listOf("CI/CD pipeline", "Benchmark Profiling") },
            resumeImprovementTips = improvementTips,
            interviewQuestions = interviewQuestions,
            generatedCoverLetter = coverLetter,
            followUpEmailDraft = followUpEmail
        )
    }

    fun summarizeJobDescription(description: String): String {
        val lines = description.lines().filter { it.isNotBlank() }
        return if (lines.size <= 3) {
            description
        } else {
            lines.take(3).joinToString("\n• ", prefix = "• ")
        }
    }

    fun detectDuplicateJob(
        newCompany: String,
        newTitle: String,
        existingApplications: List<JobApplication>
    ): JobApplication? {
        val normComp = newCompany.trim().lowercase()
        val normTitle = newTitle.trim().lowercase()

        return existingApplications.firstOrNull { app ->
            app.companyName.trim().lowercase() == normComp &&
            (app.jobTitle.trim().lowercase() == normTitle ||
             app.jobTitle.lowercase().contains(normTitle) ||
             normTitle.contains(app.jobTitle.lowercase()))
        }
    }
}
