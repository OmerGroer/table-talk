package com.example.tabletalk

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        myContext = applicationContext
    }

    companion object {
        private var myContext: Context? = null

        val context: Context
            get() = myContext  ?: throw Exception("Context not found")
    }
}

