
package com.example.swipebay.app_ui.screens

import coil.compose.AsyncImage

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val imageUrls: List<String> = emptyList()
)

@Composable
fun MyItemsScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var myItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var itemToDelete by remember { mutableStateOf<Item?>(null) }
    // Edit dialog state
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedPrice by remember { mutableStateOf("") }

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
                    Item(
                        documentId = doc.id,
                        title = title,
                        description = description,
                        price = price,
                        imageUrls = imageUrls
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "€${item.price}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(onClick = { itemToDelete = item }) {
                                    Text("Delete")
                                }
                                Button(onClick = {
                                    itemToEdit = item
                                    editedTitle = item.title
                                    editedDescription = item.description
                                    editedPrice = item.price
                                }) {
                                    Text("Edit")
                                }
                            }
                        }
                        if (item.imageUrls.isNotEmpty()) {
                            AsyncImage(
                                model = item.imageUrls.first(),
                                contentDescription = "Item Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(start = 8.dp)
                            )
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
    }
}

