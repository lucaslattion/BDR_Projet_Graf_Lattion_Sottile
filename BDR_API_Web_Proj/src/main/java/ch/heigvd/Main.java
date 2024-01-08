package ch.heigvd;

import ch.heigvd.auth.AuthController;
import ch.heigvd.users.UsersController;
import io.javalin.Javalin;

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

        // Controllers
        AuthController authController = new AuthController(jdbcUrl, dbUsername, dbPassword); // Assuming changes for database support
        UsersController usersController = new UsersController(jdbcUrl, dbUsername, dbPassword);

        // Using try-with-resources to ensure proper closure of the app
        try (Javalin app = Javalin.create()) {
            // Auth routes
            app.post("/login", authController::login);
            app.post("/logout", authController::logout);
            app.get("/profile", authController::profile);

            // Users routes
            app.post("/users", usersController::create);
            app.get("/users", usersController::getMany);
            app.get("/users/{email}", usersController::getOne);
            app.put("/users/{email}", usersController::update);
            app.delete("/users/{email}", usersController::delete);

            app.start(PORT);

            // Keep the server running
            app.events(event -> {
                event.serverStopping(() -> System.out.println("Server is stopping"));
                event.serverStopped(() -> System.out.println("Server has stopped"));
            });

            // Hold the main thread to prevent the application from exiting
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Server interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
