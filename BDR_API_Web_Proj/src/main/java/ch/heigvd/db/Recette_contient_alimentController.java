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


public class Recette_contient_alimentController {

    private Connection conn;
    private AuthController authController;
    public Recette_contient_alimentController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String rnom = null;
            String anom = null;

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
                if (requestBody.has("anom")) {
                    anom = requestBody.get("anom").getAsString();
                }
            }

            List<Recette_contient_aliment> recette_contient_alimentList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM recette_contient_aliment"); // assuming the table name is 'aliment'

            List<String> conditions = new ArrayList<>();

            if (rnom != null) {
                conditions.add("rnom = ?");
            }
            if (anom != null) {
                conditions.add("anom = ?");
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
            if (anom != null) {
                stmt.setString(index, anom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recette_contient_aliment recette_contient_aliment = new Recette_contient_aliment();
                recette_contient_aliment.rnom = rs.getString("rnom");
				recette_contient_aliment.anom = rs.getString("anom");
                recette_contient_aliment.quantite = rs.getInt("quantite");
				recette_contient_aliment.unite_mesure = rs.getString("unite_mesure");
                recette_contient_alimentList.add(recette_contient_aliment);
            }

            ctx.json(recette_contient_alimentList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Recette_contient_aliment newRecette_contient_aliment = ctx.bodyValidator(Recette_contient_aliment.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
                    .check(obj -> obj.anom != null, "Missing aliment name")
					.check(obj -> obj.quantite >= 0, "Invalid quantite count")
					.check(obj -> obj.unite_mesure != null, "Missing unite_mesure")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO recette_contient_aliment (rnom, anom, quantite, unite_mesure) VALUES (?, ?, ?, ?)")) {

                insertStmt.setString(1, newRecette_contient_aliment.rnom);
				insertStmt.setString(2, newRecette_contient_aliment.anom);
                insertStmt.setDouble(3, newRecette_contient_aliment.quantite);
                insertStmt.setString(4, newRecette_contient_aliment.unite_mesure);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRecette_contient_aliment);
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

            Recette_contient_aliment updateRecette_contient_aliment = ctx.bodyValidator(Recette_contient_aliment.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
                    .check(obj -> obj.anom != null, "Missing aliment name")
					.check(obj -> obj.quantite >= 0, "Invalid quantite count")
					.check(obj -> obj.unite_mesure != null, "Missing unite_mesure")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recette_contient_aliment SET rnom = ?, anom = ?, quantite = ?, unite_mesure = ? WHERE rnom = ?");
            stmt.setString(1, updateRecette_contient_aliment.rnom);
			stmt.setString(8, updateRecette_contient_aliment.anom);
            stmt.setDouble(3, updateRecette_contient_aliment.quantite);
            stmt.setString(4, updateRecette_contient_aliment.unite_mesure);
			stmt.setString(9, rnom);

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

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM recette_contient_aliment WHERE rnom = ?");
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
