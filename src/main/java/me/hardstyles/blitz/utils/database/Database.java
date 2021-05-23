package me.hardstyles.blitz.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Scanner;

public class Database {

    String auth[] = getDetails();

    public Database() {

    }

    private String host = auth[0]; // The IP-address of the database host.
    private String database = auth[1]; // The name of the database.
    private String user = auth[2]; // The name of the database user.
    private String pass = auth[3]; // The password of the database user.


    String jbdcUrl = String.format("jdbc:mysql://%s:3306/%s?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=CET", host, database);
    //Call the get connection method.
    private static DataSource dataSource;

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    //Get the DataSource. If not available create the new one
    //It is not threadsafe. I didn't wanted to complicate things.
    public DataSource getDataSource() {
        if (null == dataSource) {
            System.out.println("No DataSource is available. We will create a new one.");
            createDataSource();
        }
        return dataSource;
    }

    //To create a DataSource and assigning it to variable dataSource.
    public void createDataSource() {
        HikariConfig hikariConfig = getHikariConfig();
        System.out.println("Configuration is ready.");
        System.out.println("Creating the HiakriDataSource and assigning it as the global");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        dataSource = hikariDataSource;
    }

    public HikariConfig getHikariConfig() {
        System.out.println("Creating the config with HikariConfig with maximum pool size of 5");
        HikariConfig hikaConfig = new HikariConfig();

        //This is same as passing the Connection info to the DriverManager class.
        //your jdbc url. in my case it is mysql.
        hikaConfig.setJdbcUrl(jbdcUrl);
        //username

        hikaConfig.setUsername(user);
        //password
        hikaConfig.setPassword(pass);
        //driver class name
        hikaConfig.setDriverClassName("com.mysql.jdbc.Driver");

        // Information about the pool
        //pool name. This is optional you don't have to do it.
        hikaConfig.setPoolName("MysqlPool-1");

        //the maximum connection which can be created by or resides in the pool
        hikaConfig.setMaximumPoolSize(5);


        //how much time a user can wait to get a connection from the pool.
        //if it exceeds the time limit then a SQlException is thrown
        hikaConfig.setConnectionTimeout(Duration.ofSeconds(30).toMillis());

        //The maximum time a connection can sit idle in the pool.
        // If it exceeds the time limit it is removed form the pool.
        // If you don't want to retire the connections simply put 0.
        hikaConfig.setIdleTimeout(Duration.ofMinutes(2).toMillis());

        return hikaConfig;
    }

    private String[] getDetails() {
        try {
            String[] details = new String[4];
            File file = new File("Database.txt");
            Scanner scanner = null;
            scanner = new Scanner(file);
            if (!scanner.useDelimiter("\\A").hasNext()) {
                scanner.close();
                return null;
            }
            String content = scanner.useDelimiter("\\A").next();


            String[] contents = content.split("\n");
            details[0] = contents[0].replaceAll("Host: ", "");
            details[1] = contents[1].replaceAll("Database: ", "");
            details[2] = contents[2].replaceAll("Username: ", "");
            details[3] = contents[3].replaceAll("Password: ", "");

            return details;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
