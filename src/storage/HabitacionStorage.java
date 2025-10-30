package src.storage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.classes.Habitacion;

public class HabitacionStorage extends BaseStorage {

    public HabitacionStorage(String dbPath) {
        super(dbPath);
    }

    @Override
    protected String getCreateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS habitaciones (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    nombre TEXT NOT NULL,
                                    descripcion TEXT,
                                    precio REAL NOT NULL,
                                    capacidad INTEGER NOT NULL
                                );
                               \s""";
    }

    public Integer save(Habitacion h) {
        String sql = "INSERT INTO habitaciones (nombre, descripcion, precio, capacidad) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, h.getNombre());
            pstmt.setString(2, h.getDescripcion());
            pstmt.setDouble(3, h.getPrecio());
            pstmt.setInt(4, h.getCapacidad());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("💾 Habitacion guardado id = " + id);
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }
        return -1;

    }
    public Map<Integer, Habitacion> getAll() {
        Map<Integer, Habitacion> habitacionesMap = new HashMap<>();
        String sql = "SELECT * FROM habitaciones";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int cap = rs.getInt("capacidad");
                Habitacion.tipoHabitacion tipo = null;
                switch (cap) {
                    case 2:
                        tipo = Habitacion.tipoHabitacion.INDIVIDUAL;
                        break;
                    case 3:
                        tipo = Habitacion.tipoHabitacion.MATRIMONIAL;
                        break;
                    case 5:
                        tipo = Habitacion.tipoHabitacion.COMPARTIDA;
                        break;
                }
                assert tipo != null;
                Habitacion h = new Habitacion(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        tipo
                );
                int id = rs.getInt("id");
                habitacionesMap.put(id, h);
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return habitacionesMap;
    }
    }



