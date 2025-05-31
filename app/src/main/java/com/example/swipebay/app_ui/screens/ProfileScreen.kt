package com.example.swipebay.app_ui.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.colorspace.WhitePoint
import coil.compose.AsyncImage
import com.example.swipebay.viewmodel.ProfileViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImagePainter

data class Item(
    val documentId: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
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

    // Mark as sold dialog state
    var itemToMarkSold by remember { mutableStateOf<Item?>(null) }

    // Edit dialog state
    var itemToEdit by remember { mutableStateOf<Item?>(null) }

    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedPrice by remember { mutableStateOf("") }

    // Profile edit dialog state
    var showEditProfile by remember { mutableStateOf(false) }
    var editedFirstName by remember { mutableStateOf("") }
    var editedLastName by remember { mutableStateOf("") }
    var editedRegion by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }

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
    } else {
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
                                    Image(
                                        painter = profilePainter,
                                        contentDescription = "Profile Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleMedium)
                        Text(user.bio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("@${user.username}", style = MaterialTheme.typography.titleSmall)
                                Text("Username", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(user.region, style = MaterialTheme.typography.titleSmall)
                                Text("Region", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${myItems.size}", style = MaterialTheme.typography.titleSmall)
                                Text("Items", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                showEditProfile = true
                                editedFirstName = user.firstName
                                editedLastName = user.lastName
                                editedRegion = user.region
                                editedBio = user.bio
                            }
                        ) {
                            Text("Edit Profile")
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
                        text = "My Items:",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Button(onClick = { viewModel.fetchItems() }) {
                    Text("Refresh")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f, fill = false)
            ) {
                items(count = myItems.size) { index ->
                    val item = myItems[index]
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
                                val painterState = painter.state

                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                ) {
                                    if (painterState is AsyncImagePainter.State.Loading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(MaterialTheme.shapes.medium),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    AsyncImage(
                                        model = item.imageUrls.first(),
                                        contentDescription = "Item Image",
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
                                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
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
                                        Text("Sold")
                                    }
                                    Button(onClick = { itemToDelete = item }) {
                                        Text("Delete")
                                    }
                                }
                            }

                            IconButton(onClick = {
                                itemToEdit = item
                                editedTitle = item.title
                                editedDescription = item.description
                                editedPrice = item.price
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Bearbeiten",
                                    tint = MaterialTheme.colorScheme.primary,
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
                title = { Text("Delete Item") },
                text = { Text("Are you sure you want to delete '${item.title}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteItem(item)
                        itemToDelete = null
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Mark as Sold dialog
        itemToMarkSold?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToMarkSold = null },
                title = { Text("Mark as Sold") },
                text = { Text("Are you sure you want to mark '${item.title}' as sold?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.markItemAsSold(item)
                        itemToMarkSold = null
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToMarkSold = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Edit dialog
        itemToEdit?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToEdit = null },
                title = { Text("Edit Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Title") }
                        )
                        OutlinedTextField(
                            value = editedDescription,
                            onValueChange = { editedDescription = it },
                            label = { Text("Description") }
                        )
                        OutlinedTextField(
                            value = editedPrice,
                            onValueChange = { editedPrice = it },
                            label = { Text("Price") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.editItem(item, editedTitle, editedDescription, editedPrice)
                        itemToEdit = null
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToEdit = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Edit Profile dialog
        if (showEditProfile && userInfo != null) {
            AlertDialog(
                onDismissRequest = { showEditProfile = false },
                title = { Text("Edit Profile") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editedFirstName,
                            onValueChange = { editedFirstName = it },
                            label = { Text("First Name") }
                        )
                        OutlinedTextField(
                            value = editedLastName,
                            onValueChange = { editedLastName = it },
                            label = { Text("Last Name") }
                        )
                        OutlinedTextField(
                            value = editedBio,
                            onValueChange = { editedBio = it },
                            label = { Text("Bio") }
                        )
                        OutlinedTextField(
                            value = editedRegion,
                            onValueChange = { editedRegion = it },
                            label = { Text("Region") }
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
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfile = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

