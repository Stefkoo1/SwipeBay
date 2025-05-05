package com.example.swipebay.app_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    isWishlistItem: Boolean = false
) {
    var offsetX by remember { mutableStateOf(0f) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(product.id) {
        visible = true
    }

    LaunchedEffect(product.id) {
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
                        onDragEnd = {
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX += dragAmount
                        if (offsetX > 700) {
                            onSwipeRight()
                            offsetX = 0f
                        } else if (offsetX < -700) {
                            onSwipeLeft()
                            offsetX = 0f
                        }
                    }
                } else Modifier
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {


                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier
                            .size((dismissAlpha * 100).dp)
                            .graphicsLayer(alpha = dismissAlpha),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        modifier = Modifier
                            .size((likeAlpha * 100).dp)
                            .graphicsLayer(alpha = likeAlpha),
                        tint = MaterialTheme.colorScheme.primary

                    )
                }
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.98f)
                    .height(500.dp)
                    .padding(8.dp)
                    .graphicsLayer {
                        translationX = offsetX

                        scaleX = 1f - (abs(offsetX) / 3000f).coerceIn(0f, 0.05f)
                        scaleY = 1f - (abs(offsetX) / 3000f).coerceIn(0f, 0.05f)
                    },
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { onClick() }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = product.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = product.price, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = product.description, fontSize = 14.sp)

                    }
                    if (isWishlistItem) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Message Seller",
                                modifier = Modifier
                                    .clickable { /* TODO: Implement message seller functionality */ }
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Remove from Wishlist",
                                modifier = Modifier
                                    .clickable { /* TODO: Implement remove from wishlist functionality */ }
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
