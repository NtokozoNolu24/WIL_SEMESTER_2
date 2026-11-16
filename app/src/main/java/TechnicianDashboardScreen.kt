
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mzansiautorepairs.JobCard
import com.mzansiautorepairs.JobStatus
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.components.StatusChip

@Composable
fun TechnicianDashboardScreen(
    technicianId: String,
    technicianName: String,
    onBackClick: () -> Unit = {},
    onJobCardClick: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val technicianJobs = MockData.jobCards.filter {
        it.technicianId == technicianId
    }

    val pendingCount = technicianJobs.count {
        it.status == JobStatus.PENDING
    }

    val inProgressCount = technicianJobs.count {
        it.status == JobStatus.IN_PROGRESS
    }

    val completedCount = technicianJobs.count {
        it.status == JobStatus.COMPLETED
    }

    val filteredJobs = technicianJobs.filter { job ->
        when (selectedFilter) {
            "Pending" -> job.status == JobStatus.PENDING
            "In Progress" -> job.status == JobStatus.IN_PROGRESS
            "Completed" -> job.status == JobStatus.COMPLETED
            else -> true
        }
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Technician Dashboard",
                onBackClick = onBackClick
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column {
                    Text(
                        text = "Welcome, $technicianName!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Here is an overview of your assigned jobs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TechnicianStatCard(
                        title = "Total Jobs",
                        count = technicianJobs.size,
                        modifier = Modifier.weight(1f)
                    )

                    TechnicianStatCard(
                        title = "Pending",
                        count = pendingCount,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TechnicianStatCard(
                        title = "In Progress",
                        count = inProgressCount,
                        modifier = Modifier.weight(1f)
                    )

                    TechnicianStatCard(
                        title = "Completed",
                        count = completedCount,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "My Assigned Jobs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "All",
                        "Pending",
                        "In Progress",
                        "Completed"
                    ).forEach { filter ->

                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
            }

            if (filteredJobs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (technicianJobs.isEmpty()) {
                                "You currently have no assigned jobs."
                            } else {
                                "No jobs found for this filter."
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(
                    items = filteredJobs,
                    key = { it.id }
                ) { job ->

                    TechnicianJobCard(
                        job = job,
                        onStatusChange = { newStatus ->
                            MockData.updateJobCardStatus(
                                jobCardId = job.id,
                                newStatus = newStatus
                            )
                        },
                        onViewDetails = {
                            onJobCardClick(job.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TechnicianStatCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TechnicianJobCard(
    job: JobCard,
    onStatusChange: (JobStatus) -> Unit,
    onViewDetails: () -> Unit
) {
    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${job.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                StatusChip(status = job.status)
            }

            Text(
                text = "Customer: ${job.customerName}",
                fontWeight = FontWeight.SemiBold
            )

            Text(text = "Vehicle: ${job.vehicleReg}")

            Text(
                text = job.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Date: ${job.createdDate}",
                style = MaterialTheme.typography.bodySmall
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            statusMenuExpanded = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = job.status != JobStatus.COMPLETED &&
                                job.status != JobStatus.CANCELLED
                    ) {
                        Text("Update Status")
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = {
                            statusMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pending") },
                            onClick = {
                                onStatusChange(JobStatus.PENDING)
                                statusMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("In Progress") },
                            onClick = {
                                onStatusChange(JobStatus.IN_PROGRESS)
                                statusMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Completed") },
                            onClick = {
                                onStatusChange(JobStatus.COMPLETED)
                                statusMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
