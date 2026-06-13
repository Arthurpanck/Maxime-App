package com.maxime.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Stockage local de toutes les données de l'application.
 *
 * Tout est conservé dans des SharedPreferences (un simple fichier sur le
 * téléphone) : la liste des maximes au format JSON, ainsi que les réglages
 * (heure de déclenchement, dernière date d'affichage...).
 *
 * Un seul exemplaire partagé est utilisé dans toute l'app (voir [get]).
 */
class MaximeStore private constructor(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("maxime_store", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    private val _maximes = MutableStateFlow(chargerMaximes())
    val maximes: StateFlow<List<Maxime>> = _maximes.asStateFlow()

    // ---------------------------------------------------------------------
    // Liste des maximes
    // ---------------------------------------------------------------------

    private fun chargerMaximes(): List<Maxime> {
        val brut = prefs.getString(CLE_MAXIMES, null)
        if (brut.isNullOrBlank()) return maximesParDefaut()
        return runCatching { json.decodeFromString<List<Maxime>>(brut) }
            .getOrElse { maximesParDefaut() }
    }

    private fun sauvegarder(liste: List<Maxime>) {
        prefs.edit().putString(CLE_MAXIMES, json.encodeToString(liste)).apply()
        _maximes.value = liste
    }

    fun ajouter(texte: String, auteur: String) {
        if (texte.isBlank()) return
        val nouvelle = Maxime(id = System.currentTimeMillis(), texte = texte.trim(), auteur = auteur.trim())
        sauvegarder(_maximes.value + nouvelle)
    }

    fun modifier(id: Long, texte: String, auteur: String) {
        if (texte.isBlank()) return
        sauvegarder(_maximes.value.map {
            if (it.id == id) it.copy(texte = texte.trim(), auteur = auteur.trim()) else it
        })
    }

    fun supprimer(id: Long) {
        sauvegarder(_maximes.value.filterNot { it.id == id })
    }

    /** Renvoie une maxime au hasard (ou null si la liste est vide). */
    fun maximeAuHasard(): Maxime? = _maximes.value.randomOrNull()

    // ---------------------------------------------------------------------
    // Réglages : heure de déclenchement + suivi du dernier affichage
    // ---------------------------------------------------------------------

    /** Heure de déclenchement (0-23). Par défaut 7h. */
    var heureDeclenchement: Int
        get() = prefs.getInt(CLE_HEURE, 7)
        set(value) = prefs.edit().putInt(CLE_HEURE, value.coerceIn(0, 23)).apply()

    /** Minute de déclenchement (0-59). Par défaut 0. */
    var minuteDeclenchement: Int
        get() = prefs.getInt(CLE_MINUTE, 0)
        set(value) = prefs.edit().putInt(CLE_MINUTE, value.coerceIn(0, 59)).apply()

    /** Activer / désactiver l'affichage automatique du matin. */
    var affichageAutoActif: Boolean
        get() = prefs.getBoolean(CLE_AUTO_ACTIF, true)
        set(value) = prefs.edit().putBoolean(CLE_AUTO_ACTIF, value).apply()

    /** Dernière date (format AAAAMMJJ) à laquelle la maxime du matin a été montrée. */
    private var derniereDateAffichee: Int
        get() = prefs.getInt(CLE_DERNIERE_DATE, 0)
        set(value) = prefs.edit().putInt(CLE_DERNIERE_DATE, value).apply()

    /**
     * Détermine s'il faut afficher la maxime maintenant, en fonction de
     * l'heure courante et de la dernière date d'affichage.
     *
     * @param dateDuJour date au format AAAAMMJJ
     * @param minutesDepuisMinuit heure courante exprimée en minutes depuis minuit
     */
    fun doitAfficher(dateDuJour: Int, minutesDepuisMinuit: Int): Boolean {
        if (!affichageAutoActif) return false
        if (derniereDateAffichee == dateDuJour) return false
        val seuil = heureDeclenchement * 60 + minuteDeclenchement
        return minutesDepuisMinuit >= seuil
    }

    /** À appeler une fois la maxime du matin affichée. */
    fun marquerAffichee(dateDuJour: Int) {
        derniereDateAffichee = dateDuJour
    }

    companion object {
        private const val CLE_MAXIMES = "maximes_json"
        private const val CLE_HEURE = "heure_declenchement"
        private const val CLE_MINUTE = "minute_declenchement"
        private const val CLE_AUTO_ACTIF = "affichage_auto_actif"
        private const val CLE_DERNIERE_DATE = "derniere_date_affichee"

        @Volatile
        private var instance: MaximeStore? = null

        fun get(context: Context): MaximeStore =
            instance ?: synchronized(this) {
                instance ?: MaximeStore(context).also { instance = it }
            }

        private fun maximesParDefaut(): List<Maxime> = listOf(
            Maxime(1, "Le bonheur n'est réel que lorsqu'il est partagé.", "Christopher McCandless"),
            Maxime(2, "Ce qui ne me tue pas me rend plus fort.", "Friedrich Nietzsche"),
            Maxime(3, "La vie est ce qui arrive pendant que tu fais d'autres projets.", "John Lennon"),
        )
    }
}
