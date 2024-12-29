package com.maybank.customerApp;

import com.maybank.customerApp.ui.CustomerAddressFrame;

import javax.swing.*;

public class CustomerAddressApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new CustomerAddressFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}