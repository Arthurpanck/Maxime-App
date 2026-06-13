package com.maxime.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Relance le service d'écoute après le redémarrage du téléphone (ou après une
 * mise à jour de l'application), afin que la maxime du matin continue de
 * s'afficher sans avoir à rouvrir l'app manuellement.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                UnlockService.demarrer(context)
            }
        }
    }
}
