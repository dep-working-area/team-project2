package lk.ijse.dep10.teamproject2.db;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;

    private final Connection connection;

    private DBConnection(){
        try {
            File file = new File("application.properties");
            FileReader fr = new FileReader(file);

            Properties properties = new Properties();
            properties.load(fr);
            String host = properties.getProperty("mysql.host", "localhost");
            String port = properties.getProperty("mysql.port","3306");
            String database = properties.getProperty("mysql.database","dep10_team_project");
            String userName = properties.getProperty("mysql.username","root");
            String password = properties.getProperty("mysql.password","mySQL@123@");

            String url ="jdbc:mysql://"+host+":"+port+"/"+database+"?createDatabaseIfNotExist=true&allowMultiQueries=true";
            connection = DriverManager.getConnection(url, userName, password);

            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to establish the database").showAndWait();
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance(){
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }
    public Connection getConnection(){
        return connection;
    }
}
