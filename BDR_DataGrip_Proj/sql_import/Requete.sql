-- Récupérer les informations d'un utilisateur
SELECT
    email,
    prenom,
    nom
FROM utilisateur;

-- Récupérer les régimes existants
SELECT regnom as regime
FROM regime;

-- Récupérer la liste des aliments qui existent
SELECT anom AS nom
FROM aliment;

-- Récupérer les types existants
SELECT tnom as type
FROM type;

-- Récupérer les liste d'un utilisateur spécifique
SELECT
    lnom AS nom_liste
FROM liste
WHERE email LIKE 'calvin.graf@heig-vd.ch';

-- Récupérer le contenu d'une liste d'un utilisateur spécifique
SELECT
     lcr.rnom AS Recette
FROM liste l
INNER JOIN liste_contient_recette lcr
    ON l.lnom = lcr.lnom and l.email = lcr.email
WHERE
    l.lnom LIKE 'Noel 2022' AND
    l.email LIKE 'calvin.graf@heig-vd.ch';

-- Récupérer les aliments d'une recette spécifique
SELECT
    r.rnom AS Recette,
    a.anom AS Aliment
FROM recette r
INNER JOIN recette_contient_aliment rca
    ON r.rnom = rca.rnom
INNER JOIN aliment a
    ON rca.anom = a.anom
WHERE r.rnom LIKE 'Pizza Margherita';

-- Récupérer les ustensiles d'une recette spécifique
SELECT
    r.rnom AS Recette,
    u.unom AS Ustensile
FROM recette r
INNER JOIN recette_utilise_ustensil ruu
    ON r.rnom = ruu.rnom
INNER JOIN ustensil u
    ON ruu.unom = u.unom
WHERE r.rnom LIKE 'Pizza Margherita';


-- Récupérer toutes les informations nutritive d'une recette
SELECT
    r.rnom AS Recette,
    SUM(a.kcal) AS Calories,
    SUM(a.proteines) AS Proteines,
    SUM(a.glucides) AS Glucides,
    SUM(a.lipides) AS Lipides,
    SUM(a.fibres) AS Fibres,
    SUM(a.sodium) AS Sodium,
    SUM(aav.quantite) AS Vitamine_A,
    SUM(ab1v.quantite) AS Vitamine_B1,
    SUM(ab2v.quantite) AS Vitamine_B2,
    SUM(ab3v.quantite) AS Vitamine_B3,
    SUM(ab6v.quantite) AS Vitamine_B6,
    SUM(ab12v.quantite) AS Vitamine_B12,
    SUM(acv.quantite) AS Vitamine_C,
    SUM(aev.quantite) AS Vitamine_D,
    SUM(aev.quantite) AS Vitamine_E
FROM
    Recette r
INNER JOIN Recette_contient_Aliment rca
        ON r.rnom = rca.rnom
INNER JOIN Aliment a
        ON rca.anom = a.anom
LEFT JOIN Aliment_contient_Vitamine aav
    ON a.anom = aav.anom AND aav.vinom = 'Vitamine A'
LEFT JOIN Aliment_contient_Vitamine ab1v
    ON a.anom = ab1v.anom AND ab1v.vinom = 'Vitamine B1'
LEFT JOIN Aliment_contient_Vitamine ab2v
    ON a.anom = ab2v.anom AND ab2v.vinom = 'Vitamine B2'
LEFT JOIN Aliment_contient_Vitamine ab3v
    ON a.anom = ab3v.anom AND ab3v.vinom = 'Vitamine B3'
LEFT JOIN Aliment_contient_Vitamine ab6v
    ON a.anom = ab6v.anom AND ab6v.vinom = 'Vitamine B6'
LEFT JOIN Aliment_contient_Vitamine ab12v
    ON a.anom = ab12v.anom AND ab12v.vinom = 'Vitamine B12'
LEFT JOIN Aliment_contient_Vitamine acv
    ON a.anom = acv.anom AND acv.vinom = 'Vitamine C'
LEFT JOIN Aliment_contient_Vitamine adv
    ON a.anom = adv.anom AND adv.vinom = 'Vitamine D'
LEFT JOIN Aliment_contient_Vitamine aev
    ON a.anom = aev.anom AND aev.vinom = 'Vitamine E'
GROUP BY
    r.rnom;