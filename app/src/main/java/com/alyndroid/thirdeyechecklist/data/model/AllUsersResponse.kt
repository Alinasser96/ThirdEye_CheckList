package com.alyndroid.thirdeyechecklist.data.model

data class AllUsersResponse(
    val `data`: List<AllUsersData>,
    val message: String,
    val success: Boolean
)