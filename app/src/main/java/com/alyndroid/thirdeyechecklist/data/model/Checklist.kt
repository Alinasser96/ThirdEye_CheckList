package com.alyndroid.thirdeyechecklist.data.model

data class Checklist(
    val active: String,
    val assign_later: String,
    val availability_num: Int,
    val availability_type: String,
    val checklist_shifted_anthor_due: String,
    val created_at: String,
    val created_by: Int,
    val deleted_by: Any,
    val force_answer: String,
    val holiday_action: Int,
    val id: Int,
    val last_ended_at: Any,
    val last_started_at: Any,
    val name: String,
    val next_ended_at: Any,
    val next_started_at: Any,
    val notified_before: String,
    val notify_before_num: Int,
    val notify_before_type: String,
    val repeat: String,
    val repeat_num: Int,
    val repeat_type: String,
    val show_last_status: String,
    val start_at: String,
    val updated_at: String,
    val start_time_inmills: String,
    val updated_by: Any,
    val working_from: String,
    val working_to: String
)