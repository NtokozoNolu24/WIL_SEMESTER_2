package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var applicationToReject by remember {
        mutableStateOf<PendingStaffApplication?>(null)
    }

    var processingUid by remember { mutableStateOf<String?>(null) }

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

    fun approveApplication(application: PendingStaffApplication) {
        scope.launch {
            processingUid = application.uid
            errorMessage = ""

            try {
                repository.approveStaffApplication(application.uid)

                successMessage =
                    "${application.fullName} has been approved."

                loadApplications()

            } catch (e: Exception) {
                errorMessage = e.message
                    ?: "Could not approve this application."
            } finally {
                processingUid = null
            }
        }
    }

    fun rejectApplication(application: PendingStaffApplication) {
        scope.launch {
            processingUid = application.uid
            errorMessage = ""

            try {
                repository.rejectStaffApplication(application.uid)

                successMessage =
                    "${application.fullName}'s application was rejected."

                applicationToReject = null
                loadApplications()

            } catch (e: Exception) {
                errorMessage = e.message
                    ?: "Could not reject this application."
            } finally {
                processingUid = null
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
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Pending Applications",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Review and manage new staff registrations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        successMessage = ""
                        loadApplications()
                    },
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh applications"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (successMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        successMessage,
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            if (errorMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        errorMessage,
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                Spacer(Modifier.height(12.dp))
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

                applications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "No Pending Applications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "There are currently no staff registrations waiting for approval.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        "${applications.size} application(s) awaiting review",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(
                            applications,
                            key = { it.uid }
                        ) { application ->

                            StaffApplicationCard(
                                application = application,
                                isProcessing = processingUid == application.uid,
                                onApprove = {
                                    approveApplication(application)
                                },
                                onReject = {
                                    applicationToReject = application
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    applicationToReject?.let { application ->

        AlertDialog(
            onDismissRequest = {
                if (processingUid == null) {
                    applicationToReject = null
                }
            },
            title = {
                Text("Reject Application?")
            },
            text = {
                Text(
                    "Are you sure you want to reject " +
                            "${application.fullName}'s staff application?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        rejectApplication(application)
                    },
                    enabled = processingUid == null
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
                    },
                    enabled = processingUid == null
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StaffApplicationCard(
    application: PendingStaffApplication,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        application.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        application.requestedRole.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "PENDING",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Email: ${application.email}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(5.dp))

            Text(
                "Phone: ${application.phone}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            if (isProcessing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Approve")
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Reject")
                    }
                }
            }
        }
    }
}
