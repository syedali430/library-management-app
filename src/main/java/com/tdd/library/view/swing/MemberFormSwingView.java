package com.tdd.library.view.swing;

import com.tdd.library.controller.MemberController;
import com.tdd.library.model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MemberFormSwingView extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextField emailField;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private MemberController memberController;
    private final Runnable onCloseCallback;

    public MemberFormSwingView(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;

        setTitle("Library - Member Manager");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Member Info"));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("Add Member");
        addButton.addActionListener(this::addMember);
        buttonPanel.add(addButton);

        tableModel = new DefaultTableModel(new String[]{"Name", "Email"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(memberTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Member List"));

        JPanel deletePanel = new JPanel(new BorderLayout());
        JLabel noteLabel = new JLabel("Please select a member from the list to delete.");
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        deletePanel.add(noteLabel, BorderLayout.NORTH);

        JButton deleteButton = new JButton("Delete Member");
        deleteButton.addActionListener(this::deleteMember);
        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButtonPanel.add(deleteButton);
        deletePanel.add(deleteButtonPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(deletePanel, BorderLayout.SOUTH);
    }

    public void setMemberController(MemberController controller) {
        this.memberController = controller;
        controller.loadMembers();
    }

    public void displayMembers(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{m.getName(), m.getEmail()});
        }
    }

    private void addMember(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Member member = new Member();
        member.setName(name);
        member.setEmail(email);

        memberController.saveMember(member);
        clearForm();

        if (onCloseCallback != null) {
            onCloseCallback.run();
        }

        dispose(); 
    }

    private void deleteMember(ActionEvent e) {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = (String) tableModel.getValueAt(selectedRow, 1);
        Member toDelete = memberController.getMemberByEmail(email);

        if (toDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete member: " + toDelete.getName() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                memberController.deleteMember(toDelete);
                clearForm();
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
