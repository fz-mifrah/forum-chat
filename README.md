# Forum Chat — Forum de discussion en réseau (Java Sockets)

![Java](https://img.shields.io/badge/Java-11%2B-007396?logo=openjdk&logoColor=white)
![Sockets](https://img.shields.io/badge/R%C3%A9seau-TCP%20Sockets-555)
![Multithreading](https://img.shields.io/badge/Concurrence-Multithreading-orange)
![No dependencies](https://img.shields.io/badge/D%C3%A9pendances-aucune-brightgreen)

Application **client-serveur** en Java pur qui permet à plusieurs utilisateurs de **créer et rejoindre des salons de discussion** thématiques. Chaque salon est un canal indépendant : un message envoyé dans un salon est diffusé à tous ses membres, et seulement à eux.

Le projet illustre la programmation réseau avec les **sockets TCP**, la gestion de **plusieurs clients simultanés** (un thread par client) et la conception d'un **protocole texte** simple.

---

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Protocole](#protocole)
- [Démarrage rapide](#démarrage-rapide)
- [Exemple de session](#exemple-de-session)
- [Structure du projet](#structure-du-projet)
- [Pistes d'amélioration](#pistes-damélioration)
- [Auteure](#auteure)

---

## Fonctionnalités

- Choix d'un **pseudonyme**.
- **Création** de salons et **liste** des salons existants.
- **Entrée / sortie** d'un salon, avec notification système aux autres membres.
- **Diffusion** des messages à tous les membres du salon courant.
- Suppression automatique d'un salon quand le dernier membre le quitte.
- Client console avec **commandes slash** (`/join`, `/list`…) et réception des messages en temps réel.

---

## Architecture

```
 ┌──────────────┐                         ┌──────────────────────────────────────┐
 │  ChatClient  │ ──── TCP (port 12345) ──▶│              ChatServer              │
 │   (Alice)    │ ◀──────────────────────  │                                      │
 └──────────────┘                         │  ServerSocket.accept()               │
 ┌──────────────┐                         │     └─▶ 1 thread ClientHandler        │
 │  ChatClient  │ ──── TCP ──────────────▶│          par connexion               │
 │    (Bob)     │ ◀──────────────────────  │                                      │
 └──────────────┘                         │  rooms : ConcurrentHashMap<           │
                                          │     String, CopyOnWriteArraySet<      │
                                          │        ClientHandler>>                │
                                          └──────────────────────────────────────┘
```

- **Serveur** (`ChatServer`) : accepte les connexions et lance un `ClientHandler` dans un thread dédié pour chaque client.
- **Gestion concurrente des salons** : `ConcurrentHashMap` pour les salons et `CopyOnWriteArraySet` pour leurs membres, ce qui permet des accès simultanés sans verrou explicite.
- **Client** (`ChatClient`) : un thread lit en continu les messages du serveur pendant que le thread principal lit la saisie de l'utilisateur.

---

## Protocole

Le client et le serveur échangent des lignes de texte.

| Commande client | Description | Réponse serveur |
|---|---|---|
| `NICK <nom>` | Définir son pseudonyme | `OK NICK <nom>` |
| `CREATE <salon>` | Créer un salon | `OK CREATE <salon>` |
| `JOIN <salon>` | Rejoindre un salon (quitte le précédent) | `OK JOIN <salon>` + notification aux membres |
| `LEAVE` | Quitter le salon courant | `OK LEAVE` + notification aux membres |
| `LIST` | Lister les salons | `ROOMS salon1,salon2,…` |
| `MSG <texte>` | Envoyer un message au salon courant | `[salon][pseudo]: texte` diffusé aux membres |

Les erreurs sont renvoyées sous la forme `ERR <raison>` (commande inconnue, argument manquant, message hors d'un salon…).

Commandes disponibles dans le client console :

| Commande | Effet |
|---|---|
| `/nick NOM` | Changer de pseudonyme |
| `/create SALON` | Créer un salon |
| `/join SALON` | Rejoindre un salon |
| `/leave` | Quitter le salon |
| `/list` | Lister les salons |
| `/help` | Afficher l'aide |
| `/quit` | Quitter le client |
| *texte libre* | Envoyer un message dans le salon courant |

---

## Démarrage rapide

**Prérequis** : JDK 11 ou plus récent. Aucune dépendance externe.

```bash
# 1. Compiler
javac -d out src/module-info.java src/forum/*.java

# 2. Démarrer le serveur (port 12345 par défaut)
java --module-path out -m forum_chat/forum.ChatServer 12345

# 3. Dans d'autres terminaux, démarrer un ou plusieurs clients
java --module-path out -m forum_chat/forum.ChatClient localhost 12345
```

Test automatisé avec deux clients simulés (Alice et Bob), serveur démarré :

```bash
java --module-path out -m forum_chat/forum.TestClients localhost 12345
```

Un guide de démonstration pas à pas est disponible dans [`docs/GUIDE_DEMO.md`](docs/GUIDE_DEMO.md).

---

## Exemple de session

```text
# Terminal Alice                         # Terminal Bob
> /nick Alice                            > /nick Bob
OK NICK Alice                            OK NICK Bob
> /create java                           > /list
OK CREATE java                           ROOMS java
> /join java                             > /join java
OK JOIN java                             OK JOIN java
SYSTEM: Alice joined java                SYSTEM: Bob joined java
SYSTEM: Bob joined java                  [java][Alice]: Bonjour Bob !
> Bonjour Bob !
[java][Alice]: Bonjour Bob !
```

---

## Structure du projet

```
forum-chat/
├── src/
│   ├── module-info.java        # Module Java « forum_chat »
│   └── forum/
│       ├── ChatServer.java     # Serveur multi-salons multithread
│       ├── ChatClient.java     # Client console interactif
│       └── TestClients.java    # Scénario de test avec deux clients
└── docs/
    └── GUIDE_DEMO.md           # Guide de démonstration
```

---

## Pistes d'amélioration

- Authentification des utilisateurs et pseudonymes uniques.
- Historique des messages par salon et persistance.
- Messages privés entre utilisateurs.
- Pool de threads (`ExecutorService`) à la place d'un thread par client.
- Interface graphique (JavaFX) ou client web via WebSocket.
- Tests unitaires JUnit et chiffrement TLS des échanges.

---

## Auteure

**Fatima Ezzahra Mifrah**
Étudiante en génie logiciel — Java, Spring Boot, Angular & React, QA Automation & DevSecOps
Casablanca, Maroc

- GitHub : [@fz-mifrah](https://github.com/fz-mifrah)
- Email : mifrah.fz1@gmail.com
