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


public class Recette_utilise_ustensilController {

    private Connection conn;
    private AuthController authController;
    public Recette_utilise_ustensilController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Recette_utilise_ustensil newRecette_utilise_ustensil = ctx.bodyValidator(Recette_utilise_ustensil.class)
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .check(obj -> obj.unom != null, "Missing unom")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO recette_utilise_ustensil (rnom, unom) VALUES (?, ?)")) {

                insertStmt.setString(1, newRecette_utilise_ustensil.rnom);
                insertStmt.setString(2, newRecette_utilise_ustensil.unom);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRecette_utilise_ustensil);
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
			String unom = ctx.pathParam("unom");

            Recette_utilise_ustensil updateRecette_utilise_ustensil = ctx.bodyValidator(Recette_utilise_ustensil.class)
                    .check(obj -> obj.rnom != null, "Missing rnom")
                    .check(obj -> obj.unom != null, "Missing unom")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recette_utilise_ustensil SET rnom = ?, unom = ? WHERE rnom = ? AND unom = ?");
            stmt.setString(1, updateRecette_utilise_ustensil.rnom);
            stmt.setString(2, updateRecette_utilise_ustensil.unom);
            stmt.setString(3, rnom);
			stmt.setString(4, unom);
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
			String unom = ctx.pathParam("unom");

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM recette_utilise_ustensil WHERE rnom = ? AND unom = ?");
            stmt.setString(1, rnom);
			stmt.setString(2, unom);

            int deletedRows = stmt.executeUpdate();
            if (deletedRows == 0) {
                throw new NotFoundResponse();
            }

            ctx.status(HttpStatus.NO_CONTENT);
            return;
        }
        throw new UnauthorizedResponse();
    }

    public void getMany(Context ctx) throws SQLException {
        String nomRecette = ctx.pathParam("rnom");

        if (authController.validLoggedUser(ctx)) {
            // Vous devez remplacer 'conn' par votre connexion à la base de données

            String query = "SELECT * FROM Recette_utilise_Ustensil WHERE rnom = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomRecette);

                ResultSet rs = stmt.executeQuery();

                List<Recette_utilise_ustensil> recetteList = new ArrayList<>();

                while (rs.next()) {
                    Recette_utilise_ustensil recette = new Recette_utilise_ustensil();
                    recette.rnom = rs.getString("rnom");
                    recette.unom = rs.getString("unom");
                    recetteList.add(recette);
                }

                if (!recetteList.isEmpty()) {
                    ctx.json(recetteList);
                    return;
                }
            }

            ctx.status(404).json(new Error("Aucune recette trouvée pour le nom spécifié"));
        }
    }
}
