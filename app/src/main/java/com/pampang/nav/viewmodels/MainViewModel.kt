package com.pampang.nav.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.models.profileMenus
import com.pampang.nav.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val _profileMenuItems = MutableLiveData<List<ProfileMenuModel>>()
    val profileMenuItems: LiveData<List<ProfileMenuModel>> get() = _profileMenuItems

    private val _addStoreResult = MutableLiveData<Result<Unit>?>()
    val addStoreResult: LiveData<Result<Unit>?> get() = _addStoreResult

    val storeList = mainRepository.stores
    val isLoading = mainRepository.isLoading

    init {
        _profileMenuItems.value = profileMenus
    }

    fun getStores() {
        viewModelScope.launch {
            mainRepository.getStores()
        }
    }

    fun addStore(storeName: String, storeCategory: String, openingTime: String, closingTime: String) {
        viewModelScope.launch {
            val result = mainRepository.addStore(storeName, storeCategory, openingTime, closingTime)
            _addStoreResult.postValue(result)
        }
    }

    fun clearAddStoreResult() {
        _addStoreResult.value = null
    }
}