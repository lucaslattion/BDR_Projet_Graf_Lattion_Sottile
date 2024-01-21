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


public class AlimentController {

    private Connection conn;
    private AuthController authController;
    public AlimentController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String anom = null;
            String groupe = null;

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
                if (requestBody.has("groupe")) {
                    groupe = requestBody.get("groupe").getAsString();
                }
            }

            List<Aliment> alimentList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM aliment ORDER BY anom ASC"); // assuming the table name is 'aliment'

            List<String> conditions = new ArrayList<>();

            if (anom != null) {
                conditions.add("anom = ?");
            }
            if (groupe != null) {
                conditions.add("groupe = ?");
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
            if (groupe != null) {
                stmt.setString(index, groupe);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Aliment aliment = new Aliment();
                aliment.anom = rs.getString("anom");
                aliment.kcal = rs.getInt("kcal");
                aliment.proteines = rs.getDouble("proteines");
                aliment.glucides = rs.getDouble("glucides");
                aliment.lipides = rs.getDouble("lipides");
                aliment.fibres = rs.getDouble("fibres");
                aliment.sodium = rs.getDouble("sodium");
                aliment.groupe = rs.getString("groupe");
                alimentList.add(aliment);
            }

            ctx.json(alimentList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void getAvailable(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String email = ctx.cookie("user");
            String anom = null;
            String groupe = null;

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
                if (requestBody.has("groupe")) {
                    groupe = requestBody.get("groupe").getAsString();
                }
            }

            List<Aliment> alimentList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM aliment WHERE NOT EXISTS" +
                    " ( SELECT 1 FROM utilisateur_cache_aliment WHERE" +
                    " utilisateur_cache_aliment.anom = aliment.anom " +
                    "AND utilisateur_cache_aliment.email = 'calvin.graf@heig-vd.ch'\n" +
                    ")");

            queryBuilder.append(" ORDER BY anom ASC");


            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());

            int index = 1;
            if (anom != null) {
                stmt.setString(index++, anom);
            }
            if (groupe != null) {
                stmt.setString(index, groupe);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Aliment aliment = new Aliment();
                aliment.anom = rs.getString("anom");
                aliment.kcal = rs.getInt("kcal");
                aliment.proteines = rs.getDouble("proteines");
                aliment.glucides = rs.getDouble("glucides");
                aliment.lipides = rs.getDouble("lipides");
                aliment.fibres = rs.getDouble("fibres");
                aliment.sodium = rs.getDouble("sodium");
                aliment.groupe = rs.getString("groupe");
                alimentList.add(aliment);
            }

            ctx.json(alimentList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Aliment newAliment = ctx.bodyValidator(Aliment.class)
                    .check(obj -> obj.anom != null, "Missing aliment name")
                    .check(obj -> obj.kcal >= 0, "Invalid calorie count")
                    .check(obj -> obj.proteines >= 0, "Invalid protein count")
                    .check(obj -> obj.glucides >= 0, "Invalid carbohydrate count")
                    .check(obj -> obj.lipides >= 0, "Invalid fat count")
                    .check(obj -> obj.fibres >= 0, "Invalid fiber count")
                    .check(obj -> obj.sodium >= 0, "Invalid sodium count")
                    .check(obj -> obj.groupe != null, "Missing groupe")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO aliment (anom, kcal, proteines, glucides, lipides, fibres, sodium, groupe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                insertStmt.setString(1, newAliment.anom);
                insertStmt.setInt(2, newAliment.kcal);
                insertStmt.setDouble(3, newAliment.proteines);
                insertStmt.setDouble(4, newAliment.glucides);
                insertStmt.setDouble(5, newAliment.lipides);
                insertStmt.setDouble(6, newAliment.fibres);
                insertStmt.setDouble(7, newAliment.sodium);
                insertStmt.setString(8, newAliment.groupe);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newAliment);
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

            Aliment updateAliment = ctx.bodyValidator(Aliment.class)
                    .check(obj -> obj.anom != null, "Missing aliment name")
                    .check(obj -> obj.kcal >= 0, "Invalid calorie count")
                    .check(obj -> obj.proteines >= 0, "Invalid protein count")
                    .check(obj -> obj.glucides >= 0, "Invalid carbohydrate count")
                    .check(obj -> obj.lipides >= 0, "Invalid fat count")
                    .check(obj -> obj.fibres >= 0, "Invalid fiber count")
                    .check(obj -> obj.sodium >= 0, "Invalid sodium count")
                    .check(obj -> obj.groupe != null, "Missing groupe")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE aliment SET anom = ?, kcal = ?, proteines = ?, glucides = ?, lipides = ?, fibres = ?, sodium = ?, groupe = ? WHERE anom = ?");
            stmt.setString(1, updateAliment.anom);
            stmt.setInt(2, updateAliment.kcal);
            stmt.setDouble(3, updateAliment.proteines);
            stmt.setDouble(4, updateAliment.glucides);
            stmt.setDouble(5, updateAliment.lipides);
            stmt.setDouble(6, updateAliment.fibres);
            stmt.setDouble(7, updateAliment.sodium);
            stmt.setString(8, updateAliment.groupe);
            stmt.setString(9, anom);

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

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM aliment WHERE anom = ?");
            stmt.setString(1, anom);

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
