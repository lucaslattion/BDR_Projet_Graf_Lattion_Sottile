package ch.heigvd;

import ch.heigvd.auth.AuthController;
import ch.heigvd.db.*;
import ch.heigvd.user.UsersController;
import io.javalin.Javalin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public final static int PORT = 80;

    public static void main(String[] args) {
        // Database connection details
        //String host = "bdr-projet-postgresql"; //Host to use in Docker
        String host = "localhost"; //Developpement Host
        String port = "5432";
        String dbname = "bdr";
        String schema = "bdrproject"; // Nom du schéma
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?currentSchema=" + schema;
        String dbUsername = "bdr";
        String dbPassword = "bdr";

        // Using try-with-resources to ensure proper closure of the app
        try (Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Serve static files from 'src/main/resources/public'
        })) {

            app.get("/", ctx -> ctx.redirect("/login.html"));

            // Database connection
            try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                // Code pour interagir avec la base de données

                // Controllers
                AuthController authController = new AuthController(conn);
                UsersController usersController = new UsersController(conn);

                Aliment_contient_vitamineController aliment_contient_vitamineController = new Aliment_contient_vitamineController(conn, authController);
                AlimentController alimentController = new AlimentController(conn, authController);
                Liste_contient_recetteController liste_contient_recetteController = new Liste_contient_recetteController(conn, authController);
                ListeController listeController = new ListeController(conn, authController);
                Recette_contient_alimentController recette_contient_alimentController = new Recette_contient_alimentController(conn, authController);
                Recette_utilise_ustensilController recette_utilise_ustensilController = new Recette_utilise_ustensilController(conn, authController);
                RecetteController recetteController = new RecetteController(conn, authController);
                RegimeController regimeController = new RegimeController(conn, authController);
                SousgroupeController sousgroupeController = new SousgroupeController(conn, authController);
                TypeController typeController = new TypeController(conn, authController);
                UniteMesureController uniteMesureController = new UniteMesureController(conn, authController);
                UstensilController ustensilController = new UstensilController(conn, authController);
                Utilisateur_cache_alimentController utilisateur_cache_alimentController = new Utilisateur_cache_alimentController(conn, authController);
                Utilisateur_cache_recetteController utilisateur_cache_recetteController = new Utilisateur_cache_recetteController(conn, authController);
                Utilisateur_cache_sousgroupeController utilisateur_cache_sousgroupeController = new Utilisateur_cache_sousgroupeController(conn, authController);
                Utilisateur_possede_alimentController utilisateur_possede_alimentController = new Utilisateur_possede_alimentController(conn, authController);
                Utilisateur_suit_regimeController utilisateur_suit_regimeController = new Utilisateur_suit_regimeController(conn, authController);
                VitamineController vitamineController = new VitamineController(conn, authController);

                // Auth routes
                app.post("/login", authController::login);
                app.post("/logout", authController::logout);
                app.get("/profile", authController::profile);

                // User routes
                app.post("/user", usersController::create);
                app.get("/user", usersController::getMany);
                app.get("/user/{email}", usersController::getOne);
                app.put("/user/{email}", usersController::update);
                app.delete("/user/{email}", usersController::delete);

                // All the db routes

                // aliment_contient_vitamine routes
                app.get("/aliment_contient_vitamine", aliment_contient_vitamineController::getMany);
                app.post("/aliment_contient_vitamine/limit", aliment_contient_vitamineController::getMany);
                app.post("/aliment_contient_vitamine", aliment_contient_vitamineController::create);
                app.put("/aliment_contient_vitamine/{anom}/{vinom}", aliment_contient_vitamineController::update);
                app.delete("/aliment_contient_vitamine/{anom}/{vinom}", aliment_contient_vitamineController::delete);


                // Aliment routes
                app.get("/aliment", alimentController::getMany);
                app.get("/aliment/dispo", alimentController::getAvailable);
                app.post("/aliment/limit", alimentController::getMany);
                app.post("/aliment", alimentController::create);
                app.put("/aliment/{anom}", alimentController::update);
                app.delete("/aliment/{anom}", alimentController::delete);

                // liste_contient_recette routes
                app.get("/liste_contient_recette", liste_contient_recetteController::getMany);
                app.post("/liste_contient_recette/limit", liste_contient_recetteController::getMany);
                app.post("/liste_contient_recette/{lnom}/{rnom}", liste_contient_recetteController::create);
                app.put("/liste_contient_recette/{lnom}", liste_contient_recetteController::update);
                app.delete("/liste_contient_recette/{lnom}", liste_contient_recetteController::delete);

                // liste routes
                app.get("/liste", listeController::getMany);
                app.post("/liste/limit", listeController::getMany);
                app.post("/liste", listeController::create);
                app.put("/liste/{lnom}/{email}", listeController::update);
                app.delete("/liste/{lnom}/{email}", listeController::delete);

                // recette_contient_aliment routes
                app.get("/recette_contient_aliment", recette_contient_alimentController::getMany);
                app.post("/recette_contient_aliment/limit", recette_contient_alimentController::getMany);
                app.post("/recette_contient_aliment", recette_contient_alimentController::create);
                app.put("/recette_contient_aliment/{rnom}/{anom}", recette_contient_alimentController::update);
                app.delete("/recette_contient_aliment/{rnom}/{anom}", recette_contient_alimentController::delete);
                app.get("/recette_contient_aliment/{rnom}", recette_contient_alimentController::getMany);

                // recette_utilise_ustensil routes
                app.get("/recette_utilise_ustensil", recette_utilise_ustensilController::getMany);
                app.post("/recette_utilise_ustensil/limit", recette_utilise_ustensilController::getMany);
                app.post("/recette_utilise_ustensil", recette_utilise_ustensilController::create);
                app.put("/recette_utilise_ustensil/{rnom}/{unom}", recette_utilise_ustensilController::update);
                app.delete("/recette_utilise_ustensil/{rnom}/{unom}", recette_utilise_ustensilController::delete);
                app.get("/recette_utilise_ustensil/{rnom}", recette_utilise_ustensilController::getMany);

                // recette routes
                app.get("/recette", recetteController::getMany);
                app.post("/recette/limit", recetteController::getMany);
                app.post("/recette", recetteController::create);
                app.put("/recette/{rnom}", recetteController::update);
                app.delete("/recette/{rnom}", recetteController::delete);
                app.get("/recette/{rnom}", recetteController::getOne);
                app.get("/recette/getInfos/{rnom}", recetteController::getInformations);

                // regime routes
                app.get("/regime", regimeController::getMany);
                app.post("/regime/limit", regimeController::getMany);
                app.post("/regime", regimeController::create);
                app.put("/regime/{regnom}", regimeController::update);
                app.delete("/regime/{regnom}", regimeController::delete);

                // sousgroupe routes
                app.get("/sousgroupe", sousgroupeController::getMany);
                app.post("/sousgroupe/limit", sousgroupeController::getMany);
                app.post("/sousgroupe", sousgroupeController::create);
                app.put("/sousgroupe/{sgnom}", sousgroupeController::update);
                app.delete("/sousgroupe/{sgnom}", sousgroupeController::delete);

                // type routes
                app.get("/type", typeController::getMany);
                app.post("/type/limit", typeController::getMany);
                app.post("/type", typeController::create);
                app.put("/type/{tnom}", typeController::update);
                app.delete("/type/{tnom}", typeController::delete);

                app.get("/uniteMesure", uniteMesureController::getMany);
                app.post("/uniteMesure/limit", uniteMesureController::getMany);
                app.post("/uniteMesure", uniteMesureController::create);
                app.put("/uniteMesure/{udmnom}", uniteMesureController::update);
                app.delete("/uniteMesure/{udmnom}", uniteMesureController::delete);

                // ustensil routes
                app.get("/ustensil", ustensilController::getMany);
                app.post("/ustensil/limit", ustensilController::getMany);
                app.post("/ustensil", ustensilController::create);
                app.put("/ustensil/{unom}", ustensilController::update);
                app.delete("/ustensil/{unom}", ustensilController::delete);

                // utilisateur_cache_aliment routes
                app.get("/utilisateur_cache_aliment", utilisateur_cache_alimentController::getMany);
                app.post("/utilisateur_cache_aliment/limit", utilisateur_cache_alimentController::getMany);
                app.post("/utilisateur_cache_aliment", utilisateur_cache_alimentController::create);
                app.put("/utilisateur_cache_aliment/{email}/{anom}", utilisateur_cache_alimentController::update);
                app.delete("/utilisateur_cache_aliment/{anom}", utilisateur_cache_alimentController::delete);

                // utilisateur_cache_recette routes
                app.get("/utilisateur_cache_recette", utilisateur_cache_recetteController::getMany);
                app.post("/utilisateur_cache_recette/limit", utilisateur_cache_recetteController::getMany);
                app.post("/utilisateur_cache_recette", utilisateur_cache_recetteController::create);
                app.put("/utilisateur_cache_recette/{email}/{rnom}", utilisateur_cache_recetteController::update);
                app.delete("/utilisateur_cache_recette/{email}/{rnom}", utilisateur_cache_recetteController::delete);

                // utilisateur_cache_sousgroupe routes
                app.get("/utilisateur_cache_sousgroupe", utilisateur_cache_sousgroupeController::getMany);
                app.post("/utilisateur_cache_sousgroupe/limit", utilisateur_cache_sousgroupeController::getMany);
                app.post("/utilisateur_cache_sousgroupe", utilisateur_cache_sousgroupeController::create);
                app.put("/utilisateur_cache_sousgroupe/{email}/{sgnom}", utilisateur_cache_sousgroupeController::update);
                app.delete("/utilisateur_cache_sousgroupe/{email}/{sgnom}", utilisateur_cache_sousgroupeController::delete);

                // utilisateur_possede_aliment routes
                app.get("/utilisateur_possede_aliment", utilisateur_possede_alimentController::getMany);
                app.put("/utilisateur_possede_aliment/loggedUser", utilisateur_possede_alimentController::update);
                app.post("/utilisateur_possede_aliment/limit", utilisateur_possede_alimentController::getMany);
                app.post("/utilisateur_possede_aliment", utilisateur_possede_alimentController::create);
                app.put("/utilisateur_possede_aliment/{email}/{anom}", utilisateur_possede_alimentController::update);
                app.delete("/utilisateur_possede_aliment/{anom}", utilisateur_possede_alimentController::delete);

                // Utilisateur_suit_regime routes
                app.get("/utilisateur_suit_regime", utilisateur_suit_regimeController::getMany);
                app.post("/utilisateur_suit_regime/limit", utilisateur_suit_regimeController::getMany);
                app.post("/utilisateur_suit_regime/{regnom}", utilisateur_suit_regimeController::create);
                app.put("/utilisateur_suit_regime/{email}/{regnom}", utilisateur_suit_regimeController::update);
                app.delete("/utilisateur_suit_regime/{regnom}", utilisateur_suit_regimeController::delete);

                // Vitamine routes
                app.get("/vitamine", vitamineController::getMany);
                app.post("/vitamine/limit", vitamineController::getMany);
                app.post("/vitamine", vitamineController::create);
                app.put("/vitamine/{vinom}", vitamineController::update);
                app.delete("/vitamine/{vinom}", vitamineController::delete);


                app.start(PORT);

                // Keep the server running
                app.events(event -> {
                    event.serverStopping(() -> System.out.println("Server is stopping"));
                    event.serverStopped(() -> System.out.println("Server has stopped"));
                });

                // Hold the main thread to prevent the application from exiting
                Thread.currentThread().join();
            } catch (SQLException e) {
                // Gestion des exceptions SQL
                System.out.println("Server interrupted: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Server interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
