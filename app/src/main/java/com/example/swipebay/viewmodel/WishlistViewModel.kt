package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipebay.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class WishlistItem(val product: Product)

class WishlistViewModel : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    private val _wishlistUpdated = MutableStateFlow(false)
    val wishlistUpdated: StateFlow<Boolean> = _wishlistUpdated.asStateFlow()

    fun addToWishlist(product: Product) {
        if (_wishlistItems.value.none { it.product.id == product.id }) {
            _wishlistItems.value = _wishlistItems.value + WishlistItem(product)
            _wishlistUpdated.value = true

            viewModelScope.launch {
                delay(500)
                resetWishlistUpdated()
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        _wishlistItems.value = _wishlistItems.value.filterNot { it.product.id == productId }
    }

    fun clearWishlist() {
        _wishlistItems.value = emptyList()
    }

    fun resetWishlistUpdated() {
        _wishlistUpdated.value = false
    }
}
