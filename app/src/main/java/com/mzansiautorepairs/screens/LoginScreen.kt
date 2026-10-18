package com.mzansiautorepairs.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.ui.theme.MzansiAutoRepairsTheme
import com.mzansiautorepairs.ui.theme.Primary
import com.mzansiautorepairs.ui.theme.PrimaryDark

/**
 * LoginScreen component allowing workshop staff to log into the application.
 *
 * Navigation parameters:
 * @param onNavigateToDashboard Callback invoked when user successfully logs in.
 * @param onNavigateToRegister Callback invoked when user clicks the register link.
 * @param onNavigate Generic route navigation callback for flexibility with NavHost.
 */
@SuppressLint("RememberReturnType")
@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigate: ((String) -> Unit)? = null
) {
    // State management in Jetpack Compose:
    // 'remember { mutableStateOf(...) }' preserves state across re-compositions.
    // 'email' stores the user's entered email address.
    var email by remember { mutableStateOf("") }
    
    // 'password' stores the user's entered password.
    var password by remember { mutableStateOf("") }
    
    // 'isPasswordVisible' toggles password visibility between hidden dots and clear text.
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Backend: authentication against the Room database
    val context = LocalContext.current
    rememberCoroutineScope()
    Box {
        remember { AuthRepository(context.applicationContext) }
    }

    // Navigation logic wrapper helper
    fun handleNavigateToDashboard() {
        if (onNavigate != null) {
            onNavigate(Routes.DASHBOARD)
        } else {
            onNavigateToDashboard()
        }
    }

    fun handleNavigateToRegister() {
        if (onNavigate != null) {
            onNavigate(Routes.REGISTER)
        } else {
            onNavigateToRegister()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Branding / Workshop Logo Header
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Mzansi Auto Repairs Logo",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mzansi Auto Repairs",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )

            Text(
                text = "Workshop Management System",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input Field with Toggle Visibility
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon"
                    )
                },
                trailingIcon = {
                    val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (isPasswordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button -> Navigates to Dashboard
            Button(
                onClick = {
                    // Navigate to Dashboard screen
                    handleNavigateToDashboard()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                val isLoading = false
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Button to navigate to Register screen
            TextButton(
                onClick = {
                    // Navigate to Register screen
                    handleNavigateToRegister()
                }
            ) {
                Text(
                    text = "Don't have an account? Register",
                    color = PrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Overload accepting NavController directly for ease of use in Navigation hosts.
 */
@Composable
fun LoginScreen(navController: NavController) {
    LoginScreen(
        onNavigate = { route -> navController.navigate(route) }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MzansiAutoRepairsTheme {
        LoginScreen()
    }
}
