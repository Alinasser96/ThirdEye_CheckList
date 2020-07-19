package com.alyndroid.thirdeyechecklist.data.model

data class UserPagesResponse(
    val `data`: List<UserPagesData>,
    val message: String,
    val success: Boolean
)