CREATE OR REPLACE FUNCTION after_insert_utilisateur_cache_aliment()
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

CREATE OR REPLACE FUNCTION after_insert_utilisateur_cache_sousgroupe()
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
AFTER INSERT ON utilisateur_cache_aliment
FOR EACH ROW
EXECUTE FUNCTION after_insert_utilisateur_cache_aliment();

CREATE TRIGGER cache_sousgroupe_trigger
AFTER INSERT ON utilisateur_cache_sousgroupe
FOR EACH ROW
EXECUTE FUNCTION after_insert_utilisateur_cache_sousgroupe();

------------------------------------Tests------------------------------------

SELECT *
FROM Utilisateur_cache_Recette
WHERE email = 'alan.sottile@heig-vd.ch';

INSERT INTO utilisateur_cache_aliment(email, anom) VALUES ('alan.sottile@heig-vd.ch', 'Beurre');

SELECT *
FROM Utilisateur_cache_Recette
WHERE email = 'alan.sottile@heig-vd.ch';


SELECT *
FROM Utilisateur_cache_Recette
WHERE email = 'alan.sottile@heig-vd.ch';

INSERT INTO utilisateur_cache_sousgroupe(email, sgnom) VALUES ('alan.sottile@heig-vd.ch', 'Légumes');

SELECT *
FROM Utilisateur_cache_Recette
WHERE email = 'alan.sottile@heig-vd.ch';