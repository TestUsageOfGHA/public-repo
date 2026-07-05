package com.example.practice;

import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryService {

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String redisHost;
    private final int redisPort;

    public InventoryService(String jdbcUrl, String dbUser, String dbPassword, String redisHost, int redisPort) {
        this.jdbcUrl = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        createTableIfNotExists();
    }

    public void restock(String sku, int quantity) {
        int newTotal;
        try (Connection conn = openConnection();
             PreparedStatement st = conn.prepareStatement(
                     "INSERT INTO inventory (sku, quantity) VALUES (?, ?) " +
                             "ON CONFLICT (sku) DO UPDATE SET quantity = inventory.quantity + EXCLUDED.quantity " +
                             "RETURNING quantity")) {
            st.setString(1, sku);
            st.setInt(2, quantity);
            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                newTotal = rs.getInt("quantity");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to restock sku " + sku, e);
        }

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.set(cacheKey(sku), String.valueOf(newTotal));
        }
    }

    public int getStock(String sku) {
        String cacheKey = cacheKey(sku);
        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            String cached = jedis.get(cacheKey);
            if (cached != null) {
                return Integer.parseInt(cached);
            }

            int quantity = readQuantityFromPostgres(sku);
            jedis.set(cacheKey, String.valueOf(quantity));
            return quantity;
        }
    }

    private void createTableIfNotExists() {
        try (Connection conn = openConnection();
             PreparedStatement st = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS inventory (" +
                             "sku VARCHAR(255) PRIMARY KEY, " +
                             "quantity INTEGER NOT NULL)")) {
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize inventory table", e);
        }
    }

    private int readQuantityFromPostgres(String sku) {
        try (Connection conn = openConnection();
             PreparedStatement st = conn.prepareStatement(
                     "SELECT quantity FROM inventory WHERE sku = ?")) {
            st.setString(1, sku);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt("quantity") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read stock for sku " + sku, e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    private static String cacheKey(String sku) {
        return "inventory:" + sku;
    }
}
