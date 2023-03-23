package lk.ijse.dep10.teamproject2.model;

public class Customer {

    String id;
    String name;
    String Address;

    public Customer(String id, String name, String address) {
        this.id = id;
        this.name = name;
        Address = address;
    }

    public Customer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
