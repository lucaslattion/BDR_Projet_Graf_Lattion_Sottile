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


public class ListeController {

    private Connection conn;
    private AuthController authController;
    public ListeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String lnom = null;
            String email = ctx.cookie("user");

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

            List<Liste> listeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM liste "); // assuming the table name is 'liste'

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
                Liste liste = new Liste();
                liste.lnom = rs.getString("lnom");
                liste.email = rs.getString("email");
                listeList.add(liste);
            }

            ctx.json(listeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Liste newListe = ctx.bodyValidator(Liste.class)
                    .check(obj -> obj.lnom != null, "Missing lnom")
                    .check(obj -> obj.email != null, "Missing email")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO liste (lnom, email) VALUES (?, ?)")) {

                insertStmt.setString(1, newListe.lnom);
                insertStmt.setString(2, newListe.email);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newListe);
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

            String lnom = ctx.pathParam("lnom");
			String email = ctx.pathParam("email");

            Liste updateListe = ctx.bodyValidator(Liste.class)
                    .check(obj -> obj.lnom != null, "Missing lnom")
                    .check(obj -> obj.email != null, "Missing email")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE liste SET lnom = ?, email = ? WHERE lnom = ? AND email = ?");
            stmt.setString(1, updateListe.lnom);
            stmt.setString(2, updateListe.email);
            stmt.setString(3, lnom);
			stmt.setString(4, email);
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
			String email = ctx.pathParam("email");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM liste WHERE lnom = ? AND email = ?");
            stmt.setString(1, lnom);
			stmt.setString(2, email);

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
