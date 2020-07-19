package com.alyndroid.thirdeyechecklist.data.model

data class UserTasksData(
    val checklist_id: String,
    val comments: String,
    val created_at: String,
    val id: Int,
    val images: String,
    val name: String,
    val page_id: String,
    val total_score: String,
    val type: String,
    val updated_at: String,
    val choices: List<InboxChoice>
)