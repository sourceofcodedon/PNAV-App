package com.pampang.nav.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.models.NavigationHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationHistoryViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _historyItems = MutableLiveData<List<NavigationHistoryItem>>()
    val historyItems: LiveData<List<NavigationHistoryItem>> = _historyItems

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadNavigationHistory() {
        if (currentUser != null) {
            db.collection("navigationHistory")
                .whereEqualTo("user_id", currentUser.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val items = documents.toObjects(NavigationHistoryItem::class.java)
                    _historyItems.value = items
                }
                .addOnFailureListener { exception ->
                    Log.e("NavHistoryViewModel", "Error getting documents: ", exception)
                    _error.value = exception.localizedMessage
                }
        } else {
            _error.value = "User not logged in"
        }
    }
}
