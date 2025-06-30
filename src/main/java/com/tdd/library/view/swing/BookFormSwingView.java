package com.tdd.library.view.swing;

import com.tdd.library.model.Book;
import com.tdd.library.model.Member;
import com.tdd.library.controller.BookController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookFormSwingView extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JLabel isbnNoteLabel;
    private JComboBox<Member> memberComboBox;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookController bookController;
    private Runnable memberFormOpener;

    private String selectedBookIsbn = null;

    public BookFormSwingView() {
        setTitle("Library - Book Manager");
        setSize(700, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField();
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField();
        formPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        isbnField = new JTextField();
        formPanel.add(isbnField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        isbnNoteLabel = new JLabel("Only 10-digit numeric ISBN is allowed (no hyphens)");
        isbnNoteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        isbnNoteLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(isbnNoteLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Borrowed By:"), gbc);
        gbc.gridx = 1;
        memberComboBox = new JComboBox<>();
        formPanel.add(memberComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(this::addBook);
        buttonPanel.add(addButton);

        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> {
            if (memberFormOpener != null) {
                memberFormOpener.run();
            }
        });
        buttonPanel.add(addMemberButton);

        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN", "Status"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Book List"));

        bookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.getSelectedRow();
                if (row != -1) {
                    titleField.setText((String) tableModel.getValueAt(row, 0));
                    authorField.setText((String) tableModel.getValueAt(row, 1));
                    isbnField.setText((String) tableModel.getValueAt(row, 2));
                    selectedBookIsbn = (String) tableModel.getValueAt(row, 2);
                    isbnField.setEnabled(false);

                    String status = (String) tableModel.getValueAt(row, 3);
                    if (status.startsWith("Borrowed by ")) {
                        String memberName = status.substring("Borrowed by ".length());
                        for (int i = 0; i < memberComboBox.getItemCount(); i++) {
                            Member m = memberComboBox.getItemAt(i);
                            if (m != null && m.getName().equals(memberName)) {
                                memberComboBox.setSelectedIndex(i);
                                return;
                            }
                        }
                    } else {
                        memberComboBox.setSelectedIndex(0);
                    }
                }
            }
        });

        JPanel deletePanel = new JPanel(new BorderLayout());

        JLabel noteLabel = new JLabel("Please select a book from the list to delete or update.");
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        deletePanel.add(noteLabel, BorderLayout.NORTH);

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(this::deleteBook);
        bottomButtonPanel.add(deleteButton);

        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(this::updateBook);
        bottomButtonPanel.add(updateButton);

        deletePanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(deletePanel, BorderLayout.SOUTH);
    }

    public void setBookController(BookController controller) {
        this.bookController = controller;
        controller.loadBooks();
        controller.refreshMembers();
    }

    public void setMemberFormOpener(Runnable opener) {
        this.memberFormOpener = opener;
    }

    public void populateMembers(List<Member> members) {
        memberComboBox.removeAllItems();
        memberComboBox.addItem(new Member(-1, "Please select a member", ""));
        for (Member m : members) {
            memberComboBox.addItem(m);
        }
        memberComboBox.setSelectedIndex(0);
    }

    public void displayBooks(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            String status = (book.getBorrowedBy() != null)
                    ? "Borrowed by " + book.getBorrowedBy().getName()
                    : "Available";
            tableModel.addRow(new Object[]{
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    status
            });
        }
    }

    private void addBook(ActionEvent e) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        Member borrower = (Member) memberComboBox.getSelectedItem();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!isValidIsbn(isbn)) {
            JOptionPane.showMessageDialog(this,
                    "ISBN must be exactly 10 numeric digits (no hyphens).",
                    "Invalid ISBN",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (bookController.getBookByIsbn(isbn) != null) {
            JOptionPane.showMessageDialog(this,
                    "A book with this ISBN already exists.",
                    "Duplicate ISBN",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (borrower != null && borrower.getId() == -1) {
            borrower = null;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setBorrowedBy(borrower);

        bookController.saveBook(book);
        clearForm();
    }

    private void updateBook(ActionEvent e) {
        if (selectedBookIsbn == null) {
            JOptionPane.showMessageDialog(this, "Please select a book to update.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        Member borrower = (Member) memberComboBox.getSelectedItem();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields (except ISBN which is fixed).");
            return;
        }

        if (borrower != null && borrower.getId() == -1) {
            borrower = null;
        }

        Book existingBook = bookController.getBookByIsbn(selectedBookIsbn);
        if (existingBook == null) {
            JOptionPane.showMessageDialog(this, "Selected book could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        existingBook.setTitle(title);
        existingBook.setAuthor(author);
        existingBook.setBorrowedBy(borrower);

        bookController.updateBook(existingBook);
        clearForm();
    }

    private void deleteBook(ActionEvent e) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the book \"" + title + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String isbn = (String) tableModel.getValueAt(selectedRow, 2);
            bookController.deleteBookByIsbn(isbn);
            clearForm();
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        isbnField.setEnabled(true);
        selectedBookIsbn = null;

        if (memberComboBox.getItemCount() > 0) {
            memberComboBox.setSelectedIndex(0);
        }
    }

    private boolean isValidIsbn(String isbn) {
        return isbn.matches("\\d{10}");
    }

    public BookController getBookController() {
        return bookController;
    }
}
