package com.example.swipebay.app_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.data.model.Product
import kotlin.math.abs

@Composable
fun SwipeCard(
    product: Product,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit,
    isSwipeEnabled: Boolean,
    isWishlistItem: Boolean = false,
    onRemoveFromWishlist: (() -> Unit)? = null    // 👈 new callback
) {
    var offsetX by remember { mutableStateOf(0f) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(product.id) {
        visible = true
        offsetX = 0f
    }

    val likeAlpha = (abs(offsetX.coerceAtMost(0f)) / 300f).coerceIn(0f, 1f)
    val dismissAlpha = (offsetX.coerceAtLeast(0f) / 300f).coerceIn(0f, 1f)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.then(
                if (isSwipeEnabled) Modifier.pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { offsetX = 0f }
                    ) { _, dragAmount ->
                        offsetX += dragAmount
                        if (offsetX > 700) {
                            onSwipeRight(); offsetX = 0f
                        } else if (offsetX < -700) {
                            onSwipeLeft(); offsetX = 0f
                        }
                    }
                } else Modifier
            )
        ) {
            // Background icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Default.Close, "Dismiss",
                        modifier = Modifier
                            .size((dismissAlpha * 100).dp)
                            .graphicsLayer(alpha = dismissAlpha)
                    )
                    Icon(
                        Icons.Default.Favorite, "Like",
                        modifier = Modifier
                            .size((likeAlpha * 100).dp)
                            .graphicsLayer(alpha = likeAlpha)
                    )
                }
            }

            // The card itself
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.98f)
                    .height(500.dp)
                    .padding(8.dp)
                    .graphicsLayer {
                        translationX = offsetX
                        scaleX = 1f - (abs(offsetX) / 3000f).coerceIn(0f, 0.05f)
                        scaleY = scaleX
                    },
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    // Image
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { onClick() }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Text
                    Column(Modifier.padding(16.dp)) {
                        Text(product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(product.price, fontSize = 16.sp)
                        Text(product.description, fontSize = 14.sp)
                    }

                    // If this is a wishlist card, show actions
                    if (isWishlistItem) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                "Message Seller",
                                modifier = Modifier
                                    .clickable { /* implement later */ }
                                    .padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Remove from Wishlist",
                                modifier = Modifier
                                    .clickable {
                                        onRemoveFromWishlist?.invoke()    // 👈 invoke callback
                                    }
                                    .padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
