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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth


class SwipeViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // Tracks the set of disliked product IDs for the current user
    private val _dislikedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val dislikedProductIds: StateFlow<Set<String>> = _dislikedProductIds.asStateFlow()

    // Tracks the currently visible products after filtering and user actions
    private val _visibleProducts = MutableStateFlow<List<Product>>(emptyList())
    val visibleProducts: StateFlow<List<Product>> = _visibleProducts

    internal var lastRemovedProduct: Product? = null


    fun removeProduct(product: Product) {
        Log.d("SwipeViewModel", "removing Top product")
        val current = _visibleProducts.value.toMutableList()
        if (current.removeIf { it.id == product.id }) {
            lastRemovedProduct = product
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

    /**  Combines raw products + filters into a filteredProducts flow, also filters out disliked products */
    val filteredProducts = combine(
        _products,
        _filters,
        dislikedProductIds
    ) { list, fopts, disliked ->
        list.filter { product ->
            val priceInt = product.price
            val category = product.category?.trim()?.lowercase() ?: ""
            val okPrice = (fopts.minPrice == null || priceInt >= fopts.minPrice) &&
                          (fopts.maxPrice == null || priceInt <= fopts.maxPrice)
            val okCat = fopts.categories.isEmpty() || fopts.categories.any { it.trim().lowercase() == category }
            val notDisliked = !disliked.contains(product.id)
            okPrice && okCat && notDisliked
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

        // Listen to disliked products for the current user
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("disliked")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("SwipeViewModel", "Error fetching disliked products", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val ids = snapshot.documents.mapNotNull { it.id }.toSet()
                        _dislikedProductIds.value = ids
                    }
                }
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

    fun dislikeProduct(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore
        db.collection("users")
            .document(userId)
            .collection("disliked")
            .document(product.id)
            .set(
                mapOf(
                    "dislikedAt" to Timestamp.now(),
                    "title" to product.title,
                    "description" to product.description,
                    "price" to product.price,
                    "imageUrls" to product.imageUrls,
                    "category" to product.category,
                    "sellerId" to product.sellerId
                )
            )
            .addOnSuccessListener {
                Log.d("SwipeViewModel", "Disliked product ${product.id}")
            }
            .addOnFailureListener {
                Log.e("SwipeViewModel", "Failed to dislike product ${product.id}", it)
            }
    }
}

