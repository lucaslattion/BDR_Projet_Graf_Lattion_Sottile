package ch.heigvd.db;

import io.javalin.http.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import ch.heigvd.auth.AuthController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Liste_contient_recetteController {

    private Connection conn;
    private AuthController authController;
    public Liste_contient_recetteController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String lnom = null;
            String email = null;

            // Parse JSON from the request body
            if (ctx.body() != null && !ctx.body().isEmpty()) {
                JsonObject requestBody = new JsonParser().parse(ctx.body()).getAsJsonObject();

                // Extract parameters from JSON
                if (requestBody.has("limit")) {
                    limit = requestBody.get("limit").getAsInt();
                }
                if (requestBody.has("offset")) {
                    offset = requestBody.get("offset").getAsInt();
                }
                if (requestBody.has("lnom")) {
                    lnom = requestBody.get("lnom").getAsString();
                }
                if (requestBody.has("email")) {
                    email = requestBody.get("email").getAsString();
                }
            }

            List<Liste_contient_recette> liste_contient_recetteList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM liste_contient_recette"); // assuming the table name is 'liste_contient_recette'

            List<String> conditions = new ArrayList<>();

            if (lnom != null) {
                conditions.add("lnom = ?");
            }
            if (email != null) {
                conditions.add("email = ?");
            }

            if (!conditions.isEmpty()) {
                queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            if (limit > 0) {
                // Adding LIMIT and OFFSET to the query
                queryBuilder.append(" LIMIT ").append(limit);
                if (offset > 0) {
                    queryBuilder.append(" OFFSET ").append(offset);
                }
            }

            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());

            int index = 1;
            if (lnom != null) {
                stmt.setString(index++, lnom);
            }
            if (email != null) {
                stmt.setString(index, email);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Liste_contient_recette liste_contient_recette = new Liste_contient_recette();
                liste_contient_recette.lnom = rs.getString("lnom");
				liste_contient_recette.rnom = rs.getString("rnom");
                liste_contient_recette.email = rs.getString("email");
                liste_contient_recetteList.add(liste_contient_recette);
            }

            ctx.json(liste_contient_recetteList);
            return;

        }
        throw new UnauthorizedResponse();
    }


    public void create(Context ctx) throws SQLException {
        // Vérifiez l'authentification de l'utilisateur
        if (authController.validLoggedUser(ctx)) {

            // Récupérez les détails de la liste_contient_recette à partir du corps de la requête
            Liste_contient_recette newListe_contient_recette = ctx.bodyValidator(Liste_contient_recette.class)
                    .check(obj -> obj.lnom != null, "Missing liste_contient_recette name")
                    .check(obj -> obj.rnom != null, "Missing liste_contient_recette rnom")
                    .check(obj -> obj.email != null, "Missing email")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO liste_contient_recette (lnom, rnom, email) VALUES (?, ?, ?)")) {

                // Remplacez les valeurs dans la requête préparée par les valeurs du nouvel objet Liste_contient_recette
                insertStmt.setString(1, newListe_contient_recette.lnom);
                insertStmt.setString(2, newListe_contient_recette.rnom);
                insertStmt.setString(3, newListe_contient_recette.email);

                // Exécutez la requête
                insertStmt.executeUpdate();

                // Répondez avec le statut 201 CREATED et l'objet nouvellement créé
                ctx.status(HttpStatus.CREATED);
                ctx.json(newListe_contient_recette);
                return;
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) { // Violation d'unicité
                    throw new ConflictResponse();
                } else {
                    throw e;
                }
            }
        }

        // L'utilisateur n'est pas authentifié
        throw new UnauthorizedResponse();
    }

    public void update(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            String lnom = ctx.pathParam("lnom");

            Liste_contient_recette updateListe_contient_recette = ctx.bodyValidator(Liste_contient_recette.class)
                    .check(obj -> obj.lnom != null, "Missing liste_contient_recette name")
					.check(obj -> obj.rnom != null, "Missing liste_contient_recette rnom")
                    .check(obj -> obj.email != null, "Missing email")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE liste_contient_recette SET lnom = ?, rnom = ?, email = ? WHERE lnom = ?");
            stmt.setString(1, updateListe_contient_recette.lnom);
            stmt.setString(2, updateListe_contient_recette.rnom);
            stmt.setString(3, updateListe_contient_recette.email);
            stmt.setString(4, lnom);

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new NotFoundResponse();
            }

            ctx.status(HttpStatus.NO_CONTENT);
            return;
        }
        throw new UnauthorizedResponse();
    }

    public void delete(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){
            String lnom = ctx.pathParam("lnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM liste_contient_recette WHERE lnom = ?");
            stmt.setString(1, lnom);

            int deletedRows = stmt.executeUpdate();
            if (deletedRows == 0) {
                throw new NotFoundResponse();
            }

            ctx.status(HttpStatus.NO_CONTENT);
            return;
        }
        throw new UnauthorizedResponse();
    }
}
