package com.maybank.customerApp;

import com.maybank.customerApp.config.DBConnection;
import com.maybank.customerApp.model.Address;
import com.maybank.customerApp.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class CustomerAddressApp {
    private final JFrame frame;
    private final JTable customerTable;
    private final DefaultTableModel customerTableModel;
    private final JTextArea addressTextArea;
    private final JButton addButton;
    private final JButton modifyButton;
    private final JButton deleteButton;
    private final JTextField postalCodeField;

    private Connection conn;
    private final List<Customer> customers = new ArrayList<>();

    public CustomerAddressApp() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Customer Address Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Customer Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        customerTableModel = new DefaultTableModel(new String[]{"Customer ID", "Short Name", "Full Name", "City", "Postal Code"}, 0);
        customerTable = new JTable(customerTableModel);
        tablePanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Address Display Panel
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));

        addressTextArea = new JTextArea(10, 40);
        addressTextArea.setEditable(false);
        postalCodeField = new JTextField(20);
        postalCodeField.setToolTipText("Postal Code (Required)");

        addButton = new JButton("Add Address");
        modifyButton = new JButton("Modify Address");
        deleteButton = new JButton("Delete Address");

        addressPanel.add(new JLabel("Address Details:"));
        addressPanel.add(new JScrollPane(addressTextArea));
        addressPanel.add(new JLabel("Postal Code:"));
        addressPanel.add(postalCodeField);
        addressPanel.add(addButton);
        addressPanel.add(modifyButton);
        addressPanel.add(deleteButton);

        frame.add(tablePanel, BorderLayout.WEST);
        frame.add(addressPanel, BorderLayout.CENTER);

        loadCustomers();
        addListeners();

        frame.setVisible(true);
    }

    // Load all customers into the table
    private void loadCustomers() {
        customers.clear();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customers");
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setShortName(rs.getString("short_name"));
                customer.setFullName(rs.getString("full_name"));
                customer.setCity(rs.getString("city"));
                customer.setPostalCode(rs.getString("postal_code"));

                // Load addresses for the customer
                loadAddressesForCustomer(customer);

                customers.add(customer);
                customerTableModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getShortName(),
                        customer.getFullName(),
                        customer.getCity(),
                        customer.getPostalCode()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load addresses for a specific customer
    private void loadAddressesForCustomer(Customer customer) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Addresses WHERE customer_id = ?")) {
            stmt.setInt(1, customer.getCustomerId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setAddressLine1(rs.getString("address_line1"));
                address.setAddressLine2(rs.getString("address_line2"));
                address.setAddressLine3(rs.getString("address_line3"));
                customer.getAddresses().add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load addresses of the selected customer
    private void loadAddresses(int customerId) {
        Customer selectedCustomer = customers.stream()
                .filter(c -> c.getCustomerId() == customerId)
                .findFirst()
                .orElse(null);

        if (selectedCustomer != null) {
            addressTextArea.setText("");
            for (Address address : selectedCustomer.getAddresses()) {
                addressTextArea.append("Address: " + address.getAddressLine1() + "\n");
                addressTextArea.append(address.getAddressLine2() + "\n");
                addressTextArea.append(address.getAddressLine3() + "\n\n");
            }
        }
    }

    // Add Address Action
    private void addAddress(int customerId) {
        if (validatePostalCode(postalCodeField.getText())) {
            String addressLine1 = JOptionPane.showInputDialog("Enter Address Line 1");
            String addressLine2 = JOptionPane.showInputDialog("Enter Address Line 2");
            String addressLine3 = JOptionPane.showInputDialog("Enter Address Line 3");

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Addresses (customer_id, address_line1, address_line2, address_line3)")) {
                stmt.setInt(1, customerId);
                stmt.setString(2, addressLine1);
                stmt.setString(3, addressLine2);
                stmt.setString(4, addressLine3);
                stmt.executeUpdate();
                loadAddresses(customerId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Modify Address Action
    private void modifyAddress(int customerId, int addressId) {
        String addressLine1 = JOptionPane.showInputDialog("Enter New Address Line 1");
        String addressLine2 = JOptionPane.showInputDialog("Enter New Address Line 2");
        String addressLine3 = JOptionPane.showInputDialog("Enter New Address Line 3");

        try (PreparedStatement stmt = conn.prepareStatement("UPDATE Addresses SET address_line1 = ?, address_line2 = ?, address_line3 = ? WHERE address_id = ? AND customer_id = ?")) {
            stmt.setString(1, addressLine1);
            stmt.setString(2, addressLine2);
            stmt.setString(3, addressLine3);
            stmt.setInt(4, addressId);
            stmt.setInt(5, customerId);
            stmt.executeUpdate();
            loadAddresses(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Address Action
    private void deleteAddress(int customerId, int addressId) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Addresses WHERE address_id = ?")) {
            stmt.setInt(1, addressId);
            stmt.executeUpdate();
            loadAddresses(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Postal Code Validation
    private boolean validatePostalCode(String postalCode) {
        if (postalCode.isEmpty() || postalCode.length() < 5) {
            JOptionPane.showMessageDialog(frame, "Invalid Postal Code");
            return false;
        }
        return true;
    }

    // Add Action Listeners to buttons and table row selection
    private void addListeners() {
        addButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int customerId = (int) customerTable.getValueAt(selectedRow, 0);
                addAddress(customerId);
            }
        });

        modifyButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int customerId = (int) customerTable.getValueAt(selectedRow, 0);
                String addressIdStr = JOptionPane.showInputDialog("Enter Address ID to Modify");
                int addressId = Integer.parseInt(addressIdStr);
                modifyAddress(customerId, addressId);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int customerId = (int) customerTable.getValueAt(selectedRow, 0);
                String addressIdStr = JOptionPane.showInputDialog("Enter Address ID to Delete");
                int addressId = Integer.parseInt(addressIdStr);
                deleteAddress(customerId, addressId);
            }
        });

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int customerId = (int) customerTable.getValueAt(selectedRow, 0);
                loadAddresses(customerId);
            }
        });
    }

    public static void main(String[] args) {
        new CustomerAddressApp();
    }
}
