package com.example.swipebay.app_ui.screens


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star


import coil.compose.AsyncImage

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Edit
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
fun MyItemsScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var myItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var itemToDelete by remember { mutableStateOf<Item?>(null) }
    // Mark as sold dialog state
    var itemToMarkSold by remember { mutableStateOf<Item?>(null) }
    // Edit dialog state
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedPrice by remember { mutableStateOf("") }

    var userInfo by remember { mutableStateOf<UserInfo?>(null) }
    var userLoading by remember { mutableStateOf(true) }
    // Profile edit dialog state
    var showEditProfile by remember { mutableStateOf(false) }
    var editedFirstName by remember { mutableStateOf("") }
    var editedLastName by remember { mutableStateOf("") }
    var editedRegion by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    // Image picker/upload state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // Image uploading state
    var isUploadingImage by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            isUploadingImage = true
            val storageRef = FirebaseStorage.getInstance().reference
            val fileRef = storageRef.child("profile_images/${userId}.jpg")
            fileRef.putFile(it)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        db.collection("users").document(userId!!).update("profileImageUrl", downloadUri.toString())
                            .addOnSuccessListener {
                                println("✅ Upload erfolgreich")
                                userInfo = userInfo?.copy(profileImageUrl = downloadUri.toString())
                                isUploadingImage = false
                            }
                            .addOnFailureListener {
                                println("❌ Upload fehlgeschlagen: ${it.message}")

                                it.printStackTrace()
                                isUploadingImage = false
                            }
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    isUploadingImage = false
                }
        }
    }
    // Fetch user info
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val userDoc = db.collection("users").document(userId).get().await()
                userInfo = UserInfo(
                    firstName = userDoc.getString("firstName") ?: "",
                    lastName = userDoc.getString("lastName") ?: "",
                    username = userDoc.getString("username") ?: "",
                    region = userDoc.getString("region") ?: "",
                    email = userDoc.getString("email") ?: "",
                    profileImageUrl = userDoc.getString("profileImageUrl") ?: "",
                    bio = userDoc.getString("bio") ?: "",
                    userId = userDoc.getString("uid") ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                userLoading = false
            }
        } else {
            userLoading = false
        }
    }

    // Fetch items
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val snapshot = db.collection("items")
                    .whereEqualTo("sellerId", userId)
                    .get()
                    .await()
                myItems = snapshot.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: ""
                    val description = doc.getString("description") ?: ""
                    val price = doc.get("price")?.toString() ?: ""
                    val imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList()
                    val wishlistedBy = (doc.getLong("wishlistedBy") ?: 0L).toInt()
                    Item(
                        documentId = doc.id,
                        title = title,
                        description = description,
                        price = price,
                        imageUrls = imageUrls,
                        wishlistedBy = wishlistedBy
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    if (isLoading || userLoading) {
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
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.profileImageUrl.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(end = 16.dp)
                            ) {
                                AsyncImage(
                                    model = user.profileImageUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                )
                                IconButton(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .size(20.dp)
                                        .offset(x = (0).dp, y = (10).dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Photo",
                                        tint = MaterialTheme.colorScheme.primary,

                                    )
                                }
                            }
                        }
                        Column {
                            Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleMedium)
                            Text("@${user.username}", style = MaterialTheme.typography.bodyMedium)
                            Text("Region: ${user.region}", style = MaterialTheme.typography.bodyMedium)
                            Text("Items: ${myItems.size}", style = MaterialTheme.typography.bodyMedium)
                            Text("Bio:${user.bio}", style = MaterialTheme.typography.bodyMedium)

                            Button(
                                onClick = {
                                    showEditProfile = true
                                    editedFirstName = user.firstName
                                    editedLastName = user.lastName
                                    editedRegion = user.region
                                    editedBio = user.bio
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Edit Profile")
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

            LazyColumn(modifier = Modifier
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
                                .height(IntrinsicSize.Min)
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(3f)
                                    .fillMaxHeight()
                            ) {
                                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
                                Text(text = "€${item.price}", style = MaterialTheme.typography.bodyLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Wishlisted",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "${item.wishlistedBy}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { itemToDelete = item },
                                        modifier = Modifier.width(100.dp)
                                    ) {
                                        Text("Delete")
                                    }
                                    Button(
                                        onClick = {
                                            itemToEdit = item
                                            editedTitle = item.title
                                            editedDescription = item.description
                                            editedPrice = item.price
                                        },
                                        modifier = Modifier.width(100.dp)
                                    ) {
                                        Text("Edit")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {itemToMarkSold = item},
                                    modifier = Modifier.width(150.dp)
                                ) {
                                    Text("Mark as Sold")
                                }
                            }
                            if (item.imageUrls.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(start = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = item.imageUrls.first(),
                                        contentDescription = "Item Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                    )
                                }
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
                        db.collection("items").document(item.documentId).delete()
                            .addOnSuccessListener {
                                myItems = myItems.filterNot { it.documentId == item.documentId }
                                itemToDelete = null
                            }
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
                        db.collection("items").document(item.documentId).update("sold", true)
                            .addOnSuccessListener {
                                myItems = myItems.map {
                                    if (it.documentId == item.documentId) it.copy(title = "${it.title} (Sold)") else it
                                }
                                itemToMarkSold = null
                            }
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
                        db.collection("items").document(item.documentId).update(
                            mapOf(
                                "title" to editedTitle,
                                "description" to editedDescription,
                                "price" to editedPrice
                            )
                        ).addOnSuccessListener {
                            myItems = myItems.map {
                                if (it.documentId == item.documentId) it.copy(
                                    title = editedTitle,
                                    description = editedDescription,
                                    price = editedPrice
                                ) else it
                            }
                            itemToEdit = null
                        }
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
        if (showEditProfile && userInfo != null && userId != null) {
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
                        db.collection("users").document(userId).update(
                            mapOf(
                                "firstName" to editedFirstName,
                                "lastName" to editedLastName,
                                "bio" to editedBio,
                                "region" to editedRegion
                            )
                        ).addOnSuccessListener {
                            userInfo = userInfo!!.copy(
                                firstName = editedFirstName,
                                lastName = editedLastName,
                                region = editedRegion
                            )
                            showEditProfile = false
                        }
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

