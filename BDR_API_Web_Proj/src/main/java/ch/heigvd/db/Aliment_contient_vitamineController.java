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


public class Aliment_contient_vitamineController {

    private Connection conn;
    private AuthController authController;
    public Aliment_contient_vitamineController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String anom = null;
            String vinom = null;

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
                if (requestBody.has("anom")) {
                    anom = requestBody.get("anom").getAsString();
                }
                if (requestBody.has("vinom")) {
                    vinom = requestBody.get("vinom").getAsString();
                }
            }

            List<Aliment_contient_vitamine> aliment_contient_vitamineList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM aliment_contient_vitamine"); // assuming the table name is 'aliment_contient_vitamine'

            List<String> conditions = new ArrayList<>();

            if (anom != null) {
                conditions.add("anom = ?");
            }
            if (vinom != null) {
                conditions.add("vinom = ?");
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
            if (anom != null) {
                stmt.setString(index++, anom);
            }
            if (vinom != null) {
                stmt.setString(index, vinom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Aliment_contient_vitamine aliment_contient_vitamine = new Aliment_contient_vitamine();
                aliment_contient_vitamine.anom = rs.getString("anom");
                aliment_contient_vitamine.vinom = rs.getString("vinom");
                aliment_contient_vitamineList.add(aliment_contient_vitamine);
            }

            ctx.json(aliment_contient_vitamineList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Aliment_contient_vitamine newAliment_contient_vitamine = ctx.bodyValidator(Aliment_contient_vitamine.class)
                    .check(obj -> obj.anom != null, "Missing anom")
                    .check(obj -> obj.vinom != null, "Missing vinom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO aliment_contient_vitamine (anom, vinom) VALUES (?, ?)")) {

                insertStmt.setString(1, newAliment_contient_vitamine.anom);
                insertStmt.setString(2, newAliment_contient_vitamine.vinom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newAliment_contient_vitamine);
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

            String anom = ctx.pathParam("anom");
			String vinom = ctx.pathParam("vinom");

            Aliment_contient_vitamine updateAliment_contient_vitamine = ctx.bodyValidator(Aliment_contient_vitamine.class)
                    .check(obj -> obj.anom != null, "Missing anom")
                    .check(obj -> obj.vinom != null, "Missing vinom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE aliment_contient_vitamine SET anom = ?, vinom = ? WHERE anom = ? AND vinom = ?");
            stmt.setString(1, updateAliment_contient_vitamine.anom);
            stmt.setString(2, updateAliment_contient_vitamine.vinom);
            stmt.setString(3, anom);
			stmt.setString(4, vinom);
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
            String anom = ctx.pathParam("anom");
			String vinom = ctx.pathParam("vinom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM aliment_contient_vitamine WHERE anom = ? AND vinom = ?");
            stmt.setString(1, anom);
			stmt.setString(2, vinom);

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
