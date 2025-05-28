package com.example.swipebay.viewmodel

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log

import androidx.lifecycle.ViewModel
import com.example.swipebay.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SellViewModel : ViewModel() {
    private val _title       = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _price       = MutableStateFlow("")

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _region = MutableStateFlow("")
    val region: StateFlow<String> = _region.asStateFlow()
    private val _tags = MutableStateFlow("")
    val tags: StateFlow<String> = _tags.asStateFlow()

    val title: StateFlow<String>       = _title.asStateFlow()
    val description: StateFlow<String> = _description.asStateFlow()
    val price: StateFlow<String>       = _price.asStateFlow()
    fun onCategoryChange(new: String)    { _category.value = new }
    fun onRegionChange(new: String)      { _region.value = new }

    fun onTitleChange(new: String)       { _title.value = new }
    fun onDescriptionChange(new: String) { _description.value = new }
    fun onPriceChange(new: String)       { _price.value = new }
    fun onTagsChange(new: String) { _tags.value = new }


    fun listProduct() {
        // Ensure user is signed in
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.e("SellViewModel", "User not signed in, cannot list product")
            return
        }

        // Build the Product object
        val newProduct = Product(
            id = "", // Firestore will generate an ID
            price = _price.value,
            description = _description.value,
            category = _category.value,
            condition = "", // or capture from UI if added
            location = _region.value,
            sellerId = currentUser.uid,
            timestamp = System.currentTimeMillis(),
            imageUrls = emptyList(), // TODO: replace with real URLs after upload
            region = _region.value,
            tags = _tags.value.split(","), // or capture from UI if implemented
            title = _title.value
        )

        // Add to Firestore
        Firebase.firestore.collection("items")
            .add(newProduct)
            .addOnSuccessListener { documentReference ->
                // Optionally update the generated ID into the document
                Firebase.firestore.collection("items")
                    .document(documentReference.id)
                    .update("id", documentReference.id)
            }
            .addOnFailureListener { exception ->
                Log.e("SellViewModel", "Error listing product", exception)
            }
    }
}