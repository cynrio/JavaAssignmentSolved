package com.maybank.customerApp.service;

import com.maybank.customerApp.model.Customer;
import com.maybank.customerApp.exception.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private final Connection connection;

    public CustomerService(Connection connection) {
        this.connection = connection;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers ORDER BY customer_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }

        return customers;
    }

    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = """
            SELECT * FROM Customers 
            WHERE LOWER(short_name) LIKE ? 
            OR LOWER(full_name) LIKE ? 
            OR LOWER(postal_code) LIKE ?
            ORDER BY customer_id
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }

        return customers;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String query = "SELECT * FROM Customers WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }

        return null;
    }

    public void createCustomer(Customer customer) throws SQLException, ValidationException {
        validateCustomer(customer);

        String query = """
            INSERT INTO Customers (short_name, full_name, city, postal_code) 
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getShortName());
            stmt.setString(2, customer.getFullName());
            stmt.setString(3, customer.getCity());
            stmt.setString(4, customer.getPostalCode());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    public void updateCustomer(Customer customer) throws SQLException, ValidationException {
        validateCustomer(customer);

        String query = """
            UPDATE Customers 
            SET short_name = ?, full_name = ?, city = ?, postal_code = ? 
            WHERE customer_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customer.getShortName());
            stmt.setString(2, customer.getFullName());
            stmt.setString(3, customer.getCity());
            stmt.setString(4, customer.getPostalCode());
            stmt.setInt(5, customer.getCustomerId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        // First delete all addresses associated with the customer
        String deleteAddressesQuery = "DELETE FROM Addresses WHERE customer_id = ?";
        String deleteCustomerQuery = "DELETE FROM Customers WHERE customer_id = ?";

        connection.setAutoCommit(false);
        try {
            // Delete addresses first
            try (PreparedStatement stmt = connection.prepareStatement(deleteAddressesQuery)) {
                stmt.setInt(1, customerId);
                stmt.executeUpdate();
            }

            // Then delete the customer
            try (PreparedStatement stmt = connection.prepareStatement(deleteCustomerQuery)) {
                stmt.setInt(1, customerId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Deleting customer failed, no rows affected.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setShortName(rs.getString("short_name"));
        customer.setFullName(rs.getString("full_name"));
        customer.setCity(rs.getString("city"));
        customer.setPostalCode(rs.getString("postal_code"));
        return customer;
    }

    private void validateCustomer(Customer customer) throws ValidationException {
        if (customer == null) {
            throw new ValidationException("Customer cannot be null");
        }
        if (isNullOrEmpty(customer.getShortName())) {
            throw new ValidationException("Short name is required");
        }
        if (isNullOrEmpty(customer.getFullName())) {
            throw new ValidationException("Full name is required");
        }
        if (isNullOrEmpty(customer.getPostalCode())) {
            throw new ValidationException("Postal code is required");
        }
        if (!isValidPostalCode(customer.getPostalCode())) {
            throw new ValidationException("Invalid postal code format");
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("\\d{5}(-\\d{4})?");
    }
}