package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import kotlinx.coroutines.delay


@Composable
fun SwipeScreen(viewModel: SwipeViewModel, navController: NavController, wishlistViewModel: WishlistViewModel) {
    val products by viewModel.products.collectAsState()
    val showSnackbar = remember { mutableStateOf(false) }

    if (showSnackbar.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            Surface(
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Zur Wunschliste hinzugefügt!",
                    modifier = Modifier.padding(12.dp),
                    color = Color.White
                )
            }
        }

        LaunchedEffect(Unit) {
            delay(1500)
            showSnackbar.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        products.firstOrNull()?.let { product ->
            SwipeCard(
                product = product,
                onSwipeLeft = {
                    wishlistViewModel.addToWishlist(product)
                    viewModel.removeTopProduct()

                    showSnackbar.value = true
                },
                onSwipeRight = {
                    viewModel.removeTopProduct()

                },
                onClick = {
                    navController.navigate("productDetail/${product.id}")
                }
            )
        } ?: Text("Keine Produkte mehr!")
    }
}
