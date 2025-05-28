package com.example.swipebay.app_ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swipebay.viewmodel.SwipeViewModel.FilterOptions
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    current: FilterOptions,
    onApply: (FilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    // Local editable state
    var minPrice by remember { mutableStateOf(current.minPrice?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(current.maxPrice?.toString() ?: "") }
    var selectedCats by remember { mutableStateOf(current.categories.toMutableSet()) }

    // TODO: Replace with your real categories list if needed
    val allCategories = listOf("Electronics", "Clothing", "Furniture")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("Filter Items", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Price inputs
            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                label = { Text("Min Price") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Max Price") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("Categories", style = MaterialTheme.typography.labelMedium)
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                allCategories.forEach { cat ->
                    FilterChip(
                        selected = cat in selectedCats,
                        onClick = {
                            if (cat in selectedCats) selectedCats.remove(cat)
                            else selectedCats.add(cat)
                        },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    onApply(
                        FilterOptions(
                            minPrice = minPrice.toIntOrNull(),
                            maxPrice = maxPrice.toIntOrNull(),
                            categories = selectedCats
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }
        }
    }
}

