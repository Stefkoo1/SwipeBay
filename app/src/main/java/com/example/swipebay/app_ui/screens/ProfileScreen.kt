package com.example.swipebay.app_ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.swipebay.R
import com.example.swipebay.viewmodel.ProfileViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star


data class Item(
    val documentId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val wishlistedBy: Int = 0,
    val imageUrls: List<String> = emptyList()
)

data class UserInfo(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val region: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val userId: String = ""
)

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = viewModel()

    val myItems by viewModel.items.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var itemToDelete by remember { mutableStateOf<Item?>(null) }
    var itemToMarkSold by remember { mutableStateOf<Item?>(null) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }

    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedPrice by remember { mutableStateOf("") }

    var showEditProfile by remember { mutableStateOf(false) }
    var editedFirstName by remember { mutableStateOf("") }
    var editedLastName by remember { mutableStateOf("") }
    var editedRegion by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }

    var showDislikedDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(it)
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Profile card
        userInfo?.let { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(100.dp)) {
                        if (user.profileImageUrl.isNotEmpty()) {
                            val profilePainter = rememberAsyncImagePainter(model = user.profileImageUrl)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") }
                            ) {
                                AsyncImage(
                                    model = user.profileImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "@${user.username}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = stringResource(R.string.username_label),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = user.region,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = stringResource(R.string.region_label),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${myItems.size}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = stringResource(R.string.profile_my_items),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            showEditProfile = true
                            editedFirstName = user.firstName
                            editedLastName = user.lastName
                            editedRegion = user.region
                            editedBio = user.bio
                        }) {
                            Text(text = stringResource(R.string.edit_button))
                        }
                        Button(onClick = {
                            viewModel.fetchDislikedItems()
                            showDislikedDialog = true
                        }) {
                            Text(text = stringResource(R.string.disliked_button))
                        }
                    }
                }
            }
        }

        // Show loading spinner if uploading image
        if (isUploadingImage) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.profile_my_items),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Button(onClick = { viewModel.fetchItems() }) {
                Text(text = stringResource(R.string.refresh_button))
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f, fill = false)
        ) {
            items(myItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.imageUrls.isNotEmpty()) {
                            val painter = rememberAsyncImagePainter(model = item.imageUrls.first())
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                AsyncImage(
                                    model = item.imageUrls.first(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "€${item.price}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${item.wishlistedBy}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = { viewModel.markItemAsSold(item) }) {
                                    Text(text = stringResource(R.string.sold_button))
                                }
                                Button(onClick = { itemToDelete = item }) {
                                    Text(text = stringResource(R.string.delete_item_title))
                                }
                            }
                        }

                        IconButton(onClick = {
                            itemToEdit = item
                            editedTitle = item.title
                            editedDescription = item.description
                            editedPrice = item.price.toString()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for deletion
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(text = stringResource(R.string.delete_item_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_item_text,
                        item.title
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(item)
                    itemToDelete = null
                }) {
                    Text(text = stringResource(R.string.yes_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(text = stringResource(R.string.cancel_button))
                }
            }
        )
    }

    // Mark as Sold dialog
    itemToMarkSold?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToMarkSold = null },
            title = { Text(text = stringResource(R.string.mark_sold_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.mark_sold_text,
                        item.title
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.markItemAsSold(item)
                    itemToMarkSold = null
                }) {
                    Text(text = stringResource(R.string.yes_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToMarkSold = null }) {
                    Text(text = stringResource(R.string.cancel_button))
                }
            }
        )
    }

    // Edit dialog
    itemToEdit?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToEdit = null },
            title = { Text(text = stringResource(R.string.edit_title_dialog)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text(text = stringResource(R.string.title_label)) }
                    )
                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text(text = stringResource(R.string.description_label)) }
                    )
                    OutlinedTextField(
                        value = editedPrice,
                        onValueChange = { editedPrice = it },
                        label = { Text(text = stringResource(R.string.price_label)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editItem(item, editedTitle, editedDescription, editedPrice.toDouble())
                    itemToEdit = null
                }) {
                    Text(text = stringResource(R.string.edit_save_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToEdit = null }) {
                    Text(text = stringResource(R.string.edit_cancel_button))
                }
            }
        )
    }

    // Edit Profile dialog
    if (showEditProfile && userInfo != null) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text(text = stringResource(R.string.edit_profile_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedFirstName,
                        onValueChange = { editedFirstName = it },
                        label = { Text(text = stringResource(R.string.name_label)) }
                    )
                    OutlinedTextField(
                        value = editedLastName,
                        onValueChange = { editedLastName = it },
                        label = { Text(text = stringResource(R.string.lastname_label)) }
                    )
                    OutlinedTextField(
                        value = editedBio,
                        onValueChange = { editedBio = it },
                        label = { Text(text = stringResource(R.string.bio_label)) }
                    )
                    OutlinedTextField(
                        value = editedRegion,
                        onValueChange = { editedRegion = it },
                        label = { Text(text = stringResource(R.string.region_label)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editProfile(
                        firstName = editedFirstName,
                        lastName = editedLastName,
                        bio = editedBio,
                        region = editedRegion
                    )
                    showEditProfile = false
                }) {
                    Text(text = stringResource(R.string.edit_save_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) {
                    Text(text = stringResource(R.string.cancel_button))
                }
            }
        )
    }

    // Disliked items dialog
    val dislikedItems by viewModel.dislikedItems.collectAsState()
    if (showDislikedDialog) {
        AlertDialog(
            onDismissRequest = { showDislikedDialog = false },
            title = { Text(text = stringResource(R.string.disliked_title)) },
            text = {
                LazyColumn {
                    items(dislikedItems) { item ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = item.title, style = MaterialTheme.typography.titleSmall)
                            Button(onClick = {
                                viewModel.removeDislikedItem(item)
                            }) {
                                Text(text = stringResource(R.string.remove_from_disliked))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDislikedDialog = false }) {
                    Text(text = stringResource(R.string.close_button))
                }
            },
            dismissButton = {}
        )
    }
}
