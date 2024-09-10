package com.click.retina.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.click.retina.network.NetworkChecker

class MainViewModelFactory(private val repository: DataRepository, private val networkChecker: NetworkChecker) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository,networkChecker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

