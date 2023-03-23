package lk.ijse.dep10.teamproject2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep10.teamproject2.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader= new FXMLLoader(this.getClass().getResource("/view/CustomerView.fxml"));
        AnchorPane root=fxmlLoader.load();
        Scene scene= new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();


    }




}
