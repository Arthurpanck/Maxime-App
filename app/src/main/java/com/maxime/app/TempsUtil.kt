package com.maxime.app

import java.util.Calendar

/** Petits utilitaires de date/heure partagés par le service et l'UI. */
object TempsUtil {

    /** Date du jour au format entier AAAAMMJJ (ex. 20260613). */
    fun dateDuJour(cal: Calendar = Calendar.getInstance()): Int {
        val a = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val j = cal.get(Calendar.DAY_OF_MONTH)
        return a * 10000 + m * 100 + j
    }

    /** Heure courante exprimée en minutes depuis minuit (0 à 1439). */
    fun minutesDepuisMinuit(cal: Calendar = Calendar.getInstance()): Int {
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }
}
