import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 ============================================================================
 DEV-VAULT: UNIFIED MASTER APPLICATION LAUNCHER
 ============================================================================
 Lead Developer: Anam Channa
 ============================================================================
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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

            SnippetVault sharedVault = new SnippetVault();

            try {
                sharedVault.addSnippet(new Snippet("Binary Search", "java",
                        "int mid = (low + high) / 2;", Arrays.asList("#java", "#dsa")));
                sharedVault.addSnippet(new Snippet("Select Query", "sql",
                        "SELECT * FROM users;", Arrays.asList("#sql")));
                sharedVault.addSnippet(new Snippet("Stack Example", "java",
                        "Stack<Integer> st = new Stack<>();", Arrays.asList("#java", "#dsa")));
                sharedVault.addSnippet(new Snippet("Join Query", "sql",
                        "SELECT * FROM a JOIN b ON a.id = b.id;", Arrays.asList("#sql", "#database")));
            } catch (Exception e) {
                System.err.println("Warning: Failed to load default snippets: " + e.getMessage());
            }

            JFrame mainFrame = new JFrame("Dev-Vault: Integrated Development Workspace (Lead: Anam Channa)");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1250, 750);
            mainFrame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

            TextEditorGUI textEditorWindow = new TextEditorGUI();
            textEditorWindow.dispose();
            tabbedPane.addTab("Text Editor Module", textEditorWindow.getContentPane());

            SnippetManager snippetManager = new SnippetManager(sharedVault);
            SnippetPanel snippetPanel = new SnippetPanel(snippetManager);
            tabbedPane.addTab("Code Snippets & Search", snippetPanel);

            StorageEngineGUI storageWindow = new StorageEngineGUI(sharedVault);
            storageWindow.dispose();
            tabbedPane.addTab("Storage & Vault Engine", storageWindow.getContentPane());

            mainFrame.add(tabbedPane, BorderLayout.CENTER);

            JPanel statusPanel = new JPanel(new BorderLayout());
            statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            statusPanel.setBackground(new Color(30, 35, 45));

            JLabel masterStatus = new JLabel(
                    " Workspace Status: Online | Shared Vault Synchronized | UI Proportions Fixed");
            masterStatus.setForeground(Color.WHITE);
            masterStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
            statusPanel.add(masterStatus, BorderLayout.WEST);

            mainFrame.add(statusPanel, BorderLayout.SOUTH);
            mainFrame.setVisible(true);
        });
    }
}

// ============================================================================
// ALL SUPPORTING CLASSES & MODULES
// ============================================================================

class TextEditorCore {
    private StringBuilder currentText;
    private Stack<String> undoStack;
    private Stack<String> redoStack;

    public TextEditorCore() {
        currentText = new StringBuilder();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void write(String text) {
        if (text == null) return;
        undoStack.push(currentText.toString());
        currentText.append(text);
        redoStack.clear();
    }

    public void undoOperation() {
        if (undoStack.isEmpty()) return;
        redoStack.push(currentText.toString());
        String previousState = undoStack.pop();
        currentText.setLength(0);
        if (previousState != null) currentText.append(previousState);
    }

    public void redoOperation() {
        if (redoStack.isEmpty()) return;
        undoStack.push(currentText.toString());
        String newState = redoStack.pop();
        currentText.setLength(0);
        if (newState != null) currentText.append(newState);
    }

    public void delete(int count) throws IllegalArgumentException {
        if (currentText.length() == 0) {
            throw new IllegalArgumentException("Editor is already empty! No characters to delete.");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Please enter a positive number greater than 0.");
        }
        if (count > currentText.length()) {
            throw new IllegalArgumentException("Cannot delete " + count + " characters. Current text only has " + currentText.length() + " character(s).");
        }

        undoStack.push(currentText.toString());
        currentText.delete(currentText.length() - count, currentText.length());
        redoStack.clear();
    }

    public String display() {
        return currentText.toString();
    }
}

class TextEditorGUI extends JFrame {
    private TextEditorCore editor;
    private JTextArea textArea;

