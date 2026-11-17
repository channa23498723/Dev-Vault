
import javax.swing.*;
import java.awt.*;

public class TextEditorGUI extends JFrame {

    private TextEditorCore editor;
    private JTextArea textArea;

    public TextEditorGUI() {

        editor = new TextEditorCore();

        setTitle("My Text Editor");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Heading
        JLabel heading = new JLabel(
                "MY TEXT EDITOR",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Text Area
        textArea = new JTextArea();

        textArea.setFont(new Font("Arial", Font.PLAIN, 17));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(
                new GridLayout(2, 2, 10, 10)
        );

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

        // Write Button
        write.addActionListener(e -> {

            String text = JOptionPane.showInputDialog(
                    this, "Enter your text:"
            );

            if (text != null && !text.isEmpty()) {

                editor.write(text);

                refreshText();
            }
        });

        // Undo Button
        undo.addActionListener(e -> {

            editor.undoOperation();

            refreshText();
        });

        // Redo Button
        redo.addActionListener(e -> {

            editor.redoOperation();

            refreshText();
        });

        // Delete Button
        delete.addActionListener(e -> {

            String input = JOptionPane.showInputDialog(
                    this, "Enter number of characters to delete:"
            );

            if (input != null) {

                try {

                    int count = Integer.parseInt(input);

                    if (count <= 0) {
                        JOptionPane.showMessageDialog(
                                this, "Enter a positive number!"
                        );
                        return;
                    }

                    if (count > editor.display().length()) {
                        count = editor.display().length();
                    }

                    editor.delete(count);

                    refreshText();

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            this, "Invalid number!"
                    );
                }
            }
        });

        // Display Button
        display.addActionListener(e -> {

            refreshText();

            JOptionPane.showMessageDialog(
                    this,
                    editor.display().isEmpty()
                            ? "No text to display."
                            : editor.display(),
                    "Current Text",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        add(mainPanel);

        setVisible(true);
    }

    // Update GUI from backend
    private void refreshText() {
        textArea.setText(editor.display());
    }
}
