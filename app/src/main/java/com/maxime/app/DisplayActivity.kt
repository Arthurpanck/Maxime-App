package com.maxime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxime.app.data.MaximeStore
import com.maxime.app.ui.Cream
import com.maxime.app.ui.DarkBrown
import com.maxime.app.ui.MaximeTheme
import com.maxime.app.ui.Terracotta

/**
 * Écran plein écran, fond crème et texte brun en serif italique, qui affiche
 * une maxime au réveil. Un simple appui n'importe où referme l'écran.
 */
class DisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val store = MaximeStore.get(this)
        val maxime = store.maximeAuHasard()

        setContent {
            MaximeTheme {
                val texte = remember { maxime?.texte ?: "Ajoute ta première maxime dans l'application." }
                val auteur = remember { maxime?.auteur ?: "" }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Cream)
                        .clickable { finish() }
                        .padding(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "« $texte »",
                            color = DarkBrown,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 28.sp,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                        if (auteur.isNotBlank()) {
                            Text(
                                text = "— $auteur",
                                color = Terracotta,
                                fontFamily = FontFamily.Serif,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
