package ch.heigvd;

import ch.heigvd.auth.AuthController;
import ch.heigvd.auth.DbConnection;
import ch.heigvd.user.UsersController;
import ch.heigvd.aliment.alimentController;
import io.javalin.Javalin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public final static int PORT = 8080;

    public static void main(String[] args) {
        // Database connection details
        //String host = "bdr-projet-postgresql"; //Host to use in Docker
        String host = "localhost"; //Developpement Host
        String port = "5432";
        String dbname = "bdr";
        String schema = "bdrproject"; // Nom du schéma
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?currentSchema=" + schema;
        String dbUsername = "bdr";
        String dbPassword = "bdr";





        // Using try-with-resources to ensure proper closure of the app
        try (Javalin app = Javalin.create()) {

            // Database connection
            try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                // Code pour interagir avec la base de données

                // Controllers
                AuthController authController = new AuthController(conn);
                UsersController usersController = new UsersController(conn);

                // Auth routes
                app.post("/login", authController::login);
                app.post("/logout", authController::logout);
                app.get("/profile", authController::profile);

                // User routes
                app.post("/user", usersController::create);
                app.get("/user", usersController::getMany);
                app.get("/user/{email}", usersController::getOne);
                app.put("/user/{email}", usersController::update);
                app.delete("/user/{email}", usersController::delete);

                // Aliment routes
                app.get("/aliment", alimentController::getMany);
                app.post("/aliment", alimentController::create);
                app.put("/aliment", alimentController::update);
                app.delete("/aliment", alimentController::delete);

                app.start(PORT);

                // Keep the server running
                app.events(event -> {
                    event.serverStopping(() -> System.out.println("Server is stopping"));
                    event.serverStopped(() -> System.out.println("Server has stopped"));
                });

                // Hold the main thread to prevent the application from exiting
                Thread.currentThread().join();
            } catch (SQLException e) {
                // Gestion des exceptions SQL
                System.out.println("Server interrupted: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Server interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
