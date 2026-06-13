# Maxime

Une petite application Android personnelle qui affiche une **maxime** chaque
matin, au premier déverrouillage du téléphone après une heure que tu choisis.
Interface volontairement **blanche et épurée**, texte noir.

## Ce que fait l'application

- **Liste de maximes** : ajouter, modifier, supprimer (stockées localement sur
  le téléphone).
- **Réglage de l'heure** : tu choisis l'heure à partir de laquelle la maxime du
  jour peut s'afficher (par défaut 7h00).
- **Affichage du matin** : le premier déverrouillage de l'écran après cette
  heure ouvre une maxime au hasard en plein écran. Une seule fois par jour.
  Un appui sur l'écran la referme.
- **Activation / désactivation** de l'affichage automatique dans les réglages.

## Installer l'application

1. Récupère le fichier `app-debug.apk` (voir « Construire l'APK » ci‑dessous,
   ou télécharge l'artefact `maxime-apk` produit par GitHub Actions).
2. Copie-le sur ton téléphone Android.
3. Ouvre-le ; autorise « installer des applications inconnues » si Android le
   demande.

## Permissions à accorder (important)

Pour que la maxime puisse s'ouvrir **toute seule** au déverrouillage, ouvre les
**Réglages** dans l'application et accorde :

- **Afficher par-dessus les autres applications** (obligatoire pour l'ouverture
  automatique).
- **Désactiver l'optimisation de la batterie** pour Maxime (évite que le système
  coupe le service d'écoute).
- **Notifications** (Android 13+) : l'app garde une notification discrète et
  permanente, nécessaire au service qui détecte le déverrouillage.

Sans ces autorisations, l'application fonctionne toujours, mais la maxime ne
s'ouvrira pas automatiquement le matin (tu pourras la consulter en ouvrant
l'app).

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
- `UnlockService` : service en avant-plan qui écoute `ACTION_USER_PRESENT`
  (déverrouillage) et décide d'afficher ou non la maxime du jour.
- `BootReceiver` : relance le service après un redémarrage ou une mise à jour.
- `DisplayActivity` : l'écran plein écran, blanc, qui montre la maxime.
