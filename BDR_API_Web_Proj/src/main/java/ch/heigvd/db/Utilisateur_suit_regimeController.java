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


public class Utilisateur_suit_regimeController {

    private Connection conn;
    private AuthController authController;
    public Utilisateur_suit_regimeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String email = ctx.cookie("user");
            String regnom = null;

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
                if (requestBody.has("regnom")) {
                    regnom = requestBody.get("regnom").getAsString();
                }
            }

            List<Utilisateur_suit_regime> utilisateur_suit_regimeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM utilisateur_suit_regime"); // assuming the table name is 'utilisateur_suit_regime'

            List<String> conditions = new ArrayList<>();

            if (email != null) {
                conditions.add("email = ?");
            }
            if (regnom != null) {
                conditions.add("regnom = ?");
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
            if (regnom != null) {
                stmt.setString(index, regnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Utilisateur_suit_regime utilisateur_suit_regime = new Utilisateur_suit_regime();
                utilisateur_suit_regime.email = rs.getString("email");
                utilisateur_suit_regime.regnom = rs.getString("regnom");
                utilisateur_suit_regimeList.add(utilisateur_suit_regime);
            }

            ctx.json(utilisateur_suit_regimeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Utilisateur_suit_regime newUtilisateur_suit_regime = ctx.bodyValidator(Utilisateur_suit_regime.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.regnom != null, "Missing regnom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO utilisateur_suit_regime (email, regnom) VALUES (?, ?)")) {

                insertStmt.setString(1, newUtilisateur_suit_regime.email);
                insertStmt.setString(2, newUtilisateur_suit_regime.regnom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUtilisateur_suit_regime);
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
			String regnom = ctx.pathParam("regnom");

            Utilisateur_suit_regime updateUtilisateur_suit_regime = ctx.bodyValidator(Utilisateur_suit_regime.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.regnom != null, "Missing regnom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE utilisateur_suit_regime SET email = ?, regnom = ? WHERE email = ? AND regnom = ?");
            stmt.setString(1, updateUtilisateur_suit_regime.email);
            stmt.setString(2, updateUtilisateur_suit_regime.regnom);
            stmt.setString(3, email);
			stmt.setString(4, regnom);
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
			String regnom = ctx.pathParam("regnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur_suit_regime WHERE email = ? AND regnom = ?");
            stmt.setString(1, email);
			stmt.setString(2, regnom);

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
