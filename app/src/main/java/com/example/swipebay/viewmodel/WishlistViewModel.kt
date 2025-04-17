package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swipebay.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WishlistItem(val product: Product)

class WishlistViewModel : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    fun addToWishlist(product: Product) {
        if (_wishlistItems.value.none { it.product.id == product.id }) {
            _wishlistItems.value = _wishlistItems.value + WishlistItem(product)
        }
    }

    fun removeFromWishlist(productId: String) {
        _wishlistItems.value = _wishlistItems.value.filterNot { it.product.id == productId }
    }

    fun clearWishlist() {
        _wishlistItems.value = emptyList()
    }
}
