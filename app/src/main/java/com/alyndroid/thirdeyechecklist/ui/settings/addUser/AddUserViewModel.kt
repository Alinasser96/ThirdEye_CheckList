package com.alyndroid.thirdeyechecklist.ui.settings.addUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.*
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddUserViewModel: ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _addUserLoading = MutableLiveData<Boolean>()
    val addUserLoading: LiveData<Boolean>
        get() = _addUserLoading

    private val _editUserLoading = MutableLiveData<Boolean>()
    val editUserLoading: LiveData<Boolean>
        get() = _editUserLoading

    private val _addUserResponse = MutableLiveData<AddUserResponse>()
    val addUserResponse: LiveData<AddUserResponse>
        get() = _addUserResponse

    private val _editUserResponse = MutableLiveData<AddUserResponse>()
    val editUserResponse: LiveData<AddUserResponse>
        get() = _editUserResponse


    private val _relatedUsersResponse = MutableLiveData<RelatedUsersResponse>()
    val relatedUsersResponse: LiveData<RelatedUsersResponse>
        get() = _relatedUsersResponse

    private val _deleteRelatedUserResponse = MutableLiveData<BaseResponse>()
    val deleteRelatedUserResponse: LiveData<BaseResponse>
        get() = _deleteRelatedUserResponse




    fun addUser(map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _addUserLoading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.addUserAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _addUserResponse.value = stringResult
                _addUserLoading.value = false
            } catch (e: Exception) {
                _addUserLoading.value = false
            }
        }
    }

    fun editUser(userId: Int, map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _addUserLoading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.updateRelatedUserAsync(userId, map)
            try {
                val stringResult = loginDeferred.await()
                _editUserResponse.value = stringResult
                _addUserLoading.value = false
            } catch (e: Exception) {
                _addUserLoading.value = false
            }
        }
    }

    fun getUsers(userId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.getRelatedUsersAsync(userId)
            try {
                val stringResult = loginDeferred.await()
                _relatedUsersResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun deleteUser(taskId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.deleteRelatedUserAsync(taskId)
            try {
                val stringResult = loginDeferred.await()
                _deleteRelatedUserResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}