
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.mzansiautorepairs.MockData
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.PendingStaffApplication
import com.mzansiautorepairs.components.MzansiTopBar
import kotlinx.coroutines.launch

@Composable
fun StaffApprovalScreen(navController: NavController) {

    val context = LocalContext.current
    val repository = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    var applications by remember {
        mutableStateOf<List<PendingStaffApplication>>(emptyList())
    }

    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var applicationToReject by remember {
        mutableStateOf<PendingStaffApplication?>(null)
    }

    // Selected technician ID for each pending application.
    val selectedTechnicianIds = remember {
        mutableStateMapOf<String, String>()
    }

    fun loadApplications() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            try {
                applications = repository.getPendingStaffApplications()
            } catch (e: Exception) {
                errorMessage = e.message
                    ?: "Could not load staff applications."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadApplications()
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Staff Approvals",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Pending Applications",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "${applications.size} application(s) awaiting review",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { loadApplications() },
                    enabled = !isLoading && !isProcessing
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh applications"
                    )
                }
            }

            // SUCCESS MESSAGE
            if (successMessage.isNotBlank()) {
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // ERROR MESSAGE
            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when {

                // LOADING
                isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // EMPTY APPLICATION LIST
                applications.isEmpty() && errorMessage.isBlank() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No pending applications",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "New staff registrations will appear here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // APPLICATIONS
                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = applications,
                            key = { it.uid }
                        ) { application ->

                            val isTechnician =
                                application.requestedRole.equals(
                                    "TECHNICIAN",
                                    ignoreCase = true
                                )

                            val selectedTechnicianId =
                                selectedTechnicianIds[application.uid]

                            val selectedTechnician =
                                MockData.technicians.firstOrNull {
                                    it.id == selectedTechnicianId
                                }

                            var technicianMenuExpanded by remember {
                                mutableStateOf(false)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    // STAFF INFORMATION
                                    Text(
                                        text = application.fullName,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(text = "Email: ${application.email}")
                                    Text(text = "Phone: ${application.phone}")
                                    Text(text = "Requested role: ${application.requestedRole}")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // APPLICATION STATUS
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.small
                                    ) {

                                        Text(
                                            text = "PENDING APPROVAL",
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    // TECHNICIAN SELECTION
                                    if (isTechnician) {

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "Link Existing Technician",
                                            style = MaterialTheme.typography.titleSmall
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Select the technician record belonging to this staff member.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (MockData.technicians.isEmpty()) {

                                            Text(
                                                text = "No technician records available. Add a technician before approving this application.",
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )

                                        } else {

                                            Box(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {

                                                OutlinedButton(
                                                    onClick = {
                                                        technicianMenuExpanded = true
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {

                                                    Text(
                                                        text = selectedTechnician?.let {
                                                            "${it.name} (${it.id})"
                                                        } ?: "Select technician"
                                                    )
                                                }

                                                DropdownMenu(
                                                    expanded = technicianMenuExpanded,
                                                    onDismissRequest = {
                                                        technicianMenuExpanded = false
                                                    }
                                                ) {

                                                    MockData.technicians.forEach { technician ->

                                                        DropdownMenuItem(
                                                            text = {
                                                                Column {

                                                                    Text(
                                                                        text = technician.name
                                                                    )

                                                                    Text(
                                                                        text = "${technician.id} - ${technician.specialty}",
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                }
                                                            },
                                                            onClick = {

                                                                selectedTechnicianIds[application.uid] =
                                                                    technician.id

                                                                technicianMenuExpanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (selectedTechnician != null) {

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = "Selected: ${selectedTechnician.name}",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // APPROVE AND REJECT
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            enabled = !isProcessing &&
                                                    (
                                                            !isTechnician ||
                                                                    selectedTechnicianId != null
                                                            ),
                                            onClick = {

                                                scope.launch {

                                                    isProcessing = true
                                                    errorMessage = ""
                                                    successMessage = ""

                                                    try {

                                                        repository.approveStaffApplication(
                                                            uid = application.uid,
                                                            technicianId = if (isTechnician) {
                                                                selectedTechnicianId
                                                            } else {
                                                                null
                                                            }
                                                        )

                                                        applications = applications.filter {
                                                            it.uid != application.uid
                                                        }

                                                        selectedTechnicianIds.remove(
                                                            application.uid
                                                        )

                                                        successMessage =
                                                            "${application.fullName} has been approved."

                                                    } catch (e: Exception) {

                                                        errorMessage = e.message
                                                            ?: "Approval failed."

                                                    } finally {
                                                        isProcessing = false
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Approve")
                                        }

                                        OutlinedButton(
                                            modifier = Modifier.weight(1f),
                                            enabled = !isProcessing,
                                            onClick = {
                                                applicationToReject = application
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Reject")
                                        }
                                    }

                                    if (isTechnician && selectedTechnicianId == null) {

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Select an existing technician before approving.",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
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

    // REJECTION CONFIRMATION
    applicationToReject?.let { application ->

        AlertDialog(
            onDismissRequest = {
                if (!isProcessing) {
                    applicationToReject = null
                }
            },

            title = {
                Text("Reject Application?")
            },

            text = {
                Text(
                    "Are you sure you want to reject ${application.fullName}'s staff application?"
                )
            },

            confirmButton = {

                TextButton(
                    enabled = !isProcessing,
                    onClick = {

                        scope.launch {

                            isProcessing = true
                            errorMessage = ""
                            successMessage = ""

                            try {

                                repository.rejectStaffApplication(
                                    application.uid
                                )

                                applications = applications.filter {
                                    it.uid != application.uid
                                }

                                selectedTechnicianIds.remove(application.uid)

                                successMessage =
                                    "${application.fullName}'s application was rejected."

                                applicationToReject = null

                            } catch (e: Exception) {

                                errorMessage = e.message
                                    ?: "Could not reject application."

                            } finally {
                                isProcessing = false
                            }
                        }
                    }
                ) {

                    Text(
                        text = "Reject",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    enabled = !isProcessing,
                    onClick = {
                        applicationToReject = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
