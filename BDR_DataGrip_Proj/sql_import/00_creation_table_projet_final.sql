DROP SCHEMA IF EXISTS bdrProject CASCADE;
CREATE SCHEMA bdrProject;
COMMENT ON SCHEMA bdrProject IS 'Project BDR Graf Calvin, Lattion Lucas, Sottile Alan';

set search_path to bdrProject;

create table Utilisateur(
    email varchar(30),
    prenom varchar(15) NOT NULL,
    nom varchar(15) NOT NULL,
    motDePasse varchar(20) NOT NULL,
    primary key (email)
);

create table SousGroupe(
    sgnom varchar(30),
    parentID varchar(30),
    primary key (sgnom),
    FOREIGN KEY (parentID) REFERENCES SousGroupe(sgnom)
);

create table Aliment(
    anom varchar(80),
    kcal int NOT NULL,
    proteines numeric(8,2) NOT NULL,
    glucides numeric(8,2) NOT NULL,
    lipides numeric(8,2) NOT NULL,
    fibres numeric(8,2) NOT NULL,
    sodium numeric(8,2) NOT NULL,
    groupe varchar(30) NOT NULL,
    primary key (anom),
    FOREIGN KEY (groupe) REFERENCES sousgroupe(sgnom)
);

create table Regime(
    regnom varchar(30),
    primary key (regnom)
);

create table Liste(
    lnom varchar(30),
    email varchar(30),
    primary key (lnom, email),
    FOREIGN KEY (email) REFERENCES Utilisateur(email)
);

create table Ustensil(
    unom varchar(30),
    primary key (unom)
);

create table Type(
    tnom varchar(30),
    primary key (tnom)
);

create table Recette(
    rnom varchar(80),
    instructions text NOT NULL,
    type_recette varchar(30) NOT NULL,
    primary key (rnom),
    FOREIGN KEY (type_recette) REFERENCES Type(tnom)
);


create table Vitamine(
    vinom varchar(30),
    primary key (vinom)
);

create table UniteDeMesure(
    udmnom varchar(30),
    primary key (udmnom)
);

CREATE TABLE Utilisateur_possede_Aliment(
    email varchar(30),
    anom varchar(80),
    quantite numeric NOT NULL,
    unite_mesure varchar(30) NOT NULL,
    primary key (email, anom)
);

CREATE TABLE Utilisateur_cache_Aliment(
    email varchar(30),
    anom varchar(80),
    primary key (email, anom)
);

CREATE TABLE Utilisateur_suit_Regime(
    email varchar(30),
    regnom varchar(30),
    primary key (email, regnom)
);

CREATE TABLE Utilisateur_cache_Recette(
    email varchar(30),
    rnom varchar(80),
    primary key (email, rnom)
);

CREATE TABLE Utilisateur_cache_SousGroupe(
    email varchar(30),
    sgnom varchar(30),
    primary key (email, sgnom)
);

CREATE TABLE Liste_contient_Recette(
    lnom varchar(30),
	email varchar(30),
    rnom varchar(80),
    primary key (lnom, email, rnom)
);

CREATE TABLE Recette_utilise_Ustensil(
    rnom varchar(80),
    unom varchar(30),
    primary key (rnom, unom)
);


CREATE TABLE Recette_contient_Aliment(
    rnom varchar(50),
    anom varchar(120),
    quantite numeric NOT NULL ,
    unite_mesure varchar(30) NOT NULL ,
    primary key (rnom, anom)
);

CREATE TABLE Aliment_contient_Vitamine(
    anom varchar(80),
    vinom varchar(30),
    quantite numeric(8,2) NOT NULL,
    primary key (anom, vinom)
);

