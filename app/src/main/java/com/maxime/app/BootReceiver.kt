package com.maxime.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Reprogramme l'alarme du matin après un redémarrage du téléphone (les alarmes
 * `AlarmManager` ne survivent pas au reboot) ou après une mise à jour de l'app.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                MaximeScheduler.programmer(context)
            }
        }
    }
}
