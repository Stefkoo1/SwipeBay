package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swipebay.app_ui.components.SwipeCard
import com.example.swipebay.viewmodel.SwipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SwipeScreen(viewModel: SwipeViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        products.firstOrNull()?.let { product ->
            SwipeCard(
                product = product,
                onSwiped = { viewModel.removeTopProduct() }
            )
        } ?: Text("Keine Produkte mehr!")
    }
}