ALTER TABLE Utilisateur_possede_Aliment
    ADD CONSTRAINT Utilisateur_possede_Aliment_user_fkey FOREIGN KEY (email) REFERENCES Utilisateur(email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Utilisateur_possede_Aliment
    ADD CONSTRAINT Utilisateur_possede_Aliment_aliment_fkey FOREIGN KEY (anom) REFERENCES Aliment(anom) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE Utilisateur_possede_Aliment
    ADD CONSTRAINT Utilisateur_possede_Aliment_uniteDeMesure_fkey FOREIGN KEY (unite_mesure) REFERENCES UniteDeMesure(udmnom) ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE Utilisateur_cache_Aliment
    ADD CONSTRAINT Utilisateur_cache_Aliment_user_fkey FOREIGN KEY (email) REFERENCES Utilisateur(email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Utilisateur_cache_Aliment
    ADD CONSTRAINT Utilisateur_cache_Aliment_aliment_fkey FOREIGN KEY (anom) REFERENCES Aliment(anom) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE Utilisateur_suit_Regime
    ADD CONSTRAINT Utilisateur_suit_Regime_user_fkey FOREIGN KEY (email) REFERENCES Utilisateur(email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Utilisateur_suit_Regime
    ADD CONSTRAINT Utilisateur_suit_Regime_regime_fkey FOREIGN KEY (regnom) REFERENCES Regime(regnom) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE Utilisateur_cache_Recette
    ADD CONSTRAINT Utilisateur_cache_Recette_user_fkey FOREIGN KEY (email) REFERENCES Utilisateur(email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Utilisateur_cache_Recette
    ADD CONSTRAINT Utilisateur_cache_Recette_recette_fkey FOREIGN KEY (rnom) REFERENCES Recette(rnom) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE Utilisateur_cache_SousGroupe
    ADD CONSTRAINT Utilisateur_cache_SousGroupe_user_fkey FOREIGN KEY (email) REFERENCES Utilisateur(email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Utilisateur_cache_SousGroupe
    ADD CONSTRAINT Utilisateur_cache_SousGroupe_sousGroupe_fkey FOREIGN KEY (sgnom) REFERENCES SousGroupe(sgnom) ON UPDATE CASCADE ON DELETE CASCADE ;

ALTER TABLE Liste_contient_Recette
    ADD CONSTRAINT Liste_contient_Recette_liste_fkey FOREIGN KEY (lnom, email) REFERENCES Liste(lnom, email) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Liste_contient_Recette
    ADD CONSTRAINT Liste_contient_Recette_recette_fkey FOREIGN KEY (rnom) REFERENCES Recette(rnom) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE Recette_utilise_Ustensil
    ADD CONSTRAINT Recette_utilise_Ustensil_recette_fkey FOREIGN KEY (rnom) REFERENCES Recette(rnom) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Recette_utilise_Ustensil
    ADD CONSTRAINT Recette_utilise_Ustensil_ustensil_fkey FOREIGN KEY (unom) REFERENCES Ustensil(unom) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE Recette_contient_Aliment
    ADD CONSTRAINT Recette_contient_Aliment_recette_fkey FOREIGN KEY (rnom) REFERENCES Recette(rnom) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Recette_contient_Aliment
    ADD CONSTRAINT Recette_contient_Aliment_aliment_fkey FOREIGN KEY (anom) REFERENCES Aliment(anom) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE Recette_contient_Aliment
    ADD CONSTRAINT Recette_contient_Aliment_uniteDeMesure_fkey FOREIGN KEY (unite_mesure) REFERENCES UniteDeMesure(udmnom) ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE Aliment_contient_Vitamine
    ADD CONSTRAINT Aliment_contient_Vitamine_aliment_fkey FOREIGN KEY (anom) REFERENCES Aliment(anom) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE Aliment_contient_Vitamine
    ADD CONSTRAINT Aliment_contient_Vitamine_vitamine_fkey FOREIGN KEY (vinom) REFERENCES Vitamine(vinom) ON UPDATE CASCADE ON DELETE RESTRICT;

set search_path to bdrProject;

-- Insertion des Utilisateurs (26)
INSERT INTO Utilisateur(email, prenom, nom, motDePasse) VALUES
('alan.sottile@heig-vd.ch', 'Alan', 'Sottile', 'passAlan123'),
('calvin.graf@heig-vd.ch', 'Calvin', 'Graf', 'passCalvin123'),
('lucas.lattion@heig-vd.ch', 'Lucas', 'Lattion', 'passLucas123'),
('alice.durand@email.com', 'Alice', 'Durand', 'passAlice123'),
('marc.dupont@email.com', 'Marc', 'Dupont', 'passMarc123'),
('julie.lemoine@email.com', 'Julie', 'Lemoine', 'passJulie123'),
('lucas.bernard@email.com', 'Lucas', 'Bernard', 'passLucas123'),
('emilie.petit@email.com', 'Emilie', 'Petit', 'passEmilie123'),
('pierre.martin@email.com', 'Pierre', 'Martin', 'passPierre123'),
('sophie.dubois@email.com', 'Sophie', 'Dubois', 'passSophie123'),
('jean.dupuis@email.com', 'Jean', 'Dupuis', 'passJean123'),
('marie.roux@email.com', 'Marie', 'Roux', 'passMarie123'),
('antoine.girard@email.com', 'Antoine', 'Girard', 'passAntoine123'),
('camille.moreau@email.com', 'Camille', 'Moreau', 'passCamille123'),
('nicolas.lefebvre@email.com', 'Nicolas', 'Lefebvre', 'passNicolas123'),
('laura.martin@email.com', 'Laura', 'Martin', 'laura123'),
('david.robert@email.com', 'David', 'Robert', 'david123'),
('estelle.marchand@email.com', 'Estelle', 'Marchand', 'estellePass123'),
('bruno.lemoine@email.com', 'Bruno', 'Lemoine', 'brunoPass123'),
('claire.dupre@email.com', 'Claire', 'Dupre', 'clairePass123'),
('olivier.petit@email.com', 'Olivier', 'Petit', 'olivierPass123'),
('chloe.blanc@email.com', 'Chloe', 'Blanc', 'chloePass123'),
('remi.leroy@email.com', 'Remi', 'Leroy', 'remiPass123'),
('valerie.colin@email.com', 'Valerie', 'Colin', 'valeriePass123'),
('guillaume.michel@email.com', 'Guillaume', 'Michel', 'guillaumePass123'),
('marion.robert@email.com', 'Marion', 'Robert', 'marionPass123'),
('fabien.gerard@email.com', 'Fabien', 'Gerard', 'fabienPass123');



INSERT INTO type (tnom) VALUES
('Plat principal'),
('Entrée'),
('Dessert'),
('Salade'),
('Soupes'),
('Apéritif'),
('Accompagnement'),
('Snack'),
('Sauce'),
('Boisson'),
('Pizza'),
('Soupe');



INSERT INTO UniteDeMesure (udmnom) VALUES
('grammes'),
('cuillères à soupe'),
('cuillères à café'),
('pincée'),
('litres'),
('kilogrammes'),
('centilitres');






INSERT INTO ustensil (unom) VALUES
('Couteau'),
('Fourchette'),
('Cuillère'),
('Cuillère en bois'),
('Couteau de cuisine'),
('Plat à lasagne'),
('Cocotte'),
('Assiette'),
('Verre'),
('Bol'),
('Pinceau de cuisine'),
('Grill'),
('Tasse'),
('Spatule'),
('Poêle'),
('Poêle à crêpes'),
('Casserole'),
('Louche'),
('Pelle'),
('Fouet'),
('Planche à découper'),
('Saladier'),
('Mixeur'),
('Écumoire'),
('Rouleau à pâtisserie'),
('Passoire'),
('Ouvre-boîte'),
('Épluche-légumes'),
('Presse-agrumes'),
('Zesteur'),
('Blender'),
('Tire-bouchon'),
('Essoreuse à salade'),
('Moule à gâteau'),
('Moule à tarte'),
('Balance de cuisine'),
('Pince à barbecue'),
('Moulin à poivre'),
('Moulin à sel'),
('Four'),
('Plat de cuisson');





-- Insertion des groupes principaux
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Viandes', NULL),
('Poissons et fruits de mer', NULL),
('Fruits', NULL),
('Légumes', NULL),
('Produits laitiers', NULL),
('Céréales et légumineuses', NULL),
('Graines et noix', NULL),
('Épices et herbes', NULL),
('Champignon', NULL),
('Huiles et graisses', NULL),
('Sauces et condiments', NULL),
('Sucres et édulcorants', NULL),
('Boisson alcoolisées', NULL);


-- Création de sous-groupes pour "Viandes"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Viandes rouges', 'Viandes'),
('Volailles', 'Viandes');

-- Création de sous-sous-groupes pour "Viandes rouges" et "Volailles"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Boeuf', 'Viandes rouges'),
('Porc', 'Viandes rouges'),
('Agneau', 'Viandes rouges'),
('Poulet', 'Volailles'),
('Dinde', 'Volailles');

-- Création de sous-groupes pour "Poissons et fruits de mer"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Saumon', 'Poissons et fruits de mer'),
('Truite', 'Poissons et fruits de mer'),
('Crevettes', 'Poissons et fruits de mer'),
('Crabe', 'Poissons et fruits de mer'),
('Homard', 'Poissons et fruits de mer');

-- Création de sous-groupes pour "Fruits"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Fruits Tropicaux', 'Fruits'),
('Fruits Rouges', 'Fruits'),
('Agrumes', 'Fruits');

-- Création de sous-groupes pour "Légumes"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Légumes Verts', 'Légumes'),
('Légumes Racines', 'Légumes'),
('Légumes à Feuilles', 'Légumes');

-- Création de sous-groupes pour "Produits laitiers"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Lait', 'Produits laitiers'),
('Yaourt', 'Produits laitiers'),
('Fromage', 'Produits laitiers');


-- Création de sous-groupes pour "Céréales et légumineuses"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Riz', 'Céréales et légumineuses'),
('Quinoa', 'Céréales et légumineuses'),
('Avoine', 'Céréales et légumineuses'),
('Lentilles', 'Céréales et légumineuses');

-- Création de sous-groupes pour "Graines et noix"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Noix', 'Graines et noix'),
('Amandes', 'Graines et noix'),
('Graines de tournesol', 'Graines et noix'),
('Noisettes', 'Graines et noix');

-- Création de sous-groupes pour "Épices et herbes"
INSERT INTO SousGroupe (sgnom, parentID) VALUES
('Basilic', 'Épices et herbes'),
('Cumin', 'Épices et herbes'),
('Curry', 'Épices et herbes'),
('Origan', 'Épices et herbes');






INSERT INTO regime (regnom) VALUES
('Perte de poids'),
('Gain de masse musculaire'),
('Gain de poids'),
('Sans gluten');


INSERT INTO vitamine VALUES
('Vitamine A'),
('Vitamine B1'),
('Vitamine B2'),
('Vitamine B3'),
('Vitamine B6'),
('Vitamine B12'),
('Vitamine C'),
('Vitamine D'),
('Vitamine E');


INSERT INTO Aliment (anom, kcal, proteines, glucides, lipides, fibres, sodium, groupe) VALUES
('Ananas', 50, 0.5, 13, 0.2, 1.4, 1, 'Fruits Tropicaux'),
('Banane', 105, 1.3, 27, 0.3, 3.1, 1, 'Fruits'),
('Cannelle', 0, 0, 0, 0, 0, 0, 'Épices et herbes'),
('Champignon de Paris', 22, 3.1, 3.3, 0.3, 1, 5, 'Champignon'),
('Citron', 29, 1.1, 9.3, 0.3, 2.8, 2, 'Agrumes'),
('Courge', 34, 1, 8, 0.4, 2, 1, 'Légumes Verts'),
('Crevette, cuite', 85, 18, 0.1, 1.5, 0, 119, 'Poissons et fruits de mer'),
('Crème glacée, aux fruits', 207, 2, 24, 12, 0.5, 50, 'Produits laitiers'),
('Cuisse de poulet', 165, 31, 0, 4, 0, 74, 'Volailles'),
('Fraise', 32, 0.7, 7.7, 0.3, 2, 1, 'Fruits Rouges'),
('Framboise', 52, 1.5, 11.9, 0.6, 6.5, 1, 'Fruits Rouges'),
('Huile d''olive', 884, 0, 0, 100, 0, 0, 'Huiles et graisses'),
('Lait de coco', 354, 3.3, 3.4, 36, 2.2, 18, 'Produits laitiers'),
('Mangue', 60, 0.8, 15, 0.4, 1.6, 1, 'Fruits Tropicaux'),
('Miel', 304, 0.3, 82.1, 0.2, 0.2, 4, 'Sucres et édulcorants'),
('Moutarde', 66, 4.4, 6.1, 3.2, 2.6, 829, 'Épices et herbes'),
('Mozzarella', 280, 21, 2, 21, 0, 650, 'Produits laitiers'),
('Myrtille', 43, 0.7, 9.7, 0.4, 2.4, 1, 'Fruits Rouges'),
('Noix', 654, 15, 13, 64, 6, 2, 'Graines et noix'),
('Parmesan', 392, 35, 3.2, 25.8, 0, 1631, 'Produits laitiers'),
('Poulet entier, avec peau, cru', 215, 19, 0, 15, 0, 82, 'Volailles'),
('Pâtes', 131, 4.4, 25.7, 1.1, 1.2, 1, 'Céréales et légumineuses'),
('Sauce tomate', 32, 0.9, 7, 0.2, 2, 348, 'Sauces et condiments'),
('Saumon d''élevage, cru', 206, 20, 0, 13, 0, 47, 'Poissons et fruits de mer'),
('Saumon sauvage, cru', 206, 22, 0, 13, 0, 39, 'Poissons et fruits de mer'),
('Vinaigrette (avec huile de colza)', 120, 0, 1, 13, 0, 300, 'Sauces et condiments'),
('Yogourt aux fraises', 93, 3.4, 15.5, 2, 0.2, 50, 'Produits laitiers'),
('Yogourt, nature', 59, 3.9, 7.4, 0.4, 0, 36, 'Produits laitiers'),
('Laitue romaine', 15, 1.4, 2.9, 0.3, 2.1, 8, 'Légumes à Feuilles'),
('Sauce Worcestershire', 78, 0, 19, 0, 0, 872, 'Sauces et condiments'),
('Ail', 149, 6.4, 33, 0.5, 2.1, 17, 'Épices et herbes'),
('Poivre', 251, 10.4, 64, 3.3, 25.3, 20, 'Épices et herbes'),
('Pain', 265, 9, 49, 3.2, 2.7, 491, 'Céréales et légumineuses'),
('Sel', 0, 0, 0, 0, 0, 38758, 'Épices et herbes'),
('Poulet', 239, 27, 0, 14, 0, 82, 'Volailles'),
('Thym', 276, 9.1, 63.9, 7.4, 37, 55, 'Épices et herbes'),
('Tomate', 18, 0.9, 3.9, 0.2, 1.2, 5, 'Légumes'),
('Poivrons', 20, 0.9, 4.7, 0.2, 1.7, 3, 'Légumes Verts'),
('Concombre', 15, 0.7, 3.6, 0.1, 0.5, 2, 'Légumes'),
('Oignon', 40, 1.1, 9.3, 0.1, 1.7, 4, 'Légumes'),
('Haricots noirs', 339, 21.6, 63.3, 0.9, 16.0, 2, 'Céréales et légumineuses'),
('Pain à hamburger', 0, 9, 43, 4.2, 2.3, 490, 'Céréales et légumineuses'),
('Laitue', 15, 1.4, 2.9, 0.2, 1.3, 28, 'Légumes à Feuilles'),
('Pâte brisée', 527, 7.3, 45.2, 34.8, 2.3, 368, 'Céréales et légumineuses'),
('Pommes', 52, 0.3, 13.8, 0.2, 2.4, 1, 'Fruits'),
('Sucre', 387, 0, 100, 0, 0, 1, 'Sucres et édulcorants'),
('Carottes', 41, 0.9, 9.6, 0.2, 2.8, 69, 'Légumes Racines'),
('Pommes de terre', 77, 2.0, 17.5, 0.1, 2.2, 6, 'Légumes Racines'),
('Céleri', 16, 0.7, 3.0, 0.2, 1.6, 80, 'Légumes'),
('Poivron', 20, 0.9, 4.7, 0.2, 1.7, 3, 'Légumes Verts'),
('Aubergine', 25, 1, 6, 0.2, 3, 2, 'Légumes'),
('Courgette', 17, 1.2, 3.1, 0.3, 1, 8, 'Légumes Verts'),
('Herbe de Provence', 265, 4.9, 42.6, 7, 29, 26, 'Épices et herbes'),
('Farine', 364, 10, 76, 1, 2.7, 2, 'Céréales et légumineuses'),
('Oeuf', 155, 13, 1.1, 11, 0, 124, 'Produits laitiers'),
('Lait', 42, 3.4, 5, 1, 0, 50, 'Produits laitiers'),
('Beurre', 717, 0.9, 0.1, 81, 0, 11, 'Produits laitiers'),
('Lardon', 300, 27, 0, 20, 0, 1500, 'Viandes rouges'),
('Crème fraîche', 193, 2.1, 4, 19, 0, 41, 'Produits laitiers'),
('Noix de muscade', 525, 6, 49, 36, 21, 16, 'Épices et herbes'),
('Riz Arborio', 130, 2.7, 28, 0.3, 0, 1, 'Céréales et légumineuses'),
('Champignon', 22, 3.1, 3.3, 0.3, 1, 5, 'Champignon'),
('Bouillon de légumes', 20, 1.8, 1.9, 0.9, 0.1, 860, 'Sauces et condiments'),
('Vin blanc', 82, 0.1, 2.6, 0, 0, 4, 'Boisson alcoolisées'),
('Chocolat', 546, 5.4, 61.7, 31, 7, 24, 'Sucres et édulcorants'),
('Feuille de lasagne', 350, 12, 72, 1.5, 3, 25, 'Céréales et légumineuses'),
('Épinard', 23, 2.9, 3.6, 0.4, 2.2, 79, 'Légumes à Feuilles'),
('Ricotta', 174, 11, 3, 13, 0, 84, 'Produits laitiers'),
('Basilic', 22, 3.2, 2.7, 0.6, 1.6, 4, 'Épices et herbes'),
('Poireau', 61, 1.5, 14.2, 0.3, 1.8, 20, 'Légumes'),
('Quinoa', 120, 4.1, 21.3, 1.9, 2.8, 7, 'Céréales et légumineuses'),
('Oignon rouge', 40, 1.1, 9.3, 0.1, 1.7, 4, 'Légumes'),
('Avocat', 160, 2, 8.5, 14.7, 6.7, 7, 'Fruits'),
('Lentille', 116, 9.02, 20.13, 0.38, 7.9, 2, 'Céréales et légumineuses'),
('Carotte', 41, 0.9, 9.6, 0.2, 2.8, 69, 'Légumes Racines'),
('Tortilla', 218, 6, 36, 6, 2, 536, 'Céréales et légumineuses'),
('Maïs', 86, 3.2, 19, 1.2, 2.4, 15, 'Céréales et légumineuses'),
('Haricot noir', 339, 21.6, 63.3, 0.9, 16.0, 2, 'Céréales et légumineuses'),
('Citron vert', 30, 0.7, 10.6, 0.2, 2.8, 2, 'Agrumes'),
('Coriandre', 23, 2.1, 3.7, 0.5, 2.8, 46, 'Épices et herbes'),
('Huile végétale', 884, 0, 0, 100, 0, 0, 'Huiles et graisses'),
('Lait végétal', 50, 1, 9.4, 2.5, 0.5, 100, 'Produits laitiers'),
('Vinaigre de cidre', 21, 0, 0.9, 0, 0, 5, 'Sauces et condiments'),
('Vanille', 288, 0.1, 12.7, 0.1, 0, 9, 'Épices et herbes'),
('Riz basmati', 130, 2.7, 28, 0.3, 0.4, 1, 'Céréales et légumineuses'),
('Lait d''amande', 50, 1, 9.4, 2.5, 0.5, 100, 'Graines et noix'),
('Graines de chia', 486, 17, 42, 31, 34, 16, 'Graines et noix'),
('Lentilles corail', 116, 9.02, 20.13, 0.38, 7.9, 2, 'Céréales et légumineuses'),
('Tomates pelées', 22, 1.2, 4.9, 0.2, 1.4, 5, 'Légumes'),
('Cumin', 375, 18, 44, 22, 11, 168, 'Épices et herbes'),
('Paprika', 282, 14, 54, 13, 35, 68, 'Épices et herbes'),
('Lentilles vertes', 116, 9.02, 20.13, 0.38, 7.9, 2, 'Céréales et légumineuses'),
('Laurier', 313, 8, 75, 8, 26, 23, 'Épices et herbes'),
('Asperge', 20, 2.2, 3.9, 0.1, 2.1, 2, 'Légumes'),
('Feta', 264, 14, 4, 21, 0, 1116, 'Produits laitiers'),
('Romarin', 131, 3.3, 20.7, 5.9, 14.1, 26, 'Épices et herbes'),
('Brocoli', 34, 2.8, 6.6, 0.4, 2.6, 33, 'Légumes'),
('Pois', 81, 5.4, 14.5, 0.4, 5.1, 2, 'Céréales et légumineuses'),
('Tofu', 76, 8, 1.9, 4.8, 0.3, 7, 'Céréales et légumineuses'),
('Bœuf haché', 250, 26, 0, 15, 0, 75, 'Viandes'),
('Fruits assortis', 52, 0.7, 13.7, 0.2, 2.4, 1, 'Fruits'),
('Fromage frais', 98, 11, 3.3, 5, 0, 340, 'Produits laitiers'),
('Lard', 898, 0, 0, 99.5, 0, 38, 'Viandes'),
('Pâte à pizza', 131, 4.4, 25.7, 1.1, 1.2, 1, 'Céréales et légumineuses');

-- Vitamine A
INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine A', 120),
('Banane', 'Vitamine A', 140),
('Cannelle', 'Vitamine A', 10),
('Champignon de Paris', 'Vitamine A', 15),
('Citron', 'Vitamine A', 30),
('Courge', 'Vitamine A', 300),
('Crevette, cuite', 'Vitamine A', 80),
('Crème glacée, aux fruits', 'Vitamine A', 50),
('Cuisse de poulet', 'Vitamine A', 100),
('Fraise', 'Vitamine A', 60),
('Framboise', 'Vitamine A', 70),
('Lait de coco', 'Vitamine A', 5),
('Mangue', 'Vitamine A', 180),
('Moutarde', 'Vitamine A', 25),
('Mozzarella', 'Vitamine A', 200),
('Myrtille', 'Vitamine A', 90),
('Noix', 'Vitamine A', 40),
('Parmesan', 'Vitamine A', 250),
('Poulet entier, avec peau, cru', 'Vitamine A', 110),
('Sauce tomate', 'Vitamine A', 190),
('Saumon d''élevage, cru', 'Vitamine A', 220),
('Saumon sauvage, cru', 'Vitamine A', 210),
('Vinaigrette (avec huile de colza)', 'Vitamine A', 5),
('Yogourt aux fraises', 'Vitamine A', 60),
('Yogourt, nature', 'Vitamine A', 70),
('Laitue romaine', 'Vitamine A', 250),
('Sauce Worcestershire', 'Vitamine A', 20),
('Ail', 'Vitamine A', 15),
('Poivre', 'Vitamine A', 10),
('Poulet', 'Vitamine A', 100),
('Thym', 'Vitamine A', 30),
('Tomate', 'Vitamine A', 200),
('Poivrons', 'Vitamine A', 210),
('Concombre', 'Vitamine A', 20),
('Oignon', 'Vitamine A', 40),
('Haricots noirs', 'Vitamine A', 60),
('Laitue', 'Vitamine A', 250),
('Pommes', 'Vitamine A', 50),
('Carottes', 'Vitamine A', 830),
('Pommes de terre', 'Vitamine A', 10),
('Céleri', 'Vitamine A', 40),
('Poivron', 'Vitamine A', 190),
('Aubergine', 'Vitamine A', 20),
('Courgette', 'Vitamine A', 30),
('Herbe de Provence', 'Vitamine A', 50),
('Oeuf', 'Vitamine A', 140),
('Lait', 'Vitamine A', 60),
('Beurre', 'Vitamine A', 680),
('Lardon', 'Vitamine A', 70),
('Crème fraîche', 'Vitamine A', 90),
('Noix de muscade', 'Vitamine A', 20),
('Champignon', 'Vitamine A', 10),
('Bouillon de légumes', 'Vitamine A', 5),
('Chocolat', 'Vitamine A', 30),
('Épinard', 'Vitamine A', 480),
('Ricotta', 'Vitamine A', 180),
('Basilic', 'Vitamine A', 40),
('Poireau', 'Vitamine A', 80),
('Oignon rouge', 'Vitamine A', 40),
('Avocat', 'Vitamine A', 160),
('Carotte', 'Vitamine A', 830),
('Maïs', 'Vitamine A', 190),
('Haricot noir', 'Vitamine A', 60),
('Citron vert', 'Vitamine A', 30),
('Coriandre', 'Vitamine A', 50),
('Lait végétal', 'Vitamine A', 10);


-- Insertion des valeurs de Vitamine B1 pour chaque aliment
INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine B1', 0.15),
('Banane', 'Vitamine B1', 0.18),
('Cannelle', 'Vitamine B1', 0.01),
('Champignon de Paris', 'Vitamine B1', 0.05),
('Citron', 'Vitamine B1', 0.03),
('Courge', 'Vitamine B1', 0.04),
('Crevette, cuite', 'Vitamine B1', 0.02),
('Crème glacée, aux fruits', 'Vitamine B1', 0.01),
('Cuisse de poulet', 'Vitamine B1', 0.07),
('Fraise', 'Vitamine B1', 0.03),
('Framboise', 'Vitamine B1', 0.04),
('Mangue', 'Vitamine B1', 0.05),
('Moutarde', 'Vitamine B1', 0.06),
('Mozzarella', 'Vitamine B1', 0.01),
('Myrtille', 'Vitamine B1', 0.03),
('Noix', 'Vitamine B1', 0.2),
('Parmesan', 'Vitamine B1', 0.01),
('Poulet entier, avec peau, cru', 'Vitamine B1', 0.06),
('Sauce tomate', 'Vitamine B1', 0.04),
('Saumon d''élevage, cru', 'Vitamine B1', 0.15),
('Saumon sauvage, cru', 'Vitamine B1', 0.18),
('Yogourt aux fraises', 'Vitamine B1', 0.02),
('Yogourt, nature', 'Vitamine B1', 0.03),
('Laitue romaine', 'Vitamine B1', 0.05),
('Ail', 'Vitamine B1', 0.08),
('Poivre', 'Vitamine B1', 0.04),
('Thym', 'Vitamine B1', 0.07),
('Tomate', 'Vitamine B1', 0.04),
('Poivrons', 'Vitamine B1', 0.06),
('Concombre', 'Vitamine B1', 0.03),
('Oignon', 'Vitamine B1', 0.04),
('Haricots noirs', 'Vitamine B1', 0.15),
('Laitue', 'Vitamine B1', 0.05),
('Pommes', 'Vitamine B1', 0.02),
('Carottes', 'Vitamine B1', 0.07),
('Pommes de terre', 'Vitamine B1', 0.1),
('Céleri', 'Vitamine B1', 0.05),
('Poivron', 'Vitamine B1', 0.06),
('Aubergine', 'Vitamine B1', 0.03),
('Courgette', 'Vitamine B1', 0.03),
('Herbe de Provence', 'Vitamine B1', 0.06),
('Oeuf', 'Vitamine B1', 0.04),
('Lait', 'Vitamine B1', 0.03),
('Beurre', 'Vitamine B1', 0.01),
('Lardon', 'Vitamine B1', 0.09),
('Crème fraîche', 'Vitamine B1', 0.01),
('Noix de muscade', 'Vitamine B1', 0.05),
('Riz Arborio', 'Vitamine B1', 0.1),
('Champignon', 'Vitamine B1', 0.05),
('Épinard', 'Vitamine B1', 0.08),
('Ricotta', 'Vitamine B1', 0.02),
('Basilic', 'Vitamine B1', 0.06),
('Poireau', 'Vitamine B1', 0.04),
('Quinoa', 'Vitamine B1', 0.12),
('Oignon rouge', 'Vitamine B1', 0.04),
('Avocat', 'Vitamine B1', 0.07),
('Lentille', 'Vitamine B1', 0.15),
('Maïs', 'Vitamine B1', 0.09),
('Haricot noir', 'Vitamine B1', 0.15),
('Citron vert', 'Vitamine B1', 0.03),
('Coriandre', 'Vitamine B1', 0.07);


-- Insertion des valeurs de Vitamine B2 pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine B2', 0.05),
('Banane', 'Vitamine B2', 0.08),
('Cannelle', 'Vitamine B2', 0.03),
('Champignon de Paris', 'Vitamine B2', 0.11),
('Citron', 'Vitamine B2', 0.02),
('Courge', 'Vitamine B2', 0.04),
('Crevette, cuite', 'Vitamine B2', 0.1),
('Crème glacée, aux fruits', 'Vitamine B2', 0.07),
('Cuisse de poulet', 'Vitamine B2', 0.15),
('Fraise', 'Vitamine B2', 0.07),
('Framboise', 'Vitamine B2', 0.08),
('Huile d''olive', 'Vitamine B2', 0.01),
('Lait de coco', 'Vitamine B2', 0.03),
('Mangue', 'Vitamine B2', 0.06),
('Miel', 'Vitamine B2', 0.01),
('Moutarde', 'Vitamine B2', 0.05),
('Mozzarella', 'Vitamine B2', 0.18),
('Myrtille', 'Vitamine B2', 0.09),
('Noix', 'Vitamine B2', 0.03),
('Parmesan', 'Vitamine B2', 0.21),
('Poulet entier, avec peau, cru', 'Vitamine B2', 0.17),
('Pâtes', 'Vitamine B2', 0.05),
('Sauce tomate', 'Vitamine B2', 0.04),
('Saumon d''élevage, cru', 'Vitamine B2', 0.22),
('Saumon sauvage, cru', 'Vitamine B2', 0.2),
('Vinaigrette (avec huile de colza)', 'Vitamine B2', 0.01),
('Yogourt aux fraises', 'Vitamine B2', 0.1),
('Yogourt, nature', 'Vitamine B2', 0.09),
('Laitue romaine', 'Vitamine B2', 0.12),
('Sauce Worcestershire', 'Vitamine B2', 0.03),
('Ail', 'Vitamine B2', 0.04),
('Poivre', 'Vitamine B2', 0.02),
('Pain', 'Vitamine B2', 0.05),
('Poulet', 'Vitamine B2', 0.17),
('Thym', 'Vitamine B2', 0.06),
('Tomate', 'Vitamine B2', 0.04),
('Poivrons', 'Vitamine B2', 0.05),
('Concombre', 'Vitamine B2', 0.02),
('Oignon', 'Vitamine B2', 0.03),
('Haricots noirs', 'Vitamine B2', 0.16),
('Pain à hamburger', 'Vitamine B2', 0.05),
('Laitue', 'Vitamine B2', 0.12),
('Pâte brisée', 'Vitamine B2', 0.05),
('Pommes', 'Vitamine B2', 0.02),
('Carottes', 'Vitamine B2', 0.05),
('Pommes de terre', 'Vitamine B2', 0.03),
('Céleri', 'Vitamine B2', 0.03),
('Poivron', 'Vitamine B2', 0.05),
('Aubergine', 'Vitamine B2', 0.04),
('Courgette', 'Vitamine B2', 0.03),
('Herbe de Provence', 'Vitamine B2', 0.07),
('Farine', 'Vitamine B2', 0.01),
('Oeuf', 'Vitamine B2', 0.15),
('Lait', 'Vitamine B2', 0.18),
('Beurre', 'Vitamine B2', 0.05),
('Lardon', 'Vitamine B2', 0.12),
('Crème fraîche', 'Vitamine B2', 0.08),
('Noix de muscade', 'Vitamine B2', 0.06),
('Riz Arborio', 'Vitamine B2', 0.01),
('Champignon', 'Vitamine B2', 0.11),
('Bouillon de légumes', 'Vitamine B2', 0.02),
('Vin blanc', 'Vitamine B2', 0.01),
('Chocolat', 'Vitamine B2', 0.12),
('Feuille de lasagne', 'Vitamine B2', 0.04),
('Épinard', 'Vitamine B2', 0.2),
('Ricotta', 'Vitamine B2', 0.14),
('Basilic', 'Vitamine B2', 0.08),
('Poireau', 'Vitamine B2', 0.05),
('Quinoa', 'Vitamine B2', 0.1),
('Oignon rouge', 'Vitamine B2', 0.03),
('Avocat', 'Vitamine B2', 0.08),
('Lentille', 'Vitamine B2', 0.11),
('Carotte', 'Vitamine B2', 0.05),
('Tortilla', 'Vitamine B2', 0.04),
('Maïs', 'Vitamine B2', 0.05),
('Haricot noir', 'Vitamine B2', 0.16),
('Citron vert', 'Vitamine B2', 0.02),
('Coriandre', 'Vitamine B2', 0.06),
('Huile végétale', 'Vitamine B2', 0.01),
('Lait végétal', 'Vitamine B2', 0.04),
('Vinaigre de cidre', 'Vitamine B2', 0.01),
('Vanille', 'Vitamine B2', 0.01),
('Pâte à pizza', 'Vitamine B2', 0.05),
('Riz basmati', 'Vitamine B2', 0.01);

-- Insertion des valeurs de Vitamine B3 pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine B3', 0.5),
('Banane', 'Vitamine B3', 0.6),
('Cannelle', 'Vitamine B3', 1.2),
('Champignon de Paris', 'Vitamine B3', 3.8),
('Citron', 'Vitamine B3', 0.1),
('Courge', 'Vitamine B3', 0.6),
('Crevette, cuite', 'Vitamine B3', 1.5),
('Crème glacée, aux fruits', 'Vitamine B3', 0.2),
('Cuisse de poulet', 'Vitamine B3', 7.8),
('Fraise', 'Vitamine B3', 0.3),
('Framboise', 'Vitamine B3', 0.4),
('Lait de coco', 'Vitamine B3', 0.2),
('Mangue', 'Vitamine B3', 0.7),
('Moutarde', 'Vitamine B3', 0.1),
('Mozzarella', 'Vitamine B3', 0.6),
('Myrtille', 'Vitamine B3', 0.4),
('Noix', 'Vitamine B3', 1),
('Parmesan', 'Vitamine B3', 0.1),
('Poulet entier, avec peau, cru', 'Vitamine B3', 10),
('Pâtes', 'Vitamine B3', 0.5),
('Sauce tomate', 'Vitamine B3', 0.9),
('Saumon d''élevage, cru', 'Vitamine B3', 8.5),
('Saumon sauvage, cru', 'Vitamine B3', 10),
('Yogourt aux fraises', 'Vitamine B3', 0.2),
('Yogourt, nature', 'Vitamine B3', 0.1),
('Laitue romaine', 'Vitamine B3', 0.3),
('Sauce Worcestershire', 'Vitamine B3', 0.7),
('Ail', 'Vitamine B3', 0.1),
('Poivre', 'Vitamine B3', 1),
('Pain', 'Vitamine B3', 4.7),
('Poulet', 'Vitamine B3', 9),
('Thym', 'Vitamine B3', 4.9),
('Tomate', 'Vitamine B3', 0.7),
('Poivrons', 'Vitamine B3', 0.5),
('Concombre', 'Vitamine B3', 0.1),
('Oignon', 'Vitamine B3', 0.2),
('Haricots noirs', 'Vitamine B3', 2),
('Pain à hamburger', 'Vitamine B3', 4.5),
('Laitue', 'Vitamine B3', 0.3),
('Pâte brisée', 'Vitamine B3', 0.4),
('Pommes', 'Vitamine B3', 0.1),
('Carottes', 'Vitamine B3', 0.6),
('Pommes de terre', 'Vitamine B3', 1.4),
('Céleri', 'Vitamine B3', 0.3),
('Poivron', 'Vitamine B3', 0.5),
('Aubergine', 'Vitamine B3', 0.6),
('Courgette', 'Vitamine B3', 0.5),
('Herbe de Provence', 'Vitamine B3', 4.8),
('Farine', 'Vitamine B3', 0.4),
('Oeuf', 'Vitamine B3', 0.1),
('Lait', 'Vitamine B3', 0.1),
('Beurre', 'Vitamine B3', 0.1),
('Lardon', 'Vitamine B3', 5.7),
('Crème fraîche', 'Vitamine B3', 0.1),
('Noix de muscade', 'Vitamine B3', 0.5),
('Riz Arborio', 'Vitamine B3', 1.6),
('Champignon', 'Vitamine B3', 3.9),
('Bouillon de légumes', 'Vitamine B3', 0.2),
('Vin blanc', 'Vitamine B3', 0.1),
('Chocolat', 'Vitamine B3', 1.2),
('Feuille de lasagne', 'Vitamine B3', 0.5),
('Épinard', 'Vitamine B3', 0.6),
('Ricotta', 'Vitamine B3', 0.2),
('Basilic', 'Vitamine B3', 0.8),
('Poireau', 'Vitamine B3', 0.4),
('Quinoa', 'Vitamine B3', 1.4),
('Oignon rouge', 'Vitamine B3', 0.2),
('Avocat', 'Vitamine B3', 1.9),
('Lentille', 'Vitamine B3', 2.1),
('Carotte', 'Vitamine B3', 0.6),
('Tortilla', 'Vitamine B3', 0.4),
('Maïs', 'Vitamine B3', 1.5),
('Haricot noir', 'Vitamine B3', 2),
('Citron vert', 'Vitamine B3', 0.1),
('Coriandre', 'Vitamine B3', 0.5),
('Lait végétal', 'Vitamine B3', 0.2),
('Vanille', 'Vitamine B3', 0.1),
('Pâte à pizza', 'Vitamine B3', 0.5),
('Riz basmati', 'Vitamine B3', 1.6);


-- Insertion des valeurs de Vitamine B6 pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine B6', 0.1),
('Banane', 'Vitamine B6', 0.4),
('Cannelle', 'Vitamine B6', 0.02),
('Champignon de Paris', 'Vitamine B6', 0.1),
('Citron', 'Vitamine B6', 0.05),
('Courge', 'Vitamine B6', 0.09),
('Crevette, cuite', 'Vitamine B6', 0.2),
('Crème glacée, aux fruits', 'Vitamine B6', 0.03),
('Cuisse de poulet', 'Vitamine B6', 0.5),
('Fraise', 'Vitamine B6', 0.06),
('Framboise', 'Vitamine B6', 0.05),
('Lait de coco', 'Vitamine B6', 0.01),
('Mangue', 'Vitamine B6', 0.1),
('Moutarde', 'Vitamine B6', 0.03),
('Mozzarella', 'Vitamine B6', 0.07),
('Myrtille', 'Vitamine B6', 0.05),
('Noix', 'Vitamine B6', 0.1),
('Parmesan', 'Vitamine B6', 0.02),
('Poulet entier, avec peau, cru', 'Vitamine B6', 0.6),
('Pâtes', 'Vitamine B6', 0.01),
('Sauce tomate', 'Vitamine B6', 0.1),
('Saumon d''élevage, cru', 'Vitamine B6', 0.8),
('Saumon sauvage, cru', 'Vitamine B6', 0.7),
('Vinaigrette (avec huile de colza)', 'Vitamine B6', 0.02),
('Yogourt aux fraises', 'Vitamine B6', 0.04),
('Yogourt, nature', 'Vitamine B6', 0.05),
('Laitue romaine', 'Vitamine B6', 0.1),
('Sauce Worcestershire', 'Vitamine B6', 0.1),
('Ail', 'Vitamine B6', 0.2),
('Poivre', 'Vitamine B6', 0.1),
('Pain', 'Vitamine B6', 0.05),
('Poulet', 'Vitamine B6', 0.5),
('Thym', 'Vitamine B6', 0.2),
('Tomate', 'Vitamine B6', 0.11),
('Poivrons', 'Vitamine B6', 0.2),
('Concombre', 'Vitamine B6', 0.04),
('Oignon', 'Vitamine B6', 0.12),
('Haricots noirs', 'Vitamine B6', 0.2),
('Pain à hamburger', 'Vitamine B6', 0.06),
('Laitue', 'Vitamine B6', 0.1),
('Pâte brisée', 'Vitamine B6', 0.03),
('Pommes', 'Vitamine B6', 0.05),
('Carottes', 'Vitamine B6', 0.1),
('Pommes de terre', 'Vitamine B6', 0.3),
('Céleri', 'Vitamine B6', 0.05),
('Poivron', 'Vitamine B6', 0.2),
('Aubergine', 'Vitamine B6', 0.07),
('Courgette', 'Vitamine B6', 0.08),
('Herbe de Provence', 'Vitamine B6', 0.3),
('Farine', 'Vitamine B6', 0.02),
('Oeuf', 'Vitamine B6', 0.1),
('Lait', 'Vitamine B6', 0.04),
('Beurre', 'Vitamine B6', 0.01),
('Lardon', 'Vitamine B6', 0.4),
('Crème fraîche', 'Vitamine B6', 0.03),
('Noix de muscade', 'Vitamine B6', 0.15),
('Riz Arborio', 'Vitamine B6', 0.1),
('Champignon', 'Vitamine B6', 0.1),
('Bouillon de légumes', 'Vitamine B6', 0.02),
('Vin blanc', 'Vitamine B6', 0.01),
('Chocolat', 'Vitamine B6', 0.05),
('Feuille de lasagne', 'Vitamine B6', 0.02),
('Épinard', 'Vitamine B6', 0.2),
('Ricotta', 'Vitamine B6', 0.05),
('Basilic', 'Vitamine B6', 0.1),
('Poireau', 'Vitamine B6', 0.1),
('Quinoa', 'Vitamine B6', 0.2),
('Oignon rouge', 'Vitamine B6', 0.12),
('Avocat', 'Vitamine B6', 0.2),
('Lentille', 'Vitamine B6', 0.1),
('Carotte', 'Vitamine B6', 0.1),
('Tortilla', 'Vitamine B6', 0.04),
('Maïs', 'Vitamine B6', 0.2),
('Haricot noir', 'Vitamine B6', 0.2),
('Citron vert', 'Vitamine B6', 0.05),
('Coriandre', 'Vitamine B6', 0.1),
('Lait végétal', 'Vitamine B6', 0.03),
('Vinaigre de cidre', 'Vitamine B6', 0.01),
('Vanille', 'Vitamine B6', 0.01),
('Pâte à pizza', 'Vitamine B6', 0.04),
('Riz basmati', 'Vitamine B6', 0.1);

-- Insertion des valeurs de Vitamine B12 pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Champignon de Paris', 'Vitamine B12', 0.01),
('Crevette, cuite', 'Vitamine B12', 1.2),
('Cuisse de poulet', 'Vitamine B12', 1.5),
('Mozzarella', 'Vitamine B12', 1.8),
('Parmesan', 'Vitamine B12', 1.7),
('Poulet entier, avec peau, cru', 'Vitamine B12', 2),
('Saumon d''élevage, cru', 'Vitamine B12', 3),
('Saumon sauvage, cru', 'Vitamine B12', 2.8),
('Yogourt aux fraises', 'Vitamine B12', 0.5),
('Yogourt, nature', 'Vitamine B12', 0.6),
('Sauce Worcestershire', 'Vitamine B12', 0.02),
('Poulet', 'Vitamine B12', 2),
('Oeuf', 'Vitamine B12', 0.5),
('Lait', 'Vitamine B12', 0.9),
('Beurre', 'Vitamine B12', 0.02),
('Lardon', 'Vitamine B12', 2.5),
('Crème fraîche', 'Vitamine B12', 0.1),
('Champignon', 'Vitamine B12', 0.02),
('Chocolat', 'Vitamine B12', 0.1),
('Ricotta', 'Vitamine B12', 0.5),
('Lait végétal', 'Vitamine B12', 0.3);

-- Insertion des valeurs de Vitamine C pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine C', 47.8),
('Banane', 'Vitamine C', 8.7),
('Cannelle', 'Vitamine C', 0.3),
('Champignon de Paris', 'Vitamine C', 2.1),
('Citron', 'Vitamine C', 53),
('Courge', 'Vitamine C', 9),
('Crème glacée, aux fruits', 'Vitamine C', 1),
('Fraise', 'Vitamine C', 58.8),
('Framboise', 'Vitamine C', 26.2),
('Mangue', 'Vitamine C', 36.4),
('Miel', 'Vitamine C', 0.5),
('Moutarde', 'Vitamine C', 0.7),
('Myrtille', 'Vitamine C', 9.7),
('Noix', 'Vitamine C', 1.3),
('Sauce tomate', 'Vitamine C', 15.6),
('Saumon d''élevage, cru', 'Vitamine C', 2.1),
('Yogourt aux fraises', 'Vitamine C', 1.2),
('Laitue romaine', 'Vitamine C', 24),
('Ail', 'Vitamine C', 31.2),
('Poivre', 'Vitamine C', 2),
('Tomate', 'Vitamine C', 14),
('Poivrons', 'Vitamine C', 128.7),
('Concombre', 'Vitamine C', 2.8),
('Oignon', 'Vitamine C', 7.4),
('Laitue', 'Vitamine C', 24),
('Pommes', 'Vitamine C', 4.6),
('Carottes', 'Vitamine C', 5.9),
('Pommes de terre', 'Vitamine C', 19.7),
('Céleri', 'Vitamine C', 3.1),
('Poivron', 'Vitamine C', 128.7),
('Aubergine', 'Vitamine C', 2.2),
('Courgette', 'Vitamine C', 17.9),
('Crème fraîche', 'Vitamine C', 0.6),
('Champignon', 'Vitamine C', 2.1),
('Épinard', 'Vitamine C', 28.1),
('Poireau', 'Vitamine C', 12),
('Oignon rouge', 'Vitamine C', 7.4),
('Avocat', 'Vitamine C', 10),
('Carotte', 'Vitamine C', 5.9),
('Maïs', 'Vitamine C', 6.8),
('Citron vert', 'Vitamine C', 29.1);

-- Insertion des valeurs de Vitamine D pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Champignon de Paris', 'Vitamine D', 0.1),
('Crevette, cuite', 'Vitamine D', 2),
('Cuisse de poulet', 'Vitamine D', 0.2),
('Mozzarella', 'Vitamine D', 0.3),
('Parmesan', 'Vitamine D', 0.5),
('Poulet entier, avec peau, cru', 'Vitamine D', 0.4),
('Saumon d''élevage, cru', 'Vitamine D', 10),
('Saumon sauvage, cru', 'Vitamine D', 15),
('Poulet', 'Vitamine D', 0.2),
('Oeuf', 'Vitamine D', 1.1),
('Lait', 'Vitamine D', 0.1),
('Beurre', 'Vitamine D', 1.5),
('Lardon', 'Vitamine D', 1.2),
('Crème fraîche', 'Vitamine D', 0.1),
('Champignon', 'Vitamine D', 0.1),
('Chocolat', 'Vitamine D', 0.2),
('Épinard', 'Vitamine D', 0.1),
('Lait végétal', 'Vitamine D', 0.1);

-- Insertion des valeurs de Vitamine E pour chaque aliment

INSERT INTO aliment_contient_vitamine (anom, vinom, quantite) VALUES
('Ananas', 'Vitamine E', 0.02),
('Banane', 'Vitamine E', 0.1),
('Cannelle', 'Vitamine E', 2.3),
('Champignon de Paris', 'Vitamine E', 0.01),
('Citron', 'Vitamine E', 0.03),
('Courge', 'Vitamine E', 0.13),
('Crevette, cuite', 'Vitamine E', 1.9),
('Crème glacée, aux fruits', 'Vitamine E', 0.2),
('Cuisse de poulet', 'Vitamine E', 0.4),
('Fraise', 'Vitamine E', 0.29),
('Framboise', 'Vitamine E', 0.87),
('Huile d''olive', 'Vitamine E', 14),
('Lait de coco', 'Vitamine E', 0.1),
('Mangue', 'Vitamine E', 0.9),
('Moutarde', 'Vitamine E', 0.2),
('Mozzarella', 'Vitamine E', 0.1),
('Myrtille', 'Vitamine E', 0.57),
('Noix', 'Vitamine E', 0.7),
('Parmesan', 'Vitamine E', 0.2),
('Poulet entier, avec peau, cru', 'Vitamine E', 0.3),
('Pâtes', 'Vitamine E', 0.12),
('Sauce tomate', 'Vitamine E', 1.6),
('Saumon d''élevage, cru', 'Vitamine E', 2.8),
('Saumon sauvage, cru', 'Vitamine E', 2.5),
('Vinaigrette (avec huile de colza)', 'Vitamine E', 2.6),
('Yogourt aux fraises', 'Vitamine E', 0.1),
('Yogourt, nature', 'Vitamine E', 0.1),
('Laitue romaine', 'Vitamine E', 0.18),
('Sauce Worcestershire', 'Vitamine E', 0.05),
('Ail', 'Vitamine E', 0.08),
('Poivre', 'Vitamine E', 0.4),
('Pain', 'Vitamine E', 0.25),
('Poulet', 'Vitamine E', 0.3),
('Thym', 'Vitamine E', 0.55),
('Tomate', 'Vitamine E', 0.54),
('Poivrons', 'Vitamine E', 1.58),
('Concombre', 'Vitamine E', 0.03),
('Oignon', 'Vitamine E', 0.02),
('Haricots noirs', 'Vitamine E', 0.21),
('Pain à hamburger', 'Vitamine E', 0.27),
('Laitue', 'Vitamine E', 0.18),
('Pâte brisée', 'Vitamine E', 0.9),
('Pommes', 'Vitamine E', 0.18),
('Carottes', 'Vitamine E', 0.66),
('Pommes de terre', 'Vitamine E', 0.01),
('Céleri', 'Vitamine E', 0.27),
('Poivron', 'Vitamine E', 1.58),
('Aubergine', 'Vitamine E', 0.3),
('Courgette', 'Vitamine E', 0.12),
('Herbe de Provence', 'Vitamine E', 0.39),
('Farine', 'Vitamine E', 0.08),
('Oeuf', 'Vitamine E', 1.05),
('Lait', 'Vitamine E', 0.03),
('Beurre', 'Vitamine E', 2.32),
('Lardon', 'Vitamine E', 0.6),
('Crème fraîche', 'Vitamine E', 0.7),
('Noix de muscade', 'Vitamine E', 0.47),
('Champignon', 'Vitamine E', 0.01),
('Bouillon de légumes', 'Vitamine E', 0.02),
('Chocolat', 'Vitamine E', 0.25),
('Feuille de lasagne', 'Vitamine E', 0.11),
('Épinard', 'Vitamine E', 2.03),
('Ricotta', 'Vitamine E', 0.1),
('Basilic', 'Vitamine E', 0.3),
('Poireau', 'Vitamine E', 0.55),
('Quinoa', 'Vitamine E', 0.63),
('Oignon rouge', 'Vitamine E', 0.02),
('Avocat', 'Vitamine E', 2.07),
('Lentille', 'Vitamine E', 0.11),
('Carotte', 'Vitamine E', 0.66),
('Tortilla', 'Vitamine E', 0.15),
('Maïs', 'Vitamine E', 0.07),
('Haricot noir', 'Vitamine E', 0.21),
('Citron vert', 'Vitamine E', 0.03),
('Coriandre', 'Vitamine E', 0.26),
('Huile végétale', 'Vitamine E', 5.1),
('Lait végétal', 'Vitamine E', 0.12),
('Vanille', 'Vitamine E', 0.11),
('Pâte à pizza', 'Vitamine E', 0.12);



INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Poulet rôti au yogourt', 'Préchauffez le four à 200°C. Assaisonnez le poulet avec du yogurt, du sel et du poivre. Placez-le au four pendant 1 heure. Retournez-le à mi-cuisson. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Poulet rôti au yogourt', 'Couteau'),
('Poulet rôti au yogourt', 'Plat de cuisson'),
('Poulet rôti au yogourt', 'Four'),
('Poulet rôti au yogourt', 'Moulin à poivre'),
('Poulet rôti au yogourt', 'Moulin à sel');


INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Poulet rôti au yogourt', 'Poulet entier, avec peau, cru', 230, 'grammes'),
('Poulet rôti au yogourt', 'Yogourt, nature', 50, 'grammes'),
('Poulet rôti au yogourt', 'Sel', 2, 'pincée');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Smoothie aux fruits tropicaux', 'Mélangez dans un blender des morceaux de mangue, d''ananas, de banane, du yaourt nature et du miel. Mixez jusqu''à obtenir une consistance lisse. Servez frais.', 'Boisson');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Smoothie aux fruits tropicaux', 'Blender'),
('Smoothie aux fruits tropicaux', 'Couteau');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Smoothie aux fruits tropicaux', 'Mangue', 30, 'grammes'),
('Smoothie aux fruits tropicaux', 'Ananas', 20, 'grammes'),
('Smoothie aux fruits tropicaux', 'Banane', 50, 'grammes'),
('Smoothie aux fruits tropicaux', 'Yogourt aux fraises', 100, 'grammes'),
('Smoothie aux fruits tropicaux', 'Miel', 10, 'centilitres');

INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Pâtes à l''ail', 'Faites cuire les pâtes al dente. Dans une poêle, faites revenir de l''ail et du piment(option.) dans de l''huile d''olive. Mélangez les pâtes avec la sauce. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Pâtes à l''ail', 'Casserole'),
('Pâtes à l''ail', 'Poêle'),
('Pâtes à l''ail', 'Moulin à poivre'),
('Pâtes à l''ail', 'Moulin à sel'),
('Pâtes à l''ail', 'Cuillère');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Pâtes à l''ail', 'Pâtes', 100, 'grammes'),
('Pâtes à l''ail', 'Ail', 20, 'grammes'),
('Pâtes à l''ail', 'Huile d''olive', 15, 'cuillères à soupe');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Saumon au citron', 'Préchauffez le four à 180°C. Assaisonnez le saumon avec du citron, de l''aneth (option.), du sel et du poivre. Cuisez au four pendant 20 minutes. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Saumon au citron', 'Couteau'),
('Saumon au citron', 'Plat de cuisson'),
('Saumon au citron', 'Four'),
('Saumon au citron', 'Moulin à poivre'),
('Saumon au citron', 'Moulin à sel');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Saumon au citron', 'Saumon d''élevage, cru', 150, 'grammes'),
('Saumon au citron', 'Citron', 2, 'cuillères à café'),
('Saumon au citron', 'Sel', 1, 'pincée');

INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Omelette aux champignons', 'Battez les œufs dans un bol. Faites sauter des champignons dans une poêle avec du beurre. Versez les œufs battus sur les champignons. Cuisez des deux côtés jusqu''à ce que l''omelette soit bien cuite. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Omelette aux champignons', 'Bol'),
('Omelette aux champignons', 'Poêle'),
('Omelette aux champignons', 'Cuillère'),
('Omelette aux champignons', 'Moulin à sel'),
('Omelette aux champignons', 'Moulin à poivre');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Omelette aux champignons', 'Oeuf', 120, 'grammes'),
('Omelette aux champignons', 'Champignon de Paris', 30, 'grammes'),
('Omelette aux champignons', 'Beurre', 20, 'grammes'),
('Omelette aux champignons', 'Sel', 2, 'pincée');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Tarte aux pommes', 'Préparez une pâte à tarte. Épluchez et coupez des pommes en tranches. Disposez les tranches de pommes sur la pâte et saupoudrez de sucre et de cannelle. Cuisez au four jusqu''à ce que la croûte soit dorée. Servez tiède.', 'Dessert');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Tarte aux pommes', 'Rouleau à pâtisserie'),
('Tarte aux pommes', 'Plat de cuisson'),
('Tarte aux pommes', 'Couteau');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Tarte aux pommes', 'Pâte brisée', 200, 'grammes'),
('Tarte aux pommes', 'Pommes', 200, 'grammes'),
('Tarte aux pommes', 'Sucre', 100, 'grammes'),
('Tarte aux pommes', 'Cannelle', 20, 'grammes');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Smoothie aux baies', 'Mélangez dans un blender des baies mixtes (framboises, myrtilles, fraises), du yaourt nature et du miel. Mixez jusqu''à obtenir une consistance lisse. Servez frais.', 'Boisson');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Smoothie aux baies', 'Blender'),
('Smoothie aux baies', 'Cuillère');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Smoothie aux baies', 'Framboise', 30, 'grammes'),
('Smoothie aux baies', 'Myrtille', 20, 'grammes'),
('Smoothie aux baies', 'Fraise', 30, 'grammes'),
('Smoothie aux baies', 'Yogourt, nature', 60, 'grammes'),
('Smoothie aux baies', 'Miel', 15, 'centilitres');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Pâtes carbonara', 'Faites cuire les pâtes selon les instructions. Dans une poêle, faites revenir du lard. Mélangez les pâtes cuites avec du lard, des œufs battus et du parmesan. Assaisonnez avec du poivre. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Pâtes carbonara', 'Casserole'),
('Pâtes carbonara', 'Poêle'),
('Pâtes carbonara', 'Moulin à poivre');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Pâtes carbonara', 'Pâtes', 150, 'grammes'),
('Pâtes carbonara', 'Lardon', 40, 'grammes'),
('Pâtes carbonara', 'Oeuf', 20, 'grammes'),
('Pâtes carbonara', 'Parmesan', 20, 'grammes');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Poulet au miel et à la moutarde', 'Assaisonnez des cuisses de poulet avec du sel, du poivre, du miel et de la moutarde. Faites cuire au four jusqu''à ce que le poulet soit bien cuit et doré. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Poulet au miel et à la moutarde', 'Couteau'),
('Poulet au miel et à la moutarde', 'Plat de cuisson'),
('Poulet au miel et à la moutarde', 'Four'),
('Poulet au miel et à la moutarde', 'Moulin à poivre');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Poulet au miel et à la moutarde', 'Cuisse de poulet', 300, 'grammes'),
('Poulet au miel et à la moutarde', 'Sel', 1, 'pincée'),
('Poulet au miel et à la moutarde', 'Miel', 20, 'centilitres'),
('Poulet au miel et à la moutarde', 'Moutarde', 20, 'grammes');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Tarte aux tomates et mozzarella', 'Préparez une pâte à tarte. Disposez des tranches de tomates et de mozzarella sur la pâte. Saupoudrez de basilic frais. Cuisez au four jusqu''à ce que la croûte soit dorée. Servez tiède.', 'Entrée');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Tarte aux tomates et mozzarella', 'Rouleau à pâtisserie'),
('Tarte aux tomates et mozzarella', 'Plat de cuisson'),
('Tarte aux tomates et mozzarella', 'Couteau');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Tarte aux tomates et mozzarella', 'Pâte brisée', 200, 'grammes'),
('Tarte aux tomates et mozzarella', 'Tomate', 80, 'grammes'),
('Tarte aux tomates et mozzarella', 'Mozzarella', 30, 'grammes'),
('Tarte aux tomates et mozzarella', 'Basilic', 10, 'grammes');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Smoothie à la mangue et à la noix de coco', 'Mélangez dans un blender de la mangue, du lait de coco, du yaourt et de la glace. Mixez jusqu''à obtenir une consistance lisse. Servez frais.', 'Boisson');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Smoothie à la mangue et à la noix de coco', 'Blender'),
('Smoothie à la mangue et à la noix de coco', 'Cuillère');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Smoothie à la mangue et à la noix de coco', 'Mangue', 40, 'grammes'),
('Smoothie à la mangue et à la noix de coco', 'Lait de coco', 70, 'grammes'),
('Smoothie à la mangue et à la noix de coco', 'Yogourt, nature', 30, 'grammes'),
('Smoothie à la mangue et à la noix de coco', 'Crème glacée, aux fruits', 30, 'grammes');

INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Salade de crevettes et avocat', 'Préparez une salade avec des crevettes cuites, des avocats en tranches, des tomates et de la laitue. Assaisonnez avec une vinaigrette légère à l''huile d''olive et au citron. Servez frais.', 'Salade');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Salade de crevettes et avocat', 'Saladier'),
('Salade de crevettes et avocat', 'Couteau'),
('Salade de crevettes et avocat', 'Planche à découper');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Salade de crevettes et avocat', 'Crevette, cuite', 200, 'grammes'),
('Salade de crevettes et avocat', 'Avocat', 150, 'grammes'),
('Salade de crevettes et avocat', 'Tomate', 100, 'grammes'),
('Salade de crevettes et avocat', 'Laitue', 100, 'grammes'),
('Salade de crevettes et avocat', 'Vinaigrette (avec huile de colza)', 2, 'cuillères à soupe');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Soupe de courge', 'Coupez la courge en morceaux. Faites-la cuire dans un bouillon de légumes. Mixez le tout jusqu''à obtenir une consistance lisse. Assaisonnez avec du sel et du poivre. Servez chaud.', 'Soupe');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Soupe de courge', 'Couteau'),
('Soupe de courge', 'Casserole'),
('Soupe de courge', 'Moulin à poivre'),
('Soupe de courge', 'Mixeur');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Soupe de courge', 'Courge', 120, 'grammes'),
('Soupe de courge', 'Bouillon de légumes', 300, 'grammes'),
('Soupe de courge', 'Sel', 2, 'pincée');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Poulet grillé au citron et à l''ail', 'Marinez des morceaux de poulet dans un mélange d''ail écrasé, de jus de citron, d''huile d''olive, de sel et de poivre. Faites griller jusqu''à ce que le poulet soit bien cuit. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Poulet grillé au citron et à l''ail', 'Bol'),
('Poulet grillé au citron et à l''ail', 'Grill'),
('Poulet grillé au citron et à l''ail', 'Pinceau de cuisine'),
('Poulet grillé au citron et à l''ail', 'Moulin à poivre'),
('Poulet grillé au citron et à l''ail', 'Moulin à sel');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Poulet grillé au citron et à l''ail', 'Poulet', 200, 'grammes'),
('Poulet grillé au citron et à l''ail', 'Ail', 20, 'grammes'),
('Poulet grillé au citron et à l''ail', 'Citron', 30, 'grammes'),
('Poulet grillé au citron et à l''ail', 'Huile d''olive', 1, 'cuillères à soupe'),
('Poulet grillé au citron et à l''ail', 'Sel', 1, 'pincée');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Salade de pâtes aux légumes grillés', 'Faites cuire des pâtes selon les instructions. Grillez des légumes (poivrons, courgettes, tomates) avec de l''huile d''olive, du sel et du poivre. Mélangez les légumes grillés avec les pâtes. Servez frais.', 'Salade');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Salade de pâtes aux légumes grillés', 'Casserole'),
('Salade de pâtes aux légumes grillés', 'Grill'),
('Salade de pâtes aux légumes grillés', 'Moulin à poivre'),
('Salade de pâtes aux légumes grillés', 'Moulin à sel'),
('Salade de pâtes aux légumes grillés', 'Saladier');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Salade de pâtes aux légumes grillés', 'Pâtes', 200, 'grammes'),
('Salade de pâtes aux légumes grillés', 'Poivrons', 70, 'grammes'),
('Salade de pâtes aux légumes grillés', 'Courgette', 70, 'grammes'),
('Salade de pâtes aux légumes grillés', 'Tomate', 65, 'grammes'),
('Salade de pâtes aux légumes grillés', 'Huile d''olive', 2, 'cuillères à soupe'),
('Salade de pâtes aux légumes grillés', 'Sel', 1, 'pincée');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Saumon en croûte de noix', 'Assaisonnez des filets de saumon avec du sel, du poivre et du jus de citron. Enrobez-les de noix concassées. Cuisez au four jusqu''à ce que le saumon soit cuit. Servez chaud.', 'Plat principal');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Saumon en croûte de noix', 'Couteau'),
('Saumon en croûte de noix', 'Plat de cuisson'),
('Saumon en croûte de noix', 'Four'),
('Saumon en croûte de noix', 'Moulin à poivre'),
('Saumon en croûte de noix', 'Moulin à sel');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Saumon en croûte de noix', 'Saumon sauvage, cru', 150, 'grammes'),
('Saumon en croûte de noix', 'Sel', 1, 'pincée'),
('Saumon en croûte de noix', 'Citron', 20, 'grammes'),
('Saumon en croûte de noix', 'Noix', 40, 'grammes');

INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Pizza Margherita', 'Préparez une pâte à pizza. Étalez de la sauce tomate, des tranches de mozzarella et des feuilles de basilic. Cuisez au four jusqu''à ce que la croûte soit dorée. Servez chaud.', 'Pizza');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Pizza Margherita', 'Rouleau à pâtisserie'),
('Pizza Margherita', 'Plat de cuisson'),
('Pizza Margherita', 'Couteau'),
('Pizza Margherita', 'Cuillère');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Pizza Margherita', 'Pâte à pizza', 190, 'grammes'),
('Pizza Margherita', 'Sauce tomate', 100, 'grammes'),
('Pizza Margherita', 'Mozzarella', 60, 'grammes'),
('Pizza Margherita', 'Basilic', 20, 'grammes');

INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Pizza aux champignons', 'Préparez une pâte à pizza. Étalez de la sauce tomate, des tranches de mozzarella et des feuilles de basilic et des champignons coupés en tranches. Cuisez au four jusqu''à ce que la croûte soit dorée. Servez chaud.', 'Pizza');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Pizza aux champignons', 'Rouleau à pâtisserie'),
('Pizza aux champignons', 'Plat de cuisson'),
('Pizza aux champignons', 'Couteau'),
('Pizza aux champignons', 'Cuillère');

