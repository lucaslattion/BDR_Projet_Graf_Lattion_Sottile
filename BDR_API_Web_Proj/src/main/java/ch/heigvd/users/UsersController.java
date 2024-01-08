package ch.heigvd.users;

import io.javalin.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersController {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    public UsersController(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }

    public void create(Context ctx) throws SQLException {
        User newUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.firstName != null, "Missing first name")
                .check(obj -> obj.lastName != null, "Missing last name")
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        try (Connection conn = getConnection()) {
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO utilisateur (email, prenom, nom, motdepasse) VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, newUser.email);
            insertStmt.setString(2, newUser.firstName);
            insertStmt.setString(3, newUser.lastName);
            insertStmt.setString(4, newUser.password); // Note: Storing passwords in plain text is not secure

            insertStmt.executeUpdate();
            ctx.status(HttpStatus.CREATED);
            ctx.json(newUser);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique violation
                throw new ConflictResponse();
            } else {
                throw e;
            }
        }
    }

    public void getOne(Context ctx) throws SQLException {
        String email = ctx.pathParam("email");

        try (Connection conn = getConnection()) {

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utilisateur WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundResponse();
            }

            User user = new User();
            user.email = rs.getString("email");
            user.firstName = rs.getString("prenom");
            user.lastName = rs.getString("nom");
            user.password = rs.getString("motdepasse"); // Note: Storing passwords in plain text is not secure

            ctx.json(user);
        }
    }

    public void getMany(Context ctx) throws SQLException {
        String firstName = ctx.queryParam("firstName");
        String lastName = ctx.queryParam("lastName");
        System.out.println("Request launched with firstname="+firstName+" and lastname="+lastName+".");
        List<User> userList = new ArrayList<>();

        try (Connection conn = getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM utilisateur");
            System.out.println("connection is working");
            if (firstName != null || lastName != null) {
                queryBuilder.append(" WHERE ");
                List<String> conditions = new ArrayList<>();

                if (firstName != null) {
                    conditions.add("prenom = ?");
                }
                if (lastName != null) {
                    conditions.add("nom = ?");
                }
                queryBuilder.append(String.join(" AND ", conditions));
            }

            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());

            int index = 1;
            if (firstName != null) {
                stmt.setString(index++, firstName);
            }
            if (lastName != null) {
                stmt.setString(index, lastName);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.email = rs.getString("email");
                user.firstName = rs.getString("prenom");
                user.lastName = rs.getString("nom");
                user.password = rs.getString("motdepasse"); // Note: Storing passwords in plain text is not secure
                userList.add(user);
            }
        }

        ctx.json(userList);
    }

    public void update(Context ctx) throws SQLException {
        User updateUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.firstName != null, "Missing first name")
                .check(obj -> obj.lastName != null, "Missing last name")
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        String email = ctx.pathParam("email");

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE utilisateur SET first_name = ?, last_name = ?, password = ? WHERE email = ?");
            stmt.setString(1, updateUser.firstName);
            stmt.setString(2, updateUser.lastName);
            stmt.setString(3, updateUser.password); // Note: Storing passwords in plain text is not secure
            stmt.setString(4, email);

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new NotFoundResponse();
            }
        }

        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void delete(Context ctx) throws SQLException {
        String email = ctx.pathParam("email");

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM utilisateur WHERE email = ?");
            stmt.setString(1, email);

            int deletedRows = stmt.executeUpdate();
            if (deletedRows == 0) {
                throw new NotFoundResponse();
            }
        }

        ctx.status(HttpStatus.NO_CONTENT);
    }
}
