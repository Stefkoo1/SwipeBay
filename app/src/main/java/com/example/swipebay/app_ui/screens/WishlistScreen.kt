package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.WishlistViewModel

@Composable
fun WishlistScreen(viewModel: WishlistViewModel) {
    val wishlist by viewModel.wishlistItems.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Wishlist",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))

        if (wishlist.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your wishlist is empty, add some items to it!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(wishlist) { item ->
                    SwipeCard(
                        product = item.product,
                        onSwipeLeft = {},
                        onSwipeRight = {},
                        onClick = {},
                        isSwipeEnabled = false,
                        isWishlistItem = true,
                        onRemoveFromWishlist = {
                            viewModel.removeFromWishlist(item.product.id)
                        }
                    )
                }
            }
        }
    }
}
