package com.alyndroid.thirdeyechecklist.data.model

data class AddUserResponse(
    val `data`: RemoteUserData,
    val message: String,
    val success: Boolean
)