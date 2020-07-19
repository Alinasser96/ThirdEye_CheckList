package com.alyndroid.thirdeyechecklist.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.LoginResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse>
        get() = _response


    fun login(phone:String, password: String) {
        coroutineScope.launch {
            _loading.value = true


            val loginDeferred = RepoService.SNBApi.retrofitService.loginAsync(hashMapOf(Pair("phone_number", phone), Pair("password", password)))
            try {
                val stringResult = loginDeferred.await()
                _response.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}