import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

class VaultException extends Exception {
    public VaultException(String message) { super(message); }
}

class StorageManager {
    private String filePath;

    public StorageManager(String filePath) {
        this.filePath = filePath;
    }

    public void saveVault(SnippetVault vault) throws IOException, VaultException {
        if (vault == null) throw new VaultException("Snippet vault is null.");
        File file = new File(filePath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Snippet s : vault.getAllSnippets()) {
                writer.println(s.toFileFormat());
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

public class StorageEngineGUI extends JFrame {
    private static final String STORAGE_FILE = "dev_vault_data.txt";
    private SnippetVault vault;
    private StorageManager storageManager;

    private JTable statusTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextArea logArea;

    public StorageEngineGUI(SnippetVault sharedVault) {
        this.vault = sharedVault;
        this.storageManager = new StorageManager(STORAGE_FILE);

        try {
            storageManager.loadVault(vault);
        } catch (Exception ignored) {}

        setTitle("💾 Dev-Vault: Storage & Disk Persistence Engine");
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
        JButton saveDiskBtn = new JButton("💾 Force Save to Disk");
        JButton reloadDiskBtn = new JButton("📂 Reload from Disk");
        JButton refreshTableBtn = new JButton("🔄 Refresh Table View");
        JButton clearStorageBtn = new JButton("🗑 Clear Storage File");

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

        // Listeners
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
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all storage data?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new File(STORAGE_FILE).delete();
                vault.clearVault();
                refreshStorageData();
                appendLog("WARNING: Storage cleared.");
            }
        });
    }

    public void refreshStorageData() {
        tableModel.setRowCount(0);
        List<Snippet> list = vault.getAllSnippets();
        for (Snippet s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getTitle(), s.getLanguage(), String.join(", ", s.getTags())});
        }
    }

    private void appendLog(String msg) {
        logArea.append("\n" + msg);
    }
}