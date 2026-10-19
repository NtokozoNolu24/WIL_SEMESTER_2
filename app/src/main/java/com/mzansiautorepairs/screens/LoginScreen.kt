
package com.mzansiautorepairs.screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.AuthResult
import com.mzansiautorepairs.auth.GoogleAuthHelper
import com.mzansiautorepairs.ui.theme.MzansiAutoRepairsTheme
import com.mzansiautorepairs.ui.theme.Primary
import com.mzansiautorepairs.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepo = remember {
        AuthRepository(context.applicationContext)
    }

    val googleAuthHelper = remember {
        GoogleAuthHelper(context.applicationContext)
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
            Spacer(Modifier.height(32.dp))

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

            Spacer(Modifier.height(16.dp))

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

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Lock Icon"
                    )
                },
                trailingIcon = {
                    val image = if (isPasswordVisible) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    }

                    IconButton(
                        onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Existing email and password login
            Button(
                enabled = !isLoading,
                onClick = {
                    errorMessage = null

                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Enter your email and password"
                    } else {
                        isLoading = true

                        scope.launch {
                            try {
                                when (
                                    val result = authRepo.login(
                                        email = email,
                                        password = password
                                    )
                                ) {
                                    is AuthResult.Success -> {
                                        navController.navigate(
                                            Routes.DASHBOARD
                                        ) {
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
                                Log.e(
                                    "LoginScreen",
                                    "Login failed",
                                    e
                                )
                                errorMessage =
                                    "Login failed. Please try again."
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Google Sign-In
            OutlinedButton(
                enabled = !isLoading,
                onClick = {
                    errorMessage = null
                    isLoading = true

                    scope.launch {
                        try {
                            val firebaseUser =
                                googleAuthHelper.signInWithGoogle(context)

                            val googleEmail = firebaseUser.email

                            if (googleEmail.isNullOrBlank()) {
                                errorMessage =
                                    "Your Google account has no email address."
                            } else {
                                val existingUser =
                                    authRepo.findGoogleUser(googleEmail)

                                if (existingUser != null) {
                                    // Existing local staff member
                                    navController.navigate(
                                        Routes.DASHBOARD
                                    ) {
                                        popUpTo(Routes.LOGIN) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    // Pass account details to Google registration
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            "googleName",
                                            firebaseUser.displayName.orEmpty()
                                        )

                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            "googleEmail",
                                            googleEmail
                                        )

                                    navController.navigate(
                                        Routes.GOOGLE_REGISTER
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "LoginScreen",
                                "Google sign-in failed",
                                e
                            )
                            errorMessage =
                                "Google sign-in failed or was cancelled. Please try again."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Continue with Google")
            }

            errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                enabled = !isLoading,
                onClick = {
                    navController.navigate(Routes.REGISTER)
                }
            ) {
                Text(
                    text = "Don't have an account? Register",
                    color = PrimaryDark
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MzansiAutoRepairsTheme {
        LoginScreen(
            navController = rememberNavController()
        )
    }
}
