package com.nasahacker.nasaeditor

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object
    {
        lateinit var instance : App
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(instance)
    }
}