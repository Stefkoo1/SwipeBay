package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel


@Composable
fun SwipeScreen(viewModel: SwipeViewModel, navController: NavController) {
    val products by viewModel.products.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        products.firstOrNull()?.let { product ->
            SwipeCard(
                product = product,
                onSwipeLeft = {
                    viewModel.removeTopProduct()
                },
                onSwipeRight = {
                    viewModel.removeTopProduct()
                },
                onClick = {
                    navController.navigate("productDetail/${product.id}")
                }
            )
        } ?: Text("Keine Produkte mehr!")
    }
}
