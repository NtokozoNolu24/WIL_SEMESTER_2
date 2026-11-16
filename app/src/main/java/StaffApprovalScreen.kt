
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Pending Applications",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        "${applications.size} application(s) awaiting review",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { loadApplications() },
                    enabled = !isLoading && !isProcessing
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh applications"
                    )
                }
            }

            if (successMessage.isNotBlank()) {
                Text(
                    successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (errorMessage.isNotBlank()) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                applications.isEmpty() && errorMessage.isBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "No pending applications",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "New staff registrations will appear here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            applications,
                            key = { it.uid }
                        ) { application ->

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        application.fullName,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text("Email: ${application.email}")
                                    Text("Phone: ${application.phone}")
                                    Text("Requested role: ${application.requestedRole}")

                                    Spacer(Modifier.height(8.dp))

                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            "PENDING APPROVAL",
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            modifier = Modifier.weight(1f),
                                            enabled = !isProcessing,
                                            onClick = {
                                                scope.launch {
                                                    isProcessing = true
                                                    errorMessage = ""
                                                    successMessage = ""

                                                    try {
                                                        repository.approveStaffApplication(
                                                            application.uid
                                                        )

                                                        applications = applications.filter {
                                                            it.uid != application.uid
                                                        }

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
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    applicationToReject?.let { application ->

        AlertDialog(
            onDismissRequest = {
                applicationToReject = null
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
                        "Reject",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
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
