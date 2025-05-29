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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
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
import com.example.swipebay.app_ui.screens.AuthScreen
import com.example.swipebay.viewmodel.AuthViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val keepSignedIn = prefs.getBoolean("keepSignedIn", false)

        // removed - no longer needed outside
        val startDestination = if (keepSignedIn && FirebaseAuth.getInstance().currentUser != null) "home" else "login"

        // FirebaseAuth listener setup
        val firebaseAuth = FirebaseAuth.getInstance()
        val authState = mutableStateOf(firebaseAuth.currentUser != null)

        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState.value = firebaseAuth.currentUser != null
        }
        firebaseAuth.addAuthStateListener(authListener)

        lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                firebaseAuth.removeAuthStateListener(authListener)
            }
        })

        setContent {
            val swipeViewModel: SwipeViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            val wishlistViewModel: WishlistViewModel = viewModel()
            val navController = rememberNavController()
            var wishList by remember { mutableStateOf(false) }
            val wishlistUpdated by wishlistViewModel.wishlistUpdated.collectAsState()

            // Use authState with remember
            val isUserSignedIn by remember { authState }

            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        if (isUserSignedIn) {
                            BottomNavBar(
                                isLoggedIn = true,
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToHome = { navController.navigate("home") },
                                onNavigateToChat = { navController.navigate("chat") },
                                onNavigateToWishlist = { navController.navigate("wishlist") },
                                onNavigateToSell = { navController.navigate("sell") },
                                navController = navController,
                                wishlistUpdated = wishlistUpdated,
                                onNavigateToMyItems = {navController.navigate("myItems")}
                            )
                        }
                    }
                ) { innerPadding ->
                    if (isUserSignedIn) {
                        AppNavGraph(
                            navController = navController,
                            viewModel = swipeViewModel,
                            modifier = Modifier.padding(innerPadding),
                            wishList = wishList,
                            wishlistViewModel = wishlistViewModel,
                            authViewModel = authViewModel,
                            startDestination = "home",
                            isUserSignedIn = true
                        )
                    } else {
                        AuthScreen(viewModel = authViewModel, navController = navController)
                    }
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
    onNavigateToWishlist: () -> Unit,
    onNavigateToSell: () -> Unit,
    onNavigateToMyItems: () -> Unit,
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
            selected = currentDestination?.route == "sell",
            onClick = onNavigateToSell,
            icon = { Icon(Icons.Default.Add, contentDescription = "Sell") },
            label = { Text("Sell") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "wishlist",
            onClick = onNavigateToWishlist,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist", modifier = Modifier.scale(scale)) },
            label = { Text("Wishlist") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "myItems",
            onClick = onNavigateToMyItems,
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "My Items", modifier = Modifier.scale(scale)) },
            label = { Text("My Items") }
        )
        NavigationBarItem(
            selected = currentDestination?.route == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )


    }
}
