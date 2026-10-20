package com.orangeboston.vipcard

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class VipCardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
