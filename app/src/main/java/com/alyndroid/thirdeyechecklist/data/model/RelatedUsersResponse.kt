package com.alyndroid.thirdeyechecklist.data.model

data class RelatedUsersResponse(
    val `data`: List<RemoteUserData>,
    val message: String,
    val success: Boolean
)