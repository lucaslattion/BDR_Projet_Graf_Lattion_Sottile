package ch.heigvd.auth;

import ch.heigvd.user.User;
import io.javalin.http.*;
import java.sql.*;

public class AuthController {

    private Connection conn;
    public AuthController(Connection connection) {
        conn = connection;
    }

    public void login(Context ctx) throws SQLException {

        User loginUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        System.out.println("User want to login with email="+loginUser.email+" and password="+loginUser.password+".");


        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utilisateur WHERE email = ? AND motdepasse = ?"); // Note: Storing and comparing passwords in plain text is not secure
        stmt.setString(1, loginUser.email);
        stmt.setString(2, loginUser.password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            ctx.cookie("user", rs.getString("email"));

            ctx.status(HttpStatus.NO_CONTENT);
            return;
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

        throw new UnauthorizedResponse();
    }

    public boolean validLoggedUser(Context ctx) throws SQLException {
        String userEmail = ctx.cookie("user");

        if (userEmail == null) {
            throw new UnauthorizedResponse();
        }

        if(userEmail.equalsIgnoreCase("admin")) {
            return true;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utilisateur WHERE email = ?");
        stmt.setString(1, userEmail);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.email = rs.getString("email");
            user.firstName = rs.getString("prenom");
            user.lastName = rs.getString("nom");
            user.password = rs.getString("motdepasse"); // Note: Storing passwords in plain text is not secure

            if (user != null) {
                if (user.email.isEmpty() == false) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }
}