INSERT INTO Recette_contient_Aliment (rnom, anom, quantite, unite_mesure) VALUES
('Pizza aux champignons', 'Pâte à pizza', 190, 'grammes'),
('Pizza aux champignons', 'Sauce tomate', 100, 'grammes'),
('Pizza aux champignons', 'Mozzarella', 80, 'grammes'),
('Pizza aux champignons', 'Champignon de Paris', 50, 'grammes'),
('Pizza aux champignons', 'Basilic', 20, 'grammes');


-- Liste des favoris
INSERT INTO Liste(lnom, email) VALUES
('Mes favoris', 'alan.sottile@heig-vd.ch'),
('Mes favoris', 'calvin.graf@heig-vd.ch'),
('Mes favoris', 'lucas.lattion@heig-vd.ch'),
('Mes favoris', 'alice.durand@email.com'),
('Mes favoris', 'marc.dupont@email.com'),
('Mes favoris', 'julie.lemoine@email.com'),
('Mes favoris', 'lucas.bernard@email.com'),
('Mes favoris', 'emilie.petit@email.com'),
('Mes favoris', 'pierre.martin@email.com'),
('Mes favoris', 'sophie.dubois@email.com'),
('Mes favoris', 'jean.dupuis@email.com'),
('Mes favoris', 'marie.roux@email.com'),
('Mes favoris', 'antoine.girard@email.com'),
('Mes favoris', 'camille.moreau@email.com'),
('Mes favoris', 'nicolas.lefebvre@email.com'),
('Mes favoris', 'laura.martin@email.com'),
('Mes favoris', 'david.robert@email.com'),
('Mes favoris', 'estelle.marchand@email.com'),
('Mes favoris', 'bruno.lemoine@email.com'),
('Mes favoris', 'claire.dupre@email.com'),
('Mes favoris', 'olivier.petit@email.com'),
('Mes favoris', 'chloe.blanc@email.com'),
('Mes favoris', 'remi.leroy@email.com'),
('Mes favoris', 'valerie.colin@email.com'),
('Mes favoris', 'guillaume.michel@email.com'),
('Mes favoris', 'marion.robert@email.com'),
('Mes favoris', 'fabien.gerard@email.com');

