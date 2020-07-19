package com.alyndroid.thirdeyechecklist.data.model

data class AnswerInfoResponse(
    val `data`: List<AnswerInfoData>,
    val message: String,
    val success: Boolean
)