-- =====================================================================================
-- SCHÉMA DE BASE DE DONNÉES FIXÉ - GESTION SCOLARITÉ
-- Compatible avec toutes les versions MySQL (5.7, 8.0, 8.4)
-- Université Ibn Khaldoun - Tiaret
-- =====================================================================================

-- Configuration initiale
SET FOREIGN_KEY_CHECKS = 0;
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- Création de la base de données
DROP DATABASE IF EXISTS gestion_scolarite;
CREATE DATABASE gestion_scolarite 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;
USE gestion_scolarite;

-- =====================================================================================
-- 1. TABLES PRINCIPALES
-- =====================================================================================

-- Table ETUDIANT
CREATE TABLE etudiant (
    id_etudiant INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    origine_scolaire VARCHAR(50) NOT NULL COMMENT 'DUT, CPI, CPGE',
    date_naissance DATE NULL,
    email VARCHAR(150) UNIQUE,
    telephone VARCHAR(20),
    adresse TEXT,
    date_inscription DATE NULL,
    
    KEY idx_nom_prenom (nom, prenom),
    KEY idx_email (email),
    KEY idx_origine (origine_scolaire)
) ENGINE=InnoDB COMMENT='Table des étudiants';

-- Table ENSEIGNANT
CREATE TABLE enseignant (
    id_enseignant INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    grade VARCHAR(100) COMMENT 'Professeur, Maître de Conférences, etc.',
    email VARCHAR(150) UNIQUE,
    telephone VARCHAR(20),
    specialite VARCHAR(200),
    
    KEY idx_nom_prenom (nom, prenom),
    KEY idx_grade (grade),
    KEY idx_specialite (specialite)
) ENGINE=InnoDB COMMENT='Table des enseignants';

-- Table ANNEE_SCOLAIRE
CREATE TABLE annee_scolaire (
    id_annee INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(30) NOT NULL UNIQUE COMMENT 'Ex: 2025/2026',
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    active BOOLEAN DEFAULT FALSE,
    
    KEY idx_active (active),
    KEY idx_dates (date_debut, date_fin),
    
    CONSTRAINT chk_dates CHECK (date_fin > date_debut)
) ENGINE=InnoDB COMMENT='Années scolaires';

-- Table PROGRAMME
CREATE TABLE programme (
    id_programme INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    annee_etude INT NOT NULL,
    description TEXT,
    niveau VARCHAR(50) COMMENT 'L1, L2, L3, M1, M2',
    specialite VARCHAR(100),
    duree_en_semestres INT DEFAULT 2,
    actif BOOLEAN DEFAULT TRUE,
    
    KEY idx_code (code),
    KEY idx_annee_etude (annee_etude),
    KEY idx_niveau (niveau),
    KEY idx_actif (actif),
    
    CONSTRAINT chk_annee_etude CHECK (annee_etude BETWEEN 1 AND 3)
) ENGINE=InnoDB COMMENT='Programmes d\'études';

-- Table MATIERE
CREATE TABLE matiere (
    id_matiere INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    objectif TEXT,
    semestre INT COMMENT '1 ou 2',
    
    KEY idx_nom (nom),
    KEY idx_semestre (semestre),
    
    CONSTRAINT chk_semestre CHECK (semestre IN (1, 2))
) ENGINE=InnoDB COMMENT='Matières enseignées';

-- Table INSCRIPTION
CREATE TABLE inscription (
    id_inscription INT PRIMARY KEY AUTO_INCREMENT,
    id_etudiant INT NOT NULL,
    id_programme INT NOT NULL,
    id_annee INT NOT NULL,
    moyenne_generale DECIMAL(5,2),
    statut_fin_annee ENUM('admis', 'redoublant', 'exclu', 'en_cours') DEFAULT 'en_cours',
    date_inscription DATE NULL,
    
    UNIQUE KEY unique_inscription (id_etudiant, id_programme, id_annee),
    KEY idx_etudiant_annee (id_etudiant, id_annee),
    KEY idx_statut (statut_fin_annee),
    KEY idx_moyenne (moyenne_generale),
    
    CONSTRAINT chk_moyenne CHECK (moyenne_generale BETWEEN 0 AND 20),
    
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant) ON DELETE CASCADE,
    FOREIGN KEY (id_programme) REFERENCES programme(id_programme) ON DELETE CASCADE,
    FOREIGN KEY (id_annee) REFERENCES annee_scolaire(id_annee) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Inscriptions des étudiants';

