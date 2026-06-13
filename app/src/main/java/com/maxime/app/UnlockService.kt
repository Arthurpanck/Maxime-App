package com.maxime.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.maxime.app.data.MaximeStore

/**
 * Service en avant-plan qui reste actif en permanence pour écouter le
 * déverrouillage de l'écran (`ACTION_USER_PRESENT`).
 *
 * Cette diffusion système ne peut être reçue que par un receiver enregistré
 * dynamiquement (impossible via le manifeste) : c'est pourquoi un service
 * persistant est nécessaire. À chaque déverrouillage, on vérifie s'il faut
 * afficher la maxime du matin.
 */
class UnlockService : Service() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_USER_PRESENT) {
                verifierEtAfficher()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        creerCanal()
        startForeground(NOTIF_ID, construireNotification())
        registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Si le système tue le service, il sera relancé.
        return START_STICKY
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(receiver) }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun verifierEtAfficher() {
        val store = MaximeStore.get(this)
        val date = TempsUtil.dateDuJour()
        val minutes = TempsUtil.minutesDepuisMinuit()
        if (store.doitAfficher(date, minutes)) {
            store.marquerAffichee(date)
            val display = Intent(this, DisplayActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(display)
        }
    }

    private fun creerCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Maxime du matin",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Permet d'afficher une maxime au réveil."
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(canal)
        }
    }

    private fun construireNotification(): Notification {
        return NotificationCompat.Builder(this, CANAL_ID)
            .setContentTitle("Maxime")
            .setContentText("Prête à t'accompagner chaque matin.")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    companion object {
        private const val CANAL_ID = "maxime_service"
        private const val NOTIF_ID = 1

        /** Démarre le service de façon compatible avec toutes les versions. */
        fun demarrer(context: Context) {
            val intent = Intent(context, UnlockService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
