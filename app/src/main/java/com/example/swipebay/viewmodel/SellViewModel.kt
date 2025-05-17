package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SellViewModel : ViewModel() {
    private val _title       = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _price       = MutableStateFlow("")

    val title: StateFlow<String>       = _title.asStateFlow()
    val description: StateFlow<String> = _description.asStateFlow()
    val price: StateFlow<String>       = _price.asStateFlow()

    fun onTitleChange(new: String)       { _title.value = new }
    fun onDescriptionChange(new: String) { _description.value = new }
    fun onPriceChange(new: String)       { _price.value = new }

    fun listProduct() {
        // TODO: call your repository to push the new Product to Firestore/RTDB
    }
}