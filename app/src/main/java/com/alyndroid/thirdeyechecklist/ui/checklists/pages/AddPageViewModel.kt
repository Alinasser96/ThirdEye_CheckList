package com.alyndroid.thirdeyechecklist.ui.checklists.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.UserPagesResponse
import com.alyndroid.thirdeyechecklist.data.model.AddPageResponse
import com.alyndroid.thirdeyechecklist.data.model.BaseResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddPageViewModel: ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _addPageLoading = MutableLiveData<Boolean>()
    val addPageLoading: LiveData<Boolean>
        get() = _addPageLoading

    private val _addPageResponse = MutableLiveData<AddPageResponse>()
    val addPageResponse: LiveData<AddPageResponse>
        get() = _addPageResponse


    private val _deletePageResponse = MutableLiveData<BaseResponse>()
    val deletePageResponse: LiveData<BaseResponse>
        get() = _deletePageResponse

    private val _userPagesResponse = MutableLiveData<UserPagesResponse>()
    val userPagesResponse: LiveData<UserPagesResponse>
        get() = _userPagesResponse


    fun addPage(map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _addPageLoading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.addPagesAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _addPageResponse.value = stringResult
                _addPageLoading.value = false
            } catch (e: Exception) {
                _addPageLoading.value = false
            }
        }
    }

    fun getUserPages(checklistID: Int) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.getUserPagesAsync(checklistID)
            try {
                val stringResult = loginDeferred.await()
                _userPagesResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun deletePage(taskId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.deletePageAsync(taskId)
            try {
                val stringResult = loginDeferred.await()
                _deletePageResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}