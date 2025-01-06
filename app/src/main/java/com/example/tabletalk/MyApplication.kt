package com.example.tabletalk

import android.app.Application
import android.content.Context

class MyApplication: Application() {
    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
    }
}