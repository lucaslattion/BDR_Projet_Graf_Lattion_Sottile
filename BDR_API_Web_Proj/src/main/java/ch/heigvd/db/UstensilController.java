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


public class UstensilController {

    private Connection conn;
    private AuthController authController;
    public UstensilController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }


    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
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
                if (requestBody.has("unom")) {
                    unom = requestBody.get("unom").getAsString();
                }
            }

            List<Ustensil> ustensilList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ustensil ORDER BY unom ASC");

            List<String> conditions = new ArrayList<>();

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
            if (unom != null) {
                stmt.setString(index++, unom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ustensil ustensil = new Ustensil();
                ustensil.unom = rs.getString("unom");;
                ustensilList.add(ustensil);


            }

            ctx.json(ustensilList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Ustensil newUstensil = ctx.bodyValidator(Ustensil.class)
                    .check(obj -> obj.unom != null, "Missing ustensil name")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO ustensil (unom) VALUES (?)")) {
                insertStmt.setString(1, newUstensil.unom);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUstensil);
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
            String unom = ctx.pathParam("unom");
            Ustensil updateUstensil = ctx.bodyValidator(Ustensil.class)
                    .check(obj -> obj.unom != null, "Missing ustensil name")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE ustensil SET unom = ? WHERE unom = ?");
            stmt.setString(1, updateUstensil.unom);
            stmt.setString(2, unom);

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
            String unom = ctx.pathParam("unom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM ustensil WHERE unom = ?");
            stmt.setString(1, unom);

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
