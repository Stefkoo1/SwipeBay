package com.example.swipebay.app_ui.screens

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.R
import com.example.swipebay.viewmodel.SwipeViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontStyle

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: SwipeViewModel,
    navController: NavController
) {
    val product = viewModel.getProductById(productId)
    if (product == null) {
        Text(stringResource(id = R.string.product_not_found), modifier = Modifier.padding(16.dp))
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
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
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
            Text(
                text = stringResource(id = R.string.product_price_format, product.price),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = getLocalizedCategory(product.category),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = getLocalizedCondition(product.condition),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = getLocalizedRegion(product.region),
                style = MaterialTheme.typography.bodyMedium
            )

            val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(Date(product.timestamp))
            Text(
                text = stringResource(id = R.string.listed_on_format, formattedDate),
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
@Composable
fun getLocalizedCondition(condition: String): String {
    Log.d("Product Detail Screen", condition.lowercase())
    return when (condition.lowercase()) {
        "new" -> stringResource(id = R.string.condition_new)
        "used - like new" -> stringResource(id = R.string.condition_used_like_new)
        "used - good" -> stringResource(id = R.string.condition_used_good)
        "used - fair" -> stringResource(id = R.string.condition_used_fair)
        "neu" -> stringResource(id = R.string.condition_new)
        "gebraucht - wie neu" -> stringResource(id = R.string.condition_used_like_new)
        "gebraucht - gut" -> stringResource(id = R.string.condition_used_good)
        "gebraucht - mäßig" -> stringResource(id = R.string.condition_used_fair)


        else -> condition // fallback to raw value if unknown
    }
}
@Composable
fun getLocalizedRegion(region: String): String {
    return when (region.lowercase()) {
        "vienna" -> stringResource(id = R.string.region_Vienna)


        else -> region // fallback to raw value if unknown
    }
}
@Composable
fun getLocalizedCategory(category: String) : String {
    return when (category.lowercase()) {
        "electronics" -> stringResource(id = R.string.electronics_category)
        "elektronik" -> stringResource(id = R.string.electronics_category)
        "photography" -> stringResource(id = R.string.photography_category)
        "fotografie" -> stringResource(id = R.string.photography_category)
        "home" -> stringResource(id = R.string.home_category)
        "haushalt" -> stringResource(id = R.string.home_category)
        "accessories" -> stringResource(id = R.string.accessories_category)
        "zubehör" -> stringResource(id = R.string.accessories_category)
        "musik" -> stringResource(id = R.string.music_category)
        "music" -> stringResource(id = R.string.music_category)

        else -> category
    }
}
