package almacenamiento;

import dominio.Factura;
import dominio.Factura.MetodoPago;
import dominio.Factura.EstadoFactura;
import validaciones.FunctionalException;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class FacturaStorage extends BaseStorage<Factura> {

    public FacturaStorage(Connection connection) {
        super(connection, "facturas");
    }

    @Override
    public void save(Factura f) {
        String sql = """
            INSERT INTO facturas (total, pagada, fecha_pago, metodo, cuotas, vencimiento, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, f.getTotal());
            ps.setBoolean(2, f.estaPagada());
            ps.setDate(3, f.getFechaPago() != null ? Date.valueOf(f.getFechaPago()) : null);
            ps.setString(4, f.getMetodo().name());
            ps.setInt(5, f.getCuotas());
            ps.setDate(6, Date.valueOf(f.getVencimiento()));
            ps.setString(7, f.getEstado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new FunctionalException("Error al guardar factura", e);
        }
    }

    @Override
    public void update(Factura f) {
        String sql = """
            UPDATE facturas
            SET total=?, pagada=?, fecha_pago=?, metodo=?, cuotas=?, vencimiento=?, estado=?
            WHERE id=?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, f.getTotal());
            ps.setBoolean(2, f.estaPagada());
            ps.setDate(3, f.getFechaPago() != null ? Date.valueOf(f.getFechaPago()) : null);
            ps.setString(4, f.getMetodo().name());
            ps.setInt(5, f.getCuotas());
            ps.setDate(6, Date.valueOf(f.getVencimiento()));
            ps.setString(7, f.getEstado().name());
            ps.setInt(8, f.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new FunctionalException("Error al actualizar factura", e);
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException(
                "Las facturas no se eliminan físicamente."
        );
    }

    @Override
    protected Factura mapRow(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getInt("id"),
                rs.getDouble("total"),
                MetodoPago.valueOf(rs.getString("metodo")),
                rs.getInt("cuotas"),
                rs.getDate("vencimiento").toLocalDate(),
                EstadoFactura.valueOf(rs.getString("estado")),
                rs.getDate("fecha_pago") != null ? rs.getDate("fecha_pago").toLocalDate() : null
        );
    }

    public List<Factura> findPendientesHasta(LocalDate fecha) {
        return executeQuery(
                "SELECT * FROM facturas WHERE estado='PENDIENTE' AND vencimiento <= ?",
                Date.valueOf(fecha)
        );
    }

    @Override
    public Factura findById(int id) {
        List<Factura> lista = executeQuery("SELECT * FROM facturas WHERE id=?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Factura> findAll() {
        return executeQuery("SELECT * FROM facturas");
    }
}