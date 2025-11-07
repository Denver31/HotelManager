package almacenamiento;

import dominio.Factura;
import dominio.Factura.MetodoPago;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class FacturaStorage extends BaseStorage<Factura> {

    public FacturaStorage(Connection connection) {
        super(connection, "facturas");
    }

    @Override
    public void save(Factura f) {
        String sql = "INSERT INTO facturas (total, pagada, fecha_pago, metodo, cuotas, vencimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, f.getTotal());
            stmt.setBoolean(2, f.isPagada());
            stmt.setString(3, f.getFechaPago() != null ? f.getFechaPago().toString() : null);
            stmt.setString(4, f.getMetodo().name());
            stmt.setInt(5, f.getCuotas());
            stmt.setString(6, f.getVencimiento().toString());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al guardar factura: " + e.getMessage());
        }
    }

    @Override
    public void update(Factura f) {
        String sql = "UPDATE facturas SET total=?, pagada=?, fecha_pago=?, metodo=?, cuotas=?, vencimiento=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, f.getTotal());
            ps.setBoolean(2, f.isPagada());
            ps.setString(3, f.getFechaPago() != null ? f.getFechaPago().toString() : null);
            ps.setString(4, f.getMetodo().name());
            ps.setInt(5, f.getCuotas());
            ps.setString(6, f.getVencimiento().toString());
            ps.setInt(7, f.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Error al actualizar factura: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM facturas WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Error al eliminar factura: " + e.getMessage());
        }
    }

    @Override
    public Factura findById(int id) {
        String sql = "SELECT * FROM facturas WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Factura(
                        rs.getInt("id"),
                        rs.getDouble("total"),
                        Factura.MetodoPago.valueOf(rs.getString("metodo")),
                        rs.getInt("cuotas"),
                        LocalDate.parse(rs.getString("vencimiento")),
                        rs.getBoolean("pagada"),
                        rs.getString("fecha_pago") != null
                                ? LocalDate.parse(rs.getString("fecha_pago"))
                                : null
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar factura: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Factura> findAll() {
        return executeQuery("SELECT * FROM facturas");
    }

    public List<Factura> findPendientesHasta(LocalDate fecha) {
        return executeQuery("SELECT * FROM facturas WHERE pagada=0 AND vencimiento<='" + fecha + "'");
    }

    @Override
    protected Factura mapRow(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getInt("id"),
                rs.getDouble("total"),
                MetodoPago.valueOf(rs.getString("metodo")),
                rs.getInt("cuotas"),
                LocalDate.parse(rs.getString("vencimiento")),
                rs.getBoolean("pagada"),
                rs.getString("fecha_pago") != null ? LocalDate.parse(rs.getString("fecha_pago")) : null
        );
    }
}