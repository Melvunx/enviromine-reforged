# CLAUDE.md — Enviromine Reforged

## Projet

Remake **personnel** du mod Minecraft *EnviroMine* (originellement 1.7.10 / Forge), réécrit pour **Minecraft 1.21.1 avec Fabric**.
Objectif : rendre Minecraft plus réaliste via des stats de survie, puis y ajouter mes propres mécaniques (structures, biomes, effets de potion, items).

Ce n'est PAS un portage ligne par ligne : on s'inspire de l'original et on réécrit proprement.

### Stats prévues
| Stat | État | Package |
|---|---|---|
| Soif | fait | `thirst/` |
| Oxygène (qualité de l'air selon profondeur/ventilation, indépendant de la nage) | fait | `oxygen/` |
| Température corporelle | fait | `temperature/` |
| Santé mentale | à faire | |
| Éboulements (blocs qui tombent avec la gravité dans les grottes) | à faire | |

### Prochaines étapes
1. Masque à gaz (premier item custom : enregistrement d'items, tags, datagen)
2. Éboulements
3. Santé mentale
4. Items/vêtements thermiques, structures, biomes, effets de potion

## Stack technique

- Minecraft **1.21.1**, Fabric Loader, Fabric API
- **Java 21**
- Mappings **Yarn** (`net.fabricmc:yarn`), Loom `net.fabricmc.fabric-loom-remap`
- **Cardinal Components API** (CCA) 6.x : `cardinal-components-base` et `cardinal-components-entity` (jar-in-jar via `include`)
- Mod id : `enviromine-reforged-mod` (renommage possible vers `enviromine_reforged` : à décider tôt, l'id apparaît dans assets, lang et Identifiers)
- Package racine : `net.melvunx.envirominereforgedmod`

## Commandes

```
./gradlew runClient      # lancer le jeu en dev
./gradlew runServer      # serveur dédié (à tester régulièrement : le code client ne doit pas fuiter)
./gradlew build          # compiler + jar
./gradlew runDatagen     # génération de données (quand la datagen sera utilisée)
```

- Pas de tests unitaires dans le projet : la vérification passe par `./gradlew build` (c'est aussi ce que lance la CI `.github/workflows/build.yml`) puis un test en jeu.
- Loom reste en **un seul source set** (`src/main`, pas de `src/client`) : le compilateur n'empêche donc PAS un import de classe client dans le code commun. Seul `runServer` révèle la fuite.
- `gradle.properties` : `org.gradle.jvmargs=-Xmx1G` ; le monter (`-Xmx4G`) si le build manque de mémoire.

## Architecture

```
net.melvunx.envirominereforgedmod
├── EnviromineReforged.java          ModInitializer commun (init réseau, enregistrements)
├── EnviromineReforgedClient.java    ClientModInitializer (tick client, HUD)
├── EnviromineReforgedDataGenerator.java
├── entity/EntityModComponents.java  Clés CCA + enregistrement des composants joueur
├── thirst/  oxygen/  temperature/   Une stat = un package
├── network/                         Payloads + réception serveur
├── mixin/                           PotionItemMixin (boire de l'eau)
└── client/StatsHud.java             Affichage des stats (une méthode drawXxx par stat)
```

### Patron d'une stat (à respecter pour toute nouvelle stat)

1. `Xxx` : interface qui étend `AutoSyncedComponent` et `ServerTickingComponent`.
2. `PlayerXxx` : implémentation du composant.
   - Logique dans `serverTick()` (CCA l'appelle par joueur : pas de handler global `ServerTickEvents`).
   - Ignorer `isCreative()` / `isSpectator()`.
   - Valeurs internes en `float` si les variations sont plus lentes que 1 unité/tick.
   - **Synchroniser (`KEY.sync(player)`) uniquement quand la valeur AFFICHÉE change**, jamais à chaque tick.
   - `readFromNbt` : toujours tester `tag.contains(...)` (un `getInt` manquant renvoie 0).
   - Recalculs coûteux (biome, scan de blocs, heightmap) : 1 fois par seconde (`player.age % 20`), résultat mis en cache dans un champ.
3. `XxxRules` : **fonctions pures et constantes d'équilibrage** (aucun état). Tout réglage se fait ici.
4. Enregistrer la clé dans `EntityModComponents` avec `RespawnCopyStrategy.LOSSLESS_ONLY` (remise à zéro à la mort, conservation au retour de l'End).
5. **Déclarer l'id du composant dans `fabric.mod.json` → `custom.cardinal-components`**, sinon CCA plante au démarrage.
6. Ajouter `drawXxx` dans `StatsHud` (y décalé de 10 px par stat, au-dessus de `h-49` qui est la zone des bulles vanilla).

### Dépendances entre stats
- Soif ← température corporelle : `ThirstRules.temperatureMultiplier(bodyTemperature)`. La soif ne lit PAS le biome directement.
- Température = ambiance (biome, heure, sous-terre, eau, pluie, `HeatSources`) → cible → inertie (1/1200 par tick).
- Oxygène = qualité de l'air (profondeur sous heightmap + lumière du ciel) → régénération / perte.
- La logique partagée client/serveur (ex. `OxygenRules.airQuality`, `WaterRaycast`) vit dans des classes pures : le client s'en sert pour le HUD, le serveur pour le gameplay.

### Points d'extension prévus (équipements futurs)
- `OxygenRules.drainMultiplier(player)` → masque à gaz
- `TemperatureRules.coldProtection(player)` / `heatProtection(player)` → vêtements, combinaisons
- `HeatSources.heatOf(state)` → à terme données externalisées (JSON / tags de blocs)

## Règles de code

- Le serveur est la **source de vérité** : toute action initiée par le client passe par un payload et est **re-validée côté serveur** (ex. `DrinkWaterPayload`).
- Ne jamais référencer de classe client (`MinecraftClient`, `DrawContext`...) depuis le code commun ou serveur.
- Valeurs d'équilibrage en `public static final` dans les `*Rules`, jamais en dur dans les composants. Prévoir une config JSON ensuite.
- Commentaires en **français**, courts, expliquant le *pourquoi*.
- Mixins : préférer les callbacks Fabric API ; mixin seulement quand aucun événement n'existe. Préfixer les méthodes injectées (`enviromine$...`) et déclarer chaque mixin dans `enviromine-reforged-mod.mixins.json`.
- Nommage : `PlayerXxx` pour les composants, `XxxRules` pour les règles, `XxxPayload` pour le réseau.

## Pièges Minecraft déjà rencontrés

- Le rayon du viseur **ignore les fluides** : `UseBlockCallback` ne reçoit jamais de bloc d'eau. Utiliser un `RaycastContext` avec `FluidHandling.SOURCE_ONLY` (voir `WaterRaycast`).
- Avec la **main vide**, le client n'envoie probablement pas de paquet « utiliser l'objet » : `UseItemCallback` ne se déclenche pas. Détecter côté client (`useKey`) et envoyer un payload personnalisé.
- `UseItemCallback` se déclenche au **début** de l'utilisation d'un objet, pas à la fin : pour « finir de boire », mixin sur `PotionItem.finishUsing`.
- La lumière du ciel (`LightType.SKY`) ne dépend pas de l'heure et descend en ligne droite dans un puits vertical.
- `HudRenderCallback` est celui de 1.21.1 ; il est remplacé par d'autres API dans les versions ultérieures.

## Équilibrage actuel (points de départ, à ajuster en jeu)

- Soif : 1 point / 240 s au repos (jauge ≈ 80 min), sprint ×3, nage ×2, multiplicateur ×0,8 à ×3 selon la température corporelle.
- Oxygène : air sain jusqu'à ~34 blocs sous la surface ; pire cas 1 point / 12 s ; régénération 0,5 point/s ; HUD masqué si jauge pleine ET air sain.
- Température : 37 °C normal ; confort ambiant 22 °C ; seuils froid 35/33/31, chaud 38,5/39,5/41. Activer `TemperatureRules.DEBUG` pour tracer ambiant/cible/corps.

## Comment m'aider (important)

Je suis développeur Java débutant en modding et je veux **progresser** :
- Avance **étape par étape**, une fonctionnalité à la fois.
- Explique le *pourquoi* des choix (patron, API Fabric/CCA, piège évité), pas seulement le code.
- Signale clairement ce qui est incertain (noms de méthodes Yarn, comportement d'API) et ce que je dois vérifier dans mon IDE.
- Améliore et corrige mon code existant quand c'est pertinent, en expliquant les corrections.
- Réponds en **français**.
- Après chaque fonctionnalité, indique comment la tester en jeu.
- Sois honnête quand tu ne peux pas compiler ou tester : ne garantis pas qu'un code fonctionne sans l'avoir vérifié.

## Licence et inspiration

Le mod original (EnviroMine) a sa propre licence : réécrire la logique à ma façon, ne pas copier de code ni d'assets sans vérifier la licence. Ce projet est sous licence MIT.
