package src.storage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import src.classes.Factura;

public class FacturaStorage extends BaseStorage {

    public FacturaStorage(String dbPath) {
        super(dbPath);
    }

    @Override
    protected String getCreateTableSQL() {
        return """
                CREATE TABLE IF NOT EXISTS facturas (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    total REAL NOT NULL,
                                    pagado REAL NOT NULL DEFAULT 0,
                                    cantPagos INTEGER NOT NULL,
                                    tipo TEXT CHECK(tipo IN ('PARCIAL', 'TOTAL')) NOT NULL,
                                    metodo TEXT CHECK(metodo IN ('TRANSFERENCIA', 'TARJETA')) NOT NULL,
                                    pagarHasta TEXT NOT NULL
                                );\s""";
    }

    public int save(Factura f) {
        String sql = "INSERT INTO facturas (total, pagado, cantPagos, tipo, metodo, pagarHasta) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, f.getTotal());
            pstmt.setDouble(2, f.getPagado());
            pstmt.setInt(3, f.getCantPagos());
            pstmt.setString(4, f.getTipo().name());
            pstmt.setString(5, f.getMetodo().name());
            pstmt.setString(6, f.getPagarHasta().toString());

            pstmt.executeUpdate();

            // Получаем id новой записи
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("💾 Factura guardada con id = " + id);
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return -1;
    }

    public Map<Integer, Factura> getAll() {
        Map<Integer, Factura> FacturasMap = new HashMap<>();
        String sql = "SELECT * FROM facturas";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Factura f = new Factura(
                        rs.getDouble("total"),
                        rs.getDouble("pagado"),
                        Factura.tipoDePago.valueOf(rs.getString("tipo")),
                        Factura.metodoDePago.valueOf(rs.getString("metodo")),
                        rs.getInt("cantPagos"),
                        LocalDate.parse(rs.getString("pagarHasta")));

                FacturasMap.put(rs.getInt("id"), f);
            }

        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }

        return FacturasMap;
    }
}



