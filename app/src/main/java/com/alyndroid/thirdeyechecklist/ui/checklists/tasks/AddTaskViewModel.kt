package com.alyndroid.thirdeyechecklist.ui.checklists.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.*
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddTaskViewModel: ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _addTaskLoading = MutableLiveData<Boolean>()
    val addTaskLoading: LiveData<Boolean>
        get() = _addTaskLoading

    private val _addTaskResponse = MutableLiveData<AddTasksResponse>()
    val addTaskResponse: LiveData<AddTasksResponse>
        get() = _addTaskResponse


    private val _userTasksResponse = MutableLiveData<UserTasksReponse>()
    val userTasksResponse: LiveData<UserTasksReponse>
        get() = _userTasksResponse

    private val _deleteTaskResponse = MutableLiveData<BaseResponse>()
    val deleteTaskResponse: LiveData<BaseResponse>
        get() = _deleteTaskResponse


    fun addTask(map: HashMap<Any, Any>) {
        coroutineScope.launch {
            _addTaskLoading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.addTasksAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _addTaskResponse.value = stringResult
                _addTaskLoading.value = false
            } catch (e: Exception) {
                _addTaskLoading.value = false
            }
        }
    }

    fun getUserTasks(pageId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.getUserTasksAsync(pageId)
            try {
                val stringResult = loginDeferred.await()
                _userTasksResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun deleteTask(taskId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.deleteTasksAsync(taskId)
            try {
                val stringResult = loginDeferred.await()
                _deleteTaskResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}