package com.maxime.app.data

import kotlinx.serialization.Serializable

/** Une maxime : un texte, et un auteur optionnel. */
@Serializable
data class Maxime(
    val id: Long,
    val texte: String,
    val auteur: String = ""
)
