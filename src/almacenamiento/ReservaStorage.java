package almacenamiento;

import dominio.*;
import dominio.Reserva.EstadoReserva;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ReservaStorage extends BaseStorage<Reserva> {

    public ReservaStorage(Connection connection) {
        super(connection, "reservas");
    }

    @Override
    public void save(Reserva r) {
        String sql = "INSERT INTO reservas (habitacion_id, huesped_id, factura_id, desde, hasta, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getHabitacion().getId());
            ps.setInt(2, r.getHuesped().getId());
            ps.setInt(3, r.getFactura().getId());
            ps.setString(4, r.getDesde().toString());
            ps.setString(5, r.getHasta().toString());
            ps.setString(6, r.getEstado().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
        }
    }

    @Override
    public void update(Reserva r) {
        String sql = "UPDATE reservas SET estado=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, r.getEstado().name());
            ps.setInt(2, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar reserva: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM reservas WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
        }
    }

    @Override
    public Reserva findById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM reservas WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar reserva: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Reserva> findAll() {
        return executeQuery("SELECT * FROM reservas");
    }

    public List<Reserva> findEntradas(LocalDate desde, LocalDate hasta) {
        return executeQuery("SELECT * FROM reservas WHERE desde BETWEEN '" + desde + "' AND '" + hasta + "'");
    }

    public List<Reserva> findSalidas(LocalDate desde, LocalDate hasta) {
        return executeQuery("SELECT * FROM reservas WHERE hasta BETWEEN '" + desde + "' AND '" + hasta + "'");
    }

    @Override
    protected Reserva mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int idHabitacion = rs.getInt("habitacion_id");
        int idHuesped = rs.getInt("huesped_id");
        int idFactura = rs.getInt("factura_id");
        LocalDate desde = LocalDate.parse(rs.getString("desde"));
        LocalDate hasta = LocalDate.parse(rs.getString("hasta"));
        Reserva.EstadoReserva estado = Reserva.EstadoReserva.valueOf(rs.getString("estado"));

        // Objetos placeholder (se completan luego en el service)
        Habitacion habitacion = new Habitacion(idHabitacion);
        Huesped huesped = new Huesped(idHuesped, "", "", "", "");
        Factura factura = new Factura(idFactura, 0, Factura.MetodoPago.TRANSFERENCIA, 1, LocalDate.now(), false, null);

        return new Reserva(id, habitacion, huesped, factura, desde, hasta, estado);
    }

    // en almacenamiento/ReservaStorage.java
    public Reserva findByFacturaId(int facturaId) {
        String sql = "SELECT * FROM reservas WHERE factura_id = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, facturaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar reserva por factura: " + e.getMessage());
        }
        return null;
    }

}