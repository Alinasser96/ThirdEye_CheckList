package com.alyndroid.thirdeyechecklist.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserChecklistData(
    val active: String,
    val assign_later: String,
    val availability_num: Int,
    val availability_type: String,
    val checklist_shifted_anthor_due: String,
    val created_at: String,
    val created_by: Int,
    val deleted_by: Int,
    val force_answer: String,
    val holiday_action: Int,
    val id: Int,
    val last_ended_at: String,
    val last_started_at: String,
    val name: String,
    val next_ended_at: String,
    val next_started_at: String,
    val notified_before: String,
    val notify_before_num: Int,
    val notify_before_type: String,
    val repeat: String,
    val repeat_num: String,
    val repeat_type: String,
    val show_last_status: String,
    val start_at: String,
    val updated_at: String,
    val updated_by: String,
    val working_from: String,
    val users : List<User>,
    val working_to: String
) : Parcelable