package com.alyndroid.thirdeyechecklist.data.model

data class AddPageResponse(
    val `data`: UserPagesData,
    val message: String,
    val success: Boolean
)