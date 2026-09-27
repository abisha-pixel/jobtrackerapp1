package com.example.jobtrackerapp1.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.ApplicationStatus
import com.example.jobtrackerapp1.domain.model.Priority
import com.example.jobtrackerapp1.domain.model.WorkType

@Composable
fun StatusBadge(status: ApplicationStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        ApplicationStatus.WISHLIST, ApplicationStatus.PREPARING ->
            Color(0xFF1E293B) to Color(0xFF94A3B8)
        ApplicationStatus.APPLIED, ApplicationStatus.APPLICATION_VIEWED ->
            Color(0xFF0369A1).copy(alpha = 0.2f) to Color(0xFF38BDF8)
        ApplicationStatus.RECRUITER_CONTACTED ->
            Color(0xFF0D9488).copy(alpha = 0.2f) to Color(0xFF2DD4BF)
        ApplicationStatus.SCREENING, ApplicationStatus.INTERVIEW,
        ApplicationStatus.TECHNICAL_INTERVIEW, ApplicationStatus.HR_INTERVIEW,
        ApplicationStatus.FINAL_ROUND ->
            Color(0xFF6D28D9).copy(alpha = 0.25f) to Color(0xFFA78BFA)
        ApplicationStatus.OFFER, ApplicationStatus.ACCEPTED ->
            Color(0xFF047857).copy(alpha = 0.25f) to Color(0xFF34D399)
        ApplicationStatus.REJECTED ->
            Color(0xFFBE123C).copy(alpha = 0.2f) to Color(0xFFFB7185)
        ApplicationStatus.WITHDRAWN ->
            Color(0xFF475569).copy(alpha = 0.2f) to Color(0xFFCBD5E1)
    }

    Text(
        text = status.readableName(),
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        Priority.LOW -> Color(0xFF475569).copy(alpha = 0.2f) to Color(0xFF94A3B8)
        Priority.MEDIUM -> Color(0xFF0284C7).copy(alpha = 0.2f) to Color(0xFF38BDF8)
        Priority.HIGH -> Color(0xFFD97706).copy(alpha = 0.25f) to Color(0xFFFBBF24)
        Priority.URGENT -> Color(0xFFE11D48).copy(alpha = 0.25f) to Color(0xFFFDA4AF)
    }

    Text(
        text = priority.name,
        color = textColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun WorkTypeBadge(workType: WorkType, modifier: Modifier = Modifier) {
    val text = when (workType) {
        WorkType.REMOTE -> "Remote"
        WorkType.HYBRID -> "Hybrid"
        WorkType.ON_SITE -> "On-Site"
    }

    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
