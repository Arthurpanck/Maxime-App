# Maxime

Une petite application Android personnelle qui affiche une **maxime** chaque
matin, au premier déverrouillage du téléphone après une heure que tu choisis.
Interface volontairement **blanche et épurée**, texte noir.

## Ce que fait l'application

- **Liste de maximes** : ajouter, modifier, supprimer (stockées localement sur
  le téléphone).
- **Réglage de l'heure** : tu choisis l'heure à partir de laquelle la maxime du
  jour peut s'afficher (par défaut 7h00).
- **Maxime du matin** : à l'heure choisie, une **notification** « Ta maxime du
  matin » est déposée. Au déverrouillage suivant, tu la vois et tu la tapes pour
  ouvrir la maxime du jour en plein écran (fond blanc, texte noir). Un appui sur
  l'écran la referme.
- **Activation / désactivation** de la maxime du matin dans les réglages.

## Installer l'application

1. Récupère le fichier `app-debug.apk` (voir « Construire l'APK » ci‑dessous,
   ou télécharge l'artefact `maxime-apk` produit par GitHub Actions).
2. Copie-le sur ton téléphone Android.
3. Ouvre-le ; autorise « installer des applications inconnues » si Android le
   demande.

## Permissions

L'application est volontairement **sobre en permissions** :

- **Notifications** (`POST_NOTIFICATIONS`, Android 13+) : pour déposer la maxime
  du matin. L'app te la demande au premier enregistrement des réglages.
- **Démarrage** (`RECEIVE_BOOT_COMPLETED`, accordée automatiquement) : pour
  reprogrammer l'alarme du matin après un redémarrage du téléphone.

Pas de service permanent, pas d'« afficher par-dessus les autres applis », pas
d'exemption de batterie. Si tu refuses les notifications, l'app marche toujours :
tu consultes simplement tes maximes en l'ouvrant.

## Construire l'APK

### Avec GitHub Actions (le plus simple)
À chaque `push`, le workflow `.github/workflows/build-apk.yml` construit l'APK
et le met à disposition en tant qu'artefact `maxime-apk` (onglet **Actions** du
dépôt).

### En local
Prérequis : JDK 17 et le SDK Android (indique son chemin dans `local.properties`
via `sdk.dir=/chemin/vers/android-sdk`).

```bash
./gradlew assembleDebug
# APK généré dans app/build/outputs/apk/debug/app-debug.apk
```

## Détails techniques

- Kotlin + Jetpack Compose, `minSdk 26`, `targetSdk 33`.
- Données stockées en JSON dans les `SharedPreferences` (kotlinx.serialization).
- `MaximeScheduler` : programme une **alarme inexacte** quotidienne via
  `AlarmManager` (aucune permission spéciale), reprogrammée chaque jour.
- `AlarmReceiver` : à l'heure dite, dépose la notification de la maxime du matin.
- `BootReceiver` : reprogramme l'alarme après un redémarrage ou une mise à jour.
- `DisplayActivity` : l'écran plein écran, blanc, qui montre la maxime (ouvert
  d'un tap sur la notification, ou depuis la liste).
