package com.maybank.customerApp.service;

import com.maybank.customerApp.model.Address;
import com.maybank.customerApp.exception.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressService {
    private final Connection connection;

    public AddressService(Connection connection) {
        this.connection = connection;
    }

    public List<Address> getAddressesForCustomer(int customerId) throws SQLException {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM addresses WHERE customer_id = ? ORDER BY address_id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        }

        return addresses;
    }

    public Address getAddress(int addressId) throws SQLException {
        String query = "SELECT * FROM addresses WHERE address_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, addressId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        }

        return null;
    }

    public void addAddress(int customerId, Address address) throws SQLException, ValidationException {
        validateAddress(address);

        String query = """
            INSERT INTO addresses (customer_id, address_line1, address_line2, address_line3) 
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, address.getAddressLine1());
            stmt.setString(3, address.getAddressLine2());
            stmt.setString(4, address.getAddressLine3());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    address.setAddressId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        }
    }

    public void updateAddress(int addressId, Address address) throws SQLException, ValidationException {
        validateAddress(address);

        String query = """
            UPDATE addresses 
            SET address_line1 = ?, address_line2 = ?, address_line3 = ? 
            WHERE address_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, address.getAddressLine1());
            stmt.setString(2, address.getAddressLine2());
            stmt.setString(3, address.getAddressLine3());
            stmt.setInt(4, addressId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating address failed, no rows affected.");
            }
        }
    }

    public void deleteAddress(int addressId) throws SQLException {
        String query = "DELETE FROM addresses WHERE address_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, addressId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting address failed, no rows affected.");
            }
        }
    }

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressId(rs.getInt("address_id"));
        address.setAddressLine1(rs.getString("address_line1"));
        address.setAddressLine2(rs.getString("address_line2"));
        address.setAddressLine3(rs.getString("address_line3"));
        return address;
    }

    private void validateAddress(Address address) throws ValidationException {
        if (address == null) {
            throw new ValidationException("Address cannot be null");
        }
    }
}