package src.storage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import src.classes.Huespede;

public class HuespedeStorage extends BaseStorage {

    public HuespedeStorage(String dbPath) {
        super(dbPath);
    }

    @Override
    protected String getCreateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS huespedes (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    nombre TEXT NOT NULL,
                                    dni TEXT NOT NULL UNIQUE
                                );
                               \s""";
    }

    public void save(Huespede h) {
        String sql = "INSERT INTO huespedes (nombre, dni) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, h.getNombre());
            pstmt.setString(2, h.getDNI());

            pstmt.executeUpdate();
            System.out.println("💾 Huespede guardado");

        } catch (SQLException e) {
            System.err.println("error" + e.getMessage());
        }
    }
    public Map<String, Huespede> getAll() {
        Map<String, Huespede> huespedesMap = new HashMap<>();
        String sql = "SELECT * FROM huespedes";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String dni = rs.getString("dni");

                Huespede h = new Huespede(
                        rs.getString("nombre"),
                        dni
                );

                huespedesMap.put(dni, h);
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return huespedesMap;
    }
}



