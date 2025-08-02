import dao.StudentDAO;
import java.util.Scanner;
import model.Student;

public class Main {
    public static void main(String[] args) {
        System.out.println("Program started.");  // Debugging start line

        Scanner sc = new Scanner(System.in);
        StudentDAO dao = new StudentDAO();

        while (true) {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter course: ");
                    String course = sc.nextLine();

                    int fee = 0;
                    while (true) {
                        System.out.print("Enter fee: ");
                        String feeInput = sc.nextLine().replace(",", "").trim();
                        try {
                            fee = Integer.parseInt(feeInput);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid fee input. Please enter a numeric value without letters or symbols (you can use commas).");
                        }
                    }

                    Student s = new Student(name, email, course, fee);
                    dao.addStudent(s);
                    break;

                case 2:
                    dao.viewStudents();
                    break;

                case 3:
                    System.out.println("👋 Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
}
