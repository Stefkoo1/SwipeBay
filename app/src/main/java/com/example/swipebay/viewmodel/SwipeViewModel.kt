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
import kotlinx.coroutines.launch


class SwipeViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // Tracks the currently visible products after filtering and user actions
    private val _visibleProducts = MutableStateFlow<List<Product>>(emptyList())
    val visibleProducts: StateFlow<List<Product>> = _visibleProducts

    internal var lastRemovedProduct: Product? = null


    fun removeTopProduct() {
        val current = _visibleProducts.value.toMutableList()
        if (current.isNotEmpty()) {
            lastRemovedProduct = current.first()
            current.removeAt(0)
            _visibleProducts.value = current
        }
    }

    fun undoRemove() {
        lastRemovedProduct?.let { product ->
            val current = _visibleProducts.value.toMutableList()
            current.add(0, product)
            _visibleProducts.value = current
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
            val category = product.category?.trim()?.lowercase() ?: ""
            val okPrice = (fopts.minPrice == null || priceInt >= fopts.minPrice) &&
                          (fopts.maxPrice == null || priceInt <= fopts.maxPrice)
            val okCat = fopts.categories.isEmpty() || fopts.categories.any { it.trim().lowercase() == category }
            okPrice && okCat
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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
                Log.d("Firestore", "Fetched ${items.size} items")
                _products.value = items
            }

        // Keep _visibleProducts in sync with filteredProducts
        viewModelScope.launch {
            filteredProducts.collect {
                _visibleProducts.value = it
            }
        }
    }

    /** Call this to apply new filters from the UI */
    fun updateFilters(newFilters: FilterOptions) {
        _filters.value = newFilters
    }
}

