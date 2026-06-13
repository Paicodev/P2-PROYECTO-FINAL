package com.gym.manager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* Singleton -> nos asegura que va a haber UNA SOLA conexión a la base de datos */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    //unico constructor de la clase
    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con éxito.");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error al conectar: " + e.getMessage(), e);
        }
    }

    //haciendolo sincronizado nos aseguramos a que si entran dos hilos al mismo tiempo no generen una instancia cada uno
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar: " + e.getMessage());
        }
    }
}