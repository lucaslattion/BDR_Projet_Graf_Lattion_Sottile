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


public class RegimeController {

    private Connection conn;
    private AuthController authController;
    public RegimeController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }


    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
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
                if (requestBody.has("regnom")) {
                    regnom = requestBody.get("regnom").getAsString();
                }
            }

            List<Regime> regimeList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM regime");

            List<String> conditions = new ArrayList<>();

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
            if (regnom != null) {
                stmt.setString(index++, regnom);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Regime regime = new Regime();
                regime.regnom = rs.getString("regnom");;
                regimeList.add(regime);


            }

            ctx.json(regimeList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Regime newRegime = ctx.bodyValidator(Regime.class)
                    .check(obj -> obj.regnom != null, "Missing regime name")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO regime (regnom) VALUES (?)")) {
                insertStmt.setString(1, newRegime.regnom);
                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRegime);
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
            String regnom = ctx.pathParam("regnom");
            Regime updateRegime = ctx.bodyValidator(Regime.class)
                    .check(obj -> obj.regnom != null, "Missing regime name")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE regime SET regnom = ? WHERE regnom = ?");
            stmt.setString(1, updateRegime.regnom);
            stmt.setString(2, regnom);

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
            String regnom = ctx.pathParam("regnom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM regime WHERE regnom = ?");
            stmt.setString(1, regnom);

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
