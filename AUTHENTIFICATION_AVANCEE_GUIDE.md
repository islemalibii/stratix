# 🔐 Guide d'Authentification Avancée - Stratix

## ✅ FONCTIONNALITÉS IMPLÉMENTÉES

### 1. **Politique de Mot de Passe Fort** ✅
- Minimum 8 caractères
- Au moins 1 majuscule
- Au moins 1 minuscule
- Au moins 1 chiffre
- Au moins 1 caractère spécial (!@#$%^&*...)
- Hashage SHA-256 des mots de passe

### 2. **Verrouillage après Tentatives** ✅
- Maximum 3 tentatives de connexion
- Verrouillage automatique pendant 15 minutes
- Déverrouillage automatique après expiration
- Compteur de tentatives réinitialisé après connexion réussie

### 3. **Mot de Passe Oublié** ✅
- Génération de token sécurisé
- Token valide pendant 1 heure
- Réinitialisation avec validation forte
- Lien "Oublié ?" sur la page de login

### 4. **Double Authentification (2FA)** ✅
- Code à 6 chiffres
- Activation/désactivation par utilisateur
- Vérification du code avant connexion
- Génération de nouveau code à chaque connexion

## 📋 ÉTAPES D'INSTALLATION

### Étape 1: Exécuter le script SQL

Ouvrez MySQL et exécutez le fichier `database_update.sql`:

```sql
-- Copier et exécuter dans MySQL
ALTER TABLE utilisateur 
ADD COLUMN failed_login_attempts INT DEFAULT 0,
ADD COLUMN account_locked BOOLEAN DEFAULT FALSE,
ADD COLUMN locked_until DATETIME NULL,
ADD COLUMN password_reset_token VARCHAR(255) NULL,
ADD COLUMN password_reset_expiry DATETIME NULL,
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(255) NULL,
ADD COLUMN last_password_change DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN password_history TEXT NULL;

CREATE INDEX idx_email ON utilisateur(email);
CREATE INDEX idx_reset_token ON utilisateur(password_reset_token);
```

### Étape 2: Mettre à jour les mots de passe existants

Les anciens mots de passe en clair doivent être hashés. Exécutez:

```sql
-- ATTENTION: Ceci va hasher tous les mots de passe
-- Sauvegardez d'abord votre base de données!
-- Après cette opération, tous les utilisateurs devront utiliser leurs mots de passe hashés
```

**OU** créez de nouveaux utilisateurs via la page Signup qui hashera automatiquement.

### Étape 3: Tester les fonctionnalités

1. **Test Mot de Passe Fort:**
   - Aller sur Signup
   - Essayer un mot de passe faible → Message d'erreur
   - Utiliser un mot de passe fort (ex: "Stratix2024!")

2. **Test Verrouillage:**
   - Se tromper 3 fois de mot de passe
   - Compte verrouillé pendant 15 minutes
   - Message: "Compte verrouillé. Réessayez dans 15 minutes."

3. **Test Mot de Passe Oublié:**
   - Cliquer sur "Oublié ?"
   - Entrer votre email
   - Copier le token affiché
   - Entrer le token et nouveau mot de passe
   - Se connecter avec le nouveau mot de passe

4. **Test 2FA:**
   - Se connecter normalement
   - Dans le dashboard, activer 2FA (à implémenter dans le profil)
   - Se déconnecter
   - Se reconnecter → Code 2FA demandé
   - Entrer le code affiché

## 🎯 UTILISATION

### Pour l'Utilisateur Final:

1. **Inscription:**
   - Mot de passe doit respecter la politique forte
   - Validation en temps réel

2. **Connexion:**
   - 3 tentatives maximum
   - Si 2FA activé, code demandé
   - Session sauvegardée

3. **Mot de Passe Oublié:**
   - Cliquer sur "Oublié ?"
   - Recevoir le token (affiché à l'écran)
   - Réinitialiser avec nouveau mot de passe fort

### Pour l'Administrateur:

1. **Débloquer un compte:**
```java
AuthenticationService.getInstance().unlockAccount("email@example.com");
```

2. **Activer 2FA pour un utilisateur:**
```java
String code = AuthenticationService.getInstance().enable2FA(userId);
```

3. **Désactiver 2FA:**
```java
AuthenticationService.getInstance().disable2FA(userId);
```

## 📊 STATISTIQUES DE SÉCURITÉ

- ✅ Hashage SHA-256
- ✅ Tokens sécurisés (32 bytes)
- ✅ Expiration automatique (1h pour reset, 15min pour lockout)
- ✅ Validation côté serveur
- ✅ Protection contre brute force

## 🚀 AMÉLIORATIONS FUTURES

1. **Email Integration:**
   - Envoyer token par email (JavaMail)
   - Envoyer code 2FA par SMS (Twilio)

2. **Historique des Mots de Passe:**
   - Empêcher réutilisation des 5 derniers mots de passe

3. **Expiration des Mots de Passe:**
   - Forcer changement tous les 90 jours

4. **Authentification Biométrique:**
   - Empreinte digitale
   - Reconnaissance faciale

5. **Logs d'Audit:**
   - Enregistrer toutes les tentatives de connexion
   - Alertes en cas d'activité suspecte

## 📝 NOTES IMPORTANTES

1. **Sécurité:**
   - Les mots de passe sont hashés (SHA-256)
   - Les tokens sont générés de manière sécurisée
   - Les codes 2FA sont aléatoires

2. **Performance:**
   - Index sur email et reset_token
   - Requêtes optimisées

3. **UX:**
   - Messages d'erreur clairs
   - Validation en temps réel
   - Feedback visuel

## 🐛 DÉPANNAGE

**Problème: "Compte verrouillé"**
- Attendre 15 minutes OU
- Exécuter: `UPDATE utilisateur SET account_locked = FALSE, failed_login_attempts = 0 WHERE email = 'votre@email.com';`

**Problème: "Token invalide"**
- Le token expire après 1 heure
- Générer un nouveau token

**Problème: "Code 2FA incorrect"**
- Vérifier que le code est bien celui affiché
- Le code change à chaque connexion

## 📞 SUPPORT

Pour toute question ou problème, consultez:
- Documentation JavaFX
- Documentation MySQL
- Code source dans `src/main/java/Services/AuthenticationService.java`
