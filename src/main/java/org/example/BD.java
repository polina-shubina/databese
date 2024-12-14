package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class BD {
    private List<Book> books;
    private String file;

    public BD(String file){
        this.file = file;
        this.books = new ArrayList<>();
        try {
            load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Book> load(String file) throws IOException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 5) {
                    Book b = new Book(
                            Integer.parseInt(parts[0]), // ID
                            parts[1],                   // Name
                            parts[2],                   // Author
                            Double.parseDouble(parts[3]), // Price
                            Boolean.parseBoolean(parts[4]) // Availability
                    );
                    books.add(b);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Создан новый.");
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return books;
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(file, "Cp1251")) {
            for (Book book : books) {
                String b = book.getId() + ", " + book.getName() + ", " + book.getAuthor() + ", "
                        + book.getPrice() + ", " + book.isAvailability();
                writer.println(b);
            }
        } catch (IOException e) {
            System.out.println("Возникла ошибка при записи файла: " + e.getMessage());
        }
    }

    public void createBackup(String backupFilePath) throws IOException {
        File originalFile = new File(file);
        File backupFile = new File(backupFilePath);

        try (FileInputStream fis = new FileInputStream(originalFile);
             FileOutputStream fos = new FileOutputStream(backupFile)) {
            StandardCharsets.UTF_8.newEncoder();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    public void restoreFromBackup(String backupFilePath) throws IOException {
        File originalFile = new File(file);
        File backupFile = new File(backupFilePath);

        try (FileInputStream fis = new FileInputStream(backupFile);
             FileOutputStream fos = new FileOutputStream(originalFile)) {
            StandardCharsets.UTF_8.newEncoder();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    public int addBook(Book book) throws Exception {
        try{
            if (!books.stream().anyMatch(b -> b.getId() == book.getId())) {
                String b = Integer.toString(book.getId()) + ", " + book.getName() + ", " + book.getAuthor() + ", " + Double.toString(book.getPrice()) + ", " + Boolean.toString(book.isAvailability());
                books.add(book);
                save();
                return 1;
            }
            else{
                return 0;
            }
        } catch (Exception e){
            System.out.println("Возникла ошибка");
        }
        return -1;
    }

    public void deleteBook(int id) {
        books.removeIf(book -> book.getId() == id);
        save();
    }

    public List<Book> search(String field, String value) {
        List<Book> results = new ArrayList<>();
        int flag = 0;
        while (flag == 0) {
            for (Book book : books) {
                switch (field.toLowerCase()) {
                    case "id":
                        if (book.getName().equalsIgnoreCase(value)) {
                            results.add(book);
                            flag = 1;
                        }
                    case "name":
                        if (book.getName().equalsIgnoreCase(value)) {
                            results.add(book);
                        }
                        break;
                    case "author":
                        if (book.getAuthor().equalsIgnoreCase(value)) {
                            results.add(book);
                        }
                        break;
                    case "price":
                        String str = Double.toString(book.getPrice());
                        if (str.equalsIgnoreCase(value)) {
                            results.add(book);
                        }
                        break;
                    case "availability":
                        String str1 = Boolean.toString(book.isAvailability());
                        if (str1.equalsIgnoreCase(value)) {
                            results.add(book);
                        }
                        break;
                    default:
                        System.out.println("No results");
                }
            }
        }
        return results;
    }

    public void editBook(int id, Book updated) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                books.set(i, updated);
                break;
            }
        }
        save();
    }

    public void clear() throws FileNotFoundException {
        books.clear();
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
        save();
    }

    public List<Book> getBooks() {
        return books;
    }

    public String getFile() {
        return file;
    }

    private Book readBook(RandomAccessFile f) throws IOException {
        int id = f.readInt();
        String name = f.readUTF();
        String author = f.readUTF();
        double price = f.readDouble();
        boolean availability = f.readBoolean();

        return new Book(id, name, author, price, availability);
    }

    public void Excel(String excelFilePath) throws IOException {
        try (RandomAccessFile f = new RandomAccessFile(file, "r");
             Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Database");

            // Создание заголовков столбцов
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name","Author", "Price", "Availability"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Чтение записей из базы данных
            int rowNum = 1;
            while (f.getFilePointer() < f.length()) {
                Book book = readBook(f);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getName());
                row.createCell(2).setCellValue(book.getAuthor());
                row.createCell(3).setCellValue(book.getPrice());
                row.createCell(4).setCellValue(book.isAvailability());
            }

            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }
        }
    }
}

