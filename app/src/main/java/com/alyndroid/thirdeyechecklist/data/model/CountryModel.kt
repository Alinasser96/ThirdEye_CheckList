package com.alyndroid.thirdeyechecklist.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryModel(
    val id: String,
    val code: String,
    val name_ar: String,
    val name_en: String
): Parcelable