package com.example.swipebay.app_ui.screens

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.viewmodel.SwipeViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Spacer

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontStyle


@Composable
fun ProductDetailScreen(productId: String, viewModel: SwipeViewModel, navController: NavController) {
    val product = viewModel.getProductById(productId)
    if (product == null) {
        Text("Product not found", modifier = Modifier.padding(16.dp))
        return
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            if (product.imageUrls.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(product.imageUrls) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            Text(text = product.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = "${product.price}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = product.condition,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }


            Text(
                text = product.region,
                style = MaterialTheme.typography.bodyMedium
            )

            val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(Date(product.timestamp))
            Text(
                text = "Listed on: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
