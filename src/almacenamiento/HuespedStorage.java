package almacenamiento;

import dominio.Huesped;
import dominio.Huesped.EstadoHuesped;
import validaciones.FunctionalException;

import java.sql.*;
import java.util.List;

public class HuespedStorage extends BaseStorage<Huesped> {

    public HuespedStorage(Connection connection) {
        super(connection, "huespedes");
    }

    @Override
    public void save(Huesped h) {
        String sql = """
            INSERT INTO huespedes (dni, nombre, apellido, email, estado)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, h.getDni());
            ps.setString(2, h.getNombre());
            ps.setString(3, h.getApellido());
            ps.setString(4, h.getEmail());
            ps.setString(5, h.getEstado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new FunctionalException("Error al guardar huésped.", e);
        }
    }

    @Override
    public void update(Huesped h) {
        String sql = """
            UPDATE huespedes
            SET nombre=?, apellido=?, email=?, estado=?
            WHERE id=?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, h.getNombre());
            ps.setString(2, h.getApellido());
            ps.setString(3, h.getEmail());
            ps.setString(4, h.getEstado().name());
            ps.setInt(5, h.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new FunctionalException("Error al actualizar huésped.", e);
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException(
                "Los huéspedes no se eliminan físicamente. Se dan de baja."
        );
    }

    public Huesped findByDni(String dni) {
        List<Huesped> lista = executeQuery("SELECT * FROM huespedes WHERE dni = ?", dni);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<Huesped> findActivos() {
        return executeQuery("SELECT * FROM huespedes WHERE estado='ACTIVO'");
    }

    @Override
    public Huesped findById(int id) {
        List<Huesped> lista = executeQuery("SELECT * FROM huespedes WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Huesped> findAll() {
        return executeQuery("SELECT * FROM huespedes");
    }

    @Override
    protected Huesped mapRow(ResultSet rs) throws SQLException {
        return new Huesped(
                rs.getInt("id"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                EstadoHuesped.valueOf(rs.getString("estado"))
        );
    }
}