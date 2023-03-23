package lk.ijse.dep10.teamproject2.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.teamproject2.db.DBConnection;
import lk.ijse.dep10.teamproject2.model.Teacher;

import java.sql.*;
import java.util.ArrayList;

public class ManageTeachersViewController {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewTeacher;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Teacher> tblTeachers;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    public void initialize() {
        tblTeachers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblTeachers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblTeachers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        loadAllCustomers();

        btnDelete.setDisable(true);
        tblTeachers.getSelectionModel().selectedItemProperty().addListener((ol, previous, current) -> {
            btnDelete.setDisable(current == null);
            if (current == null) return;

            txtId.setText(current.getId() + "");
            txtName.setText(current.getName());
            txtAddress.setText(current.getAddress());

            btnNewTeacher.setDisable(false);
        });

        btnNewTeacher.fire();

    }

    private void loadAllCustomers() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            Statement stm = connection.createStatement();
            ResultSet rstCustomers = stm.executeQuery("SELECT * FROM Teacher");

            while (rstCustomers.next()) {
                String id = rstCustomers.getString("id");
                String name = rstCustomers.getString("name");
                String address = rstCustomers.getString("address");

                ObservableList<Teacher> customerList = tblTeachers.getItems();
                customerList.add(new Teacher(id, name, address));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load teachers details").showAndWait();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String selectedTeacherId = tblTeachers.getSelectionModel().getSelectedItem().getId();

            Statement stm = connection.createStatement();
            stm.executeUpdate("DELETE FROM Teacher WHERE id = '" + selectedTeacherId + "'");


            tblTeachers.getItems().remove(tblTeachers.getSelectionModel().getSelectedItem());
            if (tblTeachers.getItems().isEmpty()) btnNewTeacher.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the teacher..try again!").showAndWait();
        }
    }

    @FXML
    void btnNewTeacherOnAction(ActionEvent event) {
        txtId.setText(generateTeacherId());
        txtName.clear();
        txtAddress.clear();

        tblTeachers.getSelectionModel().clearSelection();
        txtName.requestFocus();

        txtName.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");
    }

    private String generateTeacherId() {
        ObservableList<Teacher> teacherList = tblTeachers.getItems();

        if (teacherList.isEmpty()) return "T001";

        String lastStudentId = teacherList.get(teacherList.size() - 1).getId();
        int newTeacherId = Integer.parseInt(lastStudentId.substring(1)) + 1;

        if (newTeacherId < 10) return "T00" + newTeacherId;

        else if (newTeacherId < 100) return "T0" + newTeacherId;
        else return "T" + newTeacherId;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!isDataValid()) return;
        Connection connection = DBConnection.getInstance().getConnection();

        Teacher teacher = new Teacher(txtId.getText(), txtName.getText(), txtAddress.getText());
        Teacher selectedTeacher = tblTeachers.getSelectionModel().getSelectedItem();

        try {
            if (selectedTeacher == null) {
                String sql = "INSERT INTO Teacher (id, name, address) VALUES ('%s','%s','%S')";
                sql = String.format(sql, teacher.getId(), teacher.getName(), teacher.getAddress());
                Statement stm = connection.createStatement();
                stm.executeUpdate(sql);

                tblTeachers.getItems().add(teacher);
            }else{
                String sql = "UPDATE Teacher SET name='%s', address='%s' WHERE id='%s'";
                sql = String.format(sql, teacher.getName(), teacher.getAddress(), teacher.getId());
                Statement stm = connection.createStatement();
                stm.executeUpdate(sql);

                int selectedTeacherIndex = tblTeachers.getSelectionModel().getSelectedIndex();
                tblTeachers.getItems().set(selectedTeacherIndex,teacher);
            }

            btnNewTeacher.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to save the teacher").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private boolean isDataValid() {
        boolean isDataValid = true;

        txtName.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");

        String name = txtName.getText();
        String address = txtAddress.getText();


        if (address.strip().length() < 3) {
            isDataValid = false;
            txtAddress.selectAll();
            txtAddress.requestFocus();
            txtAddress.getStyleClass().add("invalid");
        }

        if (!name.strip().matches("[A-Za-z ]+")) {
            isDataValid = false;
            txtName.selectAll();
            txtName.requestFocus();
            txtName.getStyleClass().add("invalid");
        }
        return isDataValid;
    }

}

