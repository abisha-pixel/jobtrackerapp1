package com.example.jobtrackerapp1.presentation.screens.jobsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobtrackerapp1.domain.model.JobListing
import com.example.jobtrackerapp1.domain.model.WorkType
import com.example.jobtrackerapp1.presentation.components.WorkTypeBadge
import com.example.jobtrackerapp1.ui.theme.CyanAccent
import com.example.jobtrackerapp1.ui.theme.EmeraldSuccess
import com.example.jobtrackerapp1.ui.theme.IndigoLight
import com.example.jobtrackerapp1.ui.theme.IndigoPrimary

@Composable
fun JobSearchScreen(
    jobListings: List<JobListing>,
    onToggleSave: (String) -> Unit,
    onConvertJobToApplication: (JobListing) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var showOnlySaved by remember { mutableStateOf(false) }

    val filteredListings = remember(jobListings, query, showOnlySaved) {
        jobListings.filter { listing ->
            val matchesQuery = query.isBlank() ||
                    listing.title.contains(query, ignoreCase = true) ||
                    listing.company.contains(query, ignoreCase = true) ||
                    listing.location.contains(query, ignoreCase = true) ||
                    listing.skillsRequired.any { it.contains(query, ignoreCase = true) }

            val matchesSaved = !showOnlySaved || listing.isSaved
            matchesQuery && matchesSaved
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Job Search & Live Feed",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Explore verified opportunities and convert saved listings into active applications",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search by keywords, skills (Compose, Remote, Staff)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = IndigoLight)
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips (All vs Saved Bookmarks)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !showOnlySaved,
                onClick = { showOnlySaved = false },
                label = { Text("All Listings (${jobListings.size})") },
                shape = RoundedCornerShape(8.dp)
            )
            FilterChip(
                selected = showOnlySaved,
                onClick = { showOnlySaved = true },
                label = { Text("Saved / Bookmarks (${jobListings.count { it.isSaved }})") },
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredListings, key = { it.id }) { listing ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = listing.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${listing.company} • ${listing.location}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { onToggleSave(listing.id) }) {
                                Icon(
                                    imageVector = if (listing.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Save",
                                    tint = if (listing.isSaved) CyanAccent else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (listing.salary.isNotBlank()) {
                                Text(
                                    text = listing.salary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                            WorkTypeBadge(workType = listing.workType)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = listing.description,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            maxLines = 3,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (listing.skillsRequired.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listing.skillsRequired.forEach { skill ->
                                    Text(
                                        text = skill,
                                        fontSize = 10.sp,
                                        color = IndigoLight,
                                        modifier = Modifier
                                            .background(IndigoLight.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Source: ${listing.sourceName} • ${listing.postedDate}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { onConvertJobToApplication(listing) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                            ) {
                                Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Convert to Application", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
