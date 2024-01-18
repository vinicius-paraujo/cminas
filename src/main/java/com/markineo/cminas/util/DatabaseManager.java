package com.markineo.cminas.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;

public class DatabaseManager {
	private static BasicDataSource dataSource = new BasicDataSource();
	
	public static void configureDataSource(String url, String user, String password) {
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setMinIdle(5);
		dataSource.setMaxIdle(10);
		dataSource.setMaxTotal(20);
		
		Duration duration = Duration.ofSeconds(5);
		dataSource.setMaxWait(duration);
	}
	
	public static Connection getConnection() throws SQLException {
		int retries = 5;
        while (retries > 0) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("Erro ao obter conexão: " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                retries--;
            }
        }
        throw new SQLException("Falha ao obter conexão após várias tentativas.");
	}
	
	public static ResultSet executeQueryRs(Connection connection, String sql, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
            return null;
        }
    }
	
	public static void executeQuery(Connection connection, String sql, Object... parameters) {
        try {
             PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
