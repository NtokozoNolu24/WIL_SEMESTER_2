package com.mzansiautorepairs.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.AuthResult
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.ui.theme.MzansiAutoRepairsTheme
import com.mzansiautorepairs.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

/**
 * RegisterScreen component allowing new staff members to register for an account.
 *
 * @param onNavigateToDashboard Callback invoked when account registration succeeds.
 * @param onNavigateToLogin Callback invoked when navigating back to the login screen.
 * @param onNavigate Generic route navigation callback for flexibility with NavHost.
 */
@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigate: ((String) -> Unit)? = null
) {
    // State management in Compose using 'remember { mutableStateOf(...) }':
    // Stores Full Name input value.
    var fullName by remember { mutableStateOf("") }
    
    // Stores Email input value.
    var email by remember { mutableStateOf("") }
    
    // Stores Phone Number input value.
    var phone by remember { mutableStateOf("") }
    
    // Stores Password input value.
    var password by remember { mutableStateOf("") }
    
    // Stores Confirm Password input value.
    var confirmPassword by remember { mutableStateOf("") }

    // Toggle states for showing/hiding password characters
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Dropdown state for selecting UserRole enum (ADMIN, RECEPTIONIST, TECHNICIAN)
    var selectedRole by remember { mutableStateOf(UserRole.TECHNICIAN) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }

    // Navigation helper functions
    fun handleNavigateToDashboard() {
        if (onNavigate != null) {
            onNavigate(Routes.DASHBOARD)
        } else {
            onNavigateToDashboard()
        }
    }

    fun handleNavigateToLogin() {
        if (onNavigate != null) {
            onNavigate(Routes.LOGIN)
        } else {
            onNavigateToLogin()
        }
    }
    // Backend: registration against the Room database
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = Box {
        remember { AuthRepository(context.applicationContext) }
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Top App Bar with back button navigating to Login screen
            MzansiTopBar(
                title = "Create Account",
                onBackClick = { handleNavigateToLogin() }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join Mzansi Auto Repairs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Register new staff account and assign workshop role",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Input Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Full Name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Number Input Field
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ExposedDropdownMenuBox for selecting UserRole
            ExposedDropdownMenuBox(
                expanded = isRoleDropdownExpanded,
                onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRole.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("User Role") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = "Role")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isRoleDropdownExpanded,
                    onDismissRequest = { isRoleDropdownExpanded = false }
                ) {
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = {
                                selectedRole = role
                                isRoleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input Field with Toggle Visibility
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
                },
                trailingIcon = {
                    val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password Input Field with Toggle Visibility
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password")
                },
                trailingIcon = {
                    val image = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                    }
                },
                singleLine = true,
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Create Account" Button -> validates, saves the user (hashed password), then goes to Dashboard
            Button(
                enabled = !isLoading,
                onClick = {
                    errorMessage = null
                    isLoading = true
                    scope.launch {
                        when (val result = authRepo.register(fullName, email, phone, password, confirmPassword, selectedRole)) {
                            is AuthResult.Success -> handleNavigateToDashboard()
                            is AuthResult.Error -> errorMessage = result.message
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Link navigating back to Login screen
            TextButton(
                onClick = {
                    handleNavigateToLogin()
                }
            ) {
                Text(
                    text = "Already have an account? Sign In",
                    color = PrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun Unit.register(
    fullName: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    selectedRole: UserRole
) {
            TODO("Not yet implemented")
}

/**
 * Overload accepting NavController directly.
 */
@Composable
fun RegisterScreen(navController: NavController) {
    RegisterScreen(
        onNavigate = { route -> navController.navigate(route) }
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MzansiAutoRepairsTheme {
        RegisterScreen()
    }
}
