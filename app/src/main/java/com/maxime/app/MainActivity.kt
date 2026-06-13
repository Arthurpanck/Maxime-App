package com.maxime.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.maxime.app.data.Maxime
import com.maxime.app.data.MaximeStore
import com.maxime.app.ui.MaximeTheme

class MainActivity : ComponentActivity() {

    private lateinit var store: MaximeStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = MaximeStore.get(this)

        // S'assure que l'alarme du matin est programmée.
        MaximeScheduler.programmer(this)

        setContent {
            MaximeTheme {
                EcranPrincipal(store = store)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EcranPrincipal(store: MaximeStore) {
    val maximes by store.maximes.collectAsState()

    var dialogEdition by remember { mutableStateOf<EtatEdition?>(null) }
    var ouvrirReglages by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Maxime", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { ouvrirReglages = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Réglages")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogEdition = EtatEdition() },
                containerColor = Color(0xFF111111),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        if (maximes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aucune maxime pour l'instant.\nAppuie sur + pour en ajouter une.",
                    color = Color(0xFF777777),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(maximes, key = { it.id }) { maxime ->
                    LigneMaxime(
                        maxime = maxime,
                        onModifier = { dialogEdition = EtatEdition(maxime) },
                        onSupprimer = { store.supprimer(maxime.id) }
                    )
                }
            }
        }
    }

    dialogEdition?.let { etat ->
        DialogEdition(
            etat = etat,
            onValider = { texte, auteur ->
                if (etat.id == null) store.ajouter(texte, auteur)
                else store.modifier(etat.id, texte, auteur)
                dialogEdition = null
            },
            onAnnuler = { dialogEdition = null }
        )
    }

    if (ouvrirReglages) {
        DialogReglages(store = store, onFermer = { ouvrirReglages = false })
    }
}

@Composable
private fun LigneMaxime(maxime: Maxime, onModifier: () -> Unit, onSupprimer: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    maxime.texte,
                    color = Color(0xFF111111),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
                if (maxime.auteur.isNotBlank()) {
                    Text(
                        "— ${maxime.auteur}",
                        color = Color(0xFF888888),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = onModifier) {
                Icon(Icons.Filled.Edit, contentDescription = "Modifier", tint = Color(0xFF555555))
            }
            IconButton(onClick = onSupprimer) {
                Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = Color(0xFF555555))
            }
        }
    }
}

/** État du formulaire d'ajout / modification. id == null => ajout. */
private class EtatEdition(val id: Long? = null, val texte: String = "", val auteur: String = "") {
    constructor(m: Maxime) : this(m.id, m.texte, m.auteur)
}

@Composable
private fun DialogEdition(
    etat: EtatEdition,
    onValider: (String, String) -> Unit,
    onAnnuler: () -> Unit
) {
    var texte by remember { mutableStateOf(etat.texte) }
    var auteur by remember { mutableStateOf(etat.auteur) }

    Dialog(onDismissRequest = onAnnuler) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    if (etat.id == null) "Nouvelle maxime" else "Modifier la maxime",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = texte,
                    onValueChange = { texte = it },
                    label = { Text("Maxime") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur (facultatif)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onAnnuler) { Text("Annuler") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onValider(texte, auteur) },
                        enabled = texte.isNotBlank()
                    ) { Text("Enregistrer") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogReglages(store: MaximeStore, onFermer: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var auto by remember { mutableStateOf(store.affichageAutoActif) }
    val timeState = rememberTimePickerState(
        initialHour = store.heureDeclenchement,
        initialMinute = store.minuteDeclenchement,
        is24Hour = true
    )

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    fun enregistrer() {
        store.heureDeclenchement = timeState.hour
        store.minuteDeclenchement = timeState.minute
        store.affichageAutoActif = auto
        MaximeScheduler.programmer(context)
    }

    Dialog(
        onDismissRequest = {
            enregistrer()
            onFermer()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Réglages",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Affichage automatique le matin",
                        color = Color(0xFF111111),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = auto, onCheckedChange = { auto = it })
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Heure à partir de laquelle la maxime du jour t'attend (tu la verras au déverrouillage) :",
                    color = Color(0xFF555555),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                TimePicker(state = timeState)

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Demande la permission de notification (Android 13+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        enregistrer()
                        onFermer()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}