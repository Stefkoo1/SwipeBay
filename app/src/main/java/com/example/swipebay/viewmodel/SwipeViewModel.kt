package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swipebay.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SwipeViewModel : ViewModel() {

    private val _products = MutableStateFlow(sampleProducts)
    val products = _products.asStateFlow()

    fun removeTopProduct() {
        val current = _products.value.toMutableList()
        if (current.isNotEmpty()) {
            current.removeAt(0)
            _products.value = current
        }
    }
    fun getProductById(productId: String): Product? {
        return _products.value.find { it.id == productId }
    }

    companion object {
        val sampleProducts = listOf(
            Product(
                id = "1",
                title = "Vintage Kamera",
                price = "49,90 €",
                description = "Funktioniert einwandfrei, mit Ledertasche.",
                imageUrl = "https://picsum.photos/id/1011/400/300"
            ),
            Product(
                id = "2",
                title = "Gaming Maus",
                price = "19,99 €",
                description = "RGB und super präzise. Kaum benutzt.",
                imageUrl = "https://picsum.photos/id/1005/400/300"
            ),
            Product(
                id = "3",
                title = "Bluetooth Kopfhörer",
                price = "59,00 €",
                description = "Noise-Cancelling und faltbar.",
                imageUrl = "https://picsum.photos/id/1027/400/300"
            ),
            Product(
                id = "4",
                title = "Laptop Tasche",
                price = "14,99 €",
                description = "Für 15'' Geräte. Neuwertig.",
                imageUrl = "https://picsum.photos/id/1031/400/300"
            ),
            Product(
                id = "5",
                title = "Retro Wecker",
                price = "9,90 €",
                description = "Mechanisch mit lautem Klingelton.",
                imageUrl = "https://picsum.photos/id/1037/400/300"
            ),
            Product(
                id = "6",
                title = "Monitor 24 Zoll",
                price = "79,99 €",
                description = "Full HD, HDMI + VGA, top Zustand.",
                imageUrl = "https://picsum.photos/id/1041/400/300"
            ),
            Product(
                id = "7",
                title = "Tischlampe",
                price = "12,50 €",
                description = "Warmweißes Licht, drehbar.",
                imageUrl = "https://picsum.photos/id/1052/400/300"
            ),
            Product(
                id = "8",
                title = "Gebrauchte Gitarre",
                price = "99,00 €",
                description = "Akustisch, guter Klang, mit Tasche.",
                imageUrl = "https://picsum.photos/id/1062/400/300"
            ),
            Product(
                id = "9",
                title = "Wireless Tastatur",
                price = "22,00 €",
                description = "Ultra-slim, Akku hält lange.",
                imageUrl = "https://picsum.photos/id/1069/400/300"
            ),
            Product(
                id = "10",
                title = "Fitness Tracker",
                price = "39,90 €",
                description = "Misst Schritte, Puls und Schlaf.",
                imageUrl = "https://picsum.photos/id/1074/400/300"
            )
        )
    }
}
