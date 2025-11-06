package com.pampang.nav.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pampang.nav.R
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val application: Application
) : ViewModel() {
    private val _profileMenuItems = MutableLiveData<List<ProfileMenuModel>>()
    val profileMenuItems: LiveData<List<ProfileMenuModel>> get() = _profileMenuItems

    private val _addStoreResult = MutableLiveData<Result<Unit>?>()
    val addStoreResult: LiveData<Result<Unit>?> get() = _addStoreResult

    private val _updateStoreResult = MutableLiveData<Result<Unit>?>()
    val updateStoreResult: LiveData<Result<Unit>?> get() = _updateStoreResult

    private val _deleteStoreResult = MutableLiveData<Result<Unit>?>()
    val deleteStoreResult: LiveData<Result<Unit>?> get() = _deleteStoreResult

    val storeList = mainRepository.stores
    val isLoading = mainRepository.isLoading

    init {
        _profileMenuItems.value = listOf(
            ProfileMenuModel(R.string.profile_personal_detail),
            ProfileMenuModel(R.string.profile_contact_us),
            ProfileMenuModel(R.string.profile_privacy_security),
            ProfileMenuModel(R.string.profile_preferences),
            ProfileMenuModel(R.string.profile_logout),
        )
        getStores()
    }

    fun getStores() {
        mainRepository.getStores()
    }

    fun addStore(storeName: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?) {
        viewModelScope.launch {
            val result = mainRepository.addStore(storeName, storeCategory, openingTime, closingTime, imageBase64)
            _addStoreResult.postValue(result)
        }
    }

    fun clearAddStoreResult() {
        _addStoreResult.value = null
    }

    fun updateStore(storeId: String, storeName: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?) {
        viewModelScope.launch {
            val result = mainRepository.updateStore(storeId, storeName, storeCategory, openingTime, closingTime, imageBase64)
            _updateStoreResult.postValue(result)
        }
    }

    fun clearUpdateStoreResult() {
        _updateStoreResult.value = null
    }

    fun deleteStore(storeId: String) {
        viewModelScope.launch {
            val result = mainRepository.deleteStore(storeId)
            _deleteStoreResult.postValue(result)
        }
    }

    fun clearDeleteStoreResult() {
        _deleteStoreResult.value = null
    }
}