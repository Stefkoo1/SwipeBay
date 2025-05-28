package com.example.swipebay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.app_ui.navigation.AppNavGraph
import com.example.swipebay.viewmodel.AuthViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val keepSignedIn = prefs.getBoolean("keepSignedIn", false)

        val isUserSignedIn = FirebaseAuth.getInstance().currentUser != null
        val startDestination = if (keepSignedIn && isUserSignedIn) "home" else "login"

        setContent {
            val swipeViewModel: SwipeViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            val wishlistViewModel: WishlistViewModel = viewModel()
            val navController = rememberNavController()
            var wishList by remember { mutableStateOf(false) }
            val wishlistUpdated by wishlistViewModel.wishlistUpdated.collectAsState()

            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            isLoggedIn = false,
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToHome = { navController.navigate("home") },
                            onNavigateToChat = { navController.navigate("chat") },
                            OnNavigateToWishlist = {
                                wishlistViewModel.resetWishlistUpdated() // ✅ reset trigger
                                navController.navigate("wishlist")
                            },
                            OnNavigateToSell = {navController.navigate("sell")},
                            navController = navController,
                            wishlistUpdated = wishlistUpdated
                        )
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        viewModel = swipeViewModel,
                        modifier = Modifier.padding(innerPadding),
                        wishList = wishList,
                        wishlistViewModel = wishlistViewModel,
                        authViewModel = authViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}

@Composable
private fun MainActivity.BottomNavBar(
    isLoggedIn: Boolean,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToChat: () -> Unit,
    OnNavigateToWishlist: () -> Unit,
    OnNavigateToSell: () -> Unit,
    navController: NavController,
    wishlistUpdated: Boolean
) {
    var targetScale by remember { mutableStateOf(1f) }

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300),
        label = "wishlistScale"
    )

    LaunchedEffect(wishlistUpdated) {
        if (wishlistUpdated) {
            targetScale = 1.3f
            delay(300)
            targetScale = 1f
        }
    }

    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        NavigationBarItem(
            selected = currentDestination?.route == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "chat",
            onClick = onNavigateToChat,
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("Chat") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "sell",
            onClick = OnNavigateToSell,
            icon = { Icon(Icons.Default.Add, contentDescription = "Sell") },
            label = { Text("Sell") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "wishlist",
            onClick = OnNavigateToWishlist,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist", modifier = Modifier.scale(scale)) },
            label = { Text("Wishlist") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )


    }
}
