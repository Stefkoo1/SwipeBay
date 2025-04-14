package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.viewmodel.SwipeViewModel

@Composable
fun ProductDetailScreen(productId: String, viewModel: SwipeViewModel) {
    val product = viewModel.getProductById(productId)
    if (product == null) {
        Text("Product not found", modifier = Modifier.padding(16.dp))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = product.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text(text = product.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = product.price, style = MaterialTheme.typography.bodyLarge)
        Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
    }
}


