package com.example.swipebay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.swipebay.app_ui.screens.AuthScreen
import com.example.swipebay.app_ui.screens.SwipeScreen
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.app_ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val swipeViewModel: SwipeViewModel = viewModel()
            val navController = rememberNavController()

            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            isLoggedIn = false,
                            onNavigateToAccount = { navController.navigate("account") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToHome = { navController.navigate("home") },
                            onNavigateToChat = { navController.navigate("chat") },
                            OnNavigateToWishlist = { navController.navigate("wishlist") }
                        )
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        viewModel = swipeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun MainActivity.BottomNavBar(
    isLoggedIn: Boolean,
    onNavigateToAccount: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToChat: () -> Unit,
    OnNavigateToWishlist: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToAccount,
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            label = { Text("Account") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToChat,
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("Chat") }
        )
        NavigationBarItem(
            selected = false,
            onClick = OnNavigateToWishlist,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist") },
            label = { Text("Wishlist") }
        )
    }
}
