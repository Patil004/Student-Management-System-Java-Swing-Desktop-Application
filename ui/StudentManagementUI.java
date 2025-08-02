package ui;

import dao.StudentDAO;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Student;

public class StudentManagementUI extends JFrame {

    private JTextField nameField, emailField, courseField, feeField;
    private JTextField searchField;
    private JComboBox<String> searchComboBox;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    private StudentDAO dao;

    public StudentManagementUI() {
        dao = new StudentDAO();

        setTitle("Student Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Student"));

        // Input fields
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Course:"));
        courseField = new JTextField();
        inputPanel.add(courseField);

        inputPanel.add(new JLabel("Fee:"));
        feeField = new JTextField();
        inputPanel.add(feeField);

        // Buttons for Add, Update, Delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        inputPanel.add(buttonPanel);

        // Leave last cell empty to keep layout grid correct
        inputPanel.add(new JLabel(""));

        add(inputPanel, BorderLayout.NORTH);

        // Table for students
        tableModel = new DefaultTableModel(new String[]{"Name", "Email", "Course", "Fee"}, 0) {
            // Make table cells non-editable directly
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Search & Export Panel
        JPanel searchExportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchExportPanel.setBorder(BorderFactory.createTitledBorder("Search & Export"));

        searchComboBox = new JComboBox<>(new String[]{"Name", "Email", "Course"});
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        JButton exportButton = new JButton("Export CSV");

        searchExportPanel.add(new JLabel("Search by:"));
        searchExportPanel.add(searchComboBox);
        searchExportPanel.add(searchField);
        searchExportPanel.add(searchButton);
        searchExportPanel.add(resetButton);
        searchExportPanel.add(exportButton);

        add(searchExportPanel, BorderLayout.SOUTH);

        // Load all students initially
        refreshStudentTable(dao.getAllStudents());

        // Button Listeners

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());

        searchButton.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> resetSearch());

        exportButton.addActionListener(e -> exportToCSV());

        // Table row click listener to load data into input fields
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadStudentIntoFields(selectedRow);
                }
            }
        });
    }

    private void addStudent() {
        try {
            Student s = getStudentFromInputFields();
            dao.addStudent(s);
            refreshStudentTable(dao.getAllStudents());
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.");
            return;
        }
        try {
            Student s = getStudentFromInputFields();
            dao.updateStudent(selectedRow, s);
            refreshStudentTable(dao.getAllStudents());
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Student updated successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete selected student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteStudent(selectedRow);
            refreshStudentTable(dao.getAllStudents());
            clearInputFields();
        }
    }

    private void performSearch() {
        String field = searchComboBox.getSelectedItem().toString();
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter search text.");
            return;
        }
        List<Student> filtered = dao.searchStudents(field, text);
        refreshStudentTable(filtered);
    }

    private void resetSearch() {
        searchField.setText("");
        refreshStudentTable(dao.getAllStudents());
    }

    private void exportToCSV() {
        List<Student> studentsToExport = dao.getAllStudents();
        if (studentsToExport.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to export.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV file");
        fileChooser.setSelectedFile(new java.io.File("students.csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.println("Name,Email,Course,Fee");
                for (Student s : studentsToExport) {
                    writer.printf("%s,%s,%s,%d%n",
                            escapeCsv(s.getName()),
                            escapeCsv(s.getEmail()),
                            escapeCsv(s.getCourse()),
                            s.getFee());
                }
                JOptionPane.showMessageDialog(this, "Exported to " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String escapeCsv(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            field = "\"" + field + "\"";
        }
        return field;
    }

    private void refreshStudentTable(List<Student> studentList) {
        // Clear existing rows
        tableModel.setRowCount(0);
        for (Student s : studentList) {
            tableModel.addRow(new Object[]{s.getName(), s.getEmail(), s.getCourse(), s.getFee()});
        }
    }

    private void loadStudentIntoFields(int row) {
        nameField.setText((String) tableModel.getValueAt(row, 0));
        emailField.setText((String) tableModel.getValueAt(row, 1));
        courseField.setText((String) tableModel.getValueAt(row, 2));
        feeField.setText(tableModel.getValueAt(row, 3).toString());
    }

    private Student getStudentFromInputFields() throws Exception {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseField.getText().trim();
        String feeText = feeField.getText().replace(",", "").trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty() || feeText.isEmpty()) {
            throw new Exception("All fields are required.");
        }

        int fee;
        try {
            fee = Integer.parseInt(feeText);
            if (fee < 0) throw new Exception("Fee cannot be negative.");
        } catch (NumberFormatException e) {
            throw new Exception("Invalid fee input.");
        }

        return new Student(name, email, course, fee);
    }

    private void clearInputFields() {
        nameField.setText("");
        emailField.setText("");
        courseField.setText("");
        feeField.setText("");
        studentTable.clearSelection();
    }
}
