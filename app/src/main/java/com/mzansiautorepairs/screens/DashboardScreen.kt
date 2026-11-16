
package com.mzansiautorepairs.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.mzansiautorepairs.JobCard
import com.mzansiautorepairs.JobStatus
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.components.StatCard
import com.mzansiautorepairs.components.StatusChip
import com.mzansiautorepairs.ui.theme.MzansiAutoRepairsTheme
import com.mzansiautorepairs.ui.theme.Primary
import com.mzansiautorepairs.ui.theme.PrimaryDark

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {}
) {
    val totalCustomers = MockData.customers.size
    val totalVehicles = MockData.vehicles.size
    val totalTechnicians = MockData.technicians.size

    val activeJobCards = MockData.jobCards.count {
        it.status != JobStatus.COMPLETED &&
                it.status != JobStatus.CANCELLED
    }

    val recentJobCards = MockData.jobCards
        .filter {
            it.status != JobStatus.COMPLETED &&
                    it.status != JobStatus.CANCELLED
        }
        .take(5)

    Scaffold(
        topBar = {
            MzansiTopBar(title = "Administrator Dashboard")
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            "Welcome back, Administrator!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Manage your workshop, monitor daily operations and keep track of your business.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Business Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )

                    Text(
                        "A quick summary of your workshop.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Customers",
                            value = totalCustomers,
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.CUSTOMERS)
                            }
                        )

                        StatCard(
                            title = "Vehicles",
                            value = totalVehicles,
                            icon = Icons.Default.DirectionsCar,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.VEHICLES)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Active Jobs",
                            value = activeJobCards,
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.JOBCARDS)
                            }
                        )

                        StatCard(
                            title = "Technicians",
                            value = totalTechnicians,
                            icon = Icons.Default.Engineering,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.TECHNICIANS)
                            }
                        )
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Workshop Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )

                    Text(
                        "Quick access to important management sections.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLinkCard(
                            title = "Customers",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.CUSTOMERS)
                            }
                        )

                        QuickLinkCard(
                            title = "Vehicles",
                            icon = Icons.Default.DirectionsCar,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.VEHICLES)
                            }
                        )

                        QuickLinkCard(
                            title = "Technicians",
                            icon = Icons.Default.Engineering,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.TECHNICIANS)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLinkCard(
                            title = "Job Cards",
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.JOBCARDS)
                            }
                        )

                        QuickLinkCard(
                            title = "Services",
                            icon = Icons.Default.HomeRepairService,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onNavigate(Routes.SERVICES)
                            }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Recent Active Jobs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )

                        Text(
                            "Monitor ongoing workshop activity.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = {
                            onNavigate(Routes.JOBCARDS)
                        }
                    ) {
                        Text("View All", color = Primary)
                    }
                }
            }

            if (recentJobCards.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "No active job cards available.",
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    recentJobCards,
                    key = { it.id }
                ) { jobCard ->
                    RecentJobCardItem(
                        jobCard = jobCard,
                        onClick = {
                            onNavigate(
                                Routes.jobCardDetail(jobCard.id)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryDark,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RecentJobCardItem(
    jobCard: JobCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    jobCard.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "Registration: ${jobCard.vehicleReg}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    jobCard.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(8.dp))
            StatusChip(status = jobCard.status)
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController) {
    DashboardScreen(
        onNavigate = { route ->
            navController.navigate(route)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MzansiAutoRepairsTheme {
        DashboardScreen()
    }
}


