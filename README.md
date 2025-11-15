# 🎓 Système de Gestion Scolarité - Université Ibn Khaldoun Tiaret

![Java](https://img.shields.io/badge/Java-23-orange?style=flat&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat&logo=apache-maven)
![License](https://img.shields.io/badge/License-Academic-green?style=flat)

## 📋 Description du Projet

**Mini Projet 1 : Gestion de Scolarité**  
*Université Ibn Khaldoun – Tiaret*  
*3ème année Licence ISIL - Faculté des Mathématiques et d'Informatique*  
*Département d'Informatique - Année Universitaire 2025/2026*

Ce projet implémente un système complet de gestion scolaire avec une interface utilisateur moderne en Java Swing et une base de données MySQL. L'application offre des fonctionnalités complètes pour la gestion des notes, des étudiants, des enseignants et de l'administration académique.

---

## 🎯 Objectifs du Projet

### 1. **Base de Données**
- Implémentation du modèle physique MCD sous MySQL
- Utilisation de l'utilitaire serveur XAMPP
- Gestion complète des relations entre entités

### 2. **Application Client/Serveur**
- Architecture multi-utilisateurs avec authentification
- Interface graphique moderne en Java Swing
- Fonctionnalités spécialisées par type d'utilisateur

---

## 👥 Acteurs et Cas d'Utilisation

### 🎓 **Étudiant**
- ✅ Consultation des informations personnelles
- ✅ Consultation des notes par épreuve et par matière
- ✅ Consultation de la moyenne générale annuelle
- ✅ Consultation du statut de fin d'année (admis, redoublant, exclu)
- ✅ Génération et téléchargement du bulletin de notes (PDF)

### 👨‍🏫 **Enseignant**
- ✅ Création d'épreuves pour les matières (contrôle, examen, projet, TP)
- ✅ Saisie et modification des notes des épreuves
- ✅ Consultation des résultats des étudiants par épreuve ou matière
- ✅ Calcul et validation de la note finale d'une matière
- ✅ Export des statistiques vers Excel

### 🏢 **Responsable Scolarité/Secrétariat**
- ✅ Inscription de nouveaux étudiants
- ✅ Enregistrement de l'origine scolaire (DUT, CPI, CPGE, etc.)
- ✅ Affectation d'étudiants aux programmes et années
- ✅ Gestion des comptes utilisateurs

### 🎯 **Direction des Études/Chef de Programme**
- ✅ Création, modification et suppression de programmes
- ✅ Définition des prérequis entre programmes
- ✅ Gestion des pondérations des matières
- ✅ Validation des moyennes et statuts de fin d'année

### ⚙️ **Administrateur Système**
- ✅ Gestion des utilisateurs et droits d'accès
- ✅ Sauvegarde et restauration des données
- ✅ Génération de rapports et statistiques globales
- ✅ Tableau de bord avec indicateurs de performance

---

## 🖥️ Interface Utilisateur

### 🔐 **Écran de Connexion**
![Login Screen](screenshots/Screenshot%202025-11-15%20150227.png)
- Interface de connexion moderne avec authentification sécurisée
- Mode démonstration avec comptes de test pré-configurés

### 🏠 **Tableau de Bord Administrateur**
![Admin Dashboard](screenshots/Screenshot%202025-11-15%20150235.png)
- Vue d'ensemble du système avec statistiques en temps réel
- Gestion des utilisateurs, étudiants et enseignants
- Indicateurs de performance et logs d'activité

### 👨‍🏫 **Interface Enseignant**
![Teacher Interface](screenshots/Screenshot%202025-11-15%20150345.png)
- Gestion des épreuves et saisie de notes
- Consultation des matières enseignées
- Statistiques détaillées par épreuve et matière

### 🎓 **Interface Étudiant**
![Student Dashboard](screenshots/Screenshot%202025-11-15%20150426.png)
- Consultation des informations personnelles
- Vue d'ensemble des notes et moyennes
- Suivi du statut académique en temps réel

### 📊 **Consultation des Notes**
![Grades View](screenshots/Screenshot%202025-11-15%20150430.png)
- Tableau détaillé des notes par épreuve
- Affichage des coefficients et dates d'évaluation
- Interface claire et intuitive

### 📄 **Génération de Bulletins PDF**
![PDF Bulletin](screenshots/Screenshot%202025-11-15%20150507.png)
- Bulletins de notes officiels générés automatiquement
- Format professionnel avec en-têtes universitaires
- Téléchargement direct au format PDF

---

## 🚀 Technologies Utilisées

### **Backend**
- **Java 23** - Language de programmation
- **MySQL 8.0** - Système de gestion de base de données
- **HikariCP** - Pool de connexions haute performance
- **BCrypt** - Chiffrement sécurisé des mots de passe

### **Frontend**
- **Java Swing** - Interface graphique utilisateur
- **Look & Feel Metal** - Thème moderne et responsive
- **Custom Components** - Composants UI personnalisés

### **Bibliothèques**
- **iText PDF** - Génération de bulletins PDF
- **Apache POI** - Export Excel des statistiques
- **Jackson** - Traitement JSON pour la configuration
- **Logback** - Système de journalisation avancé
- **Apache Commons Lang** - Utilitaires et outils

### **Outils de Développement**
- **Maven 3.8+** - Gestionnaire de dépendances
- **NetBeans IDE** - Environnement de développement
- **XAMPP** - Serveur local avec MySQL

---

## 📦 Installation et Configuration

### **Prérequis**
```bash
- Java 23 ou supérieur
- Maven 3.8+
- MySQL 8.0+ (via XAMPP recommandé)
- 4 GB RAM minimum
- 500 MB d'espace disque
```

### **1. Clone du Projet**
```bash
git clone [URL_DU_REPOSITORY]
cd GestionScolarite
```

### **2. Configuration de la Base de Données**
```bash
# Démarrer XAMPP et MySQL
# Importer le schéma de base de données
mysql -u root -p < Database_Schema_Complete.sql

# Ou utiliser le script de configuration rapide
.\Database_Setup_Script.bat
```

### **3. Configuration de l'Application**
Modifier le fichier `Database_Config.properties` :
```properties
# Configuration Base de Données
db.url=jdbc:mysql://localhost:3306/gestion_scolarite
db.username=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver

# Configuration Pool de Connexions
db.pool.minimum=5
db.pool.maximum=20
db.pool.timeout=30000
```

### **4. Compilation et Exécution**
```bash
# Compilation du projet
mvn clean compile

# Génération du JAR
mvn package

# Exécution de l'application
java -jar target/GestionScolarite-1.0-SNAPSHOT.jar

# Ou via Maven
mvn exec:java -Dexec.mainClass="Main"
```

---

## 🔐 Comptes de Test

L'application fonctionne en **mode démonstration** avec les comptes suivants :

| Rôle | Nom d'utilisateur | Mot de passe | Niveau d'accès |
|------|-------------------|--------------|----------------|
| **Admin** | `admin` | `admin123` | Accès complet système |
| **Enseignant** | `enseignant` | `enseignant123` | Gestion des notes |
| **Étudiant** | `etudiant` | `etudiant123` | Consultation notes |
| **Scolarité** | `scolarite` | `scolarite123` | Gestion étudiants |
| **Direction** | `direction` | `direction123` | Rapports et statistiques |

---

## 🏗️ Architecture du Système

### **Structure du Projet**
```
src/main/java/
├── config/           # Configuration et connexions DB
├── dao/              # Couche d'accès aux données
├── exceptions/       # Gestion des exceptions
├── models/           # Modèles de données
├── security/         # Sécurité et authentification
├── services/         # Logique métier
├── utils/            # Utilitaires et helpers
└── views/            # Interfaces utilisateur
    ├── admin/        # Interfaces administrateur
    ├── components/   # Composants réutilisables
    ├── direction/    # Interfaces direction
    ├── enseignant/   # Interfaces enseignant
    ├── etudiant/     # Interfaces étudiant
    └── scolarite/    # Interfaces scolarité
```

### **Modèle de Base de Données**
- **AnneeScolaire** - Gestion des années universitaires
- **Utilisateur** - Comptes et authentification
- **Etudiant** - Informations étudiants
- **Enseignant** - Informations enseignants
- **Programme** - Filières et spécialités
- **Matiere** - Matières d'enseignement
- **Epreuve** - Examens et évaluations
- **NoteEpreuve** - Notes des épreuves
- **NoteMatiere** - Notes finales par matière
- **Inscription** - Inscriptions étudiants

---

## 📊 Fonctionnalités Principales

### **✅ Gestion des Utilisateurs**
- Authentification sécurisée avec BCrypt
- Gestion des rôles et permissions
- Sessions utilisateur avec timeout automatique
- Logs d'activité et audit trail

### **✅ Gestion Académique**
- Création et gestion des programmes d'études
- Inscription et suivi des étudiants
- Planification des épreuves et examens
- Calcul automatique des moyennes pondérées

### **✅ Saisie et Consultation des Notes**
- Interface intuitive pour la saisie des notes
- Validation automatique des données
- Consultation en temps réel des résultats
- Historique complet des modifications

### **✅ Rapports et Statistiques**
- Bulletins de notes officiels (PDF)
- Export des données vers Excel
- Statistiques de réussite par matière
- Tableaux de bord avec KPI

### **✅ Interface Moderne**
- Design moderne et responsive
- Navigation intuitive par onglets
- Notifications toast pour le feedback
- Composants UI personnalisés

---

## 🛠️ Améliorations Techniques Apportées

### **✅ Résolution de 100+ Erreurs de Compilation**
- Compatibilité Java 23 assurée
- Résolution des conflits d'imports
- Correction des signatures de méthodes
- Amélioration de la gestion des exceptions

### **✅ Architecture Améliorée**
- Séparation claire MVC (Model-View-Controller)
- Services métier structurés
- Couche DAO optimisée
- Gestion centralisée des configurations

### **✅ Performance et Sécurité**
- Pool de connexions HikariCP
- Chiffrement BCrypt des mots de passe
- Validation des entrées utilisateur
- Gestion optimisée de la mémoire

---

## 📈 Statistiques du Projet

- **📁 Fichiers Source** : 68 classes Java
- **📊 Lignes de Code** : 8,500+ lignes
- **⚡ Temps de Compilation** : ~4.5 secondes
- **🚀 Démarrage Application** : ~384ms
- **🔧 Erreurs Corrigées** : 100+ erreurs → 0 erreur

---

## 🤝 Équipe de Développement

**Développé par :**
- Étudiants de 3ème année Licence ISIL
- Encadrement : Département d'Informatique
- Université Ibn Khaldoun - Tiaret

---

## 📅 Planning du Projet

- **📅 Deadline** : 17 Novembre 2025
- **🗣️ Consultation** : 18-19 Novembre 2025
- **✅ État** : Projet terminé et fonctionnel

---

## 📝 License

Ce projet est développé dans un cadre académique pour l'Université Ibn Khaldoun - Tiaret.

---

## 📞 Support

Pour toute question ou assistance technique, veuillez contacter le département d'informatique de l'Université Ibn Khaldoun - Tiaret.

---

*Généré automatiquement par le système de gestion scolarité - Université Ibn Khaldoun Tiaret*