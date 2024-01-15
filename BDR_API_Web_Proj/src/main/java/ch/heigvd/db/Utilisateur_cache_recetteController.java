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


public class Utilisateur_cache_recetteController {

    private Connection conn;
    private AuthController authController;
    public Utilisateur_cache_recetteController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String email = null;
            String rnom = null;

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
                if (requestBody.has("email")) {
                    email = requestBody.get("email").getAsString();
                }
                if (requestBody.has("rnom")) {
                    rnom = requestBody.get("rnom").getAsString();
                }
            }

            List<Utilisateur_cache_recette> utilisateur_cache_recetteList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM utilisateur_cache_recette"); // assuming the table name is 'utilisateur_cache_recette'

            List<String> conditions = new ArrayList<>();

            if (email != null) {
                conditions.add("email = ?");
            }
            if (rnom != null) {
                conditions.add("rnom = ?");
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
            if (email != null) {
                stmt.setString(index++, email);
            }
            if (rnom != null) {
                stmt.setString(index, rnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Utilisateur_cache_recette utilisateur_cache_recette = new Utilisateur_cache_recette();
                utilisateur_cache_recette.email = rs.getString("email");
                utilisateur_cache_recette.rnom = rs.getString("rnom");
                utilisateur_cache_recetteList.add(utilisateur_cache_recette);
            }

            ctx.json(utilisateur_cache_recetteList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Utilisateur_cache_recette newUtilisateur_cache_recette = ctx.bodyValidator(Utilisateur_cache_recette.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO utilisateur_cache_recette (email, rnom) VALUES (?, ?)")) {

                insertStmt.setString(1, newUtilisateur_cache_recette.email);
                insertStmt.setString(2, newUtilisateur_cache_recette.rnom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUtilisateur_cache_recette);
                return;
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) { // Unique violation
                    throw new ConflictResponse();
                } else {
                    throw e;
                }
            }
        }
        throw new UnauthorizedResponse();
    }

    public void update(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            String email = ctx.pathParam("email");
			String rnom = ctx.pathParam("rnom");

            Utilisateur_cache_recette updateUtilisateur_cache_recette = ctx.bodyValidator(Utilisateur_cache_recette.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE utilisateur_cache_recette SET email = ?, rnom = ? WHERE email = ? AND rnom = ?");
            stmt.setString(1, updateUtilisateur_cache_recette.email);
            stmt.setString(2, updateUtilisateur_cache_recette.rnom);
            stmt.setString(3, email);
			stmt.setString(4, rnom);
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
            String email = ctx.pathParam("email");
			String rnom = ctx.pathParam("rnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur_cache_recette WHERE email = ? AND rnom = ?");
            stmt.setString(1, email);
			stmt.setString(2, rnom);

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
