import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Module 2 (Aqsa): GUI panel for snippet organization and search.
 *
 * It only calls SnippetManager methods; all search logic stays in SnippetManager.
 * Layout:
 *   top    -> search box, search type, Search / Show All buttons
 *   left   -> categories (tags)
 *   centre -> results list (top) and content area (bottom)
 *   bottom -> Add, Save Content, Remove buttons and a status line
 *
 * Because this is a JPanel, it can be placed inside the team's main window.
 */
public class SnippetPanel extends JPanel {

    private final SnippetManager manager;

    private final DefaultListModel<String> categoryModel = new DefaultListModel<>();
    private final JList<String> categoryList = new JList<>(categoryModel);

    private final DefaultListModel<Snippet> resultModel = new DefaultListModel<>();
    private final JList<Snippet> resultList = new JList<>(resultModel);

    private final JTextField searchField = new JTextField(22);
    private final JComboBox<String> searchType =
            new JComboBox<>(new String[]{"Keyword", "Tag", "Language", "Title"});
    private final JTextArea contentArea = new JTextArea();
    private final JLabel status = new JLabel(" ");

    public SnippetPanel(SnippetManager manager) {
        this.manager = manager;
        buildUI();
        refreshCategories();
        showResults(manager.listAll(), "All snippets");
    }

    // ---------------------------------------------------------------- UI

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ----- top: search bar -----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchType);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        top.add(searchBtn);
        top.add(showAllBtn);
        add(top, BorderLayout.NORTH);

        // ----- left: categories -----
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane catScroll = new JScrollPane(categoryList);
        catScroll.setBorder(BorderFactory.createTitledBorder("Categories (tags)"));
        catScroll.setPreferredSize(new Dimension(160, 100));
        add(catScroll, BorderLayout.WEST);

        // ----- centre: results + content -----
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Snippet) {
                    Snippet s = (Snippet) value;
                    setText(s.getTitle() + "   [" + s.getLanguage() + "]   " + s.getTags());
                }
                return this;
            }
        });
        JScrollPane resultScroll = new JScrollPane(resultList);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Snippets"));

        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("Content (you can edit and save)"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultScroll, contentScroll);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        // ----- bottom: buttons + status -----
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton addBtn = new JButton("Add Snippet");
        JButton saveBtn = new JButton("Save Content");
        JButton removeBtn = new JButton("Remove");
        buttons.add(addBtn);
        buttons.add(saveBtn);
        buttons.add(removeBtn);
        bottom.add(buttons, BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // ----- actions -----
        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());          // Enter key
        showAllBtn.addActionListener(e -> {
            categoryList.clearSelection();
            showResults(manager.listAll(), "All snippets");
        });

        categoryList.addListSelectionListener(e -> {
            String tag = categoryList.getSelectedValue();
            if (!e.getValueIsAdjusting() && tag != null) {
                showResults(manager.searchByTag(tag), "Tag #" + tag);
            }
        });

        resultList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Snippet s = resultList.getSelectedValue();
            contentArea.setText(s == null ? "" : s.getContent());
            contentArea.setCaretPosition(0);
        });

        addBtn.addActionListener(e -> showAddDialog());
        saveBtn.addActionListener(e -> saveContent());
        removeBtn.addActionListener(e -> removeSelected());
    }

    // ------------------------------------------------------------ actions

    private void doSearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            showResults(manager.listAll(), "All snippets");
            return;
        }
        String type = (String) searchType.getSelectedItem();
        List<Snippet> found;
        switch (type) {
            case "Tag":
                found = manager.searchByTag(text);
                break;
            case "Language":
                found = manager.searchByLanguage(text);
                break;
            case "Title":
                Snippet s = manager.findByTitle(text);
                found = new ArrayList<>();
                if (s != null) found.add(s);
                break;
            default:
                found = manager.searchByKeyword(text);
        }
        categoryList.clearSelection();
        showResults(found, type + " search for \"" + text + "\"");
    }

    private void showAddDialog() {
        JTextField titleField = new JTextField(25);
        JTextField languageField = new JTextField(25);
        JTextField tagsField = new JTextField(25);
        JTextArea contentField = new JTextArea(8, 25);
        contentField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        String[] labels = {"Title:", "Language:", "Tags (comma separated):", "Content:"};
        Component[] fields = {titleField, languageField, tagsField, new JScrollPane(contentField)};
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i; c.weightx = 0;
            form.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.weightx = 1;
            form.add(fields[i], c);
        }

        int choice = JOptionPane.showConfirmDialog(this, form, "Add Snippet",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String title = titleField.getText().trim();
        String language = languageField.getText().trim();
        if (title.isEmpty() || language.isEmpty()) {
            warn("Title and language cannot be empty.");
            return;
        }
        if (manager.findByTitle(title) != null) {
            warn("A snippet with this title already exists. Please use a different title.");
            return;
        }
        List<String> tags = new ArrayList<>();
        for (String t : tagsField.getText().split(",")) {
            if (!t.trim().isEmpty()) tags.add(t.trim());
        }
        manager.addSnippet(new Snippet(title, language, contentField.getText(), tags));
        refreshCategories();
        categoryList.clearSelection();
        showResults(manager.listAll(), "Added \"" + title + "\"");
    }

    private void saveContent() {
        Snippet s = resultList.getSelectedValue();
        if (s == null) {
            warn("Select a snippet first.");
            return;
        }
        s.setContent(contentArea.getText());
        status.setText("Content saved for \"" + s.getTitle() + "\" (in memory).");
    }

    private void removeSelected() {
        Snippet s = resultList.getSelectedValue();
        if (s == null) {
            warn("Select a snippet first.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Remove \"" + s.getTitle() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        manager.removeSnippet(s.getTitle());
        refreshCategories();
        categoryList.clearSelection();
        showResults(manager.listAll(), "Removed \"" + s.getTitle() + "\"");
    }

    // ------------------------------------------------------------ helpers

    private void showResults(List<Snippet> list, String description) {
        resultModel.clear();
        for (Snippet s : list) resultModel.addElement(s);
        contentArea.setText("");
        status.setText(description + ": " + list.size() + " snippet(s)");
    }

    private void refreshCategories() {
        categoryModel.clear();
        for (String tag : manager.listCategories()) categoryModel.addElement(tag);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // Other modules can call this after loading data from a file (Module 3)
    public void reload() {
        refreshCategories();
        showResults(manager.listAll(), "All snippets");
    }
}
