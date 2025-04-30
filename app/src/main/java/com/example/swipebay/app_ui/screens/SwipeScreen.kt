package com.example.swipebay.app_ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import kotlinx.coroutines.delay

@Composable
fun SwipeScreen(viewModel: SwipeViewModel, navController: NavController, wishlistViewModel: WishlistViewModel) {
    val products by viewModel.products.collectAsState()
    val showSnackbar = remember { mutableStateOf(false) }

    val cardVisible = remember { mutableStateOf(false) }

    LaunchedEffect(products) {
        cardVisible.value = false
        delay(100)
        if (products.isNotEmpty()) {
            cardVisible.value = true
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search products...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {

        if (products.isNotEmpty()) {
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = fadeIn() + scaleIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                exit = fadeOut()
            ) {
                val product = products.first()
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
                    },
                    isSwipeEnabled = true
                )
            }
        } else {
            Text("Keine Produkte mehr!")
        }
        if (showSnackbar.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = Color(0xFF4CAF50),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {
                    Text(
                        text = "Added to Wishlist!",
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
    }
}
