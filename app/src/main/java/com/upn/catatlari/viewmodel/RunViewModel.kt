package com.upn.catatlari.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.upn.catatlari.data.local.AppDatabase
import com.upn.catatlari.model.Run
import com.upn.catatlari.repository.RunRepository
import kotlinx.coroutines.launch

class RunViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RunRepository

    private val _runHistory = MutableLiveData<List<Run>>()
    val runHistory: LiveData<List<Run>> = _runHistory

    init {
        val db = AppDatabase.getInstance(application)
        repository = RunRepository(db.runDao())
        loadRuns()
    }

    fun addRun(run: Run) {
        viewModelScope.launch {
            repository.addRun(run)
            loadRuns()
        }
    }

    private fun loadRuns() {
        viewModelScope.launch {
            val result = repository.getAllRuns()
            result.onSuccess { _runHistory.postValue(it) }
        }
    }

    fun updateRun(run: Run) {
        viewModelScope.launch {
            repository.updateRun(run)
            loadRuns()
        }
    }
}