package com.example.jobtrackerapp1.data.local

import com.example.jobtrackerapp1.domain.model.*

object SeedData {
    val defaultCandidate = User(
        id = "user_cand_1",
        email = "alex.rivera@example.com",
        fullName = "Alex Rivera",
        photoUrl = "",
        phone = "+1 (555) 234-5678",
        location = "San Francisco, CA (Remote)",
        skills = listOf("Kotlin", "Jetpack Compose", "Android SDK", "Coroutines", "Clean Architecture", "GraphQL", "CI/CD"),
        experience = "5+ years developing high-scale mobile applications. Led mobile engineering at TechFlow.",
        education = "B.S. in Computer Science - University of California, Berkeley",
        portfolioUrl = "https://alexrivera.dev",
        linkedInUrl = "https://linkedin.com/in/alexrivera-tech",
        gitHubUrl = "https://github.com/alexrivera-mobile",
        role = UserRole.CANDIDATE
    )

    val defaultRecruiter = User(
        id = "user_rec_1",
        email = "sarah.jenkins@talentpartners.com",
        fullName = "Sarah Jenkins",
        photoUrl = "",
        phone = "+1 (555) 876-5432",
        location = "New York, NY",
        skills = listOf("Technical Sourcing", "Talent Acquisition", "Compensation Benchmarking"),
        role = UserRole.RECRUITER
    )

    val defaultHiringManager = User(
        id = "user_hm_1",
        email = "david.chen@cloudsystems.io",
        fullName = "David Chen",
        photoUrl = "",
        phone = "+1 (555) 345-6789",
        location = "Seattle, WA",
        skills = listOf("System Architecture", "Engineering Leadership", "Sprint Planning"),
        role = UserRole.HIRING_MANAGER
    )

    val defaultAdmin = User(
        id = "user_admin_1",
        email = "admin@jobtrackerapx.com",
        fullName = "Apex System Administrator",
        photoUrl = "",
        phone = "+1 (555) 999-0000",
        location = "HQ / Cloud Operations",
        skills = listOf("System Administration", "Security Auditing", "Database Management"),
        role = UserRole.ADMIN
    )

    val sampleResumes = listOf(
        ResumeVersion(
            id = "res_1",
            userId = "user_cand_1",
            versionName = "Senior Mobile Engineer - 2026",
            targetRole = "Lead / Senior Android Developer",
            fileName = "Alex_Rivera_Senior_Android_2026.pdf",
            skillsExtracted = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room", "Hilt", "Ktor", "TDD"),
            resumeSummary = "Senior Android Engineer with 5+ years building scalable, user-centric mobile applications.",
            isDefault = true
        ),
        ResumeVersion(
            id = "res_2",
            userId = "user_cand_1",
            versionName = "Full Stack Mobile Specialist",
            targetRole = "Mobile Systems Architect",
            fileName = "Alex_Rivera_FullStack_Mobile.pdf",
            skillsExtracted = listOf("Android", "Kotlin", "Node.js", "GraphQL", "PostgreSQL", "AWS"),
            resumeSummary = "Full-stack mobile engineer proficient in client-side Compose architecture and backend microservices.",
            isDefault = false
        )
    )

    val sampleCoverLetters = listOf(
        CoverLetter(
            id = "cl_1",
            userId = "user_cand_1",
            title = "Stripe Lead Android Application",
            targetCompany = "Stripe",
            content = "Dear Stripe Hiring Team,\n\nI am thrilled to apply for the Senior Android Engineer role. Having scaled payment mobile experiences with flawless reliability and sub-second latencies, Stripe's mission to increase the GDP of the internet resonates strongly with my engineering philosophy.\n\nBest regards,\nAlex Rivera"
        ),
        CoverLetter(
            id = "cl_2",
            userId = "user_cand_1",
            title = "General Tech Cover Letter",
            targetCompany = "General Enterprise",
            content = "Dear Hiring Manager,\n\nI am writing to express my strong enthusiasm for joining your engineering organization. My background in modern declarative UI with Jetpack Compose and reactive architecture aligns directly with your team's goals.\n\nSincerely,\nAlex Rivera"
        )
    )

