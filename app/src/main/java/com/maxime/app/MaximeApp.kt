package com.maxime.app

import android.app.Application

class MaximeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // S'assure que l'alarme du matin est bien programmée.
        MaximeScheduler.programmer(this)
    }
}
