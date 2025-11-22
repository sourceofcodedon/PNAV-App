package com.pampang.nav.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun getStores() {
        _isLoading.postValue(true)

        firestore.collection("stores")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("MainRepository", "Error getting stores: ${e.message}", e)
                    _stores.postValue(emptyList())
                    _isLoading.postValue(false)
                    return@addSnapshotListener
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
    }

    suspend fun addStore(storeName: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?, ownerId: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val storeData = hashMapOf<String, Any?>(
                "store_name" to storeName,
                "store_category" to storeCategory,
                "opening_time" to openingTime,
                "closing_time" to closingTime,
                "owner_id" to ownerId,
                "image" to imageBase64
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

    suspend fun updateStore(storeId: String, storeName: String, storeCategory: String, openingTime: String, closingTime: String, imageBase64: String?): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val storeData = hashMapOf(
                "store_name" to storeName,
                "store_category" to storeCategory,
                "opening_time" to openingTime,
                "closing_time" to closingTime,
                "image" to imageBase64
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
}