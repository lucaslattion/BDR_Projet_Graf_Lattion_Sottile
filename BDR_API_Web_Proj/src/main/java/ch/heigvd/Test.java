package ch.heigvd;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        //String host = "bdr-projet-postgresql"; //Host to use in Docker
        String host = "localhost"; //Developpement Host
        String port = "5432";
        String dbname = "bdr";
        String schema = "bdrproject"; // Nom du schéma
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?currentSchema=" + schema;
        String dbUsername = "bdr";
        String dbPassword = "bdr";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur");

            while (rs.next()) {
                // Remplacez ces par les noms de vos colonnes
                String email = rs.getString("email");
                String firstName = rs.getString("prenom");
                String lastName = rs.getString("nom");

                System.out.println("Email: " + email + ", First Name: " + firstName + ", Last Name: " + lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
