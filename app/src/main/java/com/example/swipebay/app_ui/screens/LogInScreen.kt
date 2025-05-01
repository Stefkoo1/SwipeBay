package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swipebay.viewmodel.AuthViewModel
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var keepSignedIn by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(authState) {
        authState?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                Toast.makeText(context, it.exceptionOrNull()?.message ?: "Unknown error", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLogin) "Login for SwipeBay" else "Sign Up for SwipeBay", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLogin) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = keepSignedIn,
                onCheckedChange = { keepSignedIn = it }
            )
            Text("Keep me signed in")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val prefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit() { putBoolean("keepSignedIn", keepSignedIn) }
                if (isLogin) viewModel.login(email, password)
                else viewModel.signUp(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(if (isLogin) "No account? Sign Up" else "Have an account? Login")
        }
    }
}
