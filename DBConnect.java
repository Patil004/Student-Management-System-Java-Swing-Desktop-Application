import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public static Connection connect() {
        try {
            String url = "jdbc:postgresql://localhost:5432/student_db";
            String user = "postgres";
            String password = "ankit2004";

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
