
package com.mzansiautorepairs.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.components.MzansiTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    val authRepository = remember {
        AuthRepository(context.applicationContext)
    }
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var darkMode by remember {
        mutableStateOf(preferences.getBoolean("dark_mode", false))
    }
    var notifications by remember {
        mutableStateOf(preferences.getBoolean("notifications", true))
    }

    LaunchedEffect(Unit) {
        try {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                fullName = user.fullName
                email = user.email
                phone = user.phone
                role = user.role
            }
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Settings",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage your account and personalise your app experience.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (email.isBlank()) {
                Text("No active user session was found.")

                Button(
                    onClick = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go to Login")
                }
            } else {
                SettingsSection(title = "MY ACCOUNT") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = fullName.ifBlank { "App User" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = role.ifBlank { "Registered user" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider()
                    ProfileDetail("Email", email, Icons.Default.Email)
                    ProfileDetail("Phone", phone, Icons.Default.Phone)
                    ProfileDetail(
                        "Role",
                        role.ifBlank { "Not specified" },
                        Icons.Default.AccountCircle
                    )
                }

                SettingsSection(title = "APP PREFERENCES") {
                    PreferenceSwitch(
                        title = "Dark mode",
                        description = "Save your preferred appearance setting.",
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            preferences.edit()
                                .putBoolean("dark_mode", it)
                                .apply()
                        }
                    )

                    HorizontalDivider()

                    PreferenceSwitch(
                        title = "Notifications",
                        description = "Save your notification preference.",
                        checked = notifications,
                        onCheckedChange = {
                            notifications = it
                            preferences.edit()
                                .putBoolean("notifications", it)
                                .apply()
                        }
                    )
                }

                SettingsSection(title = "HELP & INFORMATION") {
                    OutlinedButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Help & Support")
                    }

                    OutlinedButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("About Mzansi Auto Repairs")
                    }
                }

                SettingsSection(title = "SECURITY") {
                    Text(
                        text = "Log out of your current account on this device.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Log Out")
                    }
                }

                Text(
                    text = "Mzansi Auto Repairs • App Settings",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        scope.launch {
                            authRepository.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support") },
            text = {
                Text(
                    "Use Mzansi Auto Repairs to explore available services " +
                            "and manage your account. For assistance, contact " +
                            "your service provider or project support team."
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Mzansi Auto Repairs") },
            text = {
                Text(
                    "Mzansi Auto Repairs helps users explore automotive " +
                            "services and access app features in one place."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}


@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun PreferenceSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ProfileDetail(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
            Text(value.ifBlank { "Not provided" })
        }
    }
}