INSERT INTO Liste(lnom, email) VALUES
('Noel 2023', 'alan.sottile@heig-vd.ch'),
('Noel 2022', 'calvin.graf@heig-vd.ch'),
('Miam', 'julie.lemoine@email.com'),
('Jolie liste', 'jean.dupuis@email.com');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES
('Miam', 'julie.lemoine@email.com', 'Pizza aux champignons'),
('Miam', 'julie.lemoine@email.com', 'Soupe de courge');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES ('Jolie liste', 'jean.dupuis@email.com', 'Smoothie aux baies');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES
('Noel 2023', 'alan.sottile@heig-vd.ch', 'Salade de pâtes aux légumes grillés'),
('Noel 2023', 'alan.sottile@heig-vd.ch', 'Soupe de courge');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES ('Noel 2022', 'calvin.graf@heig-vd.ch', 'Omelette aux champignons');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES
('Mes favoris', 'calvin.graf@heig-vd.ch', 'Tarte aux pommes'),
('Mes favoris', 'calvin.graf@heig-vd.ch', 'Pâtes carbonara'),
('Mes favoris', 'calvin.graf@heig-vd.ch', 'Poulet grillé au citron et à l''ail');

INSERT INTO Liste_contient_Recette(lnom, email, rnom) VALUES
('Mes favoris', 'alan.sottile@heig-vd.ch', 'Tarte aux pommes'),
('Mes favoris', 'alan.sottile@heig-vd.ch', 'Salade de crevettes et avocat');


INSERT INTO Recette(rnom, instructions, type_recette) VALUES
('Salade César', 'Étape 1: Laver et hacher la laitue romaine. Étape 2: Préparer la vinaigrette avec de l''huile d''olive, du jus de citron, de la sauce Worcestershire, de l''ail écrasé, du sel et du poivre. Étape 3: Faire griller des morceaux de pain pour les croûtons. Étape 4: Mélanger la laitue, la vinaigrette, les croûtons et du parmesan râpé.', 'Entrée'),
('Poulet rôti', 'Étape 1: Préchauffer le four à 200°C. Étape 2: Assaisonner l''intérieur et l''extérieur du poulet avec du sel, du poivre, du thym et de l''huile d''olive. Étape 3: Rôtir le poulet pendant 1h30, en l''arrosant régulièrement avec son jus. Étape 4: Laisser reposer le poulet avant de le découper.', 'Plat principal'),
('Gazpacho', 'Étape 1: Couper des tomates, des poivrons, un concombre, et un oignon en morceaux. Étape 2: Mettre les légumes dans un mixeur, ajouter de l''ail, du vinaigre et de l''huile d''olive. Mixer jusqu''à obtenir une consistance lisse. Étape 3: Réfrigérer pendant au moins 2 heures. Servir froid avec des croûtons.', 'Soupe'),
('Burger végétarien', 'Étape 1: Préparer les galettes avec des haricots noirs écrasés, de l''oignon haché, de la chapelure et des épices. Étape 2: Faire cuire les galettes dans une poêle avec un peu d''huile. Étape 3: Assembler le burger avec des pains à hamburger, les galettes, des tranches de tomate, de la laitue et de la sauce de votre choix.', 'Plat principal'),
('Soupe de légumes', 'Étape 1: Couper en dés carottes, pommes de terre, oignons et céleri. Étape 2: Faire revenir les légumes dans une marmite avec un peu d''huile. Ajouter du bouillon de légumes et laisser mijoter jusqu''à ce que les légumes soient tendres. Étape 3: Assaisonner avec du sel, du poivre et des herbes.', 'Entrée'),
('Ratatouille', 'Étape 1: Couper en rondelles des aubergines, des courgettes, des poivrons et des tomates. Étape 2: Faire revenir séparément chaque légume dans une poêle. Étape 3: Superposer les légumes dans une casserole, ajouter de l''ail, des herbes de Provence, du sel et du poivre. Étape 4: Cuire à feu doux pendant environ 40 minutes.', 'Plat principal'),
('Crêpes', 'Étape 1: Mélanger 250g de farine, 4 œufs, 500ml de lait, une pincée de sel et 2 cuillères à soupe de sucre. Étape 2: Laisser reposer la pâte 1 heure. Étape 3: Cuire les crêpes dans une poêle chaude légèrement huilée. Retourner la crêpe une fois que les bords se décollent.', 'Dessert'),
('Quiche Lorraine', 'Étape 1: Étaler une pâte brisée dans un moule à tarte. Étape 2: Répartir des lardons et du fromage râpé sur la pâte. Étape 3: Battre 3 œufs avec 200ml de crème fraîche, du sel, du poivre et de la noix de muscade. Verser sur la pâte. Étape 4: Cuire au four à 180°C pendant 30 minutes.', 'Plat principal'),
('Risotto aux champignons', 'Étape 1: Faire revenir de l''ail et des échalotes dans une casserole avec un peu d''huile. Ajouter du riz arborio et le nacrer. Étape 2: Ajouter progressivement du bouillon de légumes chaud en remuant constamment. Étape 3: Quand le riz est presque cuit, ajouter des champignons préalablement sautés. Étape 4: Ajouter du parmesan râpé et servir chaud.', 'Plat principal'),
('Gâteau au chocolat', 'Étape 1: Faire fondre 200g de chocolat avec 100g de beurre. Étape 2: Mélanger 3 œufs avec 150g de sucre, ajouter le mélange chocolat-beurre. Incorporer 80g de farine. Étape 3: Verser la pâte dans un moule beurré et cuire au four à 180°C pendant 25 minutes.', 'Dessert'),
('Lasagnes aux épinards et ricotta', 'Faites cuire les épinards avec l''ail et l''oignon, mélangez avec la ricotta, montez les lasagnes en alternant couches de sauce, épinards-ricotta et pâtes.', 'Plat principal'),
('Quiche aux champignons et poireaux', 'Faites revenir les champignons et poireaux, versez sur la pâte, ajoutez le mélange d’œufs et de crème, et cuisez au four.', 'Plat principal'),
('Salade de quinoa aux légumes grillés', 'Cuisez le quinoa, grillez les légumes, mélangez le tout avec une vinaigrette au citron.', 'Salade'),
('Burger végétarien aux haricots noirs', 'Écrasez les haricots noirs, mélangez avec l’œuf, les épices, formez des galettes, faites-les cuire et assemblez le burger.', 'Plat principal'),
('Soupe de lentilles', 'Faites revenir les légumes, ajoutez les lentilles et le bouillon, laissez mijoter jusqu''à cuisson des lentilles.', 'Soupe'),
('Tacos aux légumes et guacamole', 'Faites sauter les légumes, préparez le guacamole, assemblez les tacos.', 'Plat principal'),
('Gâteau au chocolat végétalien', 'Mélangez les ingrédients secs et liquides séparément, combinez-les, versez dans un moule, puis cuisez au four.', 'Dessert');


INSERT INTO Recette (rnom, instructions, type_recette) VALUES
('Omelette aux épinards et feta', 'Battez les œufs, ajoutez les épinards et la feta, puis faites cuire l''omelette.', 'Plat principal'),
('Poulet grillé aux herbes', 'Assaisonnez le poulet avec des herbes, de l''ail, du sel et du poivre, puis grillez-le.', 'Plat principal'),
('Bowl de buddha au tofu', 'Cuisez le quinoa ou le riz, ajoutez le tofu grillé et les légumes frais, assaisonnez avec la sauce.', 'Plat principal'),
('Chili con carne sans haricots', 'Faites cuire le bœuf avec les légumes et les épices, servez chaud.', 'Plat principal'),
('Salade niçoise', 'Assemblez les ingrédients, ajoutez la vinaigrette.', 'Salade'),
('Smoothie bowl aux fruits et noix', 'Mixez les fruits avec le lait d''amande, versez dans un bol, garnissez de noix, graines de chia et un filet de miel.', 'Dessert'),
('Soupe de lentilles corail à la tomate', 'Faites revenir l''oignon et l''ail hachés dans l''huile d''olive. Ajoutez les épices, les lentilles corail et les tomates. Couvrez avec le bouillon et laissez mijoter jusqu''à ce que les lentilles soient tendres. Mixez pour obtenir une texture lisse.', 'Entrée'),
('Soupe de lentilles vertes et carottes', 'Faites suer l''oignon haché dans l''huile d''olive. Ajoutez les carottes coupées en rondelles, les lentilles, le thym, le laurier, et le bouillon. Laissez mijoter jusqu''à ce que les lentilles soient cuites. Retirez le thym et le laurier avant de servir.', 'Entrée'),
('Risotto aux champignons et asperges', 'Faites revenir l''oignon et l''ail dans l''huile d''olive. Ajoutez les asperges coupées et les champignons. Incorporez le riz et nacrez-le. Versez le vin, laissez évaporer, puis ajoutez progressivement le bouillon chaud. Terminez avec du parmesan et du beurre.', 'Plat principal'),
('Risotto aux champignons et épinards', 'Commencez par faire revenir l''oignon et l''ail, puis ajoutez les champignons tranchés. Une fois dorés, ajoutez les épinards et le riz Arborio. Nacrez le riz, déglacez au vin blanc, puis ajoutez le bouillon petit à petit. Terminez avec du parmesan râpé.', 'Plat principal'),
('Mousse au Chocolat', 'Faire fondre le chocolat. Battre les blancs d’œufs en neige. Incorporer délicatement les blancs au chocolat fondu. Réfrigérer pendant 2 heures.', 'Dessert'),
('Cheesecake', 'Mélanger du fromage frais avec du sucre, des œufs et un peu de vanille. Verser sur une base de biscuits écrasés et beurre. Cuire au four à 160°C pendant 45 minutes.', 'Dessert');




INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Salade César', 'Laitue romaine', 200, 'grammes'),
('Salade César', 'Huile d''olive', 1, 'cuillères à soupe'),
('Salade César', 'Sauce Worcestershire', 2, 'cuillères à soupe'),
('Salade César', 'Ail', 10, 'grammes'),
('Salade César', 'Sel', 1, 'pincée'),
('Salade César', 'Poivre', 1, 'pincée'),
('Salade César', 'Pain', 100, 'grammes'),
('Salade César', 'Parmesan', 50, 'grammes');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Poulet rôti', 'Poulet', 300, 'grammes'),
('Poulet rôti', 'Sel', 1, 'pincée'),
('Poulet rôti', 'Poivre', 1, 'pincée'),
('Poulet rôti', 'Thym', 1, 'cuillères à café'),
('Poulet rôti', 'Huile d''olive', 2, 'cuillères à soupe');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Gazpacho', 'Tomate', 500, 'grammes'),
('Gazpacho', 'Poivrons', 200, 'grammes'),
('Gazpacho', 'Concombre', 150, 'grammes'),
('Gazpacho', 'Oignon', 100, 'grammes'),
('Gazpacho', 'Ail', 20, 'grammes'),
('Gazpacho', 'Vinaigre de cidre', 2, 'cuillères à soupe'),
('Gazpacho', 'Huile d''olive', 3, 'cuillères à soupe');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Burger végétarien', 'Haricots noirs', 200, 'grammes'),
('Burger végétarien', 'Oignon', 50, 'grammes'),
('Burger végétarien', 'Paprika', 2, 'pincée'),
('Burger végétarien', 'Pain à hamburger', 200, 'grammes'),
('Burger végétarien', 'Tomate', 50, 'grammes'),
('Burger végétarien', 'Laitue', 30, 'grammes');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Soupe de légumes', 'Carottes', 200, 'grammes'),
('Soupe de légumes', 'Pommes de terre', 300, 'grammes'),
('Soupe de légumes', 'Oignon', 100, 'grammes'),
('Soupe de légumes', 'Céleri', 100, 'grammes'),
('Soupe de légumes', 'Huile d''olive', 2, 'cuillères à soupe');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Ratatouille', 'Tomate', 500, 'grammes'),
('Ratatouille', 'Poivron', 200, 'grammes'),
('Ratatouille', 'Aubergine', 200, 'grammes'),
('Ratatouille', 'Courgette', 200, 'grammes'),
('Ratatouille', 'Oignon', 100, 'grammes'),
('Ratatouille', 'Ail', 30, 'grammes'),
('Ratatouille', 'Huile d''olive', 2, 'cuillères à soupe'),
('Ratatouille', 'Herbe de Provence', 1, 'cuillères à café'),
('Ratatouille', 'Sel', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Crêpes', 'Farine', 250, 'grammes'),
('Crêpes', 'Oeuf', 200, 'grammes'),
('Crêpes', 'Lait', 50, 'centilitres'),
('Crêpes', 'Sucre', 2, 'cuillères à soupe'),
('Crêpes', 'Beurre', 30, 'grammes'),
('Crêpes', 'Sel', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Quiche Lorraine', 'Pâte brisée', 300, 'grammes'),
('Quiche Lorraine', 'Lardon', 200, 'grammes'),
('Quiche Lorraine', 'Oeuf', 150, 'grammes'),
('Quiche Lorraine', 'Crème fraîche', 20, 'centilitres'),
('Quiche Lorraine', 'Noix de muscade', 1, 'pincée'),
('Quiche Lorraine', 'Sel', 1, 'pincée'),
('Quiche Lorraine', 'Poivre', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Risotto aux champignons', 'Champignon', 200, 'grammes'),
('Risotto aux champignons', 'Oignon', 50, 'grammes'),
('Risotto aux champignons', 'Bouillon de légumes', 500, 'centilitres'),
('Risotto aux champignons', 'Parmesan', 50, 'grammes'),
('Risotto aux champignons', 'Beurre', 20, 'grammes'),
('Risotto aux champignons', 'Huile d''olive', 2, 'cuillères à soupe'),
('Risotto aux champignons', 'Sel', 1, 'pincée'),
('Risotto aux champignons', 'Poivre', 1, 'pincée');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Gâteau au chocolat', 'Chocolat', 200, 'grammes'),
('Gâteau au chocolat', 'Beurre', 100, 'grammes'),
('Gâteau au chocolat', 'Oeuf', 150, 'grammes'),
('Gâteau au chocolat', 'Sucre', 150, 'grammes'),
('Gâteau au chocolat', 'Farine', 80, 'grammes');






INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Lasagnes aux épinards et ricotta', 'Feuille de lasagne', 250, 'grammes'),
('Lasagnes aux épinards et ricotta', 'Épinard', 200, 'grammes'),
('Lasagnes aux épinards et ricotta', 'Ricotta', 250, 'grammes'),
('Lasagnes aux épinards et ricotta', 'Sauce tomate', 200, 'centilitres'),
('Lasagnes aux épinards et ricotta', 'Ail', 30, 'grammes'),
('Lasagnes aux épinards et ricotta', 'Oignon', 40, 'grammes'),
('Lasagnes aux épinards et ricotta', 'Huile d''olive', 2, 'cuillères à soupe'),
('Lasagnes aux épinards et ricotta', 'Sel', 1, 'pincée'),
('Lasagnes aux épinards et ricotta', 'Poivre', 1, 'pincée');



INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Quiche aux champignons et poireaux', 'Pâte brisée', 250, 'grammes'),
('Quiche aux champignons et poireaux', 'Champignon', 200, 'grammes'),
('Quiche aux champignons et poireaux', 'Poireau', 100, 'grammes'),
('Quiche aux champignons et poireaux', 'Oeuf', 180, 'grammes'),
('Quiche aux champignons et poireaux', 'Crème fraîche', 200, 'centilitres'),
('Quiche aux champignons et poireaux', 'Sel', 1, 'pincée'),
('Quiche aux champignons et poireaux', 'Poivre', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Salade de quinoa aux légumes grillés', 'Quinoa', 150, 'grammes'),
('Salade de quinoa aux légumes grillés', 'Aubergine', 80, 'grammes'),
('Salade de quinoa aux légumes grillés', 'Poivron', 70, 'grammes'),
('Salade de quinoa aux légumes grillés', 'Courgette', 90, 'grammes'),
('Salade de quinoa aux légumes grillés', 'Oignon rouge', 50, 'grammes'),
('Salade de quinoa aux légumes grillés', 'Huile d''olive', 2, 'cuillères à soupe'),
('Salade de quinoa aux légumes grillés', 'Sel', 1, 'pincée'),
('Salade de quinoa aux légumes grillés', 'Poivre', 1, 'pincée');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Omelette aux épinards et feta', 'Oeuf', 120, 'grammes'),
('Omelette aux épinards et feta', 'Épinard', 100, 'grammes'),
('Omelette aux épinards et feta', 'Feta', 50, 'grammes'),
('Omelette aux épinards et feta', 'Huile d''olive', 1, 'cuillères à soupe'),
('Omelette aux épinards et feta', 'Sel', 1, 'pincée'),
('Omelette aux épinards et feta', 'Poivre', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Poulet grillé aux herbes', 'Poulet', 300, 'grammes'),
('Poulet grillé aux herbes', 'Thym', 1, 'cuillères à soupe'),
('Poulet grillé aux herbes', 'Romarin', 1, 'cuillères à café'),
('Poulet grillé aux herbes', 'Ail', 25, 'grammes'),
('Poulet grillé aux herbes', 'Huile d''olive', 2, 'cuillères à soupe'),
('Poulet grillé aux herbes', 'Sel', 1, 'pincée'),
('Poulet grillé aux herbes', 'Poivre', 1, 'pincée');

INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Bowl de buddha au tofu', 'Tofu', 200, 'grammes'),
('Bowl de buddha au tofu', 'Quinoa', 100, 'grammes'),
('Bowl de buddha au tofu', 'Avocat', 80, 'grammes'),
('Bowl de buddha au tofu', 'Concombre', 100, 'grammes'),
('Bowl de buddha au tofu', 'Carotte', 115, 'grammes');


-- Chili con carne sans haricots
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Chili con carne sans haricots', 'Bœuf haché', 500, 'grammes'),
('Chili con carne sans haricots', 'Tomate', 90, 'grammes'),
('Chili con carne sans haricots', 'Poivron', 85, 'grammes'),
('Chili con carne sans haricots', 'Oignon', 45, 'grammes'),
('Chili con carne sans haricots', 'Sel', 1, 'pincée'),
('Chili con carne sans haricots', 'Poivre', 1, 'pincée');

-- Smoothie bowl aux fruits et noix
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Smoothie bowl aux fruits et noix', 'Fruits assortis', 200, 'grammes'),
('Smoothie bowl aux fruits et noix', 'Lait d''amande', 20, 'centilitres'),
('Smoothie bowl aux fruits et noix', 'Graines de chia', 2, 'cuillères à soupe'),
('Smoothie bowl aux fruits et noix', 'Noix', 50, 'grammes'),
('Smoothie bowl aux fruits et noix', 'Miel', 20, 'centilitres');

-- Soupe de lentilles corail à la tomate
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Soupe de lentilles corail à la tomate', 'Lentilles corail', 200, 'grammes'),
('Soupe de lentilles corail à la tomate', 'Tomates pelées', 400, 'grammes'),
('Soupe de lentilles corail à la tomate', 'Oignon', 40, 'grammes'),
('Soupe de lentilles corail à la tomate', 'Ail', 10, 'grammes'),
('Soupe de lentilles corail à la tomate', 'Cumin', 1, 'cuillères à café'),
('Soupe de lentilles corail à la tomate', 'Paprika', 1, 'cuillères à café'),
('Soupe de lentilles corail à la tomate', 'Bouillon de légumes', 50, 'centilitres');

-- Soupe de lentilles vertes et carottes
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Soupe de lentilles vertes et carottes', 'Lentilles vertes', 200, 'grammes'),
('Soupe de lentilles vertes et carottes', 'Carotte', 70, 'grammes'),
('Soupe de lentilles vertes et carottes', 'Oignon', 30, 'grammes'),
('Soupe de lentilles vertes et carottes', 'Bouillon de légumes', 50, 'centilitres'),
('Soupe de lentilles vertes et carottes', 'Thym', 1, 'pincée'),
('Soupe de lentilles vertes et carottes', 'Laurier', 1, 'pincée');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Risotto aux champignons et asperges', 'Riz Arborio', 200, 'grammes'),
('Risotto aux champignons et asperges', 'Champignon', 150, 'grammes'),
('Risotto aux champignons et asperges', 'Asperge', 100, 'grammes'),
('Risotto aux champignons et asperges', 'Bouillon de légumes', 50, 'centilitres'),
('Risotto aux champignons et asperges', 'Oignon', 30, 'grammes'),
('Risotto aux champignons et asperges', 'Ail', 2, 'grammes'),
('Risotto aux champignons et asperges', 'Parmesan', 50, 'grammes'),
('Risotto aux champignons et asperges', 'Vin blanc', 100, 'centilitres'),
('Risotto aux champignons et asperges', 'Beurre', 30, 'grammes'),
('Risotto aux champignons et asperges', 'Huile d''olive', 2, 'cuillères à soupe'),
('Risotto aux champignons et asperges', 'Sel', 1, 'pincée'),
('Risotto aux champignons et asperges', 'Poivre', 1, 'pincée');


INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Risotto aux champignons et épinards', 'Riz Arborio', 200, 'grammes'),
('Risotto aux champignons et épinards', 'Champignon', 150, 'grammes'),
('Risotto aux champignons et épinards', 'Épinard', 100, 'grammes'),
('Risotto aux champignons et épinards', 'Bouillon de légumes', 50, 'centilitres'),
('Risotto aux champignons et épinards', 'Oignon', 14, 'grammes'),
('Risotto aux champignons et épinards', 'Ail', 15, 'grammes'),
('Risotto aux champignons et épinards', 'Parmesan', 50, 'grammes'),
('Risotto aux champignons et épinards', 'Vin blanc', 10, 'centilitres'),
('Risotto aux champignons et épinards', 'Huile d''olive', 2, 'cuillères à soupe'),
('Risotto aux champignons et épinards', 'Sel', 1, 'pincée'),
('Risotto aux champignons et épinards', 'Poivre', 1, 'pincée');


-- Mousse au Chocolat
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Mousse au Chocolat', 'Chocolat', 150, 'grammes'),
('Mousse au Chocolat', 'Oeuf', 225, 'grammes');


-- Cheesecake
INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES
('Cheesecake', 'Fromage frais', 500, 'grammes'),
('Cheesecake', 'Sucre', 150, 'grammes'),
('Cheesecake', 'Oeuf', 175, 'grammes'),
('Cheesecake', 'Beurre', 100, 'grammes');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Salade César', 'Bol'),
('Salade César', 'Couteau'),
('Salade César', 'Planche à découper'),
('Poulet rôti', 'Plat de cuisson'),
('Poulet rôti', 'Couteau'),
('Poulet rôti', 'Pinceau de cuisine'),
('Gazpacho', 'Mixeur'),
('Gazpacho', 'Couteau'),
('Gazpacho', 'Planche à découper'),
('Gazpacho', 'Bol'),
('Burger végétarien', 'Poêle'),
('Burger végétarien', 'Spatule'),
('Burger végétarien', 'Couteau'),
('Burger végétarien', 'Planche à découper'),
('Soupe de légumes', 'Casserole'),
('Soupe de légumes', 'Couteau'),
('Soupe de légumes', 'Planche à découper'),
('Ratatouille', 'Cocotte'),
('Ratatouille', 'Couteau'),
('Ratatouille', 'Planche à découper'),
('Crêpes', 'Poêle à crêpes'),
('Crêpes', 'Louche'),
('Crêpes', 'Spatule'),
('Quiche Lorraine', 'Moule à tarte'),
('Quiche Lorraine', 'Bol'),
('Quiche Lorraine', 'Fouet'),
('Risotto aux champignons', 'Casserole'),
('Risotto aux champignons', 'Cuillère en bois'),
('Gâteau au chocolat', 'Moule à gâteau'),
('Gâteau au chocolat', 'Fouet'),
('Gâteau au chocolat', 'Bol'),
('Lasagnes aux épinards et ricotta', 'Plat à lasagne'),
('Lasagnes aux épinards et ricotta', 'Couteau'),
('Lasagnes aux épinards et ricotta', 'Bol'),
('Quiche aux champignons et poireaux', 'Moule à tarte'),
('Quiche aux champignons et poireaux', 'Couteau'),
('Quiche aux champignons et poireaux', 'Bol'),
('Salade de quinoa aux légumes grillés', 'Casserole'),
('Salade de quinoa aux légumes grillés', 'Grill'),
('Salade de quinoa aux légumes grillés', 'Bol'),
('Burger végétarien aux haricots noirs', 'Poêle'),
('Burger végétarien aux haricots noirs', 'Spatule'),
('Burger végétarien aux haricots noirs', 'Bol'),
('Soupe de lentilles', 'Casserole'),
('Soupe de lentilles', 'Couteau'),
('Soupe de lentilles', 'Planche à découper'),
('Tacos aux légumes et guacamole', 'Poêle'),
('Tacos aux légumes et guacamole', 'Bol'),
('Tacos aux légumes et guacamole', 'Presse-agrumes'),
('Gâteau au chocolat végétalien', 'Moule à gâteau'),
('Gâteau au chocolat végétalien', 'Fouet'),
('Gâteau au chocolat végétalien', 'Bol');

INSERT INTO Recette_utilise_Ustensil (rnom, unom) VALUES
('Omelette aux épinards et feta', 'Poêle'),
('Omelette aux épinards et feta', 'Bol'),
('Poulet grillé aux herbes', 'Plat de cuisson'),
('Poulet grillé aux herbes', 'Couteau de cuisine'),
('Bowl de buddha au tofu', 'Casserole'),
('Bowl de buddha au tofu', 'Couteau de cuisine'),
('Chili con carne sans haricots', 'Poêle'),
('Chili con carne sans haricots', 'Couteau de cuisine'),
('Salade niçoise', 'Bol'),
('Salade niçoise', 'Couteau de cuisine'),
('Smoothie bowl aux fruits et noix', 'Mixeur'),
('Soupe de lentilles corail à la tomate', 'Casserole'),
('Soupe de lentilles corail à la tomate', 'Mixeur'),
('Soupe de lentilles vertes et carottes', 'Casserole'),
('Risotto aux champignons et asperges', 'Casserole'),
('Risotto aux champignons et épinards', 'Casserole'),
('Mousse au Chocolat', 'Bol'),
('Mousse au Chocolat', 'Mixeur'),
('Cheesecake', 'Moule à gâteau'),
('Cheesecake', 'Fouet');




INSERT INTO Utilisateur_cache_Aliment (email, anom) VALUES
('alan.sottile@heig-vd.ch', 'Oeuf'),
('alan.sottile@heig-vd.ch', 'Banane'),
('alan.sottile@heig-vd.ch', 'Ail'),
('calvin.graf@heig-vd.ch', 'Banane'),
('calvin.graf@heig-vd.ch', 'Noix'),
('lucas.lattion@heig-vd.ch', 'Ail'),
('lucas.lattion@heig-vd.ch', 'Citron'),
('lucas.lattion@heig-vd.ch', 'Mangue'),
('antoine.girard@email.com', 'Ail'),
('sophie.dubois@email.com', 'Citron'),
('lucas.bernard@email.com', 'Tomate'),
('lucas.bernard@email.com', 'Poivron'),
('sophie.dubois@email.com', 'Mozzarella'),
('lucas.bernard@email.com', 'Sauce tomate');


-- Assignation des sous-groupes aux utilisateurs
INSERT INTO Utilisateur_cache_SousGroupe (email, sgnom) VALUES
('alan.sottile@heig-vd.ch', 'Viandes rouges'),
('alan.sottile@heig-vd.ch', 'Fruits Tropicaux'),
('calvin.graf@heig-vd.ch', 'Légumes Verts'),
('lucas.lattion@heig-vd.ch', 'Poissons et fruits de mer'),
('lucas.lattion@heig-vd.ch', 'Volailles'),
('alice.durand@email.com', 'Fruits Rouges'),
('alice.durand@email.com', 'Lait'),
('marc.dupont@email.com', 'Agrumes'),
('marc.dupont@email.com', 'Yaourt'),
('julie.lemoine@email.com', 'Légumes Racines'),
('julie.lemoine@email.com', 'Fromage'),
('lucas.bernard@email.com', 'Crevettes'),
('lucas.bernard@email.com', 'Riz'),
('emilie.petit@email.com', 'Légumes à Feuilles'),
('emilie.petit@email.com', 'Quinoa'),
('pierre.martin@email.com', 'Avoine'),
('pierre.martin@email.com', 'Lentilles'),
('sophie.dubois@email.com', 'Noix'),
('sophie.dubois@email.com', 'Basilic');

INSERT INTO Utilisateur_possede_Aliment (email, anom, quantite, unite_mesure) VALUES
('lucas.bernard@email.com', 'Tomate', 300, 'grammes'),
('lucas.lattion@heig-vd.ch', 'Ail', 500, 'grammes'),
('sophie.dubois@email.com', 'Mozzarella', 300, 'grammes'),
('calvin.graf@heig-vd.ch', 'Banane', 230, 'grammes'),
('calvin.graf@heig-vd.ch', 'Noix', 430, 'grammes'),
('alan.sottile@heig-vd.ch', 'Ail', 630, 'grammes'),
('lucas.bernard@email.com', 'Poivron', 130, 'grammes'),
('calvin.graf@heig-vd.ch', 'Saumon d''élevage, cru', 230, 'grammes'),
('lucas.lattion@heig-vd.ch', 'Mangue', 530, 'grammes'),
('sophie.dubois@email.com', 'Citron', 20, 'grammes'),
('lucas.lattion@heig-vd.ch', 'Citron', 30, 'grammes'),
('antoine.girard@email.com', 'Ail', 30, 'grammes'),
('alan.sottile@heig-vd.ch', 'Pâte à pizza', 50, 'grammes'),
('alan.sottile@heig-vd.ch', 'Sauce tomate', 30, 'grammes'),
('alan.sottile@heig-vd.ch', 'Mozzarella', 200, 'grammes'),
('alan.sottile@heig-vd.ch', 'Basilic', 230, 'grammes'),
('alan.sottile@heig-vd.ch', 'Oeuf', 30, 'grammes');

INSERT INTO Utilisateur_cache_Recette (email, rnom) VALUES
('alan.sottile@heig-vd.ch', 'Poulet rôti au yogourt'),
('alan.sottile@heig-vd.ch', 'Smoothie aux fruits tropicaux'),
('calvin.graf@heig-vd.ch', 'Pâtes à l''ail'),
('calvin.graf@heig-vd.ch', 'Saumon au citron'),
('lucas.lattion@heig-vd.ch', 'Omelette aux champignons'),
('lucas.lattion@heig-vd.ch', 'Tarte aux pommes'),
('alice.durand@email.com', 'Smoothie aux baies'),
('alice.durand@email.com', 'Pâtes carbonara'),
('marc.dupont@email.com', 'Poulet au miel et à la moutarde'),
('marc.dupont@email.com', 'Tarte aux tomates et mozzarella'),
('julie.lemoine@email.com', 'Smoothie à la mangue et à la noix de coco'),
('julie.lemoine@email.com', 'Salade de crevettes et avocat'),
('lucas.bernard@email.com', 'Soupe de courge'),
('lucas.bernard@email.com', 'Poulet grillé au citron et à l''ail'),
('emilie.petit@email.com', 'Salade de pâtes aux légumes grillés'),
('emilie.petit@email.com', 'Saumon en croûte de noix'),
('pierre.martin@email.com', 'Pizza Margherita'),
('pierre.martin@email.com', 'Pizza aux champignons'),
('sophie.dubois@email.com', 'Tarte aux pommes'),
('sophie.dubois@email.com', 'Poulet rôti au yogourt');

-- Create Views
set search_path to bdrProject;

CREATE VIEW top_groupe_type
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid is null;

CREATE VIEW sousgroupe_viande
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Viandes')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Viandes'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Viandes';

CREATE VIEW sousgroupe_Légumes
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Légumes')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Légumes'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Légumes';

CREATE VIEW sousgroupe_Fruits
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Fruits')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Fruits'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Fruits';

CREATE VIEW sousgroupe_Poissons
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Poissons et fruits de mer')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Poissons et fruits de mer'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Poissons et fruits de mer';

CREATE VIEW sousgroupe_Laitiers
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Produits laitiers')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Produits laitiers'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Produits laitiers';

CREATE VIEW sousgroupe_cereale
AS
    SELECT sgnom FROM sousgroupe
    WHERE parentid IN (
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Céréales et légumineuses')
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE parentid = 'Céréales et légumineuses'
    UNION
    SELECT sgnom FROM sousgroupe
    WHERE sousgroupe.sgnom = 'Céréales et légumineuses';


CREATE VIEW sousgroupe_animal
AS
    SELECT * FROM sousgroupe_viande
    UNION
    SELECT * FROM sousgroupe_Poissons;

CREATE VIEW sousgroupe_provenance_animal
AS
    SELECT * FROM sousgroupe_animal
    UNION
    SELECT * FROM sousgroupe_Laitiers;

SELECT * FROM top_groupe_type;
SELECT * FROM sousgroupe_viande;
SELECT * FROM sousgroupe_Légumes;
SELECT * FROM sousgroupe_Fruits;
SELECT * FROM sousgroupe_Poissons;
SELECT * FROM sousgroupe_Laitiers;
SELECT * FROM sousgroupe_animal;
SELECT * FROM sousgroupe_provenance_animal;


CREATE VIEW calories_recette AS
SELECT
    rca.rnom,
    SUM(ali.kcal * rca.quantite) AS total_calories
FROM
    Recette_contient_Aliment rca
JOIN
    Aliment ali ON rca.anom = ali.anom
GROUP BY
    rca.rnom
ORDER BY
    total_calories DESC;

SELECT * FROM calories_recette;


CREATE VIEW recette_non_vegetarien AS
SELECT rnom
FROM recette_contient_aliment rca
INNER JOIN aliment a ON rca.anom = a.anom
WHERE a.groupe IN (SELECT * FROM sousgroupe_animal);

SELECT * FROM recette_non_vegetarien;

CREATE VIEW recette_vegetarien AS
SELECT rnom
FROM recette r
WHERE rnom NOT IN (SELECT * FROM recette_non_vegetarien);

SELECT * FROM recette_vegetarien;



CREATE VIEW recette_avec_gluten AS
SELECT rnom
FROM recette_contient_aliment rca
INNER JOIN aliment a ON rca.anom = a.anom
WHERE a.groupe = 'Céréales et légumineuses';

SELECT * FROM recette_non_vegetarien;

CREATE VIEW recette_sans_gluten AS
SELECT rnom
FROM recette r
WHERE rnom NOT IN (SELECT * FROM recette_avec_gluten);

SELECT * FROM recette_sans_gluten;

SELECT rnom, anom FROM recette_contient_aliment
WHERE rnom = 'Quiche Lorraine';

SELECT rca.rnom, rca.anom, a.groupe FROM recette_contient_aliment rca
INNER JOIN aliment a ON rca.anom = a.anom
WHERE rnom = 'Bowl de buddha au tofu';


CREATE VIEW recette_cache_alan AS
    SELECT rnom FROM utilisateur_cache_recette
    WHERE email = 'alan.sottile@heig-vd.ch';

CREATE VIEW recette_alan AS
    SELECT rnom FROM recette
    WHERE rnom NOT IN (SELECT rnom FROM recette_cache_alan);

SELECT * FROM recette_cache_alan;
SELECT * FROM recette_alan;




-- Create Functions and Triggers
CREATE OR REPLACE FUNCTION after_update_utilisateur_cache_aliment()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO utilisateur_cache_recette (email, rnom)
    SELECT DISTINCT NEW.email, rca.rnom
    FROM recette_contient_aliment rca
    WHERE rca.anom = NEW.anom
    ON CONFLICT (email, rnom) DO NOTHING;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION after_update_utilisateur_cache_sousgroupe()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO utilisateur_cache_recette (email, rnom)
    SELECT DISTINCT NEW.email, rca.rnom
    FROM recette_contient_aliment rca
    JOIN aliment a ON rca.anom = a.anom
    WHERE a.groupe = NEW.sgnom
    ON CONFLICT (email, rnom) DO NOTHING;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cache_aliment_trigger
AFTER UPDATE ON utilisateur_cache_aliment
FOR EACH ROW
EXECUTE FUNCTION after_update_utilisateur_cache_aliment();

CREATE TRIGGER cache_sousgroupe_trigger
AFTER UPDATE ON utilisateur_cache_sousgroupe
FOR EACH ROW
EXECUTE FUNCTION after_update_utilisateur_cache_sousgroupe();