-- Table EPREUVE
CREATE TABLE epreuve (
    id_epreuve INT PRIMARY KEY AUTO_INCREMENT,
    type_epreuve VARCHAR(50) NOT NULL COMMENT 'Contrôle, Examen, TP, Projet',
    intitule VARCHAR(200),
    date_epreuve DATE,
    coefficient DECIMAL(4,2) NOT NULL DEFAULT 1.0,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    id_matiere INT NOT NULL,
    id_enseignant INT NOT NULL,
    id_annee INT NOT NULL,
    
    KEY idx_type (type_epreuve),
    KEY idx_matiere_annee (id_matiere, id_annee),
    KEY idx_enseignant (id_enseignant),
    KEY idx_date (date_epreuve),
    KEY idx_active (active),
    
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere) ON DELETE CASCADE,
    FOREIGN KEY (id_enseignant) REFERENCES enseignant(id_enseignant) ON DELETE CASCADE,
    FOREIGN KEY (id_annee) REFERENCES annee_scolaire(id_annee) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Épreuves d\'évaluation';

-- Table NOTE_EPREUVE
CREATE TABLE note_epreuve (
    id_note_epreuve INT PRIMARY KEY AUTO_INCREMENT,
    id_etudiant INT NOT NULL,
    id_epreuve INT NOT NULL,
    note DECIMAL(5,2),
    date_saisie DATETIME DEFAULT CURRENT_TIMESTAMP,
    modifie_par INT COMMENT 'id_enseignant qui a modifié',
    commentaire TEXT,
    
    UNIQUE KEY unique_note_epreuve (id_etudiant, id_epreuve),
    KEY idx_etudiant (id_etudiant),
    KEY idx_epreuve (id_epreuve),
    KEY idx_note (note),
    KEY idx_date_saisie (date_saisie),
    
    CONSTRAINT chk_note CHECK (note BETWEEN 0 AND 20),
    
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant) ON DELETE CASCADE,
    FOREIGN KEY (id_epreuve) REFERENCES epreuve(id_epreuve) ON DELETE CASCADE,
    FOREIGN KEY (modifie_par) REFERENCES enseignant(id_enseignant) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='Notes des épreuves';

-- Table NOTE_MATIERE
CREATE TABLE note_matiere (
    id_note_matiere INT PRIMARY KEY AUTO_INCREMENT,
    id_etudiant INT NOT NULL,
    id_matiere INT NOT NULL,
    id_annee INT NOT NULL,
    note_finale DECIMAL(5,2),
    validee BOOLEAN DEFAULT FALSE,
    date_validation DATE,
    
    UNIQUE KEY unique_note_matiere (id_etudiant, id_matiere, id_annee),
    KEY idx_etudiant_annee (id_etudiant, id_annee),
    KEY idx_matiere (id_matiere),
    KEY idx_validee (validee),
    KEY idx_note_finale (note_finale),
    
    CONSTRAINT chk_note_finale CHECK (note_finale BETWEEN 0 AND 20),
    
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant) ON DELETE CASCADE,
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere) ON DELETE CASCADE,
    FOREIGN KEY (id_annee) REFERENCES annee_scolaire(id_annee) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Notes finales par matière';

-- Table UTILISATEUR
CREATE TABLE utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt',
    role ENUM('ETUDIANT', 'ENSEIGNANT', 'SCOLARITE', 'DIRECTION', 'ADMIN') NOT NULL,
    id_etudiant INT NULL,
    id_enseignant INT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME NULL,
    
    KEY idx_login (login),
    KEY idx_role (role),
    KEY idx_actif (actif),
    
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant) ON DELETE CASCADE,
    FOREIGN KEY (id_enseignant) REFERENCES enseignant(id_enseignant) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Utilisateurs du système';

-- =====================================================================================
-- 2. DONNÉES D'INITIALISATION
-- =====================================================================================

-- Années scolaires
INSERT INTO annee_scolaire (libelle, date_debut, date_fin, active) VALUES
('2023/2024', '2023-09-01', '2024-06-30', FALSE),
('2024/2025', '2024-09-01', '2025-06-30', FALSE),
('2025/2026', '2025-09-01', '2026-06-30', TRUE);

-- Programmes d'études
INSERT INTO programme (code, nom, annee_etude, description, niveau, specialite, duree_en_semestres, actif) VALUES
('ING1_TC', 'Ingénieur 1ère année - Tronc Commun', 1, 'Première année commune à tous les étudiants ingénieurs', 'L3', 'Tronc Commun', 2, TRUE),
('ING2_GI', 'Ingénieur 2ème année - Génie Informatique', 2, 'Spécialisation en Génie Informatique', 'M1', 'Informatique', 2, TRUE),
('ING2_GM', 'Ingénieur 2ème année - Génie Mécanique', 2, 'Spécialisation en Génie Mécanique', 'M1', 'Mécanique', 2, TRUE),
('MSI', 'Master Modélisation et Simulation Informatiques', 2, 'Master en modélisation informatique', 'M1', 'Informatique', 2, TRUE),
('TSI', 'Master Traitement et Sécurité de l Information', 2, 'Master en sécurité informatique', 'M1', 'Sécurité', 2, TRUE);

