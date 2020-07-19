package com.alyndroid.thirdeyechecklist.ui.checklists.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.AllUsersResponse
import com.alyndroid.thirdeyechecklist.data.model.BaseResponse
import com.alyndroid.thirdeyechecklist.data.model.LoginResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditChecklistViewModel : ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _usersResponse = MutableLiveData<AllUsersResponse>()
    val usersResponse: LiveData<AllUsersResponse>
        get() = _usersResponse

    private val _updateChecklistResponse = MutableLiveData<BaseResponse>()
    val updateChecklistResponse: LiveData<BaseResponse>
        get() = _updateChecklistResponse


    fun updateChecklist(checklistID: Int, map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.updateChecklistAsync(checklistID, map)
            try {
                val stringResult = loginDeferred.await()
                _updateChecklistResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun getAllUsers() {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.getAllUsersAsync()
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