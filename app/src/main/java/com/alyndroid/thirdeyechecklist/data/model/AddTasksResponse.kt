package com.alyndroid.thirdeyechecklist.data.model

data class AddTasksResponse(
    val `data`: UserTasksData,
    val message: String,
    val success: Boolean
)