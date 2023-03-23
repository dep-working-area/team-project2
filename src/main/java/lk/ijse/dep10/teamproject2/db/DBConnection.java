package lk.ijse.dep10.teamproject2.db;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static DBConnection dbConnection;

    private final Connection connection;

    private DBConnection(){

        try {
            File file = new File("application.properties");
            Properties properties = new Properties();
            FileReader fr = new FileReader(file);
            properties.load(fr);
            fr.close();

            String host = properties.getProperty("customersql.host", "localhost");
            String port = properties.getProperty("customersql.port", "3306");
            String database = properties.getProperty("customersql.database", "dep10_git");
            String username = properties.getProperty("customersql.username", "root");
            String password = properties.getProperty("customersql.password", "");


            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?createDatabaseIfNotExist=true&allowMultiQueries=true";
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to obtain the database connection").showAndWait();
            System.exit(1);
            throw new RuntimeException(e);
        }

    }

    public static DBConnection getInstance(){
        return  (dbConnection==null)?dbConnection=new DBConnection():dbConnection;

    }

    public Connection getConnection(){

        return connection;

    }


}