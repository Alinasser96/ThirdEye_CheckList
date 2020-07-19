package com.alyndroid.thirdeyechecklist.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        forceRTLIfSupported()
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    public fun change() {
        val locale: Locale
        locale = Locale("ar")
        val config = Configuration(this!!.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)

        this.resources.updateConfiguration(
            config,
            this.resources.displayMetrics
        )
    }
}