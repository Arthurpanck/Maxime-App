package com.maxime.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Palette « chaleureuse » inspirée du design demandé ---------------------
val Cream = Color(0xFFF3EEE4)        // fond principal, crème
val CreamSoft = Color(0xFFF7F1E8)    // variante légèrement plus claire
val Terracotta = Color(0xFFC16A43)   // accent principal (boutons, dates)
val TerracottaDark = Color(0xFFA9572F)
val DarkBrown = Color(0xFF3A322A)    // texte principal, brun foncé
val MutedBrown = Color(0xFF8C8378)   // texte secondaire
val Sage = Color(0xFF6A8A5C)         // accent vert (validation)
val CardWhite = Color(0xFFFFFFFF)    // cartes
val NavPill = Color(0xFFE7DEF0)      // pastille de sélection (mauve doux)

private val SchemaChaleureux = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = Sage,
    onSecondary = Color.White,
    background = Cream,
    onBackground = DarkBrown,
    surface = CardWhite,
    onSurface = DarkBrown,
    surfaceVariant = CreamSoft,
    onSurfaceVariant = MutedBrown,
    outline = Color(0xFFD8CDBD),
)

/**
 * Thème de l'application : palette chaleureuse (crème / terracotta / brun),
 * identique en clair et en sombre pour rester fidèle au design choisi.
 */
@Composable
fun MaximeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SchemaChaleureux,
        typography = Typography(),
        content = content
    )
}