    val sampleContacts = listOf(
        RecruiterContact(
            id = "cont_1",
            userId = "user_cand_1",
            name = "Marcus Vance",
            company = "Stripe",
            jobTitle = "Staff Engineering Recruiter",
            email = "mvance@stripe.com",
            phone = "+1 (415) 555-1209",
            linkedInUrl = "https://linkedin.com/in/marcus-vance-talent",
            notes = "Reachable via email or LinkedIn. Very responsive on salary bands."
        ),
        RecruiterContact(
            id = "cont_2",
            userId = "user_cand_1",
            name = "Elena Rostova",
            company = "Google",
            jobTitle = "Senior Technical Recruiter",
            email = "erostova@google.com",
            phone = "+1 (650) 555-4421",
            linkedInUrl = "https://linkedin.com/in/elena-rostova-recruiter",
            notes = "Coordinates Google Core Mobile platform recruiting."
        )
    )

    val sampleApplications = listOf(
        JobApplication(
            id = "app_1",
            userId = "user_cand_1",
            companyName = "Stripe",
            jobTitle = "Staff Mobile Platform Engineer",
            jobDescription = "Lead the architecture of our core payments SDK on Android. Build ultra-secure, offline-first mobile payment pipelines.",
            jobUrl = "https://stripe.com/jobs/staff-mobile",
            location = "San Francisco, CA / Remote",
            workType = WorkType.REMOTE,
            salaryMin = 195000,
            salaryMax = 235000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (12L * 24 * 3600 * 1000),
            recruiterId = "cont_1",
            resumeVersionId = "res_1",
            coverLetterId = "cl_1",
            source = "LinkedIn",
            priority = Priority.URGENT,
            notes = "Passed initial screening with Marcus. Team is using Compose multiplatform.",
            tags = listOf("Fintech", "Mobile SDK", "Tier 1"),
            status = ApplicationStatus.TECHNICAL_INTERVIEW,
            nextAction = "Prepare for Live Coding on concurrency & offline sync",
            followUpDate = System.currentTimeMillis() + (2L * 24 * 3600 * 1000)
        ),
        JobApplication(
            id = "app_2",
            userId = "user_cand_1",
            companyName = "Google",
            jobTitle = "Senior Android Software Engineer",
            jobDescription = "Design and build next-generation experiences for Android System UI and Pixel ecosystem.",
            jobUrl = "https://careers.google.com/jobs/results/senior-android",
            location = "Mountain View, CA / Hybrid",
            workType = WorkType.HYBRID,
            salaryMin = 210000,
            salaryMax = 260000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (20L * 24 * 3600 * 1000),
            recruiterId = "cont_2",
            resumeVersionId = "res_1",
            source = "Referral",
            priority = Priority.HIGH,
            notes = "Recruiter phone call went great. Virtual Onsite scheduled next week.",
            tags = listOf("Big Tech", "Operating System", "Pixel"),
            status = ApplicationStatus.FINAL_ROUND,
            nextAction = "Review System Design & IPC architectures",
            followUpDate = System.currentTimeMillis() + (3L * 24 * 3600 * 1000)
        ),
        JobApplication(
            id = "app_3",
            userId = "user_cand_1",
            companyName = "Airbnb",
            jobTitle = "Lead Mobile Architect",
            jobDescription = "Drive the modernization of Airbnb's mobile app architecture, improving performance and modularity.",
            jobUrl = "https://careers.airbnb.com/positions/lead-mobile",
            location = "San Francisco, CA / Remote",
            workType = WorkType.REMOTE,
            salaryMin = 220000,
            salaryMax = 275000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (5L * 24 * 3600 * 1000),
            source = "Company Site",
            priority = Priority.HIGH,
            notes = "Offer received! Negotiating equity component and sign-on bonus.",
            tags = listOf("Travel", "Design Systems", "High Offer"),
            status = ApplicationStatus.OFFER,
            nextAction = "Review written offer letter & counter by Friday",
            followUpDate = System.currentTimeMillis() + (4L * 24 * 3600 * 1000)
        ),
        JobApplication(
            id = "app_4",
            userId = "user_cand_1",
            companyName = "Netflix",
            jobTitle = "Senior Software Engineer - Mobile Core",
            jobDescription = "Build seamless streaming client architectures for hundreds of millions of Android users worldwide.",
            jobUrl = "https://jobs.netflix.com/jobs/senior-mobile-core",
            location = "Los Gatos, CA / Remote",
            workType = WorkType.REMOTE,
            salaryMin = 250000,
            salaryMax = 320000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (8L * 24 * 3600 * 1000),
            source = "LinkedIn",
            priority = Priority.MEDIUM,
            notes = "Screening interview scheduled with Engineering Manager.",
            tags = listOf("Streaming", "High Compensation", "Media"),
            status = ApplicationStatus.SCREENING,
            nextAction = "Review Netflix Culture Memo and recent projects",
            followUpDate = System.currentTimeMillis() + (5L * 24 * 3600 * 1000)
        ),
        JobApplication(
            id = "app_5",
            userId = "user_cand_1",
            companyName = "Linear",
            jobTitle = "Product Engineer - Mobile",
            jobDescription = "Craft fast, keyboard-first, and delightful mobile companion experiences for developer teams.",
            jobUrl = "https://linear.app/careers/product-mobile",
            location = "Remote",
            workType = WorkType.REMOTE,
            salaryMin = 180000,
            salaryMax = 220000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (3L * 24 * 3600 * 1000),
            source = "Twitter / X",
            priority = Priority.HIGH,
            notes = "Application submitted with custom demo video.",
            tags = listOf("Productivity", "Craft", "Startup"),
            status = ApplicationStatus.APPLIED,
            nextAction = "Follow up if no update in 7 days",
            followUpDate = System.currentTimeMillis() + (4L * 24 * 3600 * 1000)
        ),
        JobApplication(
            id = "app_6",
            userId = "user_cand_1",
            companyName = "Figma",
            jobTitle = "Staff Android Engineer - Canvas",
            jobDescription = "Build hardware-accelerated rendering and collaborative vector editing tools for Android tablets.",
            jobUrl = "https://figma.com/careers/staff-android-canvas",
            location = "San Francisco, CA / Hybrid",
            workType = WorkType.HYBRID,
            salaryMin = 215000,
            salaryMax = 255000,
            salaryCurrency = "USD",
            employmentType = EmploymentType.FULL_TIME,
            applicationDate = System.currentTimeMillis() - (15L * 24 * 3600 * 1000),
            source = "Referral",
            priority = Priority.MEDIUM,
            notes = "Wishlist item. Need to tailor resume with graphics and C++ NDK experience.",
            tags = listOf("Graphics", "Creative Tools", "Canvas"),
            status = ApplicationStatus.WISHLIST,
            nextAction = "Update portfolio with custom Canvas shaders demo",
            followUpDate = System.currentTimeMillis() + (6L * 24 * 3600 * 1000)
        )
    )

