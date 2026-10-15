# Enviromine Reforged

Remake personnel du mod Minecraft *EnviroMine* (à l'origine en 1.7.10 / Forge), réécrit pour **Minecraft 1.21.1 avec Fabric**.
L'objectif : rendre Minecraft plus réaliste grâce à des stats de survie.

## Stats

| Stat | État |
|---|---|
| Soif | fait |
| Oxygène (qualité de l'air selon la profondeur) | fait |
| Température corporelle | fait |
| Santé mentale | à faire |
| Éboulements | à faire |

## Prérequis

- Java 21
- Minecraft 1.21.1, Fabric Loader ≥ 0.19.5, Fabric API
- [Cardinal Components API](https://github.com/Ladysnake/Cardinal-Components-API) 6.x (embarqué dans le jar)

## Développement

```
./gradlew runClient   # lancer le jeu en dev
./gradlew runServer   # serveur dédié
./gradlew build       # compiler + jar (dans build/libs/)
```

Pour configurer l'IDE, voir la [documentation Fabric](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up).

## Licence

Ce projet est sous licence [MIT](LICENSE).

Il s'inspire du mod original EnviroMine, qui a sa propre licence : la logique est réécrite, sans code ni assets repris.
