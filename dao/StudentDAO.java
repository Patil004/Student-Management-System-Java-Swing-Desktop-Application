package dao;

import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentDAO {

    private List<Student> students = new ArrayList<>();

    public void addStudent(Student s) {
        students.add(s);
        System.out.println("✅ Student added: " + s);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public List<Student> searchStudents(String field, String value) {
        String val = value.toLowerCase();
        return students.stream()
                .filter(s -> {
                    switch (field) {
                        case "Name":
                            return s.getName().toLowerCase().contains(val);
                        case "Email":
                            return s.getEmail().toLowerCase().contains(val);
                        case "Course":
                            return s.getCourse().toLowerCase().contains(val);
                        default:
                            return false;
                    }
                }).collect(Collectors.toList());
    }

    // Update student at index using a new Student object
    public void updateStudent(int index, Student newStudent) {
        if (index >= 0 && index < students.size()) {
            students.set(index, newStudent);
            System.out.println("✅ Student updated at index " + index);
        }
    }

    // Delete student at index
    public void deleteStudent(int index) {
        if (index >= 0 && index < students.size()) {
            students.remove(index);
            System.out.println("✅ Student deleted at index " + index);
        }
    }
}
