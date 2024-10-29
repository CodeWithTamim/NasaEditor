package com.nasahacker.nasaeditor

import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV

class NasaApplication : Application() {
    companion object
    {
        lateinit var instance : NasaApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}