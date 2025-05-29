package com.example.swipebay.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.RequiresApi
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.ktx.Firebase
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import com.example.swipebay.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

//@HiltViewModel // Uncomment if using Hilt
class SellViewModel(application: Application) : AndroidViewModel(application) {
    private val _title       = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _price       = MutableStateFlow("")

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _region = MutableStateFlow("")
    val region: StateFlow<String> = _region.asStateFlow()

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris.asStateFlow()
    fun setImageUris(uris: List<Uri>) {
        _imageUris.value = uris
    }

    val title: StateFlow<String>       = _title.asStateFlow()
    val description: StateFlow<String> = _description.asStateFlow()
    val price: StateFlow<String>       = _price.asStateFlow()
    fun onCategoryChange(new: String)    { _category.value = new }
    fun onRegionChange(new: String)      { _region.value = new }

    fun onTitleChange(new: String)       { _title.value = new }
    fun onDescriptionChange(new: String) { _description.value = new }
    fun onPriceChange(new: String)       { _price.value = new }

    fun listProduct() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.e("SellViewModel", "User not signed in, cannot list product")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrls = mutableListOf<String>()
                val storage = FirebaseStorage.getInstance()
                val userId = currentUser.uid

                for (uri in _imageUris.value) {
                    val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
                    val ref = storage.reference.child("product_images/$userId/$fileName")

                    // Compress the image before uploading
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(getApplication<Application>().contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(getApplication<Application>().contentResolver, uri)
                    }

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // ~1MB target with 80% quality
                    val data = baos.toByteArray()

                    ref.putBytes(data).await()
                    val url = ref.downloadUrl.await().toString()
                    imageUrls.add(url)
                }

                val newProduct = Product(
                    id = "",
                    price = _price.value,
                    description = _description.value,
                    category = _category.value,
                    condition = "",
                    location = _region.value,
                    sellerId = currentUser.uid,
                    timestamp = System.currentTimeMillis(),
                    imageUrls = imageUrls,
                    region = _region.value,
                    title = _title.value
                )

                val docRef = Firebase.firestore.collection("items").add(newProduct).await()
                Firebase.firestore.collection("items")
                    .document(docRef.id)
                    .update("id", docRef.id)

            } catch (e: Exception) {
                Log.e("SellViewModel", "Error uploading product with images", e)
            }
        }
    }
}