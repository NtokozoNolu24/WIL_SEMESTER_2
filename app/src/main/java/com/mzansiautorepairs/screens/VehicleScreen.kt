
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.Vehicle
import com.mzansiautorepairs.components.MzansiTopBar

@Composable
fun VehicleScreen(navController: NavController) {
    val vehicles = MockData.vehicles
    val customers = MockData.customers

    var showForm by remember { mutableStateOf(false) }
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Vehicle Management",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingVehicle = null
                    showForm = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add vehicle")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Registered Vehicles",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Add, update and manage vehicles registered with the workshop.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (vehicles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No vehicles have been registered yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 90.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vehicles, key = { it.registration }) { vehicle ->
                        val owner = customers.find {
                            it.id == vehicle.ownerId
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        vehicle.registration,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("${vehicle.make} ${vehicle.model}")
                                    Text("Year: ${vehicle.year}")
                                    Text("Owner: ${owner?.name ?: "Unknown owner"}")
                                }

                                Column {
                                    IconButton(
                                        onClick = {
                                            editingVehicle = vehicle
                                            showForm = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit vehicle"
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            vehicleToDelete = vehicle
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete vehicle",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        VehicleFormDialog(
            initialVehicle = editingVehicle,
            onDismiss = { showForm = false },
            onSave = { vehicle ->
                MockData.addVehicle(vehicle)
                showForm = false
            }
        )
    }

    vehicleToDelete?.let { vehicle ->
        AlertDialog(
            onDismissRequest = { vehicleToDelete = null },
            title = { Text("Delete Vehicle") },
            text = {
                Text(
                    "Are you sure you want to delete ${vehicle.registration}? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        MockData.deleteVehicle(vehicle.registration)
                        vehicleToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { vehicleToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun VehicleFormDialog(
    initialVehicle: Vehicle?,
    onDismiss: () -> Unit,
    onSave: (Vehicle) -> Unit
) {
    var registration by remember(initialVehicle?.registration) {
        mutableStateOf(initialVehicle?.registration ?: "")
    }
    var make by remember(initialVehicle?.registration) {
        mutableStateOf(initialVehicle?.make ?: "")
    }
    var model by remember(initialVehicle?.registration) {
        mutableStateOf(initialVehicle?.model ?: "")
    }
    var year by remember(initialVehicle?.registration) {
        mutableStateOf(initialVehicle?.year?.toString() ?: "")
    }
    var ownerId by remember(initialVehicle?.registration) {
        mutableStateOf(initialVehicle?.ownerId ?: "")
    }
    var ownerMenuExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val customers = MockData.customers
    val selectedOwner = customers.find { it.id == ownerId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialVehicle == null) "Add Vehicle" else "Edit Vehicle")
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = registration,
                    onValueChange = { registration = it.uppercase() },
                    label = { Text("Registration Number") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = initialVehicle == null,
                    singleLine = true
                )

                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Vehicle Make") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Vehicle Model") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it.filter { char -> char.isDigit() }.take(4) },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedOwner?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Owner") },
                        placeholder = { Text("Select a customer") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        DropdownMenu(
                            expanded = ownerMenuExpanded,
                            onDismissRequest = { ownerMenuExpanded = false }
                        ) {
                            customers.forEach { customer ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${customer.name} (${customer.phone})")
                                    },
                                    onClick = {
                                        ownerId = customer.id
                                        ownerMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Opens the customer selection list.
                    TextButton(
                        onClick = { ownerMenuExpanded = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Select")
                    }
                }

                if (customers.isEmpty()) {
                    Text(
                        "Register a customer before adding a vehicle.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (errorMessage.isNotBlank()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedYear = year.toIntOrNull()

                    errorMessage = when {
                        registration.isBlank() -> "Enter a registration number."
                        make.isBlank() -> "Enter the vehicle make."
                        model.isBlank() -> "Enter the vehicle model."
                        parsedYear == null || parsedYear !in 1900..2100 ->
                            "Enter a valid vehicle year."
                        selectedOwner == null ->
                            "Please select a registered customer."
                        else -> ""
                    }

                    if (errorMessage.isBlank()) {
                        onSave(
                            Vehicle(
                                registration = registration.trim(),
                                make = make.trim(),
                                model = model.trim(),
                                year = parsedYear!!,
                                ownerId = ownerId
                            )
                        )
                    }
                }
            ) {
                Text(if (initialVehicle == null) "Add Vehicle" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
