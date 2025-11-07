package almacenamiento;

import dominio.Habitacion;
import dominio.Habitacion.TipoHabitacion;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class HabitacionStorage extends BaseStorage<Habitacion> {

    public HabitacionStorage(Connection connection) {
        super(connection, "habitaciones");
    }

    @Override
    public void save(Habitacion h) {
        String sql = "INSERT INTO habitaciones (nombre, descripcion, precio, tipo, capacidad) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getNombre());
            ps.setString(2, h.getDescripcion());
            ps.setDouble(3, h.getPrecio());
            ps.setString(4, h.getTipo().name());
            ps.setInt(5, h.getCapacidad());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar habitación: " + e.getMessage());
        }
    }

    @Override
    public void update(Habitacion h) {
        String sql = "UPDATE habitaciones SET nombre=?, descripcion=?, precio=?, tipo=?, capacidad=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, h.getNombre());
            ps.setString(2, h.getDescripcion());
            ps.setDouble(3, h.getPrecio());
            ps.setString(4, h.getTipo().name());
            ps.setInt(5, h.getCapacidad());
            ps.setInt(6, h.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar habitación: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM habitaciones WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar habitación: " + e.getMessage());
        }
    }

    @Override
    public Habitacion findById(int id) {
        String sql = "SELECT * FROM habitaciones WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Habitacion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        Habitacion.TipoHabitacion.valueOf(rs.getString("tipo")),
                        rs.getInt("capacidad")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar habitación: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Habitacion> findAll() {
        return executeQuery("SELECT * FROM habitaciones");
    }

    public List<Habitacion> findDisponibles(LocalDate desde, LocalDate hasta) {
        String sql = String.format("""
        SELECT * FROM habitaciones h
        WHERE h.id NOT IN (
            SELECT r.id
            FROM reservas r
            WHERE DATE(r.desde) < DATE('%s')
              AND DATE(r.hasta) > DATE('%s')
        )
        """, hasta, desde);

        System.out.println("🟦 Ejecutando SQL:\n" + sql);

        List<Habitacion> res = executeQuery(sql);
        System.out.println("🔹 Habitaciones encontradas: " + res.size());
        res.forEach(h -> System.out.println(" - " + h.getId() + " " + h.getNombre()));

        return res;
    }


    @Override
    protected Habitacion mapRow(ResultSet rs) throws SQLException {
        return new Habitacion(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                TipoHabitacion.valueOf(rs.getString("tipo")),
                rs.getInt("capacidad")
        );
    }
}