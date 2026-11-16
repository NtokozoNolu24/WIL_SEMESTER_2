
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.components.MzansiTopBar

@Composable
fun TechnicianScreen(navController: NavController) {
    val technicians = MockData.technicians

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Technician Management",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->

        if (technicians.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No technicians have been registered yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Workshop Technicians",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "View technician details and workload.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                items(technicians, key = { it.id }) { technician ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    technician.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Specialty: ${technician.specialty}")
                                Text("Phone: ${technician.phone}")
                                Text("Active jobs: ${technician.activeJobs}")
                                Text("Completed jobs: ${technician.completedJobs}")
                            }
                        }
                    }
                }
            }
        }
    }
}
