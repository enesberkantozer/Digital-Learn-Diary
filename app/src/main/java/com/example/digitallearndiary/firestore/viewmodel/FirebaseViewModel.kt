package com.example.digitallearndiary.firestore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.firestore.repository.SyncManager
import kotlinx.coroutines.launch

class FirebaseViewModel(
    private val syncManager: SyncManager
) : ViewModel() {
    fun syncData() {
        viewModelScope.launch {
            syncManager.syncAll()
        }
    }
}

class FirebaseViewModelFactory(private val syncManager: SyncManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirebaseViewModel(syncManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}