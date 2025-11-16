package cafeteria.cafeteria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/cafeteria";
    private static final String USER = "root"; // usuario de MySQL
    private static final String PASSWORD = "1234"; // contraseña de MySQL

    /**
     * Establece y devuelve una conexión a la base de datos 'cafeteria'.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // No es estrictamente necesario en JDBC moderno, pero es buena práctica
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de MySQL no encontrado.");
            e.printStackTrace();
            throw new SQLException("Driver no encontrado", e);
        }
    }
}