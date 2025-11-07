package com.pampang.nav.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.pampang.nav.constants.SharedPrefsConst
import com.pampang.nav.utilities.SharedPrefs
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sharedPrefs: SharedPrefs
) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    suspend fun register(
        email: String,
        password: String,
        username: String,
        role: String
    ): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            val uid = user.uid

            // Update the user's profile with the username
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user.updateProfile(profileUpdates).await()

            val userData = mapOf(
                "uid" to uid,
                "email" to email,
                "username" to username,
                "role" to role,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    suspend fun login(email: String, password: String, role: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("No UID found")

            val userDoc = firestore.collection("users").document(uid).get().await()
            val storedRole = userDoc.getString("role") ?: throw Exception("User role not found")

            if (storedRole != role) {
                throw Exception("Please double-check your credentials and selected role.")
            } else {
                sharedPrefs.save(SharedPrefsConst.SHARED_PREFS_LOGGED_IN_ROLE, role)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getLoggedInRole(): String? =
        sharedPrefs.getString(SharedPrefsConst.SHARED_PREFS_LOGGED_IN_ROLE, "none")

    suspend fun logout() {
        firebaseAuth.signOut()
        sharedPrefs.clearAll()
    }

    suspend fun updateUsername(username: String): Result<Unit> {
        return try {
            _isLoading.postValue(true)
            val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user.updateProfile(profileUpdates).await()
            firestore.collection("users").document(user.uid).update("username", username).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<Unit> {
        return try {
            _isLoading.postValue(true)
            val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }
}
