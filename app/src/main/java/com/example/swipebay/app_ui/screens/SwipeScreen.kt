package com.example.swipebay.app_ui.screens

import com.google.firebase.auth.FirebaseAuth

import android.R
import android.R.bool
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swipebay.app_ui.components.FilterSheet
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel,
    navController: NavController,
    wishlistViewModel: WishlistViewModel,
) {

    // 1) Collect the visible products flow and filter out current user's products:
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val allProducts by viewModel.visibleProducts.collectAsState()

    val dislikedIds by viewModel.dislikedProductIds.collectAsState()

    val products by remember(allProducts, currentUserId, dislikedIds) {
        derivedStateOf {
            allProducts.filter {
                it.sellerId != currentUserId && !dislikedIds.contains(it.id)
            }
        }
    }
    LaunchedEffect(products) {
        println("Products: ${products.size}")
    }

    var showFilter by remember { mutableStateOf(false) }
    val showSnackbar = remember { mutableStateOf(false) }
    val cardVisible = remember { mutableStateOf(false) }

    LaunchedEffect(products) {
        cardVisible.value = false
        delay(100)
        if (products.isNotEmpty()) cardVisible.value = true
    }

    var searchQuery by remember { mutableStateOf("") }

    Column {
        // ─── Search + Filter Row ─────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products…") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showFilter = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filters")
            }
        }

        // ─── Swipe Deck ─────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (products.isNotEmpty()) {
                val product = products.first()

                androidx.compose.animation.AnimatedVisibility(
                    visible = cardVisible.value,
                    enter = fadeIn() + scaleIn() + slideInVertically { it },
                    exit = fadeOut()
                ) {
                    SwipeCard(
                        product = product,
                        onSwipeLeft = { swipedProduct ->
                            Log.d("SwipeGesture", "Swiped Left on: ${swipedProduct.title}")
                            wishlistViewModel.addToWishlist(swipedProduct)
                            viewModel.removeTopProduct()
                            showSnackbar.value = true
                            Log.d("TopProduct", "Now showing: ${products.getOrNull(1)?.title}")
                        },
                        onSwipeRight = { swipedProduct ->
                            Log.d("SwipeGesture", "Swiped Right on: ${swipedProduct.title}")
                            viewModel.removeTopProduct()
                            viewModel.dislikeProduct(product)
                            Log.d("TopProduct", "Now showing: ${products.getOrNull(1)?.title}")
                        },
                        onClick = { navController.navigate("productDetail/${product.id}") },
                        isSwipeEnabled = true
                    )
                }

                // Dismiss
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                        .size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            viewModel.removeTopProduct()
                            viewModel.dislikeProduct(product)
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Red,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                }

                // Like
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            wishlistViewModel.addToWishlist(product)
                            viewModel.removeTopProduct()
                            showSnackbar.value = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = Color(0xFFE91E63),
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                }
            } else {
                Text("Keine Produkte mehr!")
            }
        }

        // ─── “Added to Wishlist” Snackbar ───────────────────────────
        if (showSnackbar.value) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.fillMaxWidth(0.9f)
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

        // ─── Undo ↺ ──────────────────────────────────────────────────
        if (viewModel.lastRemovedProduct != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(onClick = { viewModel.undoRemove() }) {
                    Text("Undo")
                }
            }
        }
    }

    // ─── Filter Bottom‐Sheet ─────────────────────────────────────
    if (showFilter) {
        FilterSheet(
            current = viewModel.filters.collectAsState().value,
            onApply = { viewModel.updateFilters(it) },
            onDismiss = { showFilter = false }
        )
    }
}
