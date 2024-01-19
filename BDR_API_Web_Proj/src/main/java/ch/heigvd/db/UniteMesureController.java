package ch.heigvd.db;

import ch.heigvd.auth.AuthController;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UniteMesureController {

    private Connection conn;
    private AuthController authController;

    public UniteMesureController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }

    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String udmnom = null;

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
                if (requestBody.has("udmnom")) {
                    udmnom = requestBody.get("udmnom").getAsString();
                }
            }

            List<UniteMesure> uniteMesureList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM unitedemesure ORDER BY udmnom ASC");

            List<String> conditions = new ArrayList<>();

            if (udmnom != null) {
                conditions.add("udmnom = ?");
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
            if (udmnom != null) {
                stmt.setString(index++, udmnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UniteMesure uniteMesure = new UniteMesure();
                uniteMesure.udmnom = rs.getString("udmnom");;
                uniteMesureList.add(uniteMesure);


            }

            ctx.json(uniteMesureList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            UniteMesure newUniteMesure = ctx.bodyValidator(UniteMesure.class)
                    .check(obj -> obj.udmnom != null, "Missing unite mesure name")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO unitedemesure (udmnom) VALUES (?)")) {
                insertStmt.setString(1, newUniteMesure.udmnom);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newUniteMesure);
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
            String udmnom = ctx.pathParam("udmnom");
            UniteMesure updateUniteMesure = ctx.bodyValidator(UniteMesure.class)
                    .check(obj -> obj.udmnom != null, "Missing unite mesure name")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE unitedemesure SET udmnom = ? WHERE udmnom = ?");
            stmt.setString(1, updateUniteMesure.udmnom);
            stmt.setString(2, udmnom);

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
            String udmnom = ctx.pathParam("udmnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM unitedemesure WHERE udmnom = ?");
            stmt.setString(1, udmnom);

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
