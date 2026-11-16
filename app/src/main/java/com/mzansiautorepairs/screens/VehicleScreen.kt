
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.components.MzansiTopBar

@Composable
fun VehicleScreen(navController: NavController) {
    val vehicles = MockData.vehicles

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Vehicle Management",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->

        if (vehicles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No vehicles have been registered yet.")
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
                        "Registered Vehicles",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "View vehicles registered with the workshop.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                items(vehicles, key = { it.registration }) { vehicle ->
                    val owner = MockData.customers.find {
                        it.id == vehicle.ownerId
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(14.dp))

                            Column {
                                Text(
                                    vehicle.registration,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("${vehicle.make} ${vehicle.model}")
                                Text("Year: ${vehicle.year}")
                                Text(
                                    "Owner: ${owner?.name ?: "Unknown owner"}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
