package com.alim.callerid.di

import android.app.Application

class CallerID : Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.init(this)
    }
}