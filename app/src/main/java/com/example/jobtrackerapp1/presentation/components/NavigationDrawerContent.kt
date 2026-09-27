package com.example.jobtrackerapp1.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.R
import com.example.jobtrackerapp1.domain.model.User
import com.example.jobtrackerapp1.domain.model.UserRole
import com.example.jobtrackerapp1.presentation.navigation.Screen
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary

@Composable
fun NavigationDrawerContent(
    currentScreen: Screen,
    user: User,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: App Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "APEX TRACK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Career Intelligence",
                        fontSize = 11.sp,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // User Info Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.role.displayName(),
                        fontSize = 11.sp,
                        color = IndigoLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Items grouped
            val coreScreens = listOf(
                Screen.DASHBOARD,
                Screen.APPLICATIONS,
                Screen.KANBAN,
                Screen.INTERVIEWS,
                Screen.CALENDAR,
                Screen.JOB_SEARCH,
                Screen.RESUMES,
                Screen.RECRUITERS,
                Screen.REMINDERS,
                Screen.ANALYTICS,
                Screen.AI_ASSISTANT,
                Screen.ADMIN,
                Screen.SETTINGS
            )

            coreScreens.forEach { screen ->
                val isSelected = currentScreen == screen
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = screen.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = screen.title,
                            tint = if (isSelected) IndigoLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    shape = RoundedCornerShape(10.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = IndigoPrimary.copy(alpha = 0.15f),
                        selectedTextColor = IndigoLight,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
