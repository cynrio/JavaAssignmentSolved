package com.maybank.customerApp.ui;

import com.maybank.customerApp.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class CustomerDialog extends JDialog {
    private final JTextField shortNameField;
    private final JTextField fullNameField;
    private final JTextField cityField;
    private final JTextField postalCodeField;
    private boolean confirmed = false;

    public CustomerDialog(Frame owner, String title) {
        this(owner, title, null);
    }

    public CustomerDialog(Frame owner, String title, Customer customer) {
        super(owner, title, true);

        // Initialize fields
        shortNameField = new JTextField(40);
        fullNameField = new JTextField(40);
        cityField = new JTextField(40);
        postalCodeField = new JTextField(40);

        // If editing existing customer, populate fields
        if (customer != null) {
            shortNameField.setText(customer.getShortName());
            fullNameField.setText(customer.getFullName());
            cityField.setText(customer.getCity());
            postalCodeField.setText(customer.getPostalCode());
        }

        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Short Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Short Name:*"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        fieldsPanel.add(shortNameField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Full Name:*"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fieldsPanel.add(fullNameField, gbc);

        // City
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("City:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        fieldsPanel.add(cityField, gbc);

        // Postal Code
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Postal Code:*"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        fieldsPanel.add(postalCodeField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add note about required field
        JLabel noteLabel = new JLabel("* Required field");
        noteLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));
        add(noteLabel, BorderLayout.NORTH);

        // Set default button
        getRootPane().setDefaultButton(okButton);
    }

    private boolean validateInput() {
        String shortName = shortNameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String postalCode = postalCodeField.getText().trim();

        if (shortName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Short Name is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            shortNameField.requestFocus();
            return false;
        }

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Full Name is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return false;
        }

        if (shortName.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "Short Name cannot exceed 50 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            shortNameField.requestFocus();
            return false;
        }

        if (fullName.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "Full Name cannot exceed 100 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return false;
        }

        if (postalCode.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Postal Code is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return false;
        }

        if(!postalCode.matches("\\d{4,7}")){
            JOptionPane.showMessageDialog(this,
                    "Postal Code can have 4 to 7 digits only.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return false;
        }

        return true;
    }

    public Optional<Customer> showDialog() {
        setVisible(true);

        if (!confirmed) {
            return Optional.empty();
        }

        Customer customer = new Customer();
        customer.setShortName(shortNameField.getText().trim());
        customer.setFullName(fullNameField.getText().trim());
        customer.setCity(cityField.getText().trim());
        customer.setPostalCode(postalCodeField.getText().trim());

        return Optional.of(customer);
    }
}