    val sampleInterviews = listOf(
        Interview(
            id = "int_1",
            applicationId = "app_1",
            userId = "user_cand_1",
            companyName = "Stripe",
            jobTitle = "Staff Mobile Platform Engineer",
            interviewDateTime = System.currentTimeMillis() + (2L * 24 * 3600 * 1000) + (3600 * 1000 * 2),
            type = InterviewType.TECHNICAL,
            interviewerName = "Jordan Hayes",
            interviewerTitle = "Principal Engineer",
            meetingLink = "https://meet.google.com/str-tech-882",
            location = "Google Meet",
            notes = "Focus on concurrent transactions and idempotency keys.",
            preparationChecklist = listOf(
                ChecklistItem(text = "Review Kotlin Flow state management and backpressure", isCompleted = true),
                ChecklistItem(text = "Practice designing an offline queue with SQLite rollback", isCompleted = true),
                ChecklistItem(text = "Prepare 3 questions about Stripe's Android architecture", isCompleted = false)
            ),
            questions = listOf(
                "How would you guarantee zero lost payments when the network drops mid-request?",
                "Walk us through optimizing recomposition overhead in deeply nested lists."
            ),
            rating = 4,
            result = InterviewResult.PENDING
        ),
        Interview(
            id = "int_2",
            applicationId = "app_2",
            userId = "user_cand_1",
            companyName = "Google",
            jobTitle = "Senior Android Software Engineer",
            interviewDateTime = System.currentTimeMillis() + (4L * 24 * 3600 * 1000),
            type = InterviewType.FINAL_ROUND,
            interviewerName = "Priya Raman",
            interviewerTitle = "Director of Engineering",
            meetingLink = "https://meet.google.com/goo-onsite-019",
            location = "Google Meet",
            notes = "Virtual Onsite - Architecture & Leadership Round.",
            preparationChecklist = listOf(
                ChecklistItem(text = "Review Google leadership principles and STAR stories", isCompleted = true),
                ChecklistItem(text = "Review Binder IPC limits and AIDL interfaces", isCompleted = false)
            ),
            rating = 5,
            result = InterviewResult.PENDING
        )
    )

