import java.util.Stack;

public class TextEditorCore {

    private StringBuilder currentText;
    private Stack<String> undoStack;
    private Stack<String> redoStack;

    public TextEditorCore() {
        currentText = new StringBuilder();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void write(String text) {
        undoStack.push(currentText.toString());
        currentText.append(text);
        redoStack.clear();
    }

    public void undoOperation() {
        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(currentText.toString());

        String previousState = undoStack.pop();

        currentText.setLength(0);
        currentText.append(previousState);
    }

    public void redoOperation() {
        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(currentText.toString());

        String newState = redoStack.pop();

        currentText.setLength(0);
        currentText.append(newState);
    }

    public void delete(int count) {

        if (count <= 0 || currentText.length() == 0) {
            return;
        }

        count = Math.min(count, currentText.length());

        undoStack.push(currentText.toString());

        currentText.delete(
                currentText.length() - count,
                currentText.length()
        );

        redoStack.clear();
    }

    public String display() {
        return currentText.toString();
    }
}
