package almacenamiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStorage<T> {

    protected Connection connection;
    protected String tableName;

    public BaseStorage(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    public abstract void save(T entidad);
    public abstract void update(T entidad);
    public abstract void delete(int id);
    public abstract T findById(int id);
    public abstract List<T> findAll();

    /** Ejecuta una consulta genérica SELECT y devuelve un listado */
    protected List<T> executeQuery(String sql) {
        List<T> resultados = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultados.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    /** Convierte una fila de ResultSet a un objeto del dominio */
    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
