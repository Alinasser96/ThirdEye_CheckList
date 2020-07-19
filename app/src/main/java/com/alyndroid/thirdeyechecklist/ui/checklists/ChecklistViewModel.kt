package com.alyndroid.thirdeyechecklist.ui.checklists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.BaseResponse
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChecklistViewModel: ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _deleteChecklistResponse = MutableLiveData<BaseResponse>()
    val deleteChecklistResponse: LiveData<BaseResponse>
        get() = _deleteChecklistResponse

    private val _checklistsResponse = MutableLiveData<UserChecklistResponse>()
    val checklistsResponse: LiveData<UserChecklistResponse>
        get() = _checklistsResponse

    fun getUserChecklists(userID: Int) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.getUserChecklistAsync(userID)
            try {
                val stringResult = loginDeferred.await()
                _checklistsResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun deleteChecklist(checklistID: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.deleteChecklistAsync(checklistID)
            try {
                val stringResult = loginDeferred.await()
                _deleteChecklistResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}