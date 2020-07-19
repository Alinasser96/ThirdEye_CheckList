package com.alyndroid.thirdeyechecklist.data.model

data class UserTasksReponse(
    val `data`: List<UserTasksData>,
    val message: String,
    val success: Boolean
)