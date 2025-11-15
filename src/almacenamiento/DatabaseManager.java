package almacenamiento;

import java.sql.*;

public class DatabaseManager {

    private static Connection conn;
    private static final String DB_URL = "jdbc:sqlite:hotel.db";

    private DatabaseManager() {}

    /**
     * Devuelve una conexión SQLite única para toda la aplicación.
     * Si la base no existe, la crea automáticamente y genera las tablas.
     */
    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(DB_URL);

                // 🔹 Activar claves foráneas
                try (Statement st = conn.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON;");
                }

                System.out.println("Conexión establecida con SQLite: " + DB_URL);

                // Crear tablas si no existen
                initDatabase();

            } catch (ClassNotFoundException e) {
                System.err.println("No se encontró el driver SQLite JDBC. ¿Está agregado al classpath?");
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            }
        }
        return conn;
    }

    /**
     * Crea las tablas necesarias si no existen y carga datos iniciales.
     */
    private static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS huespedes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dni TEXT NOT NULL UNIQUE,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                email TEXT
            );

            CREATE TABLE IF NOT EXISTS habitaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                tipo TEXT NOT NULL CHECK (tipo IN ('INDIVIDUAL','COMPARTIDA','MATRIMONIAL')),
                capacidad INTEGER NOT NULL CHECK (capacidad > 0)
            );

            CREATE TABLE IF NOT EXISTS facturas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                total REAL NOT NULL,
                pagada BOOLEAN NOT NULL DEFAULT 0,
                fecha_pago TEXT,
                metodo TEXT NOT NULL CHECK (metodo IN ('TRANSFERENCIA','TARJETA')),
                cuotas INTEGER NOT NULL CHECK (cuotas >= 0),
                vencimiento TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS reservas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                habitacion_id INTEGER NOT NULL,
                huesped_id INTEGER NOT NULL,
                factura_id INTEGER NOT NULL,
                desde TEXT NOT NULL,
                hasta TEXT NOT NULL,
                estado TEXT NOT NULL CHECK (estado IN ('PENDIENTE','CONFIRMADA','ACTIVA','FINALIZADA','CANCELADA')),
                FOREIGN KEY (habitacion_id) REFERENCES habitaciones(id) ON DELETE CASCADE,
                FOREIGN KEY (huesped_id) REFERENCES huespedes(id) ON DELETE CASCADE,
                FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE
            );
        """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("Base de datos inicializada (tablas creadas o verificadas).");
            insertSeedData(st);
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }

    /**
     * Inserta datos de ejemplo si la tabla de habitaciones está vacía.
     */
    private static void insertSeedData(Statement st) {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) AS count FROM habitaciones")) {
            if (rs.next() && rs.getInt("count") == 0) {
                String insertSQL = """
                    INSERT INTO habitaciones (nombre, descripcion, precio, tipo, capacidad)
                    VALUES
                    ('Suite 101', 'Matrimonial con vista al mar', 15000, 'MATRIMONIAL', 2),
                    ('Habitación 201', 'Individual estándar', 8000, 'INDIVIDUAL', 1),
                    ('Dormitorio 5 camas', 'Compartida para grupo', 6000, 'COMPARTIDA', 4);
                """;
                st.executeUpdate(insertSQL);
                System.out.println("Habitaciones de ejemplo insertadas en la base de datos.");
            } else {
                System.out.println("Habitaciones ya existentes, no se insertaron datos iniciales.");
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar datos iniciales: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
