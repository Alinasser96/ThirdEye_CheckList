package com.alyndroid.thirdeyechecklist.ui.checklists.addChecklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.AllUsersResponse
import com.alyndroid.thirdeyechecklist.data.model.LoginResponse
import com.alyndroid.thirdeyechecklist.data.model.RelatedUsersResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddChecklistViewModel() : ViewModel() {


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _usersResponse = MutableLiveData<RelatedUsersResponse>()
    val usersResponse: LiveData<RelatedUsersResponse>
        get() = _usersResponse

    private val _createChecklistResponse = MutableLiveData<LoginResponse>()
    val createChecklistResponse: LiveData<LoginResponse>
        get() = _createChecklistResponse


    fun createChecklist(map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.createChecklistAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _createChecklistResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun getAllUsers(userId: Int) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.getRelatedUsersAsync(userId)
            try {
                val stringResult = loginDeferred.await()
                _usersResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

}