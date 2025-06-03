package com.example.swipebay.app_ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var minPrice by remember(current) { mutableStateOf(current.minPrice?.toString() ?: "") }
    var maxPrice by remember(current) { mutableStateOf(current.maxPrice?.toString() ?: "") }
    var selectedCats by remember(current) { mutableStateOf(current.categories.toMutableSet()) }
    var selectedConditions by remember(current) { mutableStateOf(current.conditions.toMutableSet()) }
    var selectedRegions by remember(current) { mutableStateOf(current.regions.toMutableSet()) }

    LaunchedEffect(current) {
        minPrice = current.minPrice?.toString() ?: ""
        maxPrice = current.maxPrice?.toString() ?: ""
        selectedCats = current.categories.toMutableSet()
        selectedConditions = current.conditions.toMutableSet()
        selectedRegions = current.regions.toMutableSet()
    }


    val allCategories = listOf("Electronics", "Photography", "Home", "Accessories", "Music", "Fitness")
    val conditionOptions = listOf("New", "Used - Like New", "Used - Good", "Used - Fair")
    val regionOptions = listOf("Vienna", "Salzburg", "Graz", "Innsbruck", "Linz")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            // ─── Überschrift + Reset-Button ───────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter Items", style = MaterialTheme.typography.headlineSmall)
                Row {
                    TextButton(onClick = {
                        onApply(
                            FilterOptions(
                                minPrice = minPrice.toIntOrNull(),
                                maxPrice = maxPrice.toIntOrNull(),
                                categories = selectedCats,
                                conditions = selectedConditions,
                                regions = selectedRegions
                            )
                        )
                        onDismiss()
                    }) {
                        Text("Apply", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        minPrice = ""
                        maxPrice = ""
                        selectedCats.clear()
                        selectedConditions.clear()
                        selectedRegions.clear()
                    }) {
                        Text("Reset", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // ─── Preis‐Eingaben ──────────────────────────────────────────
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

            // ─── Kategorie Auswahl ──────────────────────────────────────
            Text("Categories", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                allCategories.forEach { cat ->
                    FilterChip(
                        selected = cat in selectedCats,
                        onClick = {
                            selectedCats = selectedCats.toMutableSet().apply {
                                if (cat in this) remove(cat) else add(cat)
                            }
                        },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (cat in selectedCats)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (cat in selectedCats)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Zustand Auswahl ────────────────────────────────────────
            Text("Condition", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                conditionOptions.forEach { cond ->
                    FilterChip(
                        selected = cond in selectedConditions,
                        onClick = {
                            selectedConditions = selectedConditions.toMutableSet().apply {
                                if (cond in this) remove(cond) else add(cond)
                            }
                        },
                        label = { Text(cond) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (cond in selectedConditions)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (cond in selectedConditions)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Region Auswahl ──────────────────────────────────────────
            Text("Region", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                regionOptions.forEach { reg ->
                    FilterChip(
                        selected = reg in selectedRegions,
                        onClick = {
                            selectedRegions = selectedRegions.toMutableSet().apply {
                                if (reg in this) remove(reg) else add(reg)
                            }
                        },
                        label = { Text(reg) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (reg in selectedRegions)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (reg in selectedRegions)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ─── Apply Filters Button ───────────────────────────────────
            Button(
                onClick = {
                    onApply(
                        FilterOptions(
                            minPrice = minPrice.toIntOrNull(),
                            maxPrice = maxPrice.toIntOrNull(),
                            categories = selectedCats,
                            conditions = selectedConditions,
                            regions = selectedRegions
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Apply Filters")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
