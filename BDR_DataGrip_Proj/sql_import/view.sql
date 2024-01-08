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