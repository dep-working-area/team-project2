package lk.ijse.dep10.teamproject2.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.teamproject2.db.DbConnection;
import lk.ijse.dep10.teamproject2.model.Student;

import java.sql.*;

public class StudentViewSceneController {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewStudent;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Student> tblStudentDetails;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    public void initialize() {
        tblStudentDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Id"));
        tblStudentDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("Name"));
        tblStudentDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("Address"));

        loadAllStudents();

        tblStudentDetails.getSelectionModel().selectedItemProperty().addListener((observableValue, student, t1) -> {
            btnDelete.setDisable(t1 == null);
            if (t1 == null) return;

            txtId.setText(t1.getId() + "");
            txtName.setText(t1.getName());
            txtAddress.setText(t1.getAddress());

        });
        txtName.textProperty().addListener((observableValue, s, t1) -> {
            txtName.getStyleClass().remove("invalid");
            if (!txtName.getText().matches("[A-Za-z ]+")) {
                txtName.getStyleClass().add("invalid");

            }
        });
    }

    private void loadAllStudents() {
        Connection connection = DbConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();

            ResultSet rst = stm.executeQuery("SELECT  *FROM Students");

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");

                Student student = new Student(id, name, address);
                tblStudentDetails.getItems().add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    @FXML
    void btnNewStudentOnAction(ActionEvent event) {
        txtName.clear();
        txtAddress.clear();
        tblStudentDetails.getSelectionModel().clearSelection();
        txtName.requestFocus();
        idNumberGenerator();
        txtName.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");

    }

    private void idNumberGenerator() {
        ObservableList<Student> studentList = tblStudentDetails.getItems();
        if (studentList.isEmpty()) {
            txtId.setText("S" + String.format("%03d", 1));
        } else {
            String id = tblStudentDetails.getItems().get(studentList.size() - 1).getId();
            int lastThreeDigits = Integer.parseInt(id.substring(id.length() - 3));
            int newId = lastThreeDigits + 1;
            txtId.setText("S" + String.format("%03d", newId));
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        txtName.getStyleClass().remove("invalid");
        if (!isDataValid()) return;

            Connection connection = DbConnection.getInstance().getConnection();

            try {
                connection.setAutoCommit(false);
                if (!(tblStudentDetails.getSelectionModel().selectedItemProperty() == null)) {

                    PreparedStatement stm = connection.prepareStatement("INSERT INTO Students(id, name,address) VALUES (?,?,?)");

                    stm.setString(1, txtId.getText());
                    stm.setString(2, txtName.getText());
                    stm.setString(3, txtAddress.getText());
                    stm.executeUpdate();

                    Student newStudent = new Student(txtId.getText(), txtName.getText(), txtAddress.getText());
                    connection.commit();
                    tblStudentDetails.getItems().add(newStudent);
                    btnNewStudent.fire();
                }else {

                }

            } catch (Throwable e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.out.println("failed to save student");
            } finally {
                try {
                    DbConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

    }

    private boolean isDataValid() {
        boolean dataValid = true;
        String name = txtName.getText().strip();
        String address = txtAddress.getText().strip();

        if (!address.matches("[A-Za-z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add("invalid");
            dataValid = false;
        }

        if (!name.matches("[A-Za-z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add("invalid");
            dataValid = false;
        }
        return dataValid;
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Student selectedStudent = tblStudentDetails.getSelectionModel().getSelectedItem();
        Connection connection = DbConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement stmStudent = connection.prepareStatement("DELETE  FROM Students WHERE id=?");

            stmStudent.setString(1, selectedStudent.getId());
            stmStudent.executeUpdate();
            connection.commit();
            tblStudentDetails.getItems().remove(selectedStudent);
            if (tblStudentDetails.getItems().isEmpty()) btnNewStudent.fire();

          txtId.clear();
          txtName.clear();
          txtAddress.clear();
          txtName.getStyleClass().remove("invalid");
          txtAddress.getStyleClass().remove("invalid");

        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            System.out.println("fail to delete student");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void tblStudentDetailsOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            btnDelete.fire();
        }
    }

}
