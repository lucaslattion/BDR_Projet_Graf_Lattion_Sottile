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


public class RecetteController {

    private Connection conn;
    private AuthController authController;
    public RecetteController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String rnom = null;
            String type_recette = null;

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
                if (requestBody.has("type_recette")) {
                    type_recette = requestBody.get("type_recette").getAsString();
                }
            }

            List<Recette> recetteList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM recette ORDER BY rnom ASC"); // assuming the table name is 'recette'

            List<String> conditions = new ArrayList<>();

            if (rnom != null) {
                conditions.add("rnom = ?");
            }
            if (type_recette != null) {
                conditions.add("type_recette = ?");
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
            if (type_recette != null) {
                stmt.setString(index, type_recette);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recette recette = new Recette();
                recette.rnom = rs.getString("rnom");
				recette.instructions = rs.getString("instructions");
                recette.type_recette = rs.getString("type_recette");
                recetteList.add(recette);
            }

            ctx.json(recetteList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Recette newRecette = ctx.bodyValidator(Recette.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
					.check(obj -> obj.instructions != null, "Missing recette instructions")
                    .check(obj -> obj.type_recette != null, "Missing type_recette")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO recette (rnom, instructions, type_recette) VALUES (?, ?, ?)")) {

                insertStmt.setString(1, newRecette.rnom);
				insertStmt.setString(2, newRecette.instructions);
                insertStmt.setString(3, newRecette.type_recette);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRecette);
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

            Recette updateRecette = ctx.bodyValidator(Recette.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
					.check(obj -> obj.instructions != null, "Missing recette instructions")
                    .check(obj -> obj.type_recette != null, "Missing type_recette")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recette SET rnom = ?, instructions = ?, type_recette = ? WHERE rnom = ?");
            stmt.setString(1, updateRecette.rnom);
            stmt.setString(2, updateRecette.instructions);
            stmt.setString(3, updateRecette.type_recette);
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
            String rnom = ctx.pathParam("rnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM recette WHERE rnom = ?");
            stmt.setString(1, rnom);

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
