package almacenamiento;

import java.sql.*;

public class DatabaseManager {

    private static Connection conn;
    private static final String DB_URL = "jdbc:sqlite:hotel.db";

    private DatabaseManager() {}

    public static Connection getConnection() {
        if (conn != null) return conn;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            habilitarForeignKeys();
            inicializarEsquema();

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }

        return conn;
    }

    /**
     * Asegura que SQLite respete claves foráneas.
     */
    private static void habilitarForeignKeys() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
    }

    /**
     * Inicializa tablas si no existen.
     * Ejecuta cada sentencia por separado para evitar errores en SQLite.
     */
    private static void inicializarEsquema() throws SQLException {

        String[] tablas = {

                // HUESPEDES
                """
        CREATE TABLE IF NOT EXISTS huespedes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            dni TEXT NOT NULL UNIQUE,
            nombre TEXT NOT NULL,
            apellido TEXT NOT NULL,
            email TEXT,
            estado TEXT NOT NULL DEFAULT 'ACTIVO'
            CHECK (estado IN ('ACTIVO', 'BAJA'))
         );
        """,

                // HABITACIONES
                """
        CREATE TABLE IF NOT EXISTS habitaciones (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            precio REAL NOT NULL,
            tipo TEXT NOT NULL,
            capacidad INTEGER NOT NULL CHECK (capacidad > 0),
            estado TEXT NOT NULL CHECK (estado IN ('ACTIVA','BAJA'))
        );
        """,

                // FACTURAS
                """
        CREATE TABLE IF NOT EXISTS facturas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            total REAL NOT NULL,
            metodo TEXT NOT NULL,
            cuotas INTEGER NOT NULL,
            fecha_alta DATE NOT NULL,
            vencimiento DATE NOT NULL,
            estado TEXT NOT NULL,
            fecha_pago DATE
        );
        """,

                // RESERVAS
                """
        CREATE TABLE IF NOT EXISTS reservas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            habitacion_id INTEGER NOT NULL,
            huesped_id INTEGER NOT NULL,
            factura_id INTEGER NOT NULL,
            desde DATE NOT NULL,
            hasta DATE NOT NULL,
            estado TEXT NOT NULL,
            FOREIGN KEY (habitacion_id) REFERENCES habitaciones(id),
            FOREIGN KEY (huesped_id) REFERENCES huespedes(id),
            FOREIGN KEY (factura_id) REFERENCES facturas(id)
        );
        """
        };

        try (Statement st = conn.createStatement()) {
            for (String sql : tablas) {
                st.execute(sql);
            }
        }
    }


    public static void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando conexión", e);
        }
    }
}
