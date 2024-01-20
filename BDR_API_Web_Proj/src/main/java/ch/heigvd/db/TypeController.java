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


public class TypeController {

    private Connection conn;
    private AuthController authController;
    public TypeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }


    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String tnom = null;

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
                if (requestBody.has("tnom")) {
                    tnom = requestBody.get("tnom").getAsString();
                }
            }

            List<Type> typeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM type ORDER BY tnom ASC");

            List<String> conditions = new ArrayList<>();

            if (tnom != null) {
                conditions.add("tnom = ?");
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
            if (tnom != null) {
                stmt.setString(index++, tnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Type type = new Type();
                type.tnom = rs.getString("tnom");;
                typeList.add(type);


            }

            ctx.json(typeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Type newType = ctx.bodyValidator(Type.class)
                    .check(obj -> obj.tnom != null, "Missing type name")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO type (tnom) VALUES (?)")) {
                insertStmt.setString(1, newType.tnom);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newType);
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
            String tnom = ctx.pathParam("tnom");
            Type updateType = ctx.bodyValidator(Type.class)
                    .check(obj -> obj.tnom != null, "Missing type name")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE type SET tnom = ? WHERE tnom = ?");
            stmt.setString(1, updateType.tnom);
            stmt.setString(2, tnom);

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
            String tnom = ctx.pathParam("tnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM type WHERE tnom = ?");
            stmt.setString(1, tnom);

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
