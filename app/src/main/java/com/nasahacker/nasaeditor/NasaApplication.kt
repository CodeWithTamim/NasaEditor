package com.nasahacker.nasaeditor

import android.app.Application
import com.tencent.mmkv.MMKV

class NasaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}