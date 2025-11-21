package almacenamiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStorage<T> {

    protected final Connection connection;
    protected final String tableName;

    protected BaseStorage(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    public abstract void save(T entidad);
    public abstract void update(T entidad);
    public abstract void delete(int id);
    public abstract T findById(int id);
    public abstract List<T> findAll();
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /**
     * Ejecuta un SELECT parametrizado y convierte cada fila con mapRow().
     */
    protected List<T> executeQuery(String sql, Object... params) {
        List<T> resultados = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            cargarParametros(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error ejecutando consulta en " + tableName, e);
        }

        return resultados;
    }

    /**
     * Aplica parámetros dinámicos a un PreparedStatement,
     * manejando conversiones comunes (especialmente fechas).
     */
    private void cargarParametros(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];

            if (p instanceof java.time.LocalDate ld) {
                ps.setDate(i + 1, Date.valueOf(ld));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }
}
