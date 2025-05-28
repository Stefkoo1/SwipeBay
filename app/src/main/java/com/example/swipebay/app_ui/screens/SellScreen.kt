package com.example.swipebay.app_ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swipebay.viewmodel.SellViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    sellViewModel: SellViewModel = viewModel(),
    onProductListed: () -> Unit
) {
    val title       by sellViewModel.title.collectAsState()
    val description by sellViewModel.description.collectAsState()
    val price       by sellViewModel.price.collectAsState()
    val category    by sellViewModel.category.collectAsState()
    val region      by sellViewModel.region.collectAsState()
    val tags by sellViewModel.tags.collectAsState()
    var categoryExpanded by remember { mutableStateOf(false) }
    var regionExpanded   by remember { mutableStateOf(false) }

    val categoryOptions = listOf("Electronics", "Photography", "Home", "Accessories", "Music", "Fitness")
    val regionOptions   = listOf("Vienna", "Salzburg", "Graz", "Innsbruck", "Linz")


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


        OutlinedTextField(
            value = price,
            onValueChange = { sellViewModel.onPriceChange(it) },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sellViewModel.onCategoryChange(option)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        // ───────── Region Dropdown ─────────
        ExposedDropdownMenuBox(
            expanded = regionExpanded,
            onExpandedChange = { regionExpanded = !regionExpanded }
        ) {
            OutlinedTextField(
                value = region,
                onValueChange = {},
                readOnly = true,
                label = { Text("Region") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = regionExpanded,
                onDismissRequest = { regionExpanded = false }
            ) {
                regionOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sellViewModel.onRegionChange(option)
                            regionExpanded = false
                        }
                    )
                }
            }


        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = tags,
            onValueChange = { sellViewModel.onTagsChange(it) },
            label = { Text("Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

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

