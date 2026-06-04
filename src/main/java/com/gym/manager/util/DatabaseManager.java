/*Singleton -> lo usamos porque nos asegura que vamos a tener SOLO UNA conexión a la base de datos */

package com.gym.manager.util;

//import com.gym.manager.exceptions.ConexionBDException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // La única instancia de la clase
    private static DatabaseManager instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/mydb";    
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // El constructor es privado para que nadie más pueda instanciar la clase
    private DatabaseManager() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión a la base de datos establecida con éxito.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    // El método estático a llamar para obtener la conexión
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Método para obtener el objeto Connection
    public Connection getConnection() {
        return connection;
    }
}