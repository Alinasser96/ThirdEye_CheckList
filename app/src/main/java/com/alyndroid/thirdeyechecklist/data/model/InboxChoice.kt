package com.alyndroid.thirdeyechecklist.data.model

data class InboxChoice(
    val  checklist_due_id: Int,
    val active: Int,
    val checklist_id: Int,
    val choice_score: String,
    val choice_title: String,
    val created_at: String,
    val id: Int,
    val page_id: Int,
    val task_id: Int,
    val updated_at: String
)