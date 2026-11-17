import javax.swing.*;
import java.util.Arrays;

/**
 * Runs Module 2's GUI on its own, with a few sample snippets.
 * In the final project, the team's main window will add SnippetPanel instead.
 */
public class SnippetGUIDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnippetManager manager = new SnippetManager();
            manager.addSnippet(new Snippet("Binary Search", "java",
                    "int mid = (low + high) / 2;", Arrays.asList("#java", "#dsa")));
            manager.addSnippet(new Snippet("Select Query", "sql",
                    "SELECT * FROM users;", Arrays.asList("#sql")));
            manager.addSnippet(new Snippet("Stack Example", "java",
                    "Stack<Integer> st = new Stack<>();", Arrays.asList("#java", "#dsa")));
            manager.addSnippet(new Snippet("Join Query", "sql",
                    "SELECT * FROM a JOIN b ON a.id = b.id;", Arrays.asList("#sql", "#database")));

            JFrame frame = new JFrame("Dev-Vault: Snippet Organizer (Module 2)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new SnippetPanel(manager));
            frame.setSize(900, 560);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
