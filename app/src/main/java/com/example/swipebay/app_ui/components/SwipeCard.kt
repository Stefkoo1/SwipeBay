package com.example.swipebay.app_ui.components
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Delete
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
    onSwipeLeft: (Product) -> Unit,
    onSwipeRight: (Product) -> Unit,
    onClick: () -> Unit,
    isSwipeEnabled: Boolean,
    isWishlistItem: Boolean = false,
    onRemoveFromWishlist: (() -> Unit)? = null,    // 👈 new callback
    onSellMailToSeller: (() -> Unit)? = null,
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
                        if (offsetX > 500) {
                            onSwipeRight(product); offsetX = 0f
                        } else if (offsetX < -500) {
                            onSwipeLeft(product); offsetX = 0f
                        }
                    }
                } else Modifier
            )
        ) {
            // Background icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWishlistItem) 300.dp else 500.dp)
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
                    .height(if (isWishlistItem) 400.dp else 500.dp)
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
                        val context = LocalContext.current
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Message Seller",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { onSellMailToSeller?.invoke() }
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove from Wishlist",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { onRemoveFromWishlist?.invoke() }
                            )
                        }
                    }
                }
            }
        }
    }
}
