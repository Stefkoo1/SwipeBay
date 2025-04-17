package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swipebay.viewmodel.WishlistViewModel
import com.example.swipebay.app_ui.components.SwipeCard

@Composable
fun WishlistScreen(viewModel: WishlistViewModel) {
    val wishlist by viewModel.wishlistItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Wishlist",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (wishlist.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your wishlist is empty, add some items to it!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(wishlist) { wishlistItem ->
                    SwipeCard(
                        product = wishlistItem.product,
                        onSwipeLeft = {},
                        onSwipeRight = {},
                        onClick = {}
                    )
                }
            }
        }
    }
}