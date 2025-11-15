package almacenamiento;

import dominio.Huesped;
import java.sql.*;
import java.util.List;

public class HuespedStorage extends BaseStorage<Huesped> {

    public HuespedStorage(Connection connection) {
        super(connection, "huespedes");
    }

    @Override
    public void save(Huesped h) {
        String sql = "INSERT INTO huespedes (dni, nombre, apellido, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getDni());
            ps.setString(2, h.getNombre());
            ps.setString(3, h.getApellido());
            ps.setString(4, h.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar huésped: " + e.getMessage());
        }
    }

    @Override
    public void update(Huesped h) {
        String sql = "UPDATE huespedes SET dni=?, nombre=?, apellido=?, email=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, h.getDni());
            ps.setString(2, h.getNombre());
            ps.setString(3, h.getApellido());
            ps.setString(4, h.getEmail());
            ps.setInt(5, h.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar huésped: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM huespedes WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar huésped: " + e.getMessage());
        }
    }

    public void deleteByDni(String dni) {
        String sql = "DELETE FROM " + tableName + " WHERE dni = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Huesped findByDni(String dni) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM huespedes WHERE dni=?")) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar huésped: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Huesped findById(int id) {
        String sql = "SELECT * FROM huespedes WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Huesped(
                        rs.getInt("id"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar huésped: " + e.getMessage());
        }
        return null;
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
                rs.getString("email")
        );
    }
}