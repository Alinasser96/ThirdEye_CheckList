package com.alyndroid.thirdeyechecklist.base

import android.app.Application
import java.util.*


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val locale = Locale("ar")
        Locale.setDefault(locale)
        val conf =
            baseContext.resources.configuration
        conf.locale = locale
        baseContext.resources
            .updateConfiguration(conf, baseContext.resources.displayMetrics)

    }


}