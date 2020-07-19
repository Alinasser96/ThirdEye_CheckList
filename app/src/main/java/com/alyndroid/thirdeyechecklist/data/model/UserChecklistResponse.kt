package com.alyndroid.thirdeyechecklist.data.model

data class UserChecklistResponse(
    val `data`: List<UserChecklistData>,
    val message: String,
    val success: Boolean
)