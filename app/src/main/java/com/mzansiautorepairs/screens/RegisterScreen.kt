package com.mzansiautorepairs.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mzansiautorepairs.Routes
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.auth.AuthRepository
import com.mzansiautorepairs.auth.AuthResult
import com.mzansiautorepairs.components.MzansiTopBar
import com.mzansiautorepairs.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigate: ((String) -> Unit)? = null
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository(context.applicationContext)
    }

    fun navigateToLogin() {
        if (onNavigate != null) {
            onNavigate(Routes.LOGIN)
        } else {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            MzansiTopBar(
                title = "Create Staff Account",
                onBackClick = { navigateToLogin() }
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
                text = "Join Mzansi Auto Repairs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Create your staff account. An administrator must approve your registration before you can access the system.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    errorMessage = null
                },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = "Toggle confirm password visibility"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible)
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
                        fullName.isBlank() ->
                            errorMessage = "Enter your full name."

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(cleanEmail).matches() ->
                            errorMessage = "Enter a valid email address."

                        phone.filter { it.isDigit() }.length < 10 ->
                            errorMessage = "Enter a valid phone number."

                        password.length < 8 ->
                            errorMessage = "Password must have at least 8 characters."

                        !password.any { it.isLetter() } ||
                                !password.any { it.isDigit() } ->
                            errorMessage = "Password must contain letters and numbers."

                        password != confirmPassword ->
                            errorMessage = "Passwords do not match."

                        else -> {
                            isLoading = true

                            scope.launch {
                                try {
                                    /*
                                     * The registration repository must create
                                     * a Firebase account and save a PENDING
                                     * profile. The role must be assigned by
                                     * an authorised administrator.
                                     */
                                    val result = authRepository.register(
                                        fullName = fullName.trim(),
                                        email = cleanEmail,
                                        phone = phone.trim(),
                                        password = password,
                                        confirmPassword = confirmPassword,
                                        role = UserRole.RECEPTIONIST
                                    )

                                    when (result) {
                                        is AuthResult.Success -> {
                                            successMessage =
                                                "Registration submitted. Your account requires administrator approval."

                                            fullName = ""
                                            email = ""
                                            phone = ""
                                            password = ""
                                            confirmPassword = ""
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
                        text = "Submit Registration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                enabled = !isLoading,
                onClick = { navigateToLogin() }
            ) {
                Text(
                    text = "Already have an account? Sign In",
                    color = PrimaryDark
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    RegisterScreen(
        onNavigate = { route ->
            navController.navigate(route)
        }
    )
}

