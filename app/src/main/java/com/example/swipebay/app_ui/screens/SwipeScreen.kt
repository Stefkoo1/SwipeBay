package com.example.swipebay.app_ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swipebay.R
import com.example.swipebay.app_ui.components.FilterSheet
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel
import com.example.swipebay.viewmodel.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel,
    navController: NavController,
    wishlistViewModel: WishlistViewModel
) {
    // 1) Alle Produkte (aus visibleProducts) und dislikedIds sammeln
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

    // State‐Variablen
    var showFilter by remember { mutableStateOf(false) }
    val showSnackbar = remember { mutableStateOf(false) }
    val cardVisible = remember { mutableStateOf(false) }

    // Sobald sich products ändert, Karte kurz ausblenden und neu einblenden
    LaunchedEffect(products) {
        cardVisible.value = false
        delay(100)
        if (products.isNotEmpty()) cardVisible.value = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.swipe_topbar_title)) },
                actions = {
                    IconButton(onClick = { showFilter = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(id = R.string.filter_open_desc),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ─── Swipe-Deck ────────────────────────────────────────────────
                if (products.isNotEmpty()) {
                    val currentProduct = products.firstOrNull()
                    if (cardVisible.value && currentProduct != null) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn() + slideInVertically { it / 4 },
                            exit = fadeOut()
                        ) {
                            SwipeCard(
                                product = currentProduct,
                                onSwipeLeft = { swipedProduct ->
                                    Log.d("SwipeGesture", "Swiped Left on: ${swipedProduct.title}")
                                    wishlistViewModel.addToWishlist(currentProduct)
                                    viewModel.removeProduct(currentProduct, wasDisliked = true)
                                    viewModel.dislikeProduct(currentProduct)
                                    showSnackbar.value = true
                                },
                                onSwipeRight = { swipedProduct ->
                                    Log.d("SwipeGesture", "Swiped Right on: ${swipedProduct.title}")
                                    viewModel.removeProduct(currentProduct, wasDisliked = false)
                                    viewModel.dislikeProduct(currentProduct)
                                },
                                onClick = { navController.navigate("productDetail/${currentProduct.id}") },
                                isSwipeEnabled = true
                            )
                        }

                        // ─── „Dismiss“-Button unten links ───────────────────────────────
                        Box(
                            modifier = Modifier
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
                                    viewModel.removeProduct(currentProduct, wasDisliked = true)
                                    viewModel.dislikeProduct(currentProduct)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.swipe_dismiss_desc),
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(24.dp)
                                )
                            }
                        }

                        // ─── „Like“-Button unten rechts ─────────────────────────────────
                        Box(
                            modifier = Modifier
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
                                    wishlistViewModel.addToWishlist(currentProduct)
                                    viewModel.dislikeProduct(currentProduct)
                                    viewModel.removeProduct(currentProduct, wasDisliked = false)
                                    showSnackbar.value = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = stringResource(id = R.string.swipe_like_desc),
                                    tint = Color(0xFFE91E63),
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Wenn keine Produkte mehr vorhanden sind
                    if (!cardVisible.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                        )
                    } else {
                        Text(
                            stringResource(id = R.string.no_more_products),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // ─── „Added to Wishlist“-Snackbar ───────────────────────────────────
                if (showSnackbar.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Surface(
                            color = Color(0xFF4CAF50),
                            shape = MaterialTheme.shapes.small,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.added_to_wishlist),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    LaunchedEffect(Unit) {
                        delay(1500)
                        showSnackbar.value = false
                    }
                }

                // ─── „Undo“-Button unten zentriert ───────────────────────────────────
                if (viewModel.lastRemovedProduct != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Button(onClick = { viewModel.undoRemove() }) {
                            Text(stringResource(id = R.string.undo_button))
                        }
                    }
                }
            }

            // ─── Filter Bottom-Sheet ─────────────────────────────────────────────
            if (showFilter) {
                FilterSheet(
                    current = viewModel.filters.collectAsState().value,
                    onApply = { viewModel.updateFilters(it) },
                    onDismiss = { showFilter = false }
                )
            }
        }
    )
}