    public TextEditorGUI() {
        editor = new TextEditorCore();

        setTitle("My Text Editor");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("MY TEXT EDITOR", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        mainPanel.add(heading, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 17));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton write = new JButton("Write");
        JButton undo = new JButton("Undo");
        JButton redo = new JButton("Redo");
        JButton delete = new JButton("Delete");
        JButton display = new JButton("Display");

        buttonPanel.add(write);
        buttonPanel.add(undo);
        buttonPanel.add(redo);
        buttonPanel.add(delete);
        buttonPanel.add(display);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        write.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(this, "Enter your text:");
            if (text != null) {
                if (text.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Cannot write empty text!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                editor.write(text);
                refreshText();
            }
        });

        undo.addActionListener(e -> {
            editor.undoOperation();
            refreshText();
        });

        redo.addActionListener(e -> {
            editor.redoOperation();
            refreshText();
        });

        delete.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter number of characters to delete:");
            if (input != null) {
                if (input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Input cannot be blank!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int count = Integer.parseInt(input.trim());
                    editor.delete(count);
                    refreshText();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number format! Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        display.addActionListener(e -> {
            refreshText();
            JOptionPane.showMessageDialog(
                    this,
                    editor.display().isEmpty() ? "No text to display." : editor.display(),
                    "Current Text",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        add(mainPanel);
        setVisible(true);
    }

    private void refreshText() {
        textArea.setText(editor.display());
    }
}

class Snippet implements Serializable {
    private String id;
    private String title;
    private String language;
    private String content;
    private List<String> tags;

    public Snippet(String title, String language, String content, List<String> tags) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title != null ? title.trim() : "Untitled";
        this.language = language != null ? language.trim() : "text";
        this.content = content != null ? content : "";
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getContent() { return content; }
    public List<String> getTags() { return tags; }

    public void setTitle(String title) { this.title = title != null ? title.trim() : ""; }
    public void setLanguage(String language) { this.language = language != null ? language.trim() : ""; }
    public void setContent(String content) { this.content = content != null ? content : ""; }
    
    public void setTags(List<String> tags) { 
        this.tags = tags != null ? tags : new ArrayList<>(); 
    }

    public static String normalizeTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return "";
        tag = tag.trim().toLowerCase();
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }
        return tag;
    }

    public String toFileFormat() {
        return id + "|||" + title + "|||" + language + "|||" + 
               String.join(",", tags) + "|||" + 
               (content != null ? content.replace("\n", "\\n") : "");
    }

    public static Snippet fromFileFormat(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|\\|\\|", -1);
        if (parts.length < 5) return null;
        Snippet s = new Snippet(
            parts[1], 
            parts[2], 
            parts[4].replace("\\n", "\n"), 
            parts[3].isEmpty() ? new ArrayList<>() : Arrays.asList(parts[3].split(","))
        );
        s.id = parts[0];
        return s;
    }
}

class SnippetVault implements Serializable {
    private List<Snippet> snippets;

    public SnippetVault() {
        this.snippets = new ArrayList<>();
    }

    public synchronized void addSnippet(Snippet s) {
        if (s != null) snippets.add(s);
    }

    public synchronized boolean removeSnippet(String id) {
        if (id == null) return false;
        return snippets.removeIf(s -> s.getId().equals(id));
    }

    public synchronized List<Snippet> getAllSnippets() {
        return new ArrayList<>(snippets);
    }

    public synchronized void clearVault() {
        snippets.clear();
    }
}

class SnippetManager {
    private SnippetVault vault;

    public SnippetManager() {
        this.vault = new SnippetVault();
    }

    public SnippetManager(SnippetVault vault) {
        this.vault = vault != null ? vault : new SnippetVault();
    }

    public void addSnippet(Snippet snippet) {
        if (snippet != null) vault.addSnippet(snippet);
    }

    public List<Snippet> listAll() {
        List<Snippet> list = vault.getAllSnippets();
        return list != null ? list : new ArrayList<>();
    }

    public Snippet findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) return null;
        for (Snippet s : vault.getAllSnippets()) {
            if (s.getTitle() != null && s.getTitle().equalsIgnoreCase(title.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Snippet> searchByLanguage(String lang) {
        if (lang == null || lang.trim().isEmpty()) return listAll();
        String lower = lang.toLowerCase().trim();
        return vault.getAllSnippets().stream()
                .filter(s -> s.getLanguage() != null && s.getLanguage().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Snippet> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return listAll();
        String lower = keyword.toLowerCase().trim();
        return vault.getAllSnippets().stream()
                .filter(s -> (s.getTitle() != null && s.getTitle().toLowerCase().contains(lower)) || 
                             (s.getContent() != null && s.getContent().toLowerCase().contains(lower)) ||
                             (s.getLanguage() != null && s.getLanguage().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public List<Snippet> searchByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return listAll();
        String normTag = Snippet.normalizeTag(tag);
        return vault.getAllSnippets().stream()
                .filter(s -> s.getTags() != null && s.getTags().contains(normTag))
                .collect(Collectors.toList());
    }

    public boolean removeSnippet(String titleOrId) {
        if (titleOrId == null || titleOrId.trim().isEmpty()) return false;
        List<Snippet> list = vault.getAllSnippets();
        Snippet target = null;
        for (Snippet s : list) {
            if ((s.getTitle() != null && s.getTitle().equalsIgnoreCase(titleOrId.trim())) || 
                (s.getId() != null && s.getId().equals(titleOrId.trim()))) {
                target = s;
                break;
            }
        }
        if (target != null) {
            return vault.removeSnippet(target.getId());
        }
        return false;
    }

    public Set<String> listCategories() {
        Set<String> categories = new TreeSet<>();
        for (Snippet s : vault.getAllSnippets()) {
            if (s.getTags() != null) {
                categories.addAll(s.getTags());
            }
        }
        return categories;
    }
}

class VaultException extends Exception {
    public VaultException(String message) { super(message); }
}

class StorageManager {
    private String filePath;

    public StorageManager(String filePath) {
        this.filePath = filePath != null ? filePath : "dev_vault_data.txt";
    }

    public void saveVault(SnippetVault vault) throws IOException, VaultException {
        if (vault == null) throw new VaultException("Snippet vault is null.");
        File file = new File(filePath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Snippet s : vault.getAllSnippets()) {
                if (s != null) writer.println(s.toFileFormat());
            }
        } catch (IOException e) {
            throw new IOException("Failed to write to file: " + e.getMessage(), e);
        }
    }

    public void loadVault(SnippetVault vault) throws IOException, VaultException {
        if (vault == null) throw new VaultException("Snippet vault is null.");
        vault.clearVault();
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Snippet s = Snippet.fromFileFormat(line);
                    if (s != null) vault.addSnippet(s);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to read file: " + e.getMessage(), e);
        }
    }
}

class StorageEngineGUI extends JFrame {
    private static final String STORAGE_FILE = "dev_vault_data.txt";
    private SnippetVault vault;
    private StorageManager storageManager;

    private JTable statusTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextArea logArea;

    public StorageEngineGUI(SnippetVault sharedVault) {
        this.vault = sharedVault != null ? sharedVault : new SnippetVault();
        this.storageManager = new StorageManager(STORAGE_FILE);

        try {
            storageManager.loadVault(vault);
        } catch (Exception ignored) {}

        setTitle("Dev-Vault: Storage & Disk Persistence Engine");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initStorageGUI();
        refreshStorageData();
    }

    private void initStorageGUI() {
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton saveDiskBtn = new JButton("Force Save to Disk");
        JButton reloadDiskBtn = new JButton("Reload from Disk");
        JButton refreshTableBtn = new JButton("Refresh Table View");
        JButton clearStorageBtn = new JButton("Clear Storage File");

        topBar.add(saveDiskBtn);
        topBar.add(reloadDiskBtn);
        topBar.add(refreshTableBtn);
        topBar.add(clearStorageBtn);
        add(topBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);

        String[] headers = {"ID", "Snippet Title", "Language", "Tags"};
        tableModel = new DefaultTableModel(headers, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        statusTable = new JTable(tableModel);
        statusTable.setRowHeight(24);
        
        JScrollPane tableScroll = new JScrollPane(statusTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(" Live Vault Records (Synchronized with Snippet Module) "));
        splitPane.setLeftComponent(tableScroll);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setText("--- Storage Diagnostics ---\nTarget File: " + STORAGE_FILE + "\nShared Vault Linked Successfully.\n");
        
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(" Storage Audit Logs "));
        rightPanel.add(logScroll, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" Storage Ready.");
        statusLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
        add(statusLabel, BorderLayout.SOUTH);

        saveDiskBtn.addActionListener(e -> {
            try {
                storageManager.saveVault(vault);
                JOptionPane.showMessageDialog(this, "Vault successfully persisted to disk: " + STORAGE_FILE);
                statusLabel.setText(" Saved to disk at " + new java.util.Date());
                appendLog("INFO: Vault persisted to disk.");
                refreshStorageData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                appendLog("ERROR: " + ex.getMessage());
            }
        });

        reloadDiskBtn.addActionListener(e -> {
            try {
                storageManager.loadVault(vault);
                refreshStorageData();
                JOptionPane.showMessageDialog(this, "Vault reloaded from disk.");
                statusLabel.setText(" Reloaded from disk. Records: " + vault.getAllSnippets().size());
                appendLog("INFO: Vault reloaded.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshTableBtn.addActionListener(e -> {
            refreshStorageData();
            statusLabel.setText(" Table refreshed. Total records: " + vault.getAllSnippets().size());
            appendLog("INFO: Table view manually refreshed.");
        });

        clearStorageBtn.addActionListener(e -> {
            List<Snippet> items = vault.getAllSnippets();
            if (items == null || items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Storage is already empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all storage data?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                File f = new File(STORAGE_FILE);
                if (f.exists()) f.delete();
                vault.clearVault();
                refreshStorageData();
                appendLog("WARNING: Storage cleared.");
            }
        });
    }

    public void refreshStorageData() {
        if (tableModel == null || vault == null) return;
        tableModel.setRowCount(0);
        List<Snippet> list = vault.getAllSnippets();
        if (list != null) {
            for (Snippet s : list) {
                if (s != null) {
                    tableModel.addRow(new Object[]{s.getId(), s.getTitle(), s.getLanguage(), String.join(", ", s.getTags())});
                }
            }
        }
    }

    private void appendLog(String msg) {
        if (logArea != null && msg != null) {
            logArea.append("\n" + msg);
        }
    }
}

class SnippetPanel extends JPanel {
    private SnippetManager manager;
    private DefaultListModel<String> listModel;
    private JList<String> snippetList;
    private JTextArea contentArea;
    private JTextField searchField;
    private JComboBox<String> filterCombo;

    public SnippetPanel(SnippetManager manager) {
        this.manager = manager != null ? manager : new SnippetManager();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(18);
        JButton searchBtn = new JButton("Search");
        filterCombo = new JComboBox<>(new String[]{"All", "Language", "Tag", "Keyword"});
        JButton addBtn = new JButton("Add Snippet");
        JButton deleteBtn = new JButton("Delete Snippet");

        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);

        listModel = new DefaultListModel<>();
        snippetList = new JList<>(listModel);
        snippetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        snippetList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane listScroll = new JScrollPane(snippetList);
        listScroll.setBorder(BorderFactory.createTitledBorder(" Saved Snippets "));
        splitPane.setLeftComponent(listScroll);

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        contentArea.setEditable(false);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder(" Snippet Code Content "));
        splitPane.setRightComponent(contentScroll);

        add(splitPane, BorderLayout.CENTER);

        refreshSnippetList(this.manager.listAll());

        snippetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTitle = snippetList.getSelectedValue();
                if (selectedTitle != null) {
                    Snippet s = this.manager.findByTitle(selectedTitle);
                    if (s != null) {
                        contentArea.setText("Title: " + s.getTitle() + "\nLanguage: " + s.getLanguage() + 
                                            "\nTags: " + String.join(", ", s.getTags()) + 
                                            "\n\n--- Content ---\n" + s.getContent());
                    }
                }
            }
        });

        searchBtn.addActionListener(e -> {
            String query = searchField.getText() != null ? searchField.getText().trim() : "";
            String mode = (String) filterCombo.getSelectedItem();
            List<Snippet> results;
            if (query.isEmpty()) {
                results = this.manager.listAll();
            } else {
                switch (mode != null ? mode : "Keyword") {
                    case "Language": results = this.manager.searchByLanguage(query); break;
                    case "Tag": results = this.manager.searchByTag(query); break;
                    case "Keyword": 
                    default: results = this.manager.searchByKeyword(query); break;
                }
            }
            refreshSnippetList(results);
        });

        // FIXED: Clean Dialog Layout preventing vertical text-field stretching
        addBtn.addActionListener(e -> {
            JTextField titleField = new JTextField(20);
            JTextField langField = new JTextField(20);
            JTextField tagsField = new JTextField(20);
            JTextArea codeArea = new JTextArea(6, 22);
            codeArea.setLineWrap(true);
            codeArea.setWrapStyleWord(true);
            JScrollPane codeScroll = new JScrollPane(codeArea);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            inputPanel.add(new JLabel("Title:"));
            inputPanel.add(Box.createVerticalStrut(4));
            inputPanel.add(titleField);
            inputPanel.add(Box.createVerticalStrut(8));

            inputPanel.add(new JLabel("Language:"));
            inputPanel.add(Box.createVerticalStrut(4));
            inputPanel.add(langField);
            inputPanel.add(Box.createVerticalStrut(8));

            inputPanel.add(new JLabel("Tags (comma separated):"));
            inputPanel.add(Box.createVerticalStrut(4));
            inputPanel.add(tagsField);
            inputPanel.add(Box.createVerticalStrut(8));

            inputPanel.add(new JLabel("Code Content:"));
            inputPanel.add(Box.createVerticalStrut(4));
            inputPanel.add(codeScroll);

            int result = JOptionPane.showConfirmDialog(
                    this, inputPanel, "Add New Snippet", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String title = titleField.getText() != null ? titleField.getText().trim() : "";
                String lang = langField.getText() != null ? langField.getText().trim() : "";
                String content = codeArea.getText() != null ? codeArea.getText() : "";
                
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<String> rawTags = Arrays.asList(tagsField.getText().split(","));
                List<String> normTags = new ArrayList<>();
                for (String t : rawTags) {
                    String norm = Snippet.normalizeTag(t);
                    if (!norm.isEmpty()) normTags.add(norm);
                }

                Snippet newSnippet = new Snippet(title, lang, content, normTags);
                this.manager.addSnippet(newSnippet);
                refreshSnippetList(this.manager.listAll());
                JOptionPane.showMessageDialog(this, "Snippet added successfully!");
            }
        });

        deleteBtn.addActionListener(e -> {
            String selectedTitle = snippetList.getSelectedValue();
            if (selectedTitle != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete snippet '" + selectedTitle + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = this.manager.removeSnippet(selectedTitle);
                    if (success) {
                        refreshSnippetList(this.manager.listAll());
                        contentArea.setText("");
                        JOptionPane.showMessageDialog(this, "Snippet deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting snippet.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a snippet to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void refreshSnippetList(List<Snippet> snippets) {
        if (listModel == null) return;
        listModel.clear();
        if (snippets != null) {
            for (Snippet s : snippets) {
                if (s != null && s.getTitle() != null) {
                    listModel.addElement(s.getTitle());
                }
            }
        }
    }
}
