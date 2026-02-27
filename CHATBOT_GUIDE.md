# 🤖 Guide du Chatbot Assistant Admin

## ✅ Installation Terminée!

Le chatbot a été ajouté avec succès au dashboard admin.

## 📍 Localisation

- **Bouton flottant**: Coin inférieur droit du dashboard (💬)
- **Fenêtre de chat**: S'ouvre en cliquant sur le bouton

## 💬 Questions Supportées

### 📊 Statistiques
- "Combien d'employés actifs?"
- "Combien d'admins?"
- "Combien d'utilisateurs?"
- "Combien d'utilisateurs inactifs?"

### 👥 Identification
- "Qui est responsable RH?"
- "Qui est CEO?"
- "Liste des admins"
- "Liste des employés"

### ❓ Aide
- "aide" ou "help" ou "?"

## 🎨 Interface

- **Messages utilisateur**: Bulles bleues à droite
- **Messages bot**: Bulles blanches à gauche
- **Bouton fermer**: ✕ en haut à droite
- **Champ de saisie**: En bas avec bouton "Envoyer"

## 🚀 Utilisation

1. Cliquez sur le bouton 💬 en bas à droite
2. Tapez votre question
3. Appuyez sur Entrée ou cliquez sur "Envoyer"
4. Le bot répond instantanément!

## 📝 Exemples de Conversations

**Exemple 1:**
```
Vous: Combien d'employés actifs?
Bot: 📊 Il y a 45 employés actifs sur 50 au total.
```

**Exemple 2:**
```
Vous: Qui est responsable RH?
Bot: 👔 Responsables RH:

• Taha Mansouri
  📧 taha@stratix.com
  📱 +216 12 345 678
```

**Exemple 3:**
```
Vous: aide
Bot: 🤖 Je peux répondre à ces questions:

📊 Statistiques:
• Combien d'employés actifs?
• Combien d'admins?
...
```

## 🔧 Fichiers Créés

1. `src/main/java/services/ChatbotService.java` - Logique du chatbot
2. Modifications dans `src/main/java/controllers/DashboardAdminController.java`
3. Modifications dans `src/main/resources/dashboard_admin.fxml`
4. Styles ajoutés dans `src/main/resources/styles.css`

## 🎯 Fonctionnalités

✅ Reconnaissance de questions en langage naturel
✅ Réponses instantanées
✅ Interface moderne et intuitive
✅ Bouton flottant discret
✅ Historique de conversation
✅ Scroll automatique
✅ Design responsive

## 🚀 Test

```bash
mvn clean javafx:run
```

Connectez-vous en tant qu'admin et cliquez sur le bouton 💬!

## 💡 Améliorations Futures (Optionnelles)

- Ajouter plus de questions
- Sauvegarder l'historique
- Suggestions de questions
- Export de conversation
- Notifications proactives
