package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swipebay.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import android.content.Context

@Composable
fun SettingsScreen(navController: NavController, authViewModel: AuthViewModel) {
    val isUserSignedIn = FirebaseAuth.getInstance().currentUser != null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )

    {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall
        )

        if (!isUserSignedIn) {
            Button(onClick = { navController.navigate("account") }) {
                Text("Log In / Sign Up")
            }
        } else {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            Button(onClick = {
                coroutineScope.launch {
                    authViewModel.signOut()
                    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("keepSignedIn", false).apply()
                    navController.navigate("account") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }) {
                Text("Sign out")
            }
        }
    }
}
