package com.alyndroid.thirdeyechecklist.ui.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.thirdeyechecklist.data.model.InboxChecklistsModel
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistResponse
import com.alyndroid.thirdeyechecklist.data.rest.RepoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _checklistsResponse = MutableLiveData<InboxChecklistsModel>()
    val checklistsResponse: LiveData<InboxChecklistsModel>
        get() = _checklistsResponse

    fun getUserChecklists(userID: Int) {
        coroutineScope.launch {
            _loading.value = true

            val loginDeferred = RepoService.SNBApi.retrofitService.getUserInboxChecklistAsync(userID)
            try {
                val stringResult = loginDeferred.await()
                _checklistsResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}