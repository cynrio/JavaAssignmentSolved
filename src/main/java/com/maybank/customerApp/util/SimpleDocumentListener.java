package com.maybank.customerApp.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class SimpleDocumentListener implements DocumentListener {
    private final Consumer<DocumentEvent> listener;

    public SimpleDocumentListener(Consumer<DocumentEvent> listener) {
        this.listener = listener;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        listener.accept(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        listener.accept(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        listener.accept(e);
    }
}