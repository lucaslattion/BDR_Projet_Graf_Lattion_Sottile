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


public class Utilisateur_possede_alimentController {

    private Connection conn;
    private AuthController authController;
    public Utilisateur_possede_alimentController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String email = ctx.cookie("user");
            String anom = null;
            String quantite = null;
            String unite_mesure = null;

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
                if (requestBody.has("anom")) {
                    anom = requestBody.get("anom").getAsString();
                }
                if (requestBody.has("quantite")) {
                    email = requestBody.get("quantite").getAsString();
                }
                if (requestBody.has("unite_mesure")) {
                    anom = requestBody.get("unite_mesure").getAsString();
                }
            }

            List<Utilisateur_possede_aliment> utilisateur_possede_alimentList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM utilisateur_possede_aliment"); // assuming the table name is 'utilisateur_possede_aliment'

            List<String> conditions = new ArrayList<>();

            if (email != null) {
                conditions.add("email = ?");
            }
            if (anom != null) {
                conditions.add("anom = ?");
            }
            if (quantite != null) {
                conditions.add("quantite = ?");
            }
            if (unite_mesure != null) {
                conditions.add("unite_mesure = ?");
            }

            if (!conditions.isEmpty()) {
                queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            queryBuilder.append(" ORDER BY anom ASC");

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
            if (anom != null) {
                stmt.setString(index, anom);
            }
            if (quantite != null) {
                stmt.setString(index++, quantite);
            }
            if (unite_mesure != null) {
                stmt.setString(index, unite_mesure);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Utilisateur_possede_aliment utilisateur_possede_aliment = new Utilisateur_possede_aliment();
                utilisateur_possede_aliment.email = rs.getString("email");
                utilisateur_possede_aliment.anom = rs.getString("anom");
                utilisateur_possede_aliment.quantite = rs.getInt("quantite");
                utilisateur_possede_aliment.unite_mesure = rs.getString("unite_mesure");
                utilisateur_possede_alimentList.add(utilisateur_possede_aliment);
            }

            ctx.json(utilisateur_possede_alimentList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Utilisateur_possede_aliment newUtilisateur_possede_aliment = ctx.bodyValidator(Utilisateur_possede_aliment.class)
                    .check(obj -> obj.anom != null, "Missing anom")
                    .check(obj -> obj.quantite != 0, "Missing quantite")
                    .check(obj -> obj.unite_mesure != null, "Missing unite of measure")
                    .get();

            newUtilisateur_possede_aliment.email = ctx.cookie("user");

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO utilisateur_possede_aliment (email, anom, quantite, unite_mesure) VALUES (?, ?, ?, ?)")) {

                insertStmt.setString(1, newUtilisateur_possede_aliment.email);
                insertStmt.setString(2, newUtilisateur_possede_aliment.anom);
                insertStmt.setInt(3, newUtilisateur_possede_aliment.quantite);
                insertStmt.setString(4, newUtilisateur_possede_aliment.unite_mesure);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUtilisateur_possede_aliment);
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
			String anom = ctx.pathParam("anom");

            Utilisateur_possede_aliment updateUtilisateur_possede_aliment = ctx.bodyValidator(Utilisateur_possede_aliment.class)
                    .check(obj -> obj.email != null, "Missing email")
                    .check(obj -> obj.anom != null, "Missing anom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE utilisateur_possede_aliment SET email = ?, anom = ? WHERE email = ? AND anom = ?");
            stmt.setString(1, updateUtilisateur_possede_aliment.email);
            stmt.setString(2, updateUtilisateur_possede_aliment.anom);
            stmt.setString(3, email);
			stmt.setString(4, anom);
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
            String email = ctx.cookie("user");
			String anom = ctx.pathParam("anom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur_possede_aliment WHERE email = ? AND anom = ?");
            stmt.setString(1, email);
			stmt.setString(2, anom);

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
