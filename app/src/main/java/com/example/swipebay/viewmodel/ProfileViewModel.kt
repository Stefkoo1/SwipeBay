package com.example.swipebay.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipebay.app_ui.screens.Item
import com.example.swipebay.app_ui.screens.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

    init {
        fetchUserInfo()
        fetchItems()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            userId?.let {
                try {
                    val userDoc = db.collection("users").document(it).get().await()
                    _userInfo.value = UserInfo(
                        firstName = userDoc.getString("firstName") ?: "",
                        lastName = userDoc.getString("lastName") ?: "",
                        username = userDoc.getString("username") ?: "",
                        region = userDoc.getString("region") ?: "",
                        email = userDoc.getString("email") ?: "",
                        profileImageUrl = userDoc.getString("profileImageUrl") ?: "",
                        bio = userDoc.getString("bio") ?: "",
                        userId = userDoc.getString("uid") ?: ""
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun fetchItems() {
        viewModelScope.launch {
            userId?.let {
                try {
                    val snapshot = db.collection("items")
                        .whereEqualTo("sellerId", it)
                        .get()
                        .await()
                    _items.value = snapshot.documents.mapNotNull { doc ->
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
                } catch (_: Exception) {}
                _isLoading.value = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        userId?.let { uid ->
            _isUploadingImage.value = true
            val storageRef = FirebaseStorage.getInstance().reference
            val fileRef = storageRef.child("profile_images/${uid}.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        db.collection("users").document(uid)
                            .update("profileImageUrl", downloadUri.toString())
                            .addOnSuccessListener {
                                _userInfo.value = _userInfo.value?.copy(profileImageUrl = downloadUri.toString())
                                _isUploadingImage.value = false
                            }
                            .addOnFailureListener {
                                _isUploadingImage.value = false
                            }
                    }
                }
                .addOnFailureListener {
                    _isUploadingImage.value = false
                }
        }
    }

    fun deleteItem(item: Item) {
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("items")
                .document(item.documentId)
                .delete()
                .addOnSuccessListener {
                    _items.value = _items.value.filterNot { it.documentId == item.documentId }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }

    fun markItemAsSold(item: Item) {
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("items")
                .document(item.documentId)
                .update("sold", true)
                .addOnSuccessListener {
                    _items.value = _items.value.map {
                        if (it.documentId == item.documentId) it.copy(title = "${it.title} (Sold)") else it
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }

    fun editItem(item: Item, newTitle: String, newDescription: String, newPrice: String) {
        userId?.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(item.documentId)
                .update(
                    mapOf(
                        "title" to newTitle,
                        "description" to newDescription,
                        "price" to newPrice
                    )
                )
                .addOnSuccessListener {
                    _items.value = _items.value.map {
                        if (it.documentId == item.documentId) {
                            it.copy(title = newTitle, description = newDescription, price = newPrice)
                        } else it
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }

    fun editProfile(firstName: String, lastName: String, bio: String, region: String) {
        userId?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).update(
                mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "bio" to bio,
                    "region" to region
                )
            ).addOnSuccessListener {
                _userInfo.value = _userInfo.value?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    bio = bio,
                    region = region
                )
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }
}