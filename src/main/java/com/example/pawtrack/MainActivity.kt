package com.example.pawtrack

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawtrack.ui.theme.PawTrackTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PawTrackTheme {
                PawTrackApp()
            }
        }
    }
}

@Composable
fun PawTrackApp() {

    var currentScreen by rememberSaveable {
        mutableStateOf("login")
    }

    when (currentScreen) {

        "login" -> {
            LoginScreen(
                onLoginSuccess = {
                    currentScreen = "dashboard"
                },
                onGoToSignUp = {
                    currentScreen = "signup"
                }
            )
        }

        "signup" -> {
            SignUpScreen(
                onSignUpSuccess = {
                    currentScreen = "login"
                },
                onGoToLogin = {
                    currentScreen = "login"
                }
            )
        }

        "dashboard" -> {
            DashboardScreen(
                onLogout = {
                    currentScreen = "login"
                }
            )
        }
    }
}

/* ---------------------------------------------------
   LOGIN SCREEN
--------------------------------------------------- */

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToSignUp: () -> Unit
) {

    val context = LocalContext.current

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val preferences = remember {
        context.getSharedPreferences("pawtrack_account", 0)
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🐾",
                fontSize = 60.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PAW TRACK",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Pet Health & Location Monitoring",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = {
                    Text("Email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text("Password")
                },
                singleLine = true,
                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Text(
                            if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {

                    when {

                        email.isBlank() ->
                            errorMessage = "Please enter your email."

                        !Patterns.EMAIL_ADDRESS
                            .matcher(email)
                            .matches() ->
                            errorMessage = "Please enter a valid email."

                        password.isBlank() ->
                            errorMessage = "Please enter your password."

                        !preferences.contains("email") ->
                            errorMessage =
                                "No account found. Please create an account first."

                        email != preferences.getString("email", "") ->
                            errorMessage = "Incorrect email or password."

                        password != preferences.getString("password", "") ->
                            errorMessage = "Incorrect email or password."

                        else -> {
                            errorMessage = ""
                            onLoginSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "LOGIN",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Don't have an account?"
                )

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onGoToSignUp
                ) {
                    Text("Sign Up")
                }
            }
        }
    }
}

/* ---------------------------------------------------
   SIGN UP SCREEN
--------------------------------------------------- */

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {

    val context = LocalContext.current

    var fullName by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val preferences = remember {
        context.getSharedPreferences("pawtrack_account", 0)
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🐾",
                fontSize = 50.sp
            )

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Join Paw Track",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                },
                label = {
                    Text("Full Name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = {
                    Text("Email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text("Password")
                },
                singleLine = true,
                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Text(
                            if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = {
                    Text("Confirm Password")
                },
                singleLine = true,
                visualTransformation =
                    if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                    ) {
                        Text(
                            if (confirmPasswordVisible)
                                "Hide"
                            else
                                "Show"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {

                    when {

                        fullName.isBlank() ->
                            errorMessage =
                                "Please enter your full name."

                        email.isBlank() ->
                            errorMessage =
                                "Please enter your email."

                        !Patterns.EMAIL_ADDRESS
                            .matcher(email)
                            .matches() ->
                            errorMessage =
                                "Please enter a valid email."

                        password.length < 6 ->
                            errorMessage =
                                "Password must be at least 6 characters."

                        confirmPassword.isBlank() ->
                            errorMessage =
                                "Please confirm your password."

                        password != confirmPassword ->
                            errorMessage =
                                "Passwords do not match."

                        else -> {

                            preferences.edit()
                                .putString("fullName", fullName)
                                .putString("email", email)
                                .putString("password", password)
                                .apply()

                            errorMessage = ""

                            onSignUpSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                Text(
                    text = "CREATE ACCOUNT",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Login")
            }
        }
    }
}

/* ---------------------------------------------------
   DASHBOARD SCREEN
--------------------------------------------------- */

@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    val preferences = remember {
        context.getSharedPreferences("pawtrack_account", 0)
    }

    val fullName =
        preferences.getString("fullName", "Pet Owner")
            ?: "Pet Owner"

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Hello, $fullName! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Welcome to Paw Track",
                        fontSize = 14.sp
                    )
                }

                TextButton(
                    onClick = onLogout
                ) {
                    Text("Logout")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pet Overview",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "🌡 Temperature",
                value = "28.5 °C",
                description = "Normal"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "💧 Humidity",
                value = "65%",
                description = "Current humidity"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "📍 Location",
                value = "GPS Active",
                description = "Location data available"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "⚠️ Health Status",
                value = "Normal",
                description = "No active alerts"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "🕐 Last Updated",
                value = "Just now",
                description = "Latest available reading"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Paw Track Monitoring",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your pet's live sensor and GPS data will appear here once the IoT device and Firebase are connected."
                    )
                }
            }
        }
    }
}

/* ---------------------------------------------------
   INFORMATION CARD
--------------------------------------------------- */

@Composable
fun InfoCard(
    title: String,
    value: String,
    description: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = description,
                fontSize = 13.sp
            )
        }
    }
}