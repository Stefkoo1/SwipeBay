package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipebay.data.model.Product
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SwipeViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _dislikedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val dislikedProductIds: StateFlow<Set<String>> = _dislikedProductIds.asStateFlow()

    private val _visibleProducts = MutableStateFlow<List<Product>>(emptyList())
    val visibleProducts: StateFlow<List<Product>> = _visibleProducts

    internal var lastRemovedProduct: Product? = null

    fun removeProduct(product: Product, wasDisliked: Boolean) {
        if (wasDisliked) {
            lastRemovedProduct = product
            _dislikedProductIds.value = _dislikedProductIds.value + product.id
        }
    }

    fun undoRemove() {
        lastRemovedProduct?.let { product ->
            _dislikedProductIds.value = _dislikedProductIds.value - product.id
            lastRemovedProduct = null
        }
    }

    fun getProductById(productId: String): Product? {
        return _products.value.find { it.id == productId }
    }

    /**
     * Definiert, welche Filter der Nutzer setzen kann:
     *  • minPrice / maxPrice
     *  • categories (z.B. Electronics, Home, …)
     *  • conditions (z.B. New, Used – Like New, …)
     *  • regions (z.B. Vienna, Salzburg, …)
     */
    data class FilterOptions(
        val minPrice: Int? = null,
        val maxPrice: Int? = null,
        val categories: Set<String> = emptySet(),
        val conditions: Set<String> = emptySet(),
        val regions: Set<String> = emptySet()
    )

    // Backing-Flow für die aktuellen Filter-Werte
    private val _filters = MutableStateFlow(FilterOptions())
    val filters: StateFlow<FilterOptions> = _filters.asStateFlow()

    /**
     * Kombiniert Roh-Produkte (_products) + _filters + dislikedProductIds
     * und liefert nur jene Produkte, die **alle** Kriterien erfüllen.
     */
    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _filters,
        dislikedProductIds
    ) { list, fopts, disliked ->
        list.filter { product ->
            // 1) Preis-Check
            val priceInt = product.price.toInt()
            val okPrice = (fopts.minPrice == null || priceInt >= fopts.minPrice) &&
                    (fopts.maxPrice == null || priceInt <= fopts.maxPrice)

            // 2) Kategorie-Check (falls Filter gesetzt)
            val category = product.category.trim()
            val okCat = fopts.categories.isEmpty() || fopts.categories.contains(category)

            // 3) Zustand-Check (falls Filter gesetzt)
            val okCondition = fopts.conditions.isEmpty() || fopts.conditions.contains(product.condition)

            // 4) Region-Check (falls Filter gesetzt)
            val okRegion = fopts.regions.isEmpty() || fopts.regions.contains(product.region)

            // 5) Nicht bereits “disliked”
            val notDisliked = !disliked.contains(product.id)

            okPrice && okCat && okCondition && okRegion && notDisliked
        }
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        // Firestore-Listener: Alle Produkte (items) in _products laden
        val db = Firebase.firestore
        db.collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.apply { id = doc.id }
                }
                _products.value = items
            }

        // Firestore-Listener: Disliked-Produkte für aktuellen User
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("disliked")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val ids = snapshot.documents.mapNotNull { it.id }.toSet()
                        _dislikedProductIds.value = ids
                    }
                }
        }

        viewModelScope.launch {
            filteredProducts.collect { filteredList ->
                _visibleProducts.value = filteredList
            }
        }
    }

    /** Vom UI aufgerufene Funktion: Setze neue Filter-Werte */
    fun updateFilters(newFilters: FilterOptions) {
        _filters.value = newFilters
    }

    /**
     * Markiere ein Produkt als “disliked” (füge es in die Firestore-Subcollection „disliked“ ein)
     */
    fun dislikeProduct(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore
        db.collection("users")
            .document(userId)
            .collection("disliked")
            .document(product.id)
            .set(
                mapOf(
                    "dislikedAt"  to Timestamp.now(),
                    "title"       to product.title,
                    "description" to product.description,
                    "price"       to product.price.toDouble(),
                    "imageUrls"   to product.imageUrls,
                    "category"    to product.category,
                    "condition"   to product.condition,
                    "region"      to product.region,
                    "sellerId"    to product.sellerId
                )
            )
            .addOnSuccessListener { /* optional: Erfolg loggen */ }
            .addOnFailureListener { /* optional: Fehler loggen */ }
    }
}