    val sampleReminders = listOf(
        TaskReminder(
            id = "rem_1",
            userId = "user_cand_1",
            applicationId = "app_1",
            title = "Prepare Stripe Technical Coding Environment",
            description = "Test IDE, verify Kotlin compiler version, test microphone and screen share.",
            dueDate = System.currentTimeMillis() + (24 * 3600 * 1000),
            reminderType = ReminderType.INTERVIEW_PREP,
            isCompleted = false,
            priority = Priority.URGENT
        ),
        TaskReminder(
            id = "rem_2",
            userId = "user_cand_1",
            applicationId = "app_3",
            title = "Respond to Airbnb Offer Letter",
            description = "Negotiate equity refreshers and confirm proposed start date.",
            dueDate = System.currentTimeMillis() + (3L * 24 * 3600 * 1000),
            reminderType = ReminderType.OFFER_EXPIRATION,
            isCompleted = false,
            priority = Priority.HIGH
        ),
        TaskReminder(
            id = "rem_3",
            userId = "user_cand_1",
            applicationId = "app_5",
            title = "Follow-up email with Linear recruiter",
            description = "Sent application 3 days ago. If no feedback by Monday, ping hiring lead on X.",
            dueDate = System.currentTimeMillis() + (4L * 24 * 3600 * 1000),
            reminderType = ReminderType.FOLLOW_UP_AFTER_APPLICATION,
            isCompleted = false,
            priority = Priority.MEDIUM
        )
    )

    val sampleNotifications = listOf(
        AppNotification(
            id = "notif_1",
            userId = "user_cand_1",
            title = "Interview in 48 Hours",
            message = "Stripe Technical Interview with Jordan Hayes is scheduled for tomorrow at 2:00 PM.",
            type = NotificationType.INTERVIEW_ALERT,
            isRead = false,
            actionRoute = "interviews"
        ),
        AppNotification(
            id = "notif_2",
            userId = "user_cand_1",
            title = "Offer Expiration Reminder",
            message = "Airbnb's offer letter response window expires in 3 days.",
            type = NotificationType.OFFER_RECEIVED,
            isRead = false,
            actionRoute = "applications"
        ),
        AppNotification(
            id = "notif_3",
            userId = "user_cand_1",
            title = "Stage Updated",
            message = "Google application was moved to Final Round by Recruiter Elena Rostova.",
            type = NotificationType.STATUS_CHANGED,
            isRead = true,
            actionRoute = "kanban"
        )
    )

    val sampleJobListings = listOf(
        JobListing(
            id = "job_search_1",
            title = "Lead Mobile Engineer (Android)",
            company = "Datadog",
            location = "New York / Remote",
            salary = "$185,000 - $225,000",
            workType = WorkType.REMOTE,
            employmentType = EmploymentType.FULL_TIME,
            skillsRequired = listOf("Kotlin", "Jetpack Compose", "Observability", "APM"),
            description = "Join Datadog's Mobile RUM team to build state-of-the-art telemetry SDKs for millions of Android devices worldwide.",
            sourceUrl = "https://datadog.com/careers/lead-mobile",
            isSaved = false
        ),
        JobListing(
            id = "job_search_2",
            title = "Staff Android Core Developer",
            company = "Slack",
            location = "San Francisco / Remote",
            salary = "$200,000 - $240,000",
            workType = WorkType.REMOTE,
            employmentType = EmploymentType.FULL_TIME,
            skillsRequired = listOf("Kotlin", "Coroutines", "SQLite", "Performance", "WebSocket"),
            description = "Accelerate message rendering and offline synchronization on Slack Android client.",
            sourceUrl = "https://slack.com/careers/staff-android",
            isSaved = true
        ),
        JobListing(
            id = "job_search_3",
            title = "Senior Android Specialist",
            company = "Spotify",
            location = "Boston, MA / Hybrid",
            salary = "$190,000 - $230,000",
            workType = WorkType.HYBRID,
            employmentType = EmploymentType.FULL_TIME,
            skillsRequired = listOf("Jetpack Compose", "Audio Playback", "ExoPlayer", "Clean Architecture"),
            description = "Craft audio listening and playlist recommendation workflows loved by hundreds of millions.",
            sourceUrl = "https://spotify.com/jobs/senior-android-specialist",
            isSaved = false
        )
    )

    val sampleAuditLogs = listOf(
        AuditLogEntry(
            id = "audit_1",
            userId = "user_cand_1",
            userEmail = "alex.rivera@example.com",
            action = "STAGE_UPDATE",
            entityType = "APPLICATION",
            details = "Moved Google application from Technical Interview to Final Round"
        ),
        AuditLogEntry(
            id = "audit_2",
            userId = "user_admin_1",
            userEmail = "admin@jobtrackerapx.com",
            action = "ROLE_CONFIG",
            entityType = "USER",
            details = "Verified security policy and token rotation rules"
        )
    )
}
