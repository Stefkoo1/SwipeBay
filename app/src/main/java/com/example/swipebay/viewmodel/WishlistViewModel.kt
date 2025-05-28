package com.example.swipebay.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipebay.data.model.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

data class WishlistItem(val product: Product)

class WishlistViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("wishlist_prefs", Context.MODE_PRIVATE)
    private val db = Firebase.firestore

    // Backing flow of saved product‐IDs
    private val _ids = MutableStateFlow(prefs.getStringSet("wishlist_ids", emptySet())!!)
    /** Exposed as a read-only set of IDs */
    val wishlistIds: StateFlow<Set<String>> = _ids.asStateFlow()

    // Backing flow of full items
    private val _items = MutableStateFlow<List<WishlistItem>>(emptyList())
    /** Exposed wishlist items for your UI */
    val wishlistItems: StateFlow<List<WishlistItem>> = _items.asStateFlow()

    // A simple trigger flow for animations/badges if you want
    private val _wishlistUpdated = MutableStateFlow(false)
    val wishlistUpdated: StateFlow<Boolean> = _wishlistUpdated.asStateFlow()

    init {
        viewModelScope.launch {
            val loaded = _ids.value.mapNotNull { id ->
                try {
                    val snap = db.collection("items").document(id).get().await()
                    snap.toObject(Product::class.java)?.apply { this.id = id }
                } catch (_: Exception) {
                    null
                }
            }
            _items.value = loaded.map { WishlistItem(it) }
        }
    }

    /** Add a product to wishlist and persist */
    fun addToWishlist(product: Product) {
        if (product.id in _ids.value) return
        val newIds = _ids.value + product.id
        _ids.value = newIds
        saveIds(newIds)

        _items.value = _items.value + WishlistItem(product)
        // trigger an “update” pulse if you need it
        _wishlistUpdated.value = true
        viewModelScope.launch {
            // reset after animation
            kotlinx.coroutines.delay(500)
            _wishlistUpdated.value = false
        }
    }

    /** Remove a product and persist */
    fun removeFromWishlist(productId: String) {
        val newIds = _ids.value - productId
        _ids.value = newIds
        saveIds(newIds)

        _items.value = _items.value.filterNot { it.product.id == productId }
    }

    private fun saveIds(newIds: Set<String>) {
        prefs.edit().putStringSet("wishlist_ids", newIds).apply()
    }

    /** Clears everything */
    fun clearWishlist() {
        _ids.value = emptySet()
        saveIds(emptySet())
        _items.value = emptyList()
    }

    /** If you reset the badge animation from UI */
    fun resetWishlistUpdated() {
        _wishlistUpdated.value = false
    }
}
