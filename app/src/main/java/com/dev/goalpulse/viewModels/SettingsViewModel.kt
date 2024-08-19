package com.dev.goalpulse.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.dev.goalpulse.ResponseState

class SettingsViewModel: ViewModel() {
    private val _accountMutableLiveData = MutableLiveData<ResponseState<FirebaseUser?>>()
    val accountLiveData: LiveData<ResponseState<FirebaseUser?>> = _accountMutableLiveData

    fun setAccount(account: FirebaseUser?) {
        _accountMutableLiveData.value = ResponseState.Loading()
        _accountMutableLiveData.value = ResponseState.Success(account)
    }

    fun loadAccount() {
        _accountMutableLiveData.value = ResponseState.Loading()
    }
}