package com.alvintio.pedulipanganseller.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.alvintio.pedulipanganseller.utils.Const
import com.alvintio.pedulipanganseller.utils.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences): ViewModel() {

    // simplify single invocation of preferences with property params
    fun getUserPreferences(property:String): LiveData<String> {
        return when(property){
            Const.UserPreferences.UserUID.name -> pref.getUserUid().asLiveData()
            Const.UserPreferences.UserToken.name -> pref.getUserToken().asLiveData()
            Const.UserPreferences.UserName.name -> pref.getUserName().asLiveData()
            Const.UserPreferences.UserEmail.name -> pref.getUserEmail().asLiveData()
            Const.UserPreferences.UserLastLogin.name -> pref.getUserLastLogin().asLiveData()
            else -> pref.getUserUid().asLiveData()
        }
    }

    fun setUserPreferences(userToken: String, userUid: String, userName:String, userEmail: String) {
        viewModelScope.launch {
            pref.saveLoginSession(userToken,userUid,userName,userEmail)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.clearLoginSession()
        }
    }


}