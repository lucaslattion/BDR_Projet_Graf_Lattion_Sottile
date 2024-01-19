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


public class VitamineController {

    private Connection conn;
    private AuthController authController;
    public VitamineController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }


    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
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
                if (requestBody.has("vinom")) {
                    vinom = requestBody.get("vinom").getAsString();
                }
            }

            List<Vitamine> vitamineList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM vitamine ORDER BY vinom");

            List<String> conditions = new ArrayList<>();

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
            if (vinom != null) {
                stmt.setString(index++, vinom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vitamine vitamine = new Vitamine();
                vitamine.vinom = rs.getString("vinom");;
                vitamineList.add(vitamine);


            }

            ctx.json(vitamineList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Vitamine newVitamine = ctx.bodyValidator(Vitamine.class)
                    .check(obj -> obj.vinom != null, "Missing vitamine name")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO vitamine (vinom) VALUES (?)")) {
                insertStmt.setString(1, newVitamine.vinom);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newVitamine);
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
            String vinom = ctx.pathParam("vinom");
            Vitamine updateVitamine = ctx.bodyValidator(Vitamine.class)
                    .check(obj -> obj.vinom != null, "Missing vitamine name")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE vitamine SET vinom = ? WHERE vinom = ?");
            stmt.setString(1, updateVitamine.vinom);
            stmt.setString(2, vinom);

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
            String vinom = ctx.pathParam("vinom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM vitamine WHERE vinom = ?");
            stmt.setString(1, vinom);

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
