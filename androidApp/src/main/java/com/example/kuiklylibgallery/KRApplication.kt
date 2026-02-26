package com.example.kuiklylibgallery

import android.app.Application
import com.tencent.mmkv.MMKV

class KRApplication : Application() {

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        
        // 初始化 MMKV
        MMKV.initialize(this)
    }

    companion object {
        lateinit var application: Application
    }
}