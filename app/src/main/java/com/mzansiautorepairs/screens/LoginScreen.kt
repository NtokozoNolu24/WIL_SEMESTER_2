package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.AuthResult
import com.mzansiautorepairs.auth.GoogleAuthHelper
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigate: ((String) -> Unit)? = null,
    onNavigateToGoogleRegister: ((String, String) -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository(context.applicationContext)
    }
    val googleAuthHelper = remember {
        GoogleAuthHelper(context.applicationContext)
    }

    fun navigateToRegister() {
        if (onNavigate != null) {
            onNavigate(Routes.REGISTER)
        } else {
            onNavigateToRegister()
        }
    }

    fun navigateToDashboard() {
        if (onNavigate != null) {
            onNavigate(Routes.DASHBOARD)
        } else {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Staff Login"
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Mzansi Auto Repairs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Sign in to access workshop operations and management.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            successMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                enabled = !isLoading,
                onClick = {
                    errorMessage = null
                    successMessage = null

                    val cleanEmail = email.trim().lowercase()

                    when {
                        cleanEmail.isBlank() || password.isBlank() ->
                            errorMessage = "Enter your email and password."

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(cleanEmail).matches() ->
                            errorMessage = "Enter a valid email address."

                        else -> {
                            isLoading = true

                            scope.launch {
                                try {
                                    val result = authRepository.login(
                                        email = cleanEmail,
                                        password = password
                                    )

                                    when (result) {
                                        is AuthResult.Success -> {
                                            navigateToDashboard()
                                        }

                                        is AuthResult.PendingApproval -> {
                                            errorMessage = result.message
                                        }

                                        is AuthResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                } catch (e: Exception) {
                                    errorMessage =
                                        "Sign-in failed. Please check your connection and try again."
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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

            OutlinedButton(
                enabled = !isLoading,
                onClick = {
                    errorMessage = null
                    successMessage = null
                    isLoading = true

                    scope.launch {
                        try {
                            val googleUser = googleAuthHelper.signInWithGoogle(context)
                            val googleEmail = googleUser.email.orEmpty()
                            val googleName = googleUser.displayName.orEmpty()

                            val existingUser = authRepository.findGoogleUser(googleEmail)
                            if (existingUser != null) {
                                navigateToDashboard()
                            } else {
                                if (onNavigateToGoogleRegister != null) {
                                    onNavigateToGoogleRegister(googleName, googleEmail)
                                } else if (onNavigate != null) {
                                    onNavigate(Routes.GOOGLE_REGISTER)
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Google Sign-In failed."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Sign In with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                enabled = !isLoading,
                onClick = { navigateToRegister() }
            ) {
                Text(
                    text = "Don't have an account? Register",
                    color = PrimaryDark
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    LoginScreen(
        onNavigateToDashboard = {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        },
        onNavigateToRegister = {
            navController.navigate(Routes.REGISTER)
        },
        onNavigateToGoogleRegister = { name, email ->
            navController.currentBackStackEntry?.savedStateHandle?.set("googleName", name)
            navController.currentBackStackEntry?.savedStateHandle?.set("googleEmail", email)
            navController.navigate(Routes.GOOGLE_REGISTER)
        },
        onNavigate = { route ->
            if (route == Routes.DASHBOARD) {
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                navController.navigate(route)
            }
        }
    )
}
