package com.pampang.nav.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.models.BookmarkModel
import com.pampang.nav.models.StoreModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _stores = MutableLiveData<List<StoreModel>>()
    val stores: LiveData<List<StoreModel>> get() = _stores

    private val _bookmarks = MutableLiveData<List<BookmarkModel>>()
    val bookmarks: LiveData<List<BookmarkModel>> get() = _bookmarks

    fun getStores() {
        _isLoading.postValue(true)
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get().addOnSuccessListener { userDocument ->
                val role = userDocument.getString("role")
                val query = when (role) {
                    "admin" -> firestore.collection("stores")
                    "seller" -> firestore.collection("stores").whereEqualTo("owner_id", userId)
                    else -> firestore.collection("stores").whereEqualTo("status", "approved")
                }
                query.addSnapshotListener(getStoresSnapshotListener())
            }.addOnFailureListener {
                firestore.collection("stores").whereEqualTo("status", "approved")
                    .addSnapshotListener(getStoresSnapshotListener())
            }
        } else {
            firestore.collection("stores").whereEqualTo("status", "approved")
                .addSnapshotListener(getStoresSnapshotListener())
        }
    }

    private fun getStoresSnapshotListener() =
        com.google.firebase.firestore.EventListener<com.google.firebase.firestore.QuerySnapshot> { snapshot, e ->
            if (e != null) {
                Log.e("MainRepository", "Error getting stores: ${e.message}", e)
                _stores.postValue(emptyList())
                _isLoading.postValue(false)
                return@EventListener
            }

            if (snapshot != null) {
                val storeList = snapshot.documents.mapNotNull { document ->
                    document.toObject(StoreModel::class.java)?.apply {
                        id = document.id
                    }
                }
                _stores.postValue(storeList)
            }
            _isLoading.postValue(false)
        }


    suspend fun addStore(
        storeName: String,
        storeNumber: String,
        storeCategory: String,
        openingTime: String,
        closingTime: String,
        imageBase64: String?,
        ownerId: String,
        description: String,
        businessPermitUrl: String?
    ): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val storeData = hashMapOf<String, Any?>(
                "store_name" to storeName,
                "store_number" to storeNumber,
                "store_category" to storeCategory,
                "opening_time" to openingTime,
                "closing_time" to closingTime,
                "owner_id" to ownerId,
                "image" to imageBase64,
                "status" to "pending",
                "description" to description,
                "business_permit_url" to businessPermitUrl
            )

            val querySnapshot = firestore.collection("stores")
                .whereEqualTo("store_category", storeCategory)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                // Add new store
                firestore.collection("stores").add(storeData.filterValues { it != null }).await()
            } else {
                // Update existing store
                val documentId = querySnapshot.documents.first().id
                firestore.collection("stores").document(documentId).update(storeData.filterValues { it != null }).await()
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    suspend fun updateStore(
        storeId: String,
        storeName: String,
        storeNumber: String,
        storeCategory: String,
        openingTime: String,
        closingTime: String,
        imageBase64: String?,
        description: String,
        businessPermitUrl: String?
    ): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val storeData = hashMapOf(
                "store_name" to storeName,
                "store_number" to storeNumber,
                "store_category" to storeCategory,
                "opening_time" to openingTime,
                "closing_time" to closingTime,
                "image" to imageBase64,
                "description" to description,
                "business_permit_url" to businessPermitUrl
            )

            firestore.collection("stores").document(storeId).update(storeData as Map<String, Any>).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    suspend fun deleteStore(storeId: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)
            firestore.collection("stores").document(storeId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun getBookmarks(userId: String) {
        _isLoading.postValue(true)
        firestore.collection("bookmarks")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("MainRepository", "Error getting bookmarks: ${e.message}", e)
                    _bookmarks.postValue(emptyList())
                    _isLoading.postValue(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val bookmarkList = snapshot.documents.mapNotNull { document ->
                        document.toObject(BookmarkModel::class.java)?.apply {
                            id = document.id
                        }
                    }
                    _bookmarks.postValue(bookmarkList)
                }
                _isLoading.postValue(false)
            }
    }

    suspend fun addBookmark(storeId: String, userId: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val bookmarkData = hashMapOf(
                "store_id" to storeId,
                "user_id" to userId
            )

            firestore.collection("bookmarks").add(bookmarkData).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    suspend fun deleteBookmark(bookmarkId: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)
            firestore.collection("bookmarks").document(bookmarkId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }
}
