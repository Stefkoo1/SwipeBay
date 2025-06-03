package com.example.swipebay.app_ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.swipebay.R
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.WishlistViewModel

@Composable
fun WishlistScreen(viewModel: WishlistViewModel) {
    val wishlist by viewModel.wishlistItems.collectAsState()
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.wishlist_title),
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))

        if (wishlist.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.wishlist_empty_text))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(wishlist) { item ->
                    // Precompute subject and body outside the non-Composable lambda:
                    val subject = stringResource(
                        id = R.string.message_seller_subject_format,
                        item.product.title,
                        item.product.price
                    )
                    val body = stringResource(id = R.string.message_seller_body)

                    SwipeCard(
                        product = item.product,
                        onSwipeLeft = {},
                        onSwipeRight = {},
                        onClick = {},
                        isSwipeEnabled = false,
                        isWishlistItem = true,
                        onRemoveFromWishlist = {
                            viewModel.removeFromWishlist(item.product.id)
                        },
                        onSellMailToSeller = {
                            viewModel.getSellerEmail(item.product.sellerId) { email ->
                                if (email != null) {
                                    val uri = Uri.parse(
                                        "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                                    )
                                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                                    context.startActivity(Intent.createChooser(intent, null))
                                } else {
                                    Log.e("EmailError", "E-Mail konnte nicht geladen werden.")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
