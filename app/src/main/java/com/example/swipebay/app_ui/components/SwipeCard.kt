package com.example.swipebay.app_ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import android.media.MediaDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.data.model.Product
import kotlin.math.abs
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SwipeCard(
    product: Product,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    LaunchedEffect(product.id) {
        offsetX = 0f
    }

    val alpha = 1f - (abs(offsetX) / 1000f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), 0) }
            .graphicsLayer(alpha = alpha)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX += dragAmount
                    if (offsetX > 500) {
                        onSwipeRight()
                        offsetX = 0f
                    } else if (offsetX < -500) {
                        onSwipeLeft()
                        offsetX = 0f
                    }
                }
            }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .height(500.dp)
                .padding(8.dp),
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
                    Spacer(modifier = Modifier.height(100.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "Like", fontSize = 16.sp)
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary,

                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.error,

                        )
                        Text(text = "Dismiss", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
