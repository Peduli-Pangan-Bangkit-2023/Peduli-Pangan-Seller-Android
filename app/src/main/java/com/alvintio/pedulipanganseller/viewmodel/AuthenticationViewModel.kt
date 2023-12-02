package com.alvintio.pedulipanganseller.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvintio.pedulipanganseller.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthenticationViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState>
        get() = _loginState

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User>
        get() = _userInfo

    sealed class LoginState {
        object Success : LoginState()
        class Error(val message: String) : LoginState()
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()

                authResult.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()

                val user = User(name, email, authResult.user?.uid ?: "")
                _userInfo.postValue(user)

                firestore.collection("users")
                    .document(authResult.user?.uid ?: "")
                    .set(user)
                    .await()

                _loginState.postValue(LoginState.Success)
            } catch (e: Exception) {
                _loginState.postValue(LoginState.Error(e.message ?: "Registration error"))
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()

                val user = User(
                    authResult.user?.displayName ?: "",
                    authResult.user?.email ?: "",
                    authResult.user?.uid ?: ""
                )
                _userInfo.postValue(user)

                _loginState.postValue(LoginState.Success)
            } catch (e: Exception) {
                _loginState.postValue(LoginState.Error(e.message ?: "Login error"))
            }
        }
    }

}
