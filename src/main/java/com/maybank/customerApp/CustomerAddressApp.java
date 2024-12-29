package com.maybank.customerApp;

import com.maybank.customerApp.config.DBConnection;
import com.maybank.customerApp.model.Address;
import com.maybank.customerApp.model.Customer;
import com.maybank.customerApp.service.CustomerService;
import com.maybank.customerApp.service.AddressService;
import com.maybank.customerApp.exception.ValidationException;
import com.maybank.customerApp.ui.AddressDialog;
import com.maybank.customerApp.ui.CustomerDialog;
import com.maybank.customerApp.util.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerAddressApp extends JFrame {
    private static final String TITLE = "Customer Address Management";
    private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
    private static final String[] TABLE_COLUMNS = {
            "Customer ID", "Short Name", "Full Name", "City", "Postal Code"
    };

    private final CustomerService customerService;
    private final AddressService addressService;
    private final DefaultTableModel customerTableModel;
    private final JTable customerTable;
    private final JTextArea addressTextArea;
    private final JButton addButton;
    private final JButton modifyButton;
    private final JButton deleteButton;
    private final JTextField searchField;
    private final JButton newCustomerButton;


    public CustomerAddressApp() {
        super(TITLE);

        // Initialize services
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            customerService = new CustomerService(conn);
            addressService = new AddressService(conn);
        } catch (SQLException e) {
            handleError("Database Connection Error", e);
            throw new RuntimeException(e);
        }

        // Setup main window
        setSize(WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Initialize components
        customerTableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        customerTable = createCustomerTable();
        addressTextArea = createAddressTextArea();
        searchField = createSearchField();

        // Initialize buttons
        newCustomerButton = new JButton("New Customer");
        addButton = new JButton("Add Address");
        modifyButton = new JButton("Modify Address");
        deleteButton = new JButton("Delete Address");

        // Setup UI
        setupUI();
        setupListeners();
        loadCustomers("");
    }

    private JTable createCustomerTable() {
        JTable table = new JTable(customerTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private JTextArea createAddressTextArea() {
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }


    private JTextField createSearchField() {
        JTextField field = new JTextField(20);
        field.setToolTipText("Search by name or postal code");
        return field;
    }

    private void setupUI() {
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Address Panel
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));
        addressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JLabel addressLabel = new JLabel("Address Details");
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressPanel.add(addressLabel);
        addressPanel.add(Box.createVerticalStrut(10));

        addressPanel.add(new JScrollPane(addressTextArea));
        addressPanel.add(Box.createVerticalStrut(10));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        addressPanel.add(buttonPanel);

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(newCustomerButton);

        // Add panels to frame
        add(bottomLeftPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);
        add(addressPanel, BorderLayout.EAST);
    }

    private void setupListeners() {
        // Search functionality
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(
                e -> SwingUtilities.invokeLater(() -> loadCustomers(searchField.getText()))
        ));

        // Table selection
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomerAddresses();
            }
        });

        // Button actions
        addButton.addActionListener(e -> handleAddAddress());
        modifyButton.addActionListener(e -> handleModifyAddress());
        deleteButton.addActionListener(e -> handleDeleteAddress());
        newCustomerButton.addActionListener(e -> handleNewCustomer());
    }

    private void loadCustomers(String searchTerm) {
        try {
            List<Customer> customers;

            if (searchTerm.isBlank())
                customers = customerService.getAllCustomers();
            else
                customers = customerService.searchCustomers(searchTerm);

            customerTableModel.setRowCount(0);
            for (Customer customer : customers) {
                customerTableModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getShortName(),
                        customer.getFullName(),
                        customer.getCity(),
                        customer.getPostalCode()
                });
            }
        } catch (SQLException e) {
            handleError("Error Loading Customers", e);
        }
    }

    private void loadSelectedCustomerAddresses() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) customerTable.getValueAt(selectedRow, 0);
            try {
                List<Address> addresses = addressService.getAddressesForCustomer(customerId);
                displayAddresses(addresses);

                // only 3 addresses can be added for the selected customer
                addButton.setEnabled(addresses.size() < 3);
            } catch (SQLException e) {
                handleError("Error Loading Addresses", e);
            }
        }
    }

    // Handle adding a new customer
    private void handleNewCustomer() {
        try {
            CustomerDialog customerDialog = new CustomerDialog(this, "New Customer");
            Optional<Customer> result = customerDialog.showDialog();

            if (result.isPresent()) {
                Customer newCustomer = result.get();
                customerService.createCustomer(newCustomer);
                loadCustomers(""); // Refresh the table
                JOptionPane.showMessageDialog(this, "Customer added successfully");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            handleError("Error Adding Customer", e);
        }
    }

    private void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        for (Address address : addresses) {
            sb.append("Address ID: ").append(address.getAddressId()).append("\n");
            sb.append(address.getAddressLine1()).append("\n");
            if (address.getAddressLine2() != null && !address.getAddressLine2().isEmpty()) {
                sb.append(address.getAddressLine2()).append("\n");
            }
            if (address.getAddressLine3() != null && !address.getAddressLine3().isEmpty()) {
                sb.append(address.getAddressLine3()).append("\n");
            }
            sb.append("\n");
        }
        if (sb.toString().isBlank())
            addressTextArea.setText("No address found for the selected customer! You can add one.");
        else
            addressTextArea.setText(sb.toString());
    }

    private void handleAddAddress() {
        try {
            int selectedRow = getSelectedCustomerRow();
            int customerId = (int) customerTable.getValueAt(selectedRow, 0);

            AddressDialog dialog = new AddressDialog(this, "Add New Address");
            Optional<Address> result = dialog.showDialog();

            if (result.isPresent()) {
                Address address = result.get();
                addressService.addAddress(customerId, address);
                loadSelectedCustomerAddresses();
                JOptionPane.showMessageDialog(this, "Address added successfully");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            handleError("Error Adding Address", e);
        }
    }

    private void handleModifyAddress() {
        try {
            String addressIdStr = JOptionPane.showInputDialog(this, "Enter Address ID to Modify:");
            if (addressIdStr != null && !addressIdStr.trim().isEmpty()) {
                int addressId = Integer.parseInt(addressIdStr);
                Address existingAddress = addressService.getAddress(addressId);

                if (existingAddress != null) {
                    AddressDialog dialog = new AddressDialog(this, "Modify Address", existingAddress);
                    Optional<Address> result = dialog.showDialog();

                    if (result.isPresent()) {
                        Address updatedAddress = result.get();
                        addressService.updateAddress(addressId, updatedAddress);
                        loadSelectedCustomerAddresses();
                        JOptionPane.showMessageDialog(this, "Address updated successfully");
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Address ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            handleError("Error Modifying Address", e);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDeleteAddress() {
        try {
            getSelectedCustomerRow(); // Verify customer is selected
            String addressIdStr = JOptionPane.showInputDialog(this, "Enter Address ID to Delete:");

            if (addressIdStr != null && !addressIdStr.trim().isEmpty()) {
                int addressId = Integer.parseInt(addressIdStr);

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this address?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    addressService.deleteAddress(addressId);
                    loadSelectedCustomerAddresses();
                    JOptionPane.showMessageDialog(this, "Address deleted successfully");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Address ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | ValidationException e) {
            handleError("Error Deleting Address", e);
        }
    }

    private int getSelectedCustomerRow() throws ValidationException {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            throw new ValidationException("Please select a customer first");
        }
        return selectedRow;
    }

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                message + ": " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new CustomerAddressApp().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}