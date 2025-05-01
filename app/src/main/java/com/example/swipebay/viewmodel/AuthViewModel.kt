package com.example.swipebay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipebay.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = repo.login(email, password)
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = repo.signUp(email, password)
        }
    }

    fun isLoggedIn(): Boolean = repo.isUserLoggedIn()

    suspend fun signOut() {
        repo.signOut()
    }
}