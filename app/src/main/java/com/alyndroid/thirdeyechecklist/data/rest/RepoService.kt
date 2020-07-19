package com.alyndroid.thirdeyechecklist.data.rest

import com.alyndroid.thirdeyechecklist.data.model.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("https://ck.prohussein.com/public/api/")
    .build()

interface RepoService {

    @GET("users")
    fun getAllUsersAsync(): Deferred<AllUsersResponse>

    @POST("login")
    fun loginAsync(@Body map: HashMap<Any, Any>): Deferred<LoginResponse>

    @POST("checklists")
    fun createChecklistAsync(@Body map: HashMap<Any, Any>): Deferred<LoginResponse>

    @POST("pages")
    fun addPagesAsync(@Body map: HashMap<Any, Any>): Deferred<AddPageResponse>

    @POST("tasks")
    fun addTasksAsync(@Body map: HashMap<Any, Any>): Deferred<AddTasksResponse>

    @POST("users")
    fun addUserAsync(@Body map: HashMap<Any, Any>): Deferred<AddUserResponse>

    @POST("send-task-answers")
    fun submitInboxTaskAsync(@Body map: HashMap<Any, Any>): Deferred<BaseResponse>

    @POST("update-send-task-answers/{infoID}")
    fun updateInboxTaskAsync(@Path("infoID") infoID: Int, @Body map: HashMap<Any, Any>): Deferred<BaseResponse>

    @GET("checklist-user/{userID}")
    fun getUserChecklistAsync(@Path("userID") userID: Int): Deferred<UserChecklistResponse>

    @GET("inbox/{userID}")
    fun getUserInboxChecklistAsync(@Path("userID") userID: Int): Deferred<InboxChecklistsModel>

    @POST("update-checklist/{checklistID}")
    fun updateChecklistAsync(@Path("checklistID") checklistID: Int, @Body map: HashMap<Any, Any>): Deferred<BaseResponse>

    @POST("update-user/{userID}")
    fun updateRelatedUserAsync(@Path("userID") userID: Int, @Body map: HashMap<Any, Any>): Deferred<AddUserResponse>

    @GET("pages-by-checklist-id/{checklistID}")
    fun getUserPagesAsync(@Path("checklistID") checklistID: Int): Deferred<UserPagesResponse>

    @GET("tasks-by-page-id/{pageID}")
    fun getUserTasksAsync(@Path("pageID") checklistID: Int): Deferred<UserTasksReponse>

    @GET("related-users/{userID}")
    fun getRelatedUsersAsync(@Path("userID") userID: Int): Deferred<RelatedUsersResponse>

    @GET("info/page/{pageID}/checklist-due/{dueID}")
    fun getAnswerInfoAsync(@Path("pageID") pageID: Int, @Path("dueID") dueID: Int): Deferred<AnswerInfoResponse>

    @DELETE("delete-task/{taskID}")
    fun deleteTasksAsync(@Path("taskID") taskID: Int): Deferred<BaseResponse>

    @DELETE("delete-page/{pageID}")
    fun deletePageAsync(@Path("pageID") pageID: Int): Deferred<BaseResponse>

    @DELETE("delete-checklist/{checklistID}")
    fun deleteChecklistAsync(@Path("checklistID") checklistID: Int): Deferred<BaseResponse>

    @DELETE("delete-user/{userID}")
    fun deleteRelatedUserAsync(@Path("userID") userID: Int): Deferred<BaseResponse>

    object SNBApi{
        val retrofitService: RepoService by lazy {
            retrofit.create(RepoService::class.java)
        }
    }
}