package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GraphInt extends JFrame {
    private final BD database;

    private final JTextField idField = new JTextField( );
    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JCheckBox availabilField = new JCheckBox("Available");
    private final JTextArea outputArea;

    public GraphInt() {
        database = new BD("book.txt");

        setTitle("Book manager");
        setSize(4000, 2000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5,5));

        panel.add(new JLabel("ID:"));
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        panel.add(new JLabel("Availability"));
        panel.add(availabilField);

        Dimension buttonSize = new Dimension(120, 30);

        JButton addButton = new JButton("Add Book");
        addButton.setPreferredSize(buttonSize);
        addButton.addActionListener(new AddBookAction());

        JButton deleteButton = new JButton("Delete Book");
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.addActionListener(new DeleteBookAction());

        JButton clearButton = new JButton("Clear Database");
        clearButton.setPreferredSize(buttonSize);
        clearButton.addActionListener(new ClearDatabaseAction());

        JButton displayButton = new JButton("Display Books");
        displayButton.setPreferredSize(buttonSize);
        displayButton.addActionListener(new DisplayBooksAction());

        JButton openButton = new JButton("Open backup file");
        openButton.setPreferredSize(buttonSize);
        openButton.addActionListener(new OpenAction());

        JButton searchButton = new JButton("Search Book");
        searchButton.setPreferredSize(buttonSize);
        searchButton.addActionListener(new SearchAction());

        JButton backupButton = new JButton("Create backup file");
        backupButton.setPreferredSize(buttonSize);
        backupButton.addActionListener(new BackupAction());

        JButton editButton = new JButton("Edit book");
        editButton.setPreferredSize(buttonSize);
        editButton.addActionListener(new EditAction());

        JButton exportButton = new JButton("Export to XLSX");
        exportButton.setPreferredSize(buttonSize);
        exportButton.addActionListener(new ExportAction());


        outputArea = new JTextArea();
        Dimension areaSize = new Dimension(750, 200);
        outputArea.setPreferredSize(areaSize);
        //setVisible(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(openButton);
        buttonPanel.add(backupButton);
        buttonPanel.add(exportButton);

        add(panel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.WEST);

        setVisible(true);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        authorField.setText("");
        priceField.setText("");
        availabilField.setText("");
    }

    private class OpenAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                database.load("backup.txt");
                outputArea.append("The data is taken from a file" + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            clearFields();
        }
    }

    private class AddBookAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String author = authorField.getText();
                double price = Double.parseDouble(priceField.getText());
                boolean availability;
                if (availabilField.isSelected()) {
                    availability = true;
                } else {
                    availability = false;
                }
                Book book = new Book(id, name, author, price, availability);
                if (database.addBook(book) == 1) {
                    outputArea.append("Added: " + book.toString() + "\n");
                }
                else{
                    outputArea.append("Indexes should not be repeated\n");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            clearFields();
        }
    }

    private class EditAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            int id = Integer.parseInt(idField.getText());
            boolean av;
            if (availabilField.isSelected()){
                av = true;
            }
            else{
                av = false;
            }
            Book b = new Book(id, nameField.getText(), authorField.getText(), Double.parseDouble(priceField.getText()), av);
            database.editBook(id, b);
            outputArea.append("Book was edited\n");
        }
    }
    private class DeleteBookAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int flag = 0;
                for (Book book : database.getBooks()) {
                    if (!idField.getText().trim().isEmpty()) {
                        flag = 1;
                        String value = idField.getText();
                        if (Integer.toString(book.getId()).equalsIgnoreCase(value)) {
                            int id = Integer.parseInt(idField.getText());
                            outputArea.append("Deleted the book with ID: " + book.getId() + "\n");
                            database.deleteBook(id);
                            break;
                        }
                    }
                    if (!nameField.getText().trim().isEmpty()) {
                        flag = 1;
                        String value = nameField.getText();
                        if (book.getName().equalsIgnoreCase(value)) {
                            int id = book.getId();
                            outputArea.append("Deleted books with name: " + value + "\n");
                            database.deleteBook(id);
                        }
                    }
                    if (!authorField.getText().trim().isEmpty()) {
                        flag = 1;
                        String value = authorField.getText();
                        if (book.getAuthor().equalsIgnoreCase(value)) {
                            outputArea.append("Deleted books with author: " + book.getAuthor() + "\n");
                            database.deleteBook(book.getId());
                        }
                    }
                    if (!priceField.getText().trim().isEmpty()) {
                        flag = 1;
                        String value = priceField.getText();
                        if (Double.toString(book.getPrice()).equalsIgnoreCase(value)) {
                            outputArea.append("Deleted books with price: " + book.getPrice() + "\n");
                            database.deleteBook(book.getId());
                        }
                    }
                    if (flag == 0) {
                        if (availabilField.isSelected()) {
                            boolean value = true;
                            if (book.isAvailability() == value) {
                                outputArea.append("Deleted book with availability: true" + "\n");
                                database.deleteBook(book.getId());
                            }
                        } else {
                            boolean value = false;
                            if (book.isAvailability() == value) {
                                outputArea.append("Deleted book with availability: false" + "\n");
                                database.deleteBook(book.getId());
                            }
                        }
                    }
                }
                clearFields();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            clearFields();
        }
    }

    private class ClearDatabaseAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.setText("");
            try {
                database.clear();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            outputArea.append("Database cleared.\n");
            clearFields();
        }
    }

    private class DisplayBooksAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.setText("");
            if (database.getBooks().isEmpty()) {
                outputArea.setText("");
                outputArea.append("Nothing found.\n");
            } else {
                outputArea.append("id   name    author    price    availability\n");
                for (Book book : database.getBooks()) {
                    outputArea.append(book.toString() + "\n");
                }
            }
            clearFields();
        }
    }

    private class BackupAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                database.createBackup("backup.txt");
                outputArea.setText("Backup file was created");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.setText("");
            try {
                int flag = 0;
                if (!idField.getText().trim().isEmpty()) {
                    flag = 1;
                    String value = idField.getText();
                    for (Book book : database.getBooks()) {
                        if (Integer.toString(book.getId()).equalsIgnoreCase(value)) {
                            outputArea.append(book.toString() + "\n");
                            break;
                        }
                    }
                }
                if (!nameField.getText().trim().isEmpty()) {
                    flag = 1;
                    String value = nameField.getText();
                    for (Book book : database.getBooks()) {
                        if (book.getName().equalsIgnoreCase(value)) {
                            outputArea.append(book.toString() + "\n");
                        }
                    }
                }
                if (!authorField.getText().trim().isEmpty()) {
                    flag = 1;
                    String value = authorField.getText();
                    for (Book book : database.getBooks()) {
                        if (book.getAuthor().equalsIgnoreCase(value)) {
                            outputArea.append(book.toString() + "\n");
                        }
                    }
                }
                if (!priceField.getText().trim().isEmpty()) {
                    flag = 1;
                    String value = priceField.getText();
                    for (Book book : database.getBooks()) {
                        if (Double.toString(book.getPrice()).equalsIgnoreCase(value)) {
                            outputArea.append(book.toString() + "\n");
                        }
                    }
                }
                if(flag == 0){
                    if (availabilField.isSelected()) {
                        boolean value = true;
                        for (Book book : database.getBooks()) {
                            if (book.isAvailability() == value) {
                                outputArea.append(book.toString() + "\n");
                            }
                        }
                    } else {
                        boolean value = false;
                        for (Book book : database.getBooks()) {
                            if (book.isAvailability() == value) {
                                outputArea.append(book.toString() + "\n");
                            }
                        }
                    }
                }
                clearFields();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class ExportAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            try {
                String exportPath = "C:\\Users\\Полина\\OneDrive\\Рабочий стол\\bd.xlsx";
                database.Excel(exportPath);
                outputArea.setText("Database exported to: " + exportPath);
            } catch (IOException ex) {
                outputArea.setText("Error exporting to XLSX: " + ex.getMessage());
            }
        }
    }
}
