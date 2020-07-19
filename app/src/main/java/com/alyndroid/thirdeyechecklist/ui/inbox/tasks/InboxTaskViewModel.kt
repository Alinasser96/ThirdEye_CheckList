package com.alyndroid.thirdeyechecklist.ui.inbox.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.*
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InboxTaskViewModel: ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _userTasksResponse = MutableLiveData<UserTasksReponse>()
    val userTasksResponse: LiveData<UserTasksReponse>
        get() = _userTasksResponse

    private val _answerInfoResponse = MutableLiveData<AnswerInfoResponse>()
    val answerInfoResponse: LiveData<AnswerInfoResponse>
        get() = _answerInfoResponse


    private val _submitLoading = MutableLiveData<Boolean>()
    val submitLoading: LiveData<Boolean>
        get() = _submitLoading

    private val _submitTasksResponse = MutableLiveData<BaseResponse>()
    val submitTasksResponse: LiveData<BaseResponse>
        get() = _submitTasksResponse

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

    fun getAnswerInfo(pageId: Int, dueId: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.getAnswerInfoAsync(pageId, dueId)
            try {
                val stringResult = loginDeferred.await()
                _answerInfoResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun submitAnswers(answers: HashMap<Any, Any>) {
        coroutineScope.launch {
            _submitLoading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.submitInboxTaskAsync(answers)
            try {
                val stringResult = loginDeferred.await()
                _submitTasksResponse.value = stringResult
                _submitLoading.value = false
            } catch (e: Exception) {
                _submitLoading.value = false
            }
        }
    }

    fun updateAnswers(dueId: Int, answers: HashMap<Any, Any>) {
        coroutineScope.launch {
            _submitLoading.value = true
            val loginDeferred = RepoService.SNBApi.retrofitService.updateInboxTaskAsync(dueId, answers)
            try {
                val stringResult = loginDeferred.await()
                _submitTasksResponse.value = stringResult
                _submitLoading.value = false
            } catch (e: Exception) {
                _submitLoading.value = false
            }
        }
    }
}