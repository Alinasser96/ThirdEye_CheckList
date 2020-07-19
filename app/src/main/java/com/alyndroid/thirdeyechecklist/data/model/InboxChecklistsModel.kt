package com.alyndroid.thirdeyechecklist.data.model

data class InboxChecklistsModel(
    val `data`: List<InboxChecklistsData>,
    val message: String,
    val success: Boolean
)