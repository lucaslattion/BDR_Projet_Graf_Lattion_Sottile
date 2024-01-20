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


public class SousgroupeController {

    private Connection conn;
    private AuthController authController;
    public SousgroupeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String snom = null;
            String parentid = null;

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
                if (requestBody.has("snom")) {
                    snom = requestBody.get("snom").getAsString();
                }
                if (requestBody.has("parentid")) {
                    parentid = requestBody.get("parentid").getAsString();
                }
            }

            List<Sousgroupe> sousgroupeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM sousgroupe ORDER BY sgnom ASC"); // assuming the table name is 'sousgroupe'

            List<String> conditions = new ArrayList<>();

            if (snom != null) {
                conditions.add("snom = ?");
            }
            if (parentid != null) {
                conditions.add("parentid = ?");
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
            if (snom != null) {
                stmt.setString(index++, snom);
            }
            if (parentid != null) {
                stmt.setString(index, parentid);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sousgroupe sousgroupe = new Sousgroupe();
                sousgroupe.snom = rs.getString("snom");
                sousgroupe.parentid = rs.getString("parentid");
                sousgroupeList.add(sousgroupe);
            }

            ctx.json(sousgroupeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Sousgroupe newSousgroupe = ctx.bodyValidator(Sousgroupe.class)
                    .check(obj -> obj.snom != null, "Missing snom")
                    .check(obj -> obj.parentid != null, "Missing parentid")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO sousgroupe (snom, parentid) VALUES (?, ?)")) {

                insertStmt.setString(1, newSousgroupe.snom);
                insertStmt.setString(2, newSousgroupe.parentid);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newSousgroupe);
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

            String snom = ctx.pathParam("snom");
			String parentid = ctx.pathParam("parentid");

            Sousgroupe updateSousgroupe = ctx.bodyValidator(Sousgroupe.class)
                    .check(obj -> obj.snom != null, "Missing snom")
                    .check(obj -> obj.parentid != null, "Missing parentid")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE sousgroupe SET snom = ?, parentid = ? WHERE snom = ? AND parentid = ?");
            stmt.setString(1, updateSousgroupe.snom);
            stmt.setString(2, updateSousgroupe.parentid);
            stmt.setString(3, snom);
			stmt.setString(4, parentid);
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
            String snom = ctx.pathParam("snom");
			String parentid = ctx.pathParam("parentid");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM sousgroupe WHERE snom = ? AND parentid = ?");
            stmt.setString(1, snom);
			stmt.setString(2, parentid);

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
