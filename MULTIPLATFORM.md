# PharmaDirect — Guide Multiplateforme (Windows, macOS, Linux)

Ce document explique comment exécuter et compiler **PharmaDirect** en tant qu'application native pour ordinateur sur **Windows**, **macOS** et **Linux** grâce à **Compose Multiplatform (JetBrains Skiko / JVM)**.

---

## 1. Architecture Multiplateforme

L'application est structurée de manière modulaire :
- **Module Android (`/app`)** : Application mobile native optimisée pour smartphones et tablettes Android, avec support d'émulateur et écran tactile.
- **Module Desktop (`/desktop`)** : Application de bureau native utilisant **Compose Multiplatform** pour **Windows**, **macOS** et **Linux**, avec fenêtrage natif, redimensionnement adaptatif, barre latérale bureautique et menus intégrés.

---

## 2. Commandes d'Exécution & de Compilation

Assurez-vous d'avoir installé un JDK 17 ou supérieur.

### Exécution directe en mode développement
```bash
# Lancer l'application de bureau sur Windows / macOS / Linux
./gradlew :desktop:run
```

### Génération des installateurs natifs

#### 🪟 Windows (.msi / .exe)
```bash
# Génère l'installeur MSI prêt pour Windows 10 et 11
./gradlew :desktop:packageMsi

# Ou pour générer l'exécutable portable :
./gradlew :desktop:packageExe
```
*Le fichier généré se trouvera dans `desktop/build/compose/binaries/main/msi/`.*

#### 🍏 macOS (.dmg / .pkg)
```bash
# Génère l'image disque DMG pour macOS (Intel & Apple Silicon)
./gradlew :desktop:packageDmg
```
*Le fichier généré se trouvera dans `desktop/build/compose/binaries/main/dmg/`.*

#### 🐧 Linux (.deb / .rpm)
```bash
# Génère le paquet DEB pour Ubuntu / Debian / Mint
./gradlew :desktop:packageDeb

# Ou le paquet RPM pour Fedora / RHEL :
./gradlew :desktop:packageRpm
```
*Le paquet généré se trouvera dans `desktop/build/compose/binaries/main/deb/`.*

---

## 3. Caractéristiques Techniques Desktop

- **Moteur Graphique** : JetBrains Skiko (moteur graphique matériel accéléré basé sur Skia).
- **Fenêtrage** : Gestion native des dimensions de fenêtre (`DpSize(1280.dp, 820.dp)`), centrage automatique et respect des décorations du système d'exploitation hôte.
- **Connectivité & Synchronisation** : Écouteur réseau haute performance, latence en direct, synchronisation cloud avec les officines et gestion locale résiliente.
- **Format Ergonomique Bureau** : 
  - Barre latérale fixe (260dp) avec accès rapide aux modules : Accueil, Catalogue, Ordonnances, Panier, Commandes, Profil et Assistance.
  - En-tête supérieur avec fil d'Ariane, statut réseau sécurisé et boutons d'action rapide.
  - Largeur de lecture contrôlée pour garantir le confort visuel sur les écrans Ultra-Wide et 4K.
