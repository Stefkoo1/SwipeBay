package com.example.swipebay.app_ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
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
    val condition   by sellViewModel.condition.collectAsState()
    val category    by sellViewModel.category.collectAsState()
    val region      by sellViewModel.region.collectAsState()
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
            value = price.toString(),
            onValueChange = {
                val parsed = it
                if (true) sellViewModel.onPriceChange(parsed)
            },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        val conditionOptions = listOf("New", "Used - Like New", "Used - Good", "Used - Fair")
        var conditionExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = conditionExpanded,
            onExpandedChange = { conditionExpanded = !conditionExpanded }
        ) {
            OutlinedTextField(
                value = condition,
                onValueChange = {},
                readOnly = true,
                label = { Text("Condition") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = conditionExpanded,
                onDismissRequest = { conditionExpanded = false }
            ) {
                conditionOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sellViewModel.onConditionChange(option)
                            conditionExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

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

        val context = LocalContext.current
        val imageUris by sellViewModel.imageUris.collectAsState()
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            sellViewModel.setImageUris(uris)
        }
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Images")
        }
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(100.dp)
                )
            }
        }
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
