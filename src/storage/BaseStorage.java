package src.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Абстрактный базовый класс
public abstract class BaseStorage {

    protected String dbPath;

    public BaseStorage(String dbPath) {
        this.dbPath = dbPath;
        ensureTableExists();
    }

    private void ensureTableExists() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement()) {

            // Выполняем SQL-команду (или несколько)
            stmt.execute(getCreateTableSQL());

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    protected abstract String getCreateTableSQL();

}
