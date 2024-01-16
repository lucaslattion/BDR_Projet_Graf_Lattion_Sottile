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


public class Recette_utilise_ustensilController {

    private Connection conn;
    private AuthController authController;
    public Recette_utilise_ustensilController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String rnom = null;
            String unom = null;

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
                if (requestBody.has("rnom")) {
                    rnom = requestBody.get("rnom").getAsString();
                }
                if (requestBody.has("unom")) {
                    unom = requestBody.get("unom").getAsString();
                }
            }

            List<Recette_utilise_ustensil> recette_utilise_ustensilList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM recette_utilise_ustensil"); // assuming the table name is 'recette_utilise_ustensil'

            List<String> conditions = new ArrayList<>();

            if (rnom != null) {
                conditions.add("rnom = ?");
            }
            if (unom != null) {
                conditions.add("unom = ?");
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
            if (rnom != null) {
                stmt.setString(index++, rnom);
            }
            if (unom != null) {
                stmt.setString(index, unom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recette_utilise_ustensil recette_utilise_ustensil = new Recette_utilise_ustensil();
                recette_utilise_ustensil.rnom = rs.getString("rnom");
                recette_utilise_ustensil.unom = rs.getString("unom");
                recette_utilise_ustensilList.add(recette_utilise_ustensil);
            }

            ctx.json(recette_utilise_ustensilList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Recette_utilise_ustensil newRecette_utilise_ustensil = ctx.bodyValidator(Recette_utilise_ustensil.class)
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .check(obj -> obj.unom != null, "Missing unom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO recette_utilise_ustensil (rnom, unom) VALUES (?, ?)")) {

                insertStmt.setString(1, newRecette_utilise_ustensil.rnom);
                insertStmt.setString(2, newRecette_utilise_ustensil.unom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRecette_utilise_ustensil);
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

            String rnom = ctx.pathParam("rnom");
			String unom = ctx.pathParam("unom");

            Recette_utilise_ustensil updateRecette_utilise_ustensil = ctx.bodyValidator(Recette_utilise_ustensil.class)
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .check(obj -> obj.unom != null, "Missing unom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recette_utilise_ustensil SET rnom = ?, unom = ? WHERE rnom = ? AND unom = ?");
            stmt.setString(1, updateRecette_utilise_ustensil.rnom);
            stmt.setString(2, updateRecette_utilise_ustensil.unom);
            stmt.setString(3, rnom);
			stmt.setString(4, unom);
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
            String rnom = ctx.pathParam("rnom");
			String unom = ctx.pathParam("unom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM recette_utilise_ustensil WHERE rnom = ? AND unom = ?");
            stmt.setString(1, rnom);
			stmt.setString(2, unom);

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
