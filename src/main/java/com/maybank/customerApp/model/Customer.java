package com.maybank.customerApp.model;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int customerId;
    private String shortName;
    private String fullName;
    private String city;
    private String postalCode;

    public Customer() {
    }

    public Customer(String shortName, String fullName, String city, String postalCode) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.city = city;
        this.postalCode = postalCode;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    private List<Address> addresses = new ArrayList<>();

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
