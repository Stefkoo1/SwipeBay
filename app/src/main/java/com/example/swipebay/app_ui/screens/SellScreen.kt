package com.example.swipebay.app_ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.R
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

    val categoryOptions = listOf(
        stringResource(id = R.string.electronics_category),
        stringResource(id = R.string.photography_category),
        stringResource(id = R.string.home_category),
        stringResource(id = R.string.accessories_category),
        stringResource(id = R.string.music_category),
        stringResource(id = R.string.fitness_category)
    )

    val regionOptions = listOf(
        "Vienna", "Salzburg", "Graz", "Innsbruck", "Linz"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(id = R.string.sell_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { sellViewModel.onTitleChange(it) },
            label = { Text(stringResource(id = R.string.title_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { sellViewModel.onDescriptionChange(it) },
            label = { Text(stringResource(id = R.string.description_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = price.toString(),
            onValueChange = { sellViewModel.onPriceChange(it) },
            label = { Text(stringResource(id = R.string.price_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Zustand Dropdown
        val conditionOptions = listOf(
            stringResource(id = R.string.condition_new),
            stringResource(id = R.string.condition_used_like_new),
            stringResource(id = R.string.condition_used_good),
            stringResource(id = R.string.condition_used_fair)
        )
        var conditionExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = conditionExpanded,
            onExpandedChange = { conditionExpanded = !conditionExpanded }
        ) {
            OutlinedTextField(
                value = condition,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(id = R.string.condition_label_sell)) },
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

        // Kategorie Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(id = R.string.category_label)) },
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

        // Region Dropdown
        ExposedDropdownMenuBox(
            expanded = regionExpanded,
            onExpandedChange = { regionExpanded = !regionExpanded }
        ) {
            OutlinedTextField(
                value = region,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(id = R.string.region_label_sell)) },
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

        // Bilder auswählen
        val imageUris by sellViewModel.imageUris.collectAsState()
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris: List<Uri> ->
            sellViewModel.setImageUris(uris)
        }
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.choose_images_button))
        }
        LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
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

        // Submit-Button
        Button(
            onClick = {
                sellViewModel.listProduct()
                onProductListed()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.submit_button))
        }
    }
}
