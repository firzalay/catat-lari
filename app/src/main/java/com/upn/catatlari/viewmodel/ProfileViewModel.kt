package com.upn.catatlari.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.upn.catatlari.data.local.AppDatabase
import com.upn.catatlari.model.User
import com.upn.catatlari.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> = _updateStatus

    init {
        val db = AppDatabase.getInstance(application)
        repository = UserRepository(db.userDao())
    }

    fun updateUser(id: Int, name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.updateUser(id, name, email, password)
            result.onSuccess {
                _updateStatus.postValue(true)
            }
            result.onFailure {
                _updateStatus.postValue(false)
            }
        }
    }

    fun updatePhotoPath(id: Int, photoPath: String?) {
        viewModelScope.launch {
            val result = repository.updatePhotoPath(id, photoPath)
            result.onSuccess {
                _updateStatus.postValue(true)
            }
            result.onFailure {
                _updateStatus.postValue(false)
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }
}
