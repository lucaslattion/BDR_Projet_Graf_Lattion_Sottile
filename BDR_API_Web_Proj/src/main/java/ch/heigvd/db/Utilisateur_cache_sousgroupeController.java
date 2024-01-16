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


public class Utilisateur_cache_sousgroupeController {

    private Connection conn;
    private AuthController authController;
    public Utilisateur_cache_sousgroupeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String email = null;
            String sgnom = null;

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
                if (requestBody.has("sgnom")) {
                    sgnom = requestBody.get("sgnom").getAsString();
                }
            }

            List<Utilisateur_cache_sousgroupe> utilisateur_cache_sousgroupeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM utilisateur_cache_sousgroupe"); // assuming the table name is 'utilisateur_cache_sousgroupe'

            List<String> conditions = new ArrayList<>();

            if (email != null) {
                conditions.add("email = ?");
            }
            if (sgnom != null) {
                conditions.add("sgnom = ?");
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
            if (sgnom != null) {
                stmt.setString(index, sgnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Utilisateur_cache_sousgroupe utilisateur_cache_sousgroupe = new Utilisateur_cache_sousgroupe();
                utilisateur_cache_sousgroupe.email = rs.getString("email");
                utilisateur_cache_sousgroupe.sgnom = rs.getString("sgnom");
                utilisateur_cache_sousgroupeList.add(utilisateur_cache_sousgroupe);
            }

            ctx.json(utilisateur_cache_sousgroupeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Utilisateur_cache_sousgroupe newUtilisateur_cache_sousgroupe = ctx.bodyValidator(Utilisateur_cache_sousgroupe.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.sgnom != null, "Missing sgnom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO utilisateur_cache_sousgroupe (email, sgnom) VALUES (?, ?)")) {

                insertStmt.setString(1, newUtilisateur_cache_sousgroupe.email);
                insertStmt.setString(2, newUtilisateur_cache_sousgroupe.sgnom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUtilisateur_cache_sousgroupe);
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
			String sgnom = ctx.pathParam("sgnom");

            Utilisateur_cache_sousgroupe updateUtilisateur_cache_sousgroupe = ctx.bodyValidator(Utilisateur_cache_sousgroupe.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.sgnom != null, "Missing sgnom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE utilisateur_cache_sousgroupe SET email = ?, sgnom = ? WHERE email = ? AND sgnom = ?");
            stmt.setString(1, updateUtilisateur_cache_sousgroupe.email);
            stmt.setString(2, updateUtilisateur_cache_sousgroupe.sgnom);
            stmt.setString(3, email);
			stmt.setString(4, sgnom);
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
			String sgnom = ctx.pathParam("sgnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur_cache_sousgroupe WHERE email = ? AND sgnom = ?");
            stmt.setString(1, email);
			stmt.setString(2, sgnom);

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
