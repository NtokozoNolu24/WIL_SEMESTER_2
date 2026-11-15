package com.mzansiautorepairs.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.Customer
import com.mzansiautorepairs.JobCard
import com.mzansiautorepairs.JobStatus
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.Technician
import com.mzansiautorepairs.Vehicle
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.components.StatusChip
import com.mzansiautorepairs.ui.theme.MzansiAutoRepairsTheme
import com.mzansiautorepairs.ui.theme.PrimaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JobCardScreen(
    navController: NavController
) {
    JobCardScreen(
        onBackClick = { navController.popBackStack() },
        onJobCardClick = { id -> navController.navigate(Routes.jobCardDetail(id)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobCardScreen(
    onBackClick: () -> Unit = {},
    onJobCardClick: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Pending", "In Progress", "Completed")

    var showCreateDialog by remember { mutableStateOf(false) }

    // Dialog state
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }

    var selectedTechnician by remember { mutableStateOf<Technician?>(null) }
    var techDropdownExpanded by remember { mutableStateOf(false) }

    var labourCostStr by remember { mutableStateOf("") }
    var partsCostStr by remember { mutableStateOf("") }

    // Filter job cards
    val filteredJobCards = MockData.jobCards.filter { jobCard ->
        when (selectedFilter) {
            "Pending" -> jobCard.status == JobStatus.PENDING
            "In Progress" -> jobCard.status == JobStatus.IN_PROGRESS
            "Completed" -> jobCard.status == JobStatus.COMPLETED
            else -> true
        }
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Job Cards",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Job Card") },
                text = { Text("Create Job Card") },
                containerColor = PrimaryDark,
                contentColor = Color.White
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Status filter chips/tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { option ->
                    FilterChip(
                        selected = (selectedFilter == option),
                        onClick = { selectedFilter = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredJobCards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No job cards found for status '$selectedFilter'.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredJobCards, key = { it.id }) { jobCard ->
                        JobCardItem(
                            jobCard = jobCard,
                            onClick = { onJobCardClick(jobCard.id) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val availableVehicles = MockData.vehicles.filter { it.ownerId == selectedCustomer?.id }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Create New Job Card",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Customer Dropdown
                    ExposedDropdownMenuBox(
                        expanded = customerDropdownExpanded,
                        onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCustomer?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Customer *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = customerDropdownExpanded,
                            onDismissRequest = { customerDropdownExpanded = false }
                        ) {
                            MockData.customers.forEach { customer ->
                                DropdownMenuItem(
                                    text = { Text("${customer.name} (${customer.id})") },
                                    onClick = {
                                        selectedCustomer = customer
                                        selectedVehicle = null // reset vehicle
                                        customerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Vehicle Dropdown
                    ExposedDropdownMenuBox(
                        expanded = vehicleDropdownExpanded,
                        onExpandedChange = { vehicleDropdownExpanded = !vehicleDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedVehicle?.let { "${it.registration} - ${it.make} ${it.model}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = selectedCustomer != null,
                            label = { Text("Select Vehicle *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = vehicleDropdownExpanded,
                            onDismissRequest = { vehicleDropdownExpanded = false }
                        ) {
                            if (availableVehicles.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No vehicles for this customer") },
                                    onClick = { vehicleDropdownExpanded = false }
                                )
                            } else {
                                availableVehicles.forEach { vehicle ->
                                    DropdownMenuItem(
                                        text = { Text("${vehicle.registration} (${vehicle.make} ${vehicle.model})") },
                                        onClick = {
                                            selectedVehicle = vehicle
                                            vehicleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Job Description *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Technician Dropdown
                    ExposedDropdownMenuBox(
                        expanded = techDropdownExpanded,
                        onExpandedChange = { techDropdownExpanded = !techDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedTechnician?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Technician") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = techDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = techDropdownExpanded,
                            onDismissRequest = { techDropdownExpanded = false }
                        ) {
                            MockData.technicians.forEach { tech ->
                                DropdownMenuItem(
                                    text = { Text("${tech.name} - ${tech.specialty}") },
                                    onClick = {
                                        selectedTechnician = tech
                                        techDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Labour Cost & Parts Cost
                    OutlinedTextField(
                        value = labourCostStr,
                        onValueChange = { labourCostStr = it },
                        label = { Text("Initial Labour Cost (R)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = partsCostStr,
                        onValueChange = { partsCostStr = it },
                        label = { Text("Initial Parts Cost (R)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val customer = selectedCustomer
                        val vehicle = selectedVehicle
                        if (customer != null && vehicle != null && description.isNotBlank()) {
                            val newJobId = "JC" + (1001 + MockData.jobCards.size)
                            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val tech = selectedTechnician

                            val newJobCard = JobCard(
                                id = newJobId,
                                customerId = customer.id,
                                customerName = customer.name,
                                vehicleReg = vehicle.registration,
                                description = description.trim(),
                                technicianId = tech?.id ?: "",
                                technicianName = tech?.name ?: "Unassigned",
                                status = JobStatus.PENDING,
                                createdDate = currentDate,
                                labourCost = labourCostStr.toDoubleOrNull() ?: 0.0,
                                partsCost = partsCostStr.toDoubleOrNull() ?: 0.0
                            )

                            MockData.createJobCard(newJobCard)

                            // Reset form & dismiss
                            selectedCustomer = null
                            selectedVehicle = null
                            description = ""
                            selectedTechnician = null
                            labourCostStr = ""
                            partsCostStr = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Create Job Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun JobCardItem(
    jobCard: JobCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${jobCard.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark
                )
                StatusChip(status = jobCard.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Customer: ${jobCard.customerName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Vehicle Reg: ${jobCard.vehicleReg}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = jobCard.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tech: ${if (jobCard.technicianName.isNotBlank()) jobCard.technicianName else "Unassigned"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "R %.2f".format(jobCard.totalCost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JobCardScreenPreview() {
    MzansiAutoRepairsTheme {
        JobCardScreen()
    }
}
