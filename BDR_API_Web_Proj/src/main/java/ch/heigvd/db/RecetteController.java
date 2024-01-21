package ch.heigvd.db;

import io.javalin.http.*;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import ch.heigvd.auth.AuthController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class RecetteController {

    private Connection conn;
    private AuthController authController;
    public RecetteController(Connection connection, AuthController authController) {
        conn = connection;
        this.authController = authController;
    }
    public void getMany(Context ctx) throws SQLException {

        if(authController.validLoggedUser(ctx)){

            int limit = 0;   // Default 0 means all elements
            int offset = 0;  // Default 0 means no skipped elements
            String rnom = null;
            String type_recette = null;

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
                if (requestBody.has("type_recette")) {
                    type_recette = requestBody.get("type_recette").getAsString();
                }
            }

            List<Recette> recetteList = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM recette ORDER BY rnom ASC"); // assuming the table name is 'recette'

            List<String> conditions = new ArrayList<>();

            if (rnom != null) {
                conditions.add("rnom = ?");
            }
            if (type_recette != null) {
                conditions.add("type_recette = ?");
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
            if (type_recette != null) {
                stmt.setString(index, type_recette);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recette recette = new Recette();
                recette.rnom = rs.getString("rnom");
				recette.instructions = rs.getString("instructions");
                recette.type_recette = rs.getString("type_recette");
                recetteList.add(recette);
            }

            ctx.json(recetteList);
            return;

        }
        throw new UnauthorizedResponse();
    }

    public void getOne(Context ctx) throws SQLException {
        String nomRecette = ctx.pathParam("rnom");

        if (authController.validLoggedUser(ctx)) {
            // Vous devez remplacer 'conn' par votre connexion à la base de données

                String query = "SELECT * FROM recette WHERE rnom = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, nomRecette);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        Recette recette = new Recette();
                        recette.rnom = rs.getString("rnom");
                        recette.instructions = rs.getString("instructions");
                        recette.type_recette = rs.getString("type_recette");

                        ctx.json(recette);
                        return;
                    }
                }

                ctx.status(404).json(new Error("Recette non trouvée"));
            }
    }

    public void create(Context ctx) throws SQLException {
        if(authController.validLoggedUser(ctx)){

            Recette newRecette = ctx.bodyValidator(Recette.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
					.check(obj -> obj.instructions != null, "Missing recette instructions")
                    .check(obj -> obj.type_recette != null, "Missing type_recette")
                    .get();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO recette (rnom, instructions, type_recette) VALUES (?, ?, ?)")) {

                insertStmt.setString(1, newRecette.rnom);
				insertStmt.setString(2, newRecette.instructions);
                insertStmt.setString(3, newRecette.type_recette);

                insertStmt.executeUpdate();
                ctx.status(HttpStatus.CREATED);
                ctx.json(newRecette);
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

            Recette updateRecette = ctx.bodyValidator(Recette.class)
                    .check(obj -> obj.rnom != null, "Missing recette name")
					.check(obj -> obj.instructions != null, "Missing recette instructions")
                    .check(obj -> obj.type_recette != null, "Missing type_recette")
                    .get();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE recette SET rnom = ?, instructions = ?, type_recette = ? WHERE rnom = ?");
            stmt.setString(1, updateRecette.rnom);
            stmt.setString(2, updateRecette.instructions);
            stmt.setString(3, updateRecette.type_recette);
            stmt.setString(4, rnom);

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

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM recette WHERE rnom = ?");
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

    public class InformationsResponse {
        public String recette;
        public double calories;
        public double proteines;
        public double glucides;
        public double lipides;
        public double fibres;
        public double sodium;
        public double vitamineA;
        public double vitamineB1;
        public double vitamineB2;
        public double vitamineB3;
        public double vitamineB6;
        public double vitamineB12;
        public double vitamineC;
        public double vitamineD;
        public double vitamineE;

        public InformationsResponse(String recette, double calories, double proteines, double glucides, double lipides,
                                    double fibres, double sodium, double vitamineA, double vitamineB1, double vitamineB2,
                                    double vitamineB3, double vitamineB6, double vitamineB12, double vitamineC,
                                    double vitamineD, double vitamineE) {
            this.recette = recette;
            this.calories = calories;
            this.proteines = proteines;
            this.glucides = glucides;
            this.lipides = lipides;
            this.fibres = fibres;
            this.sodium = sodium;
            this.vitamineA = vitamineA;
            this.vitamineB1 = vitamineB1;
            this.vitamineB2 = vitamineB2;
            this.vitamineB3 = vitamineB3;
            this.vitamineB6 = vitamineB6;
            this.vitamineB12 = vitamineB12;
            this.vitamineC = vitamineC;
            this.vitamineD = vitamineD;
            this.vitamineE = vitamineE;
        }
    }

    public void getInformations(Context ctx) throws SQLException {
        String nomRecette = ctx.pathParam("rnom");

        if (authController.validLoggedUser(ctx)) {
            // Vous devez remplacer 'conn' par votre connexion à la base de données

            String query = "SELECT " +
                    "r.rnom AS Recette, " +
                    "SUM(a.kcal) AS Calories, " +
                    "SUM(a.proteines) AS Proteines, " +
                    "SUM(a.glucides) AS Glucides, " +
                    "SUM(a.lipides) AS Lipides, " +
                    "SUM(a.fibres) AS Fibres, " +
                    "SUM(a.sodium) AS Sodium, " +
                    "SUM(aav.quantite) AS Vitamine_A, " +
                    "SUM(ab1v.quantite) AS Vitamine_B1, " +
                    "SUM(ab2v.quantite) AS Vitamine_B2, " +
                    "SUM(ab3v.quantite) AS Vitamine_B3, " +
                    "SUM(ab6v.quantite) AS Vitamine_B6, " +
                    "SUM(ab12v.quantite) AS Vitamine_B12, " +
                    "SUM(acv.quantite) AS Vitamine_C, " +
                    "SUM(adv.quantite) AS Vitamine_D, " +
                    "SUM(aev.quantite) AS Vitamine_E " +
                    "FROM Recette r " +
                    "INNER JOIN Recette_contient_Aliment rca ON r.rnom = rca.rnom " +
                    "INNER JOIN Aliment a ON rca.anom = a.anom " +
                    "LEFT JOIN Aliment_contient_Vitamine aav ON a.anom = aav.anom AND aav.vinom = 'Vitamine A' " +
                    "LEFT JOIN Aliment_contient_Vitamine ab1v ON a.anom = ab1v.anom AND ab1v.vinom = 'Vitamine B1' " +
                    "LEFT JOIN Aliment_contient_Vitamine ab2v ON a.anom = ab2v.anom AND ab2v.vinom = 'Vitamine B2' " +
                    "LEFT JOIN Aliment_contient_Vitamine ab3v ON a.anom = ab3v.anom AND ab3v.vinom = 'Vitamine B3' " +
                    "LEFT JOIN Aliment_contient_Vitamine ab6v ON a.anom = ab6v.anom AND ab6v.vinom = 'Vitamine B6' " +
                    "LEFT JOIN Aliment_contient_Vitamine ab12v ON a.anom = ab12v.anom AND ab12v.vinom = 'Vitamine B12' " +
                    "LEFT JOIN Aliment_contient_Vitamine acv ON a.anom = acv.anom AND acv.vinom = 'Vitamine C' " +
                    "LEFT JOIN Aliment_contient_Vitamine adv ON a.anom = adv.anom AND adv.vinom = 'Vitamine D' " +
                    "LEFT JOIN Aliment_contient_Vitamine aev ON a.anom = aev.anom AND aev.vinom = 'Vitamine E' " +
                    "WHERE r.rnom = ? " +
                    "GROUP BY r.rnom";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomRecette);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    InformationsResponse informations = new InformationsResponse(
                            rs.getString("Recette"),
                            rs.getDouble("Calories"),
                            rs.getDouble("Proteines"),
                            rs.getDouble("Glucides"),
                            rs.getDouble("Lipides"),
                            rs.getDouble("Fibres"),
                            rs.getDouble("Sodium"),
                            rs.getDouble("Vitamine_A"),
                            rs.getDouble("Vitamine_B1"),
                            rs.getDouble("Vitamine_B2"),
                            rs.getDouble("Vitamine_B3"),
                            rs.getDouble("Vitamine_B6"),
                            rs.getDouble("Vitamine_B12"),
                            rs.getDouble("Vitamine_C"),
                            rs.getDouble("Vitamine_D"),
                            rs.getDouble("Vitamine_E")
                    );

                    // Vous pouvez ajuster la réponse JSON en fonction de votre besoin
                    ctx.json(informations);
                    return;
                }
            }

            ctx.status(404).json(new Error("Recette non trouvée"));
        }
    }
}
