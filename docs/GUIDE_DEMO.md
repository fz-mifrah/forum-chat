# Guide de Présentation - Application Forum de Discussion en Réseau

## 📋 Résumé de l'Application
Application client-serveur permettant aux utilisateurs de **créer et rejoindre des salons de discussion thématiques**. Chaque salon fonctionne comme un canal séparé où les messages sont diffusés à tous les membres.

---

## 🚀 ÉTAPES EXACTES POUR EXÉCUTER DEVANT LES JURYS

### **ÉTAPE 1 : Préparation - Compilation**

Ouvrez un terminal et exécutez cette commande UNE SEULE FOIS avant de présenter :

```bash
cd forum-chat
rm -rf out
mkdir -p out
javac -d out src/module-info.java src/forum/*.java
```

**✅ Vérification** : Vous devriez voir l'invite de commande réapparaître sans erreur.

---

### **ÉTAPE 2 : Démarrage du Serveur**

Ouvrez un **PREMIER terminal** et gardez-le ouvert jusqu'à la fin :

```bash
cd forum-chat
java --module-path out -m forum_chat/forum.ChatServer 12345
```

**Attendez ce message** :
```
ChatServer started on port 12345
```

**⚠️ SI VOUS VOYEZ UNE EXCEPTION** "Address already in use" :
```bash
# Libérez le port (à faire UNE FOIS)
lsof -i :12345 | grep -v COMMAND | awk '{print $2}' | xargs kill -9
# Attendez 2 secondes, puis relancez la commande du serveur
```

---

### **ÉTAPE 3 : Démarrage du Premier Client (Client 1 - Alice)**

Ouvrez un **DEUXIÈME terminal** :

```bash
cd forum-chat
java --module-path out -m forum_chat/forum.ChatClient localhost 12345
```

Vous verrez l'invite `> ` prête à recevoir des commandes.

**Commandes à exécuter (l'une après l'autre)** :

```
/nick Alice
/create discussion_technique
/join discussion_technique
Bonjour, c'est le premier test du forum !
```

Attendez avant de continuer...

---

### **ÉTAPE 4 : Démarrage du Deuxième Client (Client 2 - Bob)**

Ouvrez un **TROISIÈME terminal** :

```bash
cd forum-chat
java --module-path out -m forum_chat/forum.ChatClient localhost 12345
```

Vous verrez l'invite `> ` prête à recevoir des commandes.

**Commandes à exécuter (l'une après l'autre)** :

```
/nick Bob
/join discussion_technique
```

**Bob recevra au même moment** : `SYSTEM: Bob joined discussion_technique`
**Alice recevra aussi** : `SYSTEM: Bob joined discussion_technique`

---

### **ÉTAPE 5 : Démonstration du Broadcast (Diffusion)**

**Toujours dans le Terminal 3 (Client Bob)** :

```
Salut Alice ! Je suis Bob
C'est une excellente application !
```

**Vous verrez dans le Terminal 2 (Alice)** :
```
Bob dit : Salut Alice ! Je suis Bob
Bob dit : C'est une excellente application !
```

---

### **ÉTAPE 6 : Créer un Deuxième Salon**

**Toujours dans le Terminal 2 (Alice)** :

```
/create salon_general
/join salon_general
Message dans le nouveau salon depuis Alice
```

**Bob reste dans discussion_technique** (Alice n'est plus là).

---

### **ÉTAPE 7 : Demander la Liste des Salons**

**Dans n'importe quel client** :

```
/list
```

**Vous verrez** :
```
Available rooms:
- discussion_technique (1 member)
- salon_general (1 member)
```

---

### **ÉTAPE 8 : Quitter**

Tapez dans tous les clients :

```
/quit
```

Les clients se déconnecteront proprement.
Puis arrêtez le serveur avec **Ctrl+C** dans le premier terminal.

---

## 📝 Commandes Rapides - Aide Mémoire

| Commande | Description | Exemple |
|----------|-------------|---------|
| `/nick NOM` | Définir votre pseudo | `/nick Alice` |
| `/create SALON` | Créer un nouveau salon | `/create discussion_tech` |
| `/join SALON` | Rejoindre un salon existant | `/join discussion_tech` |
| `/leave` | Quitter le salon actuel | `/leave` |
| `/list` | Afficher tous les salons disponibles | `/list` |
| Texte normal | Envoyer un message au salon | `Bonjour tout le monde !` |
| `/quit` | Se déconnecter | `/quit` |

---

## 🎯 Points Clés à Montrer aux Jurys

1. ✅ **Connexion Multiple** : Plusieurs clients se connectent au même serveur
2. ✅ **Gestion des Salons** : Créer et rejoindre différents salons
3. ✅ **Broadcast de Messages** : Les messages d'un salon ne vont QUE aux membres de ce salon
4. ✅ **Notifications Système** : Quand quelqu'un rejoint, les autres sont notifiés
5. ✅ **Architecture Client-Serveur** : Communication TCP/IP stable

---

## 🔧 Résolution des Problèmes

### Problème : "Address already in use"
```bash
# Libérez le port
lsof -i :12345 | grep LISTEN | awk '{print $2}' | xargs kill -9
sleep 2
# Relancez le serveur
```

### Problème : "Connection refused"
- **Vérifiez** que le serveur est bien démarré (Terminal 1)
- **Vérifiez** le port utilisé (par défaut 12345)
- **Attendez** quelques secondes après le démarrage du serveur

### Problème : Les messages ne s'affichent pas
- **Vérifiez** que vous êtes dans le même salon (`/join`)
- **Vérifiez** que vous avez défini un pseudo (`/nick`)
- **Tapez** le message et appuyez sur **Entrée**

---

## 📦 Fichiers Importants

- `src/module-info.java` - Configuration du module
- `src/forum/ChatServer.java` - Serveur TCP (gère salons et diffusion)
- `src/forum/ChatClient.java` - Client console interactif
- `src/forum/TestClients.java` - Tests automatisés (optionnel)

---

## ⏱️ Durée Estimée de Démonstration

- **Compilation** : 5-10 secondes
- **Démarrage serveur** : 2 secondes
- **Démarrage premier client** : 2 secondes
- **Démarrage deuxième client** : 2 secondes
- **Démonstration complète** : 3-5 minutes

**Total : ~5-10 minutes pour une présentation professionnelle**

---

## 💡 Suggestions Avancées (Bonus)

Si vous avez du temps supplémentaire, montrez :
- La création de 3-4 salons différents
- L'alternance entre salons (`/join salon1`, puis `/join salon2`)
- L'effet du `/list` avant et après création de salons
- La robustesse (connexion/déconnexion rapides)

