import java.io.Serializable;
import java.util.*;

public class SnippetVault implements Serializable {
    private List<Snippet> snippets;

    public SnippetVault() {
        this.snippets = new ArrayList<>();
    }

    public synchronized void addSnippet(Snippet s) {
        if (s != null) {
            snippets.add(s);
        }
    }

    public synchronized boolean removeSnippet(String id) {
        return snippets.removeIf(s -> s.getId().equals(id));
    }

    public synchronized List<Snippet> getAllSnippets() {
        return new ArrayList<>(snippets);
    }

    public synchronized void clearVault() {
        snippets.clear();
    }
}