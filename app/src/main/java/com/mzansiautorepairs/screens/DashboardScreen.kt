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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

/**
 * DashboardScreen displays an overview of workshop operations.
 *
 * Requirements fulfilled:
 * - MzansiTopBar titled "Mzansi Auto Repairs - Dashboard"
 * - Welcome banner "Welcome back, Workshop Staff!"
 * - 4 Stat cards (Total Customers, Total Vehicles, Active Job Cards, Technicians) calculated from MockData
 * - Quick Links (Customers, Job Cards, Services)
 * - "Recent Job Cards" section displaying recent job details with StatusChip and navigation
 *
 * @param onNavigate Navigation callback taking target route string.
 */
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {}
) {
    // 1. Calculations from MockData:
    // Total Customers count
    val totalCustomers = MockData.customers.size
    
    // Total Vehicles count
    val totalVehicles = MockData.vehicles.size
    
    // Active Job Cards count: status is neither COMPLETED nor CANCELLED
    val activeJobCards = MockData.jobCards.count {
        it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED
    }
    
    // Technicians count
    val totalTechnicians = MockData.technicians.size

    // 2. Fetch list of recent job cards from MockData
    val recentJobCards = MockData.jobCards

    Scaffold(
        topBar = {
            MzansiTopBar(title = "Mzansi Auto Repairs - Dashboard")
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Welcome Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Welcome back, Workshop Staff!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Here is an overview of today's workshop activity.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Section 2: 4 Stat Cards calculated from MockData
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Workshop Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total Customers",
                            value = totalCustomers,
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Routes.CUSTOMERS) }
                        )
                        StatCard(
                            title = "Total Vehicles",
                            value = totalVehicles,
                            icon = Icons.Default.DirectionsCar,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Active Job Cards",
                            value = activeJobCards,
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Routes.JOBCARDS) }
                        )
                        StatCard(
                            title = "Technicians",
                            value = totalTechnicians,
                            icon = Icons.Default.Engineering,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 3: Quick Links row/grid for Customers, Job Cards, Services
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Quick Links",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickLinkCard(
                            title = "Customers",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Routes.CUSTOMERS) }
                        )
                        QuickLinkCard(
                            title = "Job Cards",
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Routes.JOBCARDS) }
                        )
                        QuickLinkCard(
                            title = "Services",
                            icon = Icons.Default.HomeRepairService,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Routes.SERVICES) }
                        )
                    }
                }
            }

            // Section 4: Recent Job Cards section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Job Cards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )
                    TextButton(onClick = { onNavigate(Routes.JOBCARDS) }) {
                        Text("View All", color = Primary)
                    }
                }
            }

            // Section 5: Recent Job Cards scrollable list showing customer name, vehicle reg, description, StatusChip
            items(recentJobCards, key = { it.id }) { jobCard ->
                RecentJobCardItem(
                    jobCard = jobCard,
                    onClick = { onNavigate(Routes.jobCardDetail(jobCard.id)) }
                )
            }
        }
    }
}

/**
 * QuickLinkCard component for navigating directly to key feature screens.
 */
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * RecentJobCardItem component displaying individual job summary.
 */
@Composable
fun RecentJobCardItem(
    jobCard: JobCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = jobCard.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reg: ${jobCard.vehicleReg}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = jobCard.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            StatusChip(status = jobCard.status)
        }
    }
}

/**
 * Overload accepting NavController directly.
 */
@Composable
fun DashboardScreen(navController: NavController) {
    DashboardScreen(
        onNavigate = { route -> navController.navigate(route) }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MzansiAutoRepairsTheme {
        DashboardScreen()
    }
}
