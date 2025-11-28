package com.pampang.nav.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pampang.nav.R
import com.pampang.nav.models.ProfileMenuModel
import com.pampang.nav.models.StoreModel
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

    private val _addBookmarkResult = MutableLiveData<Result<Unit>?>()
    val addBookmarkResult: LiveData<Result<Unit>?> get() = _addBookmarkResult

    private val _deleteBookmarkResult = MutableLiveData<Result<Unit>?>()
    val deleteBookmarkResult: LiveData<Result<Unit>?> get() = _deleteBookmarkResult

    val isLoading = mainRepository.isLoading

    private val _storeList = MediatorLiveData<List<StoreModel>>()
    val storeList: LiveData<List<StoreModel>> get() = _storeList

    init {
        _profileMenuItems.value = listOf(
            ProfileMenuModel(R.string.profile_personal_detail),
            ProfileMenuModel(R.string.profile_navigation_history),
            ProfileMenuModel(R.string.profile_contact_us),
            ProfileMenuModel(R.string.profile_privacy_security),
            ProfileMenuModel(R.string.profile_preferences),
            ProfileMenuModel(R.string.profile_logout),
        )

        fun mergeData() {
            val stores = mainRepository.stores.value ?: return
            val bookmarks = mainRepository.bookmarks.value ?: emptyList()
            val updatedStores = stores.map { store ->
                store.copy(isBookmarked = bookmarks.any { it.storeId == store.id })
            }
            _storeList.value = updatedStores
        }

        _storeList.addSource(mainRepository.stores) { mergeData() }
        _storeList.addSource(mainRepository.bookmarks) { mergeData() }

        getStores()
    }

    fun getStores() {
        mainRepository.getStores()
    }

    fun addStore(storeName: String, storeNumber: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?) {
        viewModelScope.launch {
            val ownerId = FirebaseAuth.getInstance().currentUser?.uid
            if (ownerId == null) {
                _addStoreResult.postValue(Result.failure(Exception("User not logged in")))
                return@launch
            }
            val result = mainRepository.addStore(storeName, storeNumber, storeCategory, openingTime, closingTime, imageBase64, ownerId)
            _addStoreResult.postValue(result)
        }
    }

    fun clearAddStoreResult() {
        _addStoreResult.value = null
    }

    fun updateStore(storeId: String, storeName: String, storeNumber: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?) {
        viewModelScope.launch {
            val result = mainRepository.updateStore(storeId, storeName, storeNumber, storeCategory, openingTime, closingTime, imageBase64)
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

    fun getBookmarks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        mainRepository.getBookmarks(userId)
    }

    fun addBookmark(storeId: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _addBookmarkResult.postValue(Result.failure(Exception("User not logged in")))
                return@launch
            }
            val result = mainRepository.addBookmark(storeId, userId)
            _addBookmarkResult.postValue(result)
        }
    }

    fun clearAddBookmarkResult() {
        _addBookmarkResult.value = null
    }

    fun deleteBookmark(storeId: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val bookmark = mainRepository.bookmarks.value?.find { it.storeId == storeId && it.userId == userId }
            if (bookmark != null) {
                val result = mainRepository.deleteBookmark(bookmark.id)
                _deleteBookmarkResult.postValue(result)
            } else {
                _deleteBookmarkResult.postValue(Result.failure(Exception("Bookmark not found")))
            }
        }
    }

    fun clearDeleteBookmarkResult() {
        _deleteBookmarkResult.value = null
    }
}