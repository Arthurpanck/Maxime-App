package com.maxime.app

import android.app.Application

class MaximeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Démarre le service d'écoute dès l'ouverture de l'application.
        UnlockService.demarrer(this)
    }
}
