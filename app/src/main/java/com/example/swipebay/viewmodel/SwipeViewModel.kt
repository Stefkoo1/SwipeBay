package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swipebay.data.model.Product
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

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
}
