package com.example.swipebay.app_ui.navigation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.swipebay.app_ui.screens.AuthScreen
import com.example.swipebay.app_ui.screens.ProductDetailScreen
import com.example.swipebay.app_ui.screens.SellScreen
import com.example.swipebay.app_ui.screens.SettingsScreen
import com.example.swipebay.app_ui.screens.SwipeScreen
import com.example.swipebay.app_ui.screens.WishlistScreen
import com.example.swipebay.viewmodel.AuthViewModel
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: SwipeViewModel,
    authViewModel: AuthViewModel,
    wishlistViewModel: WishlistViewModel,
    modifier: Modifier = Modifier,
    wishList: Boolean,
    startDestination: String,
    isUserSignedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            if (!isUserSignedIn) {
                AuthScreen(viewModel = authViewModel, navController = navController)
            } else {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("home") {
            SwipeScreen(viewModel = viewModel, navController = navController, wishlistViewModel = wishlistViewModel)
        }
        composable("settings") {
            SettingsScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("chat") {
            Text("Chat screen")
        }
        composable("wishlist")
            {
                WishlistScreen(viewModel =  wishlistViewModel)
        }
        composable(
            "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(productId = productId, viewModel = viewModel, navController = navController)
        }
        composable("sell") {
            SellScreen(onProductListed = { navController.popBackStack() })
        }
    }
}
