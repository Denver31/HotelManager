package src.storage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import src.classes.Factura;
import src.classes.Reserva;

public class ReservaStorage extends BaseStorage {

    public ReservaStorage(String dbPath) {
        super(dbPath);
    }

    @Override
    protected String getCreateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS reservas (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    idHabitacion INTEGER NOT NULL,
                                    idHuespede INTEGER NOT NULL,
                                    idFactura INTEGER NOT NULL,
                                    desde TEXT NOT NULL,
                                    hasta TEXT NOT NULL,
                                    FOREIGN KEY (idHabitacion) REFERENCES habitaciones(id) ON DELETE CASCADE,
                                    FOREIGN KEY (idHuespede) REFERENCES huespedes(id) ON DELETE CASCADE,
                                    FOREIGN KEY (idFactura) REFERENCES facturas(id) ON DELETE CASCADE
                                );
                \s""";
    }

    public int save(Reserva r) {
        String sql = "INSERT INTO reservas (idHabitacion, idHuespede, idFactura, desde, hasta) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, r.getIdHabitacion());
            pstmt.setInt(2, r.getIdHuespede());
            pstmt.setInt(3, r.getIdFactura());
            pstmt.setString(4, r.getDesde().toString());
            pstmt.setString(5, r.getHasta().toString());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("💾 reserva guardada con id = " + id);
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return -1;
    }

    public Map<Integer, Reserva> getAll() {
        Map<Integer, Reserva> ReservasMap = new HashMap<>();
        String sql = "SELECT * FROM reservas";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Reserva r = new Reserva(
                        rs.getInt("idHabitacion"),
                        rs.getInt("idHuespede"),
                        rs.getInt("idFactura"),
                        LocalDate.parse(rs.getString("desde")),
                        LocalDate.parse(rs.getString("hasta")));

                ReservasMap.put(rs.getInt("id"), r);
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return ReservasMap;
    }
}



