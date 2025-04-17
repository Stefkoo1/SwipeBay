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
import com.example.swipebay.app_ui.screens.SwipeScreen
import com.example.swipebay.app_ui.screens.WishlistScreen
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: SwipeViewModel,
    wishlistViewModel : WishlistViewModel,
    modifier: Modifier = Modifier,
    wishList: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = "account",
        modifier = modifier
    ) {
        composable("account") {
            AuthScreen(
                onLoginClicked = { _, _ -> },
                onSignUpClicked = { _, _ -> }
            )
        }
        composable("home") {
            SwipeScreen(viewModel = viewModel, navController = navController, wishlistViewModel = wishlistViewModel)
        }
        composable("settings") {
            Text("Settings screen")
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
    }
}
