package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swipebay.data.model.Product
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


class SwipeViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    internal var lastRemovedProduct: Product? = null

    init {
        val db = Firebase.firestore
        db.collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("SwipeViewModel", "Error fetching items", error)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.apply {
                        id = doc.id
                    }
                }
                _products.value = items
            }
    }

    fun removeTopProduct() {
        val current = _products.value.toMutableList()
        if (current.isNotEmpty()) {
            lastRemovedProduct = current.first()
            current.removeAt(0)
            _products.value = current
        }
    }

    fun undoRemove() {
        lastRemovedProduct?.let { product ->
            val current = _products.value.toMutableList()
            current.add(0, product)
            _products.value = current
            lastRemovedProduct = null
        }
    }

    fun getProductById(productId: String): Product? {
        return _products.value.find { it.id == productId }
    }

    /** Defines which filters the user can set */
    data class FilterOptions(
        val minPrice: Int? = null,
        val maxPrice: Int? = null,
        val categories: Set<String> = emptySet()
    )

    /**  Backing state flow for the current filters */
    private val _filters = MutableStateFlow(FilterOptions())
    val filters: StateFlow<FilterOptions> = _filters.asStateFlow()

    /**  Combines raw products + filters into a filteredProducts flow */
    val filteredProducts = combine(
        _products,
        _filters
    ) { list, fopts ->
        list.filter { product ->
            val priceInt = product.price.toIntOrNull() ?: return@filter false
            val okPrice = (fopts.minPrice?.let { priceInt >= it } ?: true)
                    && (fopts.maxPrice?.let { priceInt <= it } ?: true)
            val okCat   = fopts.categories.isEmpty() || product.category in fopts.categories
            okPrice && okCat
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Call this to apply new filters from the UI */
    fun updateFilters(newFilters: FilterOptions) {
        _filters.value = newFilters
    }
}

