package com.maxime.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maxime.app.data.MaximeStore

/**
 * Déclenché par l'alarme quotidienne (voir [MaximeScheduler]).
 *
 * À l'heure choisie, on pose une notification « maxime du matin ». Elle attend
 * tranquillement : au déverrouillage suivant, l'utilisateur la voit et la tape
 * pour ouvrir l'écran plein écran. Cette approche ne demande qu'une seule
 * permission (les notifications) et aucun service permanent.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val store = MaximeStore.get(context)

        if (store.affichageAutoActif) {
            posterNotification(context, store)
        }

        // Reprogramme pour le lendemain.
        MaximeScheduler.programmer(context)
    }

    private fun posterNotification(context: Context, store: MaximeStore) {
        creerCanal(context)

        // L'écran plein écran s'ouvre quand on tape la notification.
        val ouvrir = Intent(context, DisplayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pi = PendingIntent.getActivity(
            context,
            0,
            ouvrir,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val apercu = store.maximeAuHasard()?.texte ?: "Ouvre l'application pour ajouter ta première maxime."

        val notification = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Ta maxime du matin ☀")
            .setContentText(apercu)
            .setStyle(NotificationCompat.BigTextStyle().bigText(apercu))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            runCatching {
                NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
            }
        }
    }

    private fun creerCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Maxime du matin",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "La maxime à découvrir chaque matin."
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
    }

    companion object {
        const val ACTION_MAXIME_MATIN = "com.maxime.app.MAXIME_MATIN"
        private const val CANAL_ID = "maxime_matin"
        private const val NOTIF_ID = 42
    }
}
