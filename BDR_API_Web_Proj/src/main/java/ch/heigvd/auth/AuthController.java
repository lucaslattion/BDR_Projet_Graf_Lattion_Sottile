package ch.heigvd.auth;

import ch.heigvd.users.User;
import ch.heigvd.users.UserLogin;
import io.javalin.http.*;
import java.sql.*;

public class AuthController {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    public AuthController(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }

    public void login(Context ctx) throws SQLException {

        UserLogin loginUser = ctx.bodyValidator(UserLogin.class)
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        System.out.println("User want to login with email="+loginUser.email+" and password="+loginUser.password+".");

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utilisateur WHERE email = ? AND motdepasse = ?"); // Note: Storing and comparing passwords in plain text is not secure
            stmt.setString(1, loginUser.email);
            stmt.setString(2, loginUser.password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ctx.cookie("user", rs.getString("email"));
                ctx.status(HttpStatus.NO_CONTENT);
                return;
            }
        }

        throw new UnauthorizedResponse();
    }

    public void logout(Context ctx) {
        ctx.removeCookie("user");
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void profile(Context ctx) throws SQLException {
        String userEmail = ctx.cookie("user");

        if (userEmail == null) {
            throw new UnauthorizedResponse();
        }

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utilisateur WHERE email = ?");
            stmt.setString(1, userEmail);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.email = rs.getString("email");
                user.firstName = rs.getString("prenom");
                user.lastName = rs.getString("nom");
                user.password = rs.getString("motdepasse"); // Note: Storing passwords in plain text is not secure

                ctx.json(user);
                return;
            }
        }

        throw new UnauthorizedResponse();
    }
}
