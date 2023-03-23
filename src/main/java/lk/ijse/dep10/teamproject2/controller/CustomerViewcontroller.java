package lk.ijse.dep10.teamproject2.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.teamproject2.db.DBConnection;
import lk.ijse.dep10.teamproject2.model.Customer;

import java.sql.*;

public class CustomerViewcontroller {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewCustomer;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Customer> tblSummary;

    @FXML
    private TextField txtAdderess;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;


    public void initialize() {

        tblSummary.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Id"));
        tblSummary.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("Name"));
        tblSummary.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("Address"));

        tblSummary.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            btnDelete.setDisable(current == null);
            if (current == null) return;
            txtId.setText(current.getId());
            txtName.setText(current.getName());
            txtAdderess.setText(current.getAddress());

        });

loadAllStudents();
    }

    private void loadAllStudents(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT *FROM Customer");

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String adress = rst.getString("address");

                Customer customer = new Customer(id, name, adress);
                tblSummary.getItems().add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isDataValidate() {
        boolean dataValid = true;

        if (!txtName.getText().matches("[A-Za-z ]+")) {
            txtName.selectAll();
            dataValid = false;
        }

        if (!txtAdderess.getText().matches("[A-Za-z ]+")) {
            txtAdderess.selectAll();
            dataValid = false;
        }

        return dataValid;
    }


    private void generateId() {

        ObservableList<Customer> studentList = tblSummary.getItems();
        if (studentList.isEmpty()){
            txtId.setText("C"+String.format("%03d", 1));
        }else {
            String id = tblSummary.getItems().get(studentList.size() - 1).getId();
            int lastThreeDigits = Integer.parseInt(id.substring(id.length() - 3));
            int newId = lastThreeDigits + 1;
            txtId.setText("C" + String.format("%03d", newId));
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

        Connection connection = DBConnection.getInstance().getConnection();
        Customer selectedItem = tblSummary.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String id = selectedItem.getId();
            tblSummary.getItems().remove(selectedItem);


            PreparedStatement prd3 = null;
            try {
                prd3 = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
                prd3.setString(1, id);
                prd3.executeUpdate();
                txtAdderess.clear();
                txtName.clear();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }


    }

    @FXML
    void btnNewCustomerOnAction(ActionEvent event) {
        generateId();

        txtName.clear();
        txtName.requestFocus();
        txtAdderess.clear();
        btnDelete.setDisable(true);

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        if (!isDataValidate()) return;

        String id = txtId.getText();
        String name = txtName.getText();
        String address = txtAdderess.getText();

        Customer selectedItem = tblSummary.getSelectionModel().getSelectedItem();


            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = null;
            try {

                if(selectedItem==null) {
                    stm = connection.prepareStatement("INSERT INTO Customer(id, name,address) VALUES (?,?,?)");
                    stm.setString(1, txtId.getText());
                    stm.setString(2, txtName.getText());
                    stm.setString(3, txtAdderess.getText());
                    stm.executeUpdate();

                    Customer customer = new Customer(id, name, address);
                    tblSummary.getItems().add(customer);
                }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
