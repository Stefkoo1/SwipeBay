package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swipebay.viewmodel.SellViewModel

@Composable
fun SellScreen(
    sellViewModel: SellViewModel = viewModel(),
    onProductListed: () -> Unit
) {
    val title       by sellViewModel.title.collectAsState()
    val description by sellViewModel.description.collectAsState()
    val price       by sellViewModel.price.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("List an Item for Sale", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { sellViewModel.onTitleChange(it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { sellViewModel.onDescriptionChange(it) },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { sellViewModel.onPriceChange(it) },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                sellViewModel.listProduct()
                onProductListed()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}