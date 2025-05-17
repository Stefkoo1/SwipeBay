package com.example.swipebay.data.model

import kotlin.jvm.JvmOverloads

data class Product @JvmOverloads constructor(
    var id: String = "",
    var price: String = "",
    var description: String = "",
    var category: String = "",
    var condition: String = "", // e.g., "New", "Used - Like New", etc.
    var location: String = "",
    var sellerId: String = "",
    var timestamp: Long = 0L, // Unix timestamp for when the product was listed
    var imageUrls: List<String> = emptyList(),
    var region: String = "",
    var tags: List<String> = emptyList(),
    var title: String = ""
) {
    companion object {
        // your sampleProducts here
    }
}