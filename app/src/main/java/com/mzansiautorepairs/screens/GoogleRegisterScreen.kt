
package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.AuthResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleRegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = remember {
        AuthRepository(context.applicationContext)
    }

    val entry = remember(navController) {
        navController.previousBackStackEntry
    }
    val googleName = entry?.savedStateHandle
        ?.get<String>("googleName").orEmpty()
    val googleEmail = entry?.savedStateHandle
        ?.get<String>("googleEmail").orEmpty()

    var fullName by remember { mutableStateOf(googleName) }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember {
        mutableStateOf(UserRole.TECHNICIAN)
    }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Complete Google Registration")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = googleEmail,
            onValueChange = {},
            readOnly = true,
            label = { Text("Google email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedRole.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Staff role") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf(
                    UserRole.TECHNICIAN,
                    UserRole.RECEPTIONIST
                ).forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        errorMessage?.let {
            Text(it)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                errorMessage = null

                if (googleEmail.isBlank()) {
                    errorMessage =
                        "Google account details are missing. Please sign in again."
                    return@Button
                }

                isLoading = true

                scope.launch {
                    try {
                        when (
                            val result = authRepo.registerWithGoogle(
                                fullName = fullName,
                                email = googleEmail,
                                phone = phone,
                                role = selectedRole
                            )
                        ) {
                            is AuthResult.Success -> {
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(Routes.LOGIN) {
                                        inclusive = true
                                    }
                                }
                            }

                            is AuthResult.Error -> {
                                errorMessage = result.message
                            }
                        }
                    } catch (e: Exception) {
                        errorMessage =
                            "Registration failed. Please try again."
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            Text(
                if (isLoading) "Registering..."
                else "Complete Registration"
            )
        }
    }
}
