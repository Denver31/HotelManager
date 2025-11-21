package almacenamiento;

import dominio.Habitacion;
import dominio.Habitacion.TipoHabitacion;
import dominio.Habitacion.EstadoHabitacion;
import validaciones.FunctionalException;

import java.sql.*;
import java.util.List;

public class HabitacionStorage extends BaseStorage<Habitacion> {

    public HabitacionStorage(Connection connection) {
        super(connection, "habitaciones");
    }

    @Override
    public void save(Habitacion h) {
        String sql = """
            INSERT INTO habitaciones (nombre, descripcion, precio, tipo, capacidad, estado)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, h.getNombre());
            ps.setString(2, h.getDescripcion());
            ps.setDouble(3, h.getPrecio());
            ps.setString(4, h.getTipo().name());
            ps.setInt(5, h.getCapacidad());
            ps.setString(6, h.getEstado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new FunctionalException("Error al guardar habitación", e);
        }
    }

    @Override
    public void update(Habitacion h) {
        String sql = """
            UPDATE habitaciones
            SET nombre=?, descripcion=?, precio=?, tipo=?, capacidad=?, estado=?
            WHERE id=?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, h.getNombre());
            ps.setString(2, h.getDescripcion());
            ps.setDouble(3, h.getPrecio());
            ps.setString(4, h.getTipo().name());
            ps.setInt(5, h.getCapacidad());
            ps.setString(6, h.getEstado().name());
            ps.setInt(7, h.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new FunctionalException("Error al actualizar habitación", e);
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException(
                "Las habitaciones no se eliminan físicamente; se dan de baja."
        );
    }

    @Override
    public Habitacion findById(int id) {
        List<Habitacion> list = executeQuery("SELECT * FROM habitaciones WHERE id=?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Habitacion> findAll() {
        return executeQuery("SELECT * FROM habitaciones");
    }

    public List<Habitacion> findTodasActivas() {
        return executeQuery("SELECT * FROM habitaciones WHERE estado='ACTIVA'");
    }

    @Override
    protected Habitacion mapRow(ResultSet rs) throws SQLException {

        return new Habitacion(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                TipoHabitacion.valueOf(rs.getString("tipo")),
                rs.getInt("capacidad"),
                EstadoHabitacion.valueOf(rs.getString("estado"))
        );
    }
}