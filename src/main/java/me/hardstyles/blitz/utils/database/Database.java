package me.hardstyles.blitz.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

@Getter
public class Database {
    private final HikariDataSource dataSource;
    private final Connection connection;

    public Database() {
        String[] details = getDetails();
        String host = details[0];
        String database = details[1];
        String user = details[2];
        String pass = details[3];
        String jbdcUrl = String.format("jdbc:mysql://%s:3306/%s?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=CET", host, database);

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jbdcUrl);
        config.setUsername(user);
        config.setPassword(pass);
        config.setPoolName("BlitzDuels-Pool");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setMaximumPoolSize(5);
        config.setAutoCommit(true);

        dataSource = new HikariDataSource(config);
        Connection c;
        try {
            c = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            c = null;
        }
        connection = c;
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.i(), ()-> {
            try (Statement statement = getConnection().createStatement()) {
                statement.execute("select uuid from data limit 1");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 6000L, 6000L);
    }

    public Connection getConnection() {
        return connection;
    }

    private String[] getDetails() {
        String[] details = new String[4];
        try {
            File file = new File("Database.txt");
            Scanner scanner = new Scanner(file);
            if (!scanner.useDelimiter("\\A").hasNext()) {
                scanner.close();
                return details;
            }
            String content = scanner.useDelimiter("\\A").next();


            String[] contents = content.split("\n");
            details[0] = contents[0].substring(6);
            details[1] = contents[1].substring(10);
            details[2] = contents[2].substring(10);
            details[3] = contents[3].substring(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return details;
    }
}
