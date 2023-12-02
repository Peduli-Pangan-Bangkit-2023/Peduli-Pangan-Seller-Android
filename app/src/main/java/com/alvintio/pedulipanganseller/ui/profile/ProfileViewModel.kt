package com.alvintio.pedulipanganseller.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alvintio.pedulipanganseller.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private var userDataListener: ListenerRegistration? = null

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun getUserData(uid: String) {
        val userRef: DocumentReference = firestore.collection("users").document(uid)

        userDataListener?.remove()

        userDataListener = userRef.addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null) {
                    _userData.value = user
                } else {
                }
            }
        }
    }


    override fun onCleared() {
        // Hapus listener saat ViewModel dihancurkan
        userDataListener?.remove()
        super.onCleared()
    }
}
