package com.alyndroid.thirdeyechecklist.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val checklist_id: Int,
    val name: String,
    val user_id: Int
): Parcelable