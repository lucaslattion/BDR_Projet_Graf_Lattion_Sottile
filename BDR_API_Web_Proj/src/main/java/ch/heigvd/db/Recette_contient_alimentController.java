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
        String nomRecette = ctx.pathParam("rnom");

        if (authController.validLoggedUser(ctx)) {
            // Vous devez remplacer 'conn' par votre connexion à la base de données

            String query = "SELECT * FROM Recette_contient_aliment WHERE rnom = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomRecette);

                ResultSet rs = stmt.executeQuery();

                List<Recette_contient_aliment> alimentList = new ArrayList<>();

                while (rs.next()) {
                    Recette_contient_aliment recette = new Recette_contient_aliment();
                    recette.rnom = rs.getString("rnom");
                    recette.anom = rs.getString("anom");
                    recette.quantite = rs.getDouble("quantite");
                    recette.unite_mesure = rs.getString("unite_mesure");
                    alimentList.add(recette);
                }

                if (!alimentList.isEmpty()) {
                    ctx.json(alimentList);
                    return;
                }
            }
            ctx.status(404).json(new Error("Aucune recette trouvée pour le nom spécifié"));
        }
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
