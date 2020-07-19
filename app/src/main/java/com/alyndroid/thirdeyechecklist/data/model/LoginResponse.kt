package com.alyndroid.thirdeyechecklist.data.model

data class LoginResponse(
    val `data`: LoginData,
    val message: String,
    val success: Boolean
)