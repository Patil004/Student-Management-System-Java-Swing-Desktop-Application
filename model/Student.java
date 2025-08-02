package model;

public class Student {
    private String name;
    private String email;
    private String course;
    private int fee;

    public Student(String name, String email, String course, int fee) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.fee = fee;
    }

    // Getters (optional)
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public int getFee() { return fee; }

   @Override
public String toString() {
    return String.format("Name: %s, Email: %s, Course: %s, Fee: %d", 
                          name, email, course, fee);

    }
}
