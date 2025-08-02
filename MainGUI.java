import javax.swing.SwingUtilities;
import ui.StudentManagementUI;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentManagementUI().setVisible(true);
        });
    }
}
