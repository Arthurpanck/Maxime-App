package com.maxime.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SchemaClair = lightColorScheme(
    primary = Color(0xFF111111),
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
)

/**
 * Thème volontairement minimal : fond blanc, texte noir, pas de couleurs vives.
 * On garde le même rendu en mode sombre pour rester fidèle à l'idée d'origine
 * (interface blanche et épurée).
 */
@Composable
fun MaximeTheme(content: @Composable () -> Unit) {
    @Suppress("UNUSED_VARIABLE")
    val sombre = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = SchemaClair,
        typography = Typography(),
        content = content
    )
}
