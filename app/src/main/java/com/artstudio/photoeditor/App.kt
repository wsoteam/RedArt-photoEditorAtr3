package com.artstudio.photoeditor

import android.app.Application
import androidx.lifecycle.LifecycleObserver

class App: Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
       // ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    companion object {

        private lateinit var sInstance: App

        fun getInstance(): App {
            return sInstance
        }
    }

}