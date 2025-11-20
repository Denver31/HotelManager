package almacenamiento;

import dominio.*;
import dominio.Reserva.EstadoReserva;
import validaciones.FunctionalException;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ReservaStorage extends BaseStorage<Reserva> {

    private final HabitacionStorage habitacionStorage;
    private final HuespedStorage huespedStorage;
    private final FacturaStorage facturaStorage;

    public ReservaStorage(Connection conn,
                          HabitacionStorage habitacionStorage,
                          HuespedStorage huespedStorage,
                          FacturaStorage facturaStorage) {

        super(conn, "reservas");

        if (habitacionStorage == null || huespedStorage == null || facturaStorage == null)
            throw new FunctionalException("Storages de dependencia no pueden ser null.");

        this.habitacionStorage = habitacionStorage;
        this.huespedStorage = huespedStorage;
        this.facturaStorage = facturaStorage;
    }

    @Override
    public void save(Reserva r) {
        String sql = """
            INSERT INTO reservas (habitacion_id, huesped_id, factura_id, desde, hasta, estado)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getHabitacion().getId());
            ps.setInt(2, r.getHuesped().getId());
            ps.setInt(3, r.getFactura().getId());
            ps.setDate(4, Date.valueOf(r.getDesde()));
            ps.setDate(5, Date.valueOf(r.getHasta()));
            ps.setString(6, r.getEstado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new FunctionalException("Error al guardar la reserva.", e);
        }
    }

    @Override
    public void update(Reserva r) {
        String sql = """
            UPDATE reservas
            SET habitacion_id=?, huesped_id=?, factura_id=?,
                desde=?, hasta=?, estado=?
            WHERE id=?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, r.getHabitacion().getId());
            ps.setInt(2, r.getHuesped().getId());
            ps.setInt(3, r.getFactura().getId());
            ps.setDate(4, Date.valueOf(r.getDesde()));
            ps.setDate(5, Date.valueOf(r.getHasta()));
            ps.setString(6, r.getEstado().name());
            ps.setInt(7, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new FunctionalException("Error al actualizar la reserva.", e);
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Las reservas no se eliminan físicamente.");
    }

    @Override
    public Reserva findById(int id) {
        List<Reserva> lista = executeQuery("SELECT * FROM reservas WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Reserva> findAll() {
        return executeQuery("SELECT * FROM reservas");
    }

    public List<Reserva> findEntradas(LocalDate desde, LocalDate hasta) {
        return executeQuery(
                "SELECT * FROM reservas WHERE desde BETWEEN ? AND ?",
                Date.valueOf(desde), Date.valueOf(hasta));
    }

    public List<Reserva> findSalidas(LocalDate desde, LocalDate hasta) {
        return executeQuery(
                "SELECT * FROM reservas WHERE hasta BETWEEN ? AND ?",
                Date.valueOf(desde), Date.valueOf(hasta));
    }

    @Override
    protected Reserva mapRow(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        LocalDate desde = rs.getDate("desde").toLocalDate();
        LocalDate hasta = rs.getDate("hasta").toLocalDate();

        Habitacion h = habitacionStorage.findById(rs.getInt("habitacion_id"));
        Huesped hu = huespedStorage.findById(rs.getInt("huesped_id"));
        Factura f = facturaStorage.findById(rs.getInt("factura_id"));

        if (h == null || hu == null || f == null)
            throw new FunctionalException("Inconsistencia referencial al reconstruir reserva.");

        EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));

        return new Reserva(id, h, hu, f, desde, hasta, estado);
    }

    public List<Reserva> findByHabitacion(int habitacionId) {
        return executeQuery("SELECT * FROM reservas WHERE habitacion_id=?", habitacionId);
    }

    public Reserva findByFacturaId(int facturaId) {
        List<Reserva> lista =
                executeQuery("SELECT * FROM reservas WHERE factura_id=? ORDER BY id DESC LIMIT 1",
                        facturaId);
        return lista.isEmpty() ? null : lista.get(0);
    }
}