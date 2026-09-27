package com.example.jobtrackerapp1.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "job_tracker_apex.db"
        const val DATABASE_VERSION = 1

        // Tables
        const val TABLE_USERS = "users"
        const val TABLE_APPLICATIONS = "applications"
        const val TABLE_APPLICATION_HISTORY = "application_history"
        const val TABLE_INTERVIEWS = "interviews"
        const val TABLE_CONTACTS = "recruiter_contacts"
        const val TABLE_COMMUNICATIONS = "communications"
        const val TABLE_RESUMES = "resumes"
        const val TABLE_COVER_LETTERS = "cover_letters"
        const val TABLE_TASKS_REMINDERS = "tasks_reminders"
        const val TABLE_NOTIFICATIONS = "notifications"
        const val TABLE_JOB_LISTINGS = "job_listings"
        const val TABLE_AUDIT_LOGS = "audit_logs"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Users Table
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                id TEXT PRIMARY KEY,
                email TEXT UNIQUE NOT NULL,
                full_name TEXT NOT NULL,
                photo_url TEXT,
                phone TEXT,
                location TEXT,
                skills TEXT,
                experience TEXT,
                education TEXT,
                portfolio_url TEXT,
                linkedin_url TEXT,
                github_url TEXT,
                resume_id TEXT,
                role TEXT NOT NULL DEFAULT 'CANDIDATE',
                created_at INTEGER NOT NULL
            )
        """.trimIndent())

        // 2. Applications Table
        db.execSQL("""
            CREATE TABLE $TABLE_APPLICATIONS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                company_name TEXT NOT NULL,
                job_title TEXT NOT NULL,
                job_description TEXT,
                job_url TEXT,
                location TEXT,
                work_type TEXT NOT NULL DEFAULT 'REMOTE',
                salary_min INTEGER DEFAULT 0,
                salary_max INTEGER DEFAULT 0,
                salary_currency TEXT DEFAULT 'USD',
                employment_type TEXT DEFAULT 'FULL_TIME',
                application_date INTEGER NOT NULL,
                application_deadline INTEGER,
                recruiter_id TEXT,
                resume_version_id TEXT,
                cover_letter_id TEXT,
                source TEXT DEFAULT 'LinkedIn',
                priority TEXT DEFAULT 'MEDIUM',
                notes TEXT,
                tags TEXT,
                status TEXT NOT NULL DEFAULT 'APPLIED',
                company_logo TEXT,
                next_action TEXT,
                follow_up_date INTEGER,
                updated_at INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 3. Application History Table (for stage change tracking)
        db.execSQL("""
            CREATE TABLE $TABLE_APPLICATION_HISTORY (
                id TEXT PRIMARY KEY,
                application_id TEXT NOT NULL,
                from_status TEXT NOT NULL,
                to_status TEXT NOT NULL,
                note TEXT,
                timestamp INTEGER NOT NULL,
                FOREIGN KEY (application_id) REFERENCES $TABLE_APPLICATIONS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 4. Interviews Table
        db.execSQL("""
            CREATE TABLE $TABLE_INTERVIEWS (
                id TEXT PRIMARY KEY,
                application_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                company_name TEXT,
                job_title TEXT,
                interview_date_time INTEGER NOT NULL,
                type TEXT NOT NULL,
                interviewer_name TEXT,
                interviewer_title TEXT,
                meeting_link TEXT,
                location TEXT,
                notes TEXT,
                checklist_json TEXT,
                questions_json TEXT,
                feedback TEXT,
                rating INTEGER DEFAULT 0,
                result TEXT DEFAULT 'PENDING',
                created_at INTEGER NOT NULL,
                FOREIGN KEY (application_id) REFERENCES $TABLE_APPLICATIONS(id) ON DELETE CASCADE,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 5. Recruiter Contacts Table
        db.execSQL("""
            CREATE TABLE $TABLE_CONTACTS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                name TEXT NOT NULL,
                company TEXT NOT NULL,
                job_title TEXT,
                email TEXT,
                phone TEXT,
                linkedin_url TEXT,
                notes TEXT,
                associated_app_ids TEXT,
                created_at INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 6. Communications Table
        db.execSQL("""
            CREATE TABLE $TABLE_COMMUNICATIONS (
                id TEXT PRIMARY KEY,
                application_id TEXT NOT NULL,
                contact_id TEXT,
                channel TEXT NOT NULL,
                subject TEXT NOT NULL,
                summary TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                FOREIGN KEY (application_id) REFERENCES $TABLE_APPLICATIONS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 7. Resumes Table
        db.execSQL("""
            CREATE TABLE $TABLE_RESUMES (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                version_name TEXT NOT NULL,
                target_role TEXT NOT NULL,
                file_name TEXT NOT NULL,
                skills_extracted TEXT,
                resume_summary TEXT,
                uploaded_at INTEGER NOT NULL,
                is_default INTEGER DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 8. Cover Letters Table
        db.execSQL("""
            CREATE TABLE $TABLE_COVER_LETTERS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                title TEXT NOT NULL,
                target_company TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 9. Tasks & Reminders Table
        db.execSQL("""
            CREATE TABLE $TABLE_TASKS_REMINDERS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                application_id TEXT,
                title TEXT NOT NULL,
                description TEXT,
                due_date INTEGER NOT NULL,
                reminder_type TEXT NOT NULL,
                is_completed INTEGER DEFAULT 0,
                priority TEXT DEFAULT 'MEDIUM',
                created_at INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 10. Notifications Table
        db.execSQL("""
            CREATE TABLE $TABLE_NOTIFICATIONS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                is_read INTEGER DEFAULT 0,
                action_route TEXT,
                timestamp INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 11. Job Listings (Search & Saved)
        db.execSQL("""
            CREATE TABLE $TABLE_JOB_LISTINGS (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                company TEXT NOT NULL,
                location TEXT NOT NULL,
                salary TEXT,
                work_type TEXT NOT NULL,
                employment_type TEXT NOT NULL,
                skills_required TEXT,
                description TEXT,
                source_url TEXT NOT NULL,
                source_name TEXT,
                is_saved INTEGER DEFAULT 0,
                posted_date TEXT
            )
        """.trimIndent())

        // 12. Audit Logs Table
        db.execSQL("""
            CREATE TABLE $TABLE_AUDIT_LOGS (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                user_email TEXT NOT NULL,
                action TEXT NOT NULL,
                entity_type TEXT NOT NULL,
                details TEXT,
                timestamp INTEGER NOT NULL
            )
        """.trimIndent())

        // Create Indexes for high-speed queries & foreign key lookups
        db.execSQL("CREATE INDEX idx_apps_user ON $TABLE_APPLICATIONS(user_id)")
        db.execSQL("CREATE INDEX idx_apps_status ON $TABLE_APPLICATIONS(status)")
        db.execSQL("CREATE INDEX idx_history_app ON $TABLE_APPLICATION_HISTORY(application_id)")
        db.execSQL("CREATE INDEX idx_interviews_user ON $TABLE_INTERVIEWS(user_id)")
        db.execSQL("CREATE INDEX idx_reminders_due ON $TABLE_TASKS_REMINDERS(due_date, is_completed)")
        db.execSQL("CREATE INDEX idx_notifications_user ON $TABLE_NOTIFICATIONS(user_id, is_read)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUDIT_LOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JOB_LISTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS_REMINDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COVER_LETTERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESUMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMMUNICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INTERVIEWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPLICATION_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPLICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
