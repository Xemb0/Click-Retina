package com.click.retina.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.click.retina.database.models.DataModel
import com.click.retina.network.NetworkChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: DataRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _data = MutableLiveData<DataModel?>()
    val data: LiveData<DataModel?> get() = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchData()
        setupNetworkCallbacks()
    }

    private fun setupNetworkCallbacks() {
        networkChecker.setOnNetworkAvailable { retry() }
        networkChecker.setOnNetworkLost { showError("Network lost") }
    }

    fun fetchData() {
        if (networkChecker.hasValidInternet) {
            _isLoading.postValue(true) // Use postValue for background thread
            viewModelScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        repository.getData()
                    }
                    // Ensure that LiveData updates are on the main thread
                    _data.postValue(result)
                    _error.postValue(null)
                } catch (e: Exception) {
                    // Ensure that LiveData updates are on the main thread
                    _error.postValue(e.message)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        } else {
            // Ensure that LiveData updates are on the main thread
            _error.postValue("No valid internet connection available.")
            _isLoading.postValue(false)
        }
    }


    fun retry() {
        fetchData()
    }

    private fun showError(message: String) {
        _error.value = message
    }
}
