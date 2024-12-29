package com.maybank.customerApp.ui;

import com.maybank.customerApp.model.Address;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class AddressDialog extends JDialog {
    private final JTextField addressLine1Field;
    private final JTextField addressLine2Field;
    private final JTextField addressLine3Field;
    private boolean confirmed = false;

    public AddressDialog(Frame owner, String title) {
        this(owner, title, null);
    }

    public AddressDialog(Frame owner, String title, Address address) {
        super(owner, title, true);

        // Initialize fields
        addressLine1Field = new JTextField(40);
        addressLine2Field = new JTextField(40);
        addressLine3Field = new JTextField(40);

        // If editing existing address, populate fields
        if (address != null) {
            addressLine1Field.setText(address.getAddressLine1());
            addressLine2Field.setText(address.getAddressLine2());
            addressLine3Field.setText(address.getAddressLine3());
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

        // Address Line 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Address Line 1:*"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        fieldsPanel.add(addressLine1Field, gbc);

        // Address Line 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Address Line 2:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fieldsPanel.add(addressLine2Field, gbc);

        // Address Line 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Address Line 3:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        fieldsPanel.add(addressLine3Field, gbc);

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
        String addressLine1 = addressLine1Field.getText().trim();
        String addressLine2 = addressLine2Field.getText().trim();
        String addressLine3 = addressLine3Field.getText().trim();

        if (addressLine1.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Address Line 1 is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            addressLine1Field.requestFocus();
            return false;
        }

        if (addressLine1.length() > 80) {
            JOptionPane.showMessageDialog(this,
                    "Address Line 1 cannot exceed 80 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            addressLine1Field.requestFocus();
            return false;
        }
        if (addressLine2.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "Address Line 2 cannot exceed 80 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            addressLine1Field.requestFocus();
            return false;
        }
        if (addressLine3.length() > 80) {
            JOptionPane.showMessageDialog(this,
                    "Address Line 3 cannot exceed 80 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            addressLine1Field.requestFocus();
            return false;
        }
        return true;
    }

    public Optional<Address> showDialog() {
        setVisible(true);

        if (!confirmed) {
            return Optional.empty();
        }

        Address address = new Address();
        address.setAddressLine1(addressLine1Field.getText().trim());
        address.setAddressLine2(addressLine2Field.getText().trim());
        address.setAddressLine3(addressLine3Field.getText().trim());

        return Optional.of(address);
    }
}