package com.upn.catatlari.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.upn.catatlari.data.local.AppDatabase
import com.upn.catatlari.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    init {
        val db = AppDatabase.getInstance(application)
        repository = UserRepository(db.userDao())
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Semua field wajib diisi")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = RegisterState.Error("Format email tidak valid")
            return
        }
        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Password minimal 6 karakter")
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            val result = repository.register(name, email, password)
            _registerState.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Registrasi gagal")
            }
        }
    }
}

sealed class RegisterState {
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}