package com.alyndroid.thirdeyechecklist.data.model

data class InboxChecklistsData(
    val availability_num: Int,
    val availability_type: String,
    val checklist: Checklist,
    val checklist_id: Int,
    val created_at: String,
    val due_in: String,
    val end_in: String,
    val id: Int,
    val type: String,
    val updated_at: String
)