-- Matières
INSERT INTO matiere (nom, objectif, semestre) VALUES
('Algorithmique Avancée', 'Maîtriser les structures de données complexes et les algorithmes optimisés', 1),
('Base de Données', 'Conception, implémentation et administration de bases de données', 1),
('Programmation Orientée Objet', 'Maîtriser les concepts POO avec Java et C++', 1),
('Réseaux Informatiques', 'Comprendre les protocoles réseau et l architecture TCP/IP', 2),
('Génie Logiciel', 'Méthodologies de développement logiciel (Agile, DevOps)', 2),
('Intelligence Artificielle', 'IA, Machine Learning et Deep Learning', 2),
('Systèmes d Exploitation', 'Fonctionnement interne des OS Unix/Linux/Windows', 1),
('Mathématiques pour l Ingénieur', 'Algèbre linéaire, analyse numérique, statistiques', 1),
('Cybersécurité', 'Sécurité des systèmes d information et cryptographie', 2),
('Architecture des Systèmes', 'Conception d architectures distribuées et microservices', 2);

-- Enseignants
INSERT INTO enseignant (nom, prenom, grade, email, telephone, specialite) VALUES
('BENALI', 'Ahmed', 'Professeur', 'a.benali@univ-tiaret.dz', '0550123456', 'Intelligence Artificielle'),
('HAMDI', 'Fatima', 'Maître de Conférences', 'f.hamdi@univ-tiaret.dz', '0661234567', 'Base de Données'),
('CHERIF', 'Mohamed', 'Professeur', 'm.cherif@univ-tiaret.dz', '0772345678', 'Réseaux et Sécurité'),
('KADRI', 'Amina', 'Maître de Conférences', 'a.kadri@univ-tiaret.dz', '0553456789', 'Génie Logiciel'),
('MEZIANE', 'Karim', 'Professeur', 'k.meziane@univ-tiaret.dz', '0664567890', 'Systèmes Distribués');

-- Étudiants de test
INSERT INTO etudiant (nom, prenom, origine_scolaire, date_naissance, email, telephone, adresse, date_inscription) VALUES
('BOUZIDI', 'Sara', 'DUT', '2000-03-15', 'sara.bouzidi@etu.univ-tiaret.dz', '0771234567', 'Tiaret', '2024-09-01'),
('MANSOURI', 'Yacine', 'CPGE', '1999-11-22', 'y.mansouri@etu.univ-tiaret.dz', '0552345678', 'Oran', '2024-09-01'),
('BELKACEM', 'Aicha', 'CPI', '2000-07-08', 'a.belkacem@etu.univ-tiaret.dz', '0663456789', 'Alger', '2024-09-01'),
('ZIDANE', 'Omar', 'DUT', '2001-01-30', 'o.zidane@etu.univ-tiaret.dz', '0774567890', 'Tiaret', '2024-09-01'),
('AMRANI', 'Leila', 'CPGE', '2000-05-12', 'l.amrani@etu.univ-tiaret.dz', '0555678901', 'Constantine', '2024-09-01');

-- Utilisateurs du système (mots de passe = nom d'utilisateur + "123")
INSERT INTO utilisateur (login, password_hash, role, id_etudiant, id_enseignant, actif) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye/VjPuqw.HvlMd/3Jz2.9Y5rY0ZLEN0K', 'ADMIN', NULL, NULL, TRUE),
('sara.bouzidi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ETUDIANT', 1, NULL, TRUE),
('yacine.mansouri', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ETUDIANT', 2, NULL, TRUE),
('prof.benali', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ENSEIGNANT', NULL, 1, TRUE),
('prof.hamdi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ENSEIGNANT', NULL, 2, TRUE),
('scolarite', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SCOLARITE', NULL, NULL, TRUE),
('direction', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DIRECTION', NULL, NULL, TRUE);

-- Inscriptions de test
INSERT INTO inscription (id_etudiant, id_programme, id_annee, statut_fin_annee, date_inscription) VALUES
(1, 1, 3, 'en_cours', '2025-09-01'),
(2, 2, 3, 'en_cours', '2025-09-01'),
(3, 1, 3, 'en_cours', '2025-09-01'),
(4, 1, 3, 'en_cours', '2025-09-01'),
(5, 2, 3, 'en_cours', '2025-09-01');

-- Finalisation
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- Message de confirmation
SELECT 'Base de données GESTION_SCOLARITE créée avec succès!' AS message,
       (SELECT COUNT(*) FROM etudiant) AS nb_etudiants,
       (SELECT COUNT(*) FROM enseignant) AS nb_enseignants,
       (SELECT COUNT(*) FROM utilisateur) AS nb_utilisateurs,
       (SELECT COUNT(*) FROM programme) AS nb_programmes;