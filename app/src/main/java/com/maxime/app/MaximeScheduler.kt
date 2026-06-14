package com.maxime.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.maxime.app.data.MaximeStore
import java.util.Calendar

/**
 * Planifie l'alarme quotidienne qui, à l'heure choisie, fera apparaître la
 * notification « maxime du matin ».
 *
 * On utilise une alarme INEXACTE (`set`), qui ne demande AUCUNE permission
 * spéciale et réveille l'appareil au bon moment. L'alarme est reprogrammée
 * pour le lendemain à chaque déclenchement, au démarrage de l'app, après un
 * changement de réglage et après un redémarrage du téléphone.
 */
object MaximeScheduler {

    private const val REQUEST_CODE = 1001

    /** (Re)programme l'alarme du matin, ou l'annule si l'option est désactivée. */
    fun programmer(context: Context) {
        val store = MaximeStore.get(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context)

        if (!store.affichageAutoActif) {
            alarmManager.cancel(pi)
            return
        }

        val prochain = prochainDeclenchement(store.heureDeclenchement, store.minuteDeclenchement)

        // On veut une alarme EXACTE qui se déclenche même quand le téléphone
        // est en veille (mode Doze) ; sinon le système la reporte jusqu'à la
        // prochaine sortie de veille (typiquement à l'ouverture de l'app),
        // d'où le bug « la notif n'arrive qu'à l'ouverture ».
        val peutExacte = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (peutExacte) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prochain, pi)
        } else {
            // Repli si la permission d'alarme exacte n'est pas accordée.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prochain, pi)
        }
    }

    fun annuler(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_MAXIME_MATIN
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Renvoie l'instant (millis) de la prochaine occurrence de heure:minute. */
    private fun prochainDeclenchement(heure: Int, minute: Int): Long {
        val maintenant = Calendar.getInstance()
        val cible = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, heure)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cible.timeInMillis <= maintenant.timeInMillis) {
            cible.add(Calendar.DAY_OF_MONTH, 1)
        }
        return cible.timeInMillis
    }
}
