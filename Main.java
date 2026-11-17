import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * ============================================================================
 * DEV-VAULT: UNIFIED MASTER APPLICATION LAUNCHER
 * ============================================================================
 * Lead Developer: Anam Channa
 * Architecture: Clean Modular Separation & Shared Vault Synchronization
 * ============================================================================
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Professional High-Contrast Styling
                Color baseWhite = Color.WHITE;
                Color darkText = new Color(30, 35, 45);
                Color panelBg = new Color(245, 247, 250);

                UIManager.put("Panel.background", panelBg);
                UIManager.put("Viewport.background", baseWhite);
                UIManager.put("TextArea.background", baseWhite);
                UIManager.put("TextArea.foreground", darkText);
                UIManager.put("TextField.background", baseWhite);
                UIManager.put("TextField.foreground", darkText);
                UIManager.put("Button.background", baseWhite);
                UIManager.put("Button.foreground", darkText);
                UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 12));

            } catch (Exception ignored) {
            }

            // --- MASTER SHARED VAULT INSTANCE (Ensures 100% Data Synchronization) ---
            SnippetVault sharedVault = new SnippetVault();

            // Preload some default entries
            sharedVault.addSnippet(new Snippet("Binary Search", "java",
                    "int mid = (low + high) / 2;", Arrays.asList("#java", "#dsa")));
            sharedVault.addSnippet(new Snippet("Select Query", "sql",
                    "SELECT * FROM users;", Arrays.asList("#sql")));

            // Master Window Frame
            JFrame mainFrame = new JFrame("Dev-Vault: Integrated Development Workspace (Lead: Anam Channa)");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1250, 750);
            mainFrame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

            // --- TAB 1: Text Editor Core (Direct Typing Enabled - Anam's Logic) ---
            JPanel textEditorTab = createTextEditorTab();
            tabbedPane.addTab("Text Editor Core", textEditorTab);

            // --- TAB 2: Code Snippets & Search ---
            SnippetManager snippetManager = new SnippetManager(sharedVault);
            SnippetPanel snippetPanel = new SnippetPanel(snippetManager);
            tabbedPane.addTab("Code Snippets & Search", snippetPanel);

            // --- TAB 3: Storage Engine & Disk Persistence (Erum's Module) ---
            StorageEngineGUI storageWindow = new StorageEngineGUI(sharedVault);
            storageWindow.dispose();
            tabbedPane.addTab("Storage & Vault Engine", storageWindow.getContentPane());

            mainFrame.add(tabbedPane, BorderLayout.CENTER);

            // Status Bar
            JPanel statusPanel = new JPanel(new BorderLayout());
            statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            statusPanel.setBackground(new Color(30, 35, 45));

            JLabel masterStatus = new JLabel(
                    " Workspace Status: Online | Shared Vault Synchronized | Storage Connected");
            masterStatus.setForeground(Color.WHITE);
            masterStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
            statusPanel.add(masterStatus, BorderLayout.WEST);

            mainFrame.add(statusPanel, BorderLayout.SOUTH);
            mainFrame.setVisible(true);
        });
    }

    /**
     * Creates Tab 1: Text Editor Core with Direct Typing and Stack-based Undo/Redo.
     */
    private static JPanel createTextEditorTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        TextEditorCore editor = new TextEditorCore();

        // Core Text Area for direct typing
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 215)));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Toolbar for Undo, Redo, Delete operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        JButton deleteBtn = new JButton("Delete Chars");
        JButton clearBtn = new JButton("Clear Editor");

        JButton[] buttons = { undoBtn, redoBtn, deleteBtn, clearBtn };
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setForeground(new Color(40, 50, 70));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(53, 116, 240), 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        buttonPanel.add(undoBtn);
        buttonPanel.add(redoBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Live Document Tracking for direct typing
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateEditorState() {
                String text = textArea.getText();
                // Sync text content into editor core stack if needed
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateEditorState();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateEditorState();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateEditorState();
            }
        });

        undoBtn.addActionListener(e -> {
            editor.undoOperation();
            textArea.setText(editor.display());
        });

        redoBtn.addActionListener(e -> {
            editor.redoOperation();
            textArea.setText(editor.display());
        });

        deleteBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(panel, "Enter number of characters to delete:");
            if (input != null) {
                try {
                    int count = Integer.parseInt(input);
                    if (count > 0) {
                        editor.delete(count);
                        textArea.setText(editor.display());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Please enter a valid integer!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearBtn.addActionListener(e -> {
            textArea.setText("");
            // editor clear logic agar zaroori ho
        });
        return panel;
    }
}