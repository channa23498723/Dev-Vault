import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.TreeSet;

public class SnippetManager {
    private SnippetVault vault;

    public SnippetManager(SnippetVault vault) {
        this.vault = vault;
    }

    public void addSnippet(Snippet snippet) {
        if (snippet != null) {
            vault.addSnippet(snippet);
        }
    }

    public List<Snippet> listAll() {
        return vault.getAllSnippets();
    }

    public Snippet findByTitle(String title) {
        if (title == null) return null;
        for (Snippet s : vault.getAllSnippets()) {
            if (s.getTitle().equalsIgnoreCase(title.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Snippet> searchByLanguage(String lang) {
        if (lang == null) return listAll();
        String lower = lang.toLowerCase().trim();
        return vault.getAllSnippets().stream()
                .filter(s -> s.getLanguage() != null && s.getLanguage().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Snippet> searchByKeyword(String keyword) {
        if (keyword == null) return listAll();
        String lower = keyword.toLowerCase().trim();
        return vault.getAllSnippets().stream()
                .filter(s -> (s.getTitle() != null && s.getTitle().toLowerCase().contains(lower)) || 
                             (s.getContent() != null && s.getContent().toLowerCase().contains(lower)) ||
                             (s.getLanguage() != null && s.getLanguage().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public List<Snippet> searchByTag(String tag) {
        if (tag == null) return listAll();
        String normTag = Snippet.normalizeTag(tag);
        return vault.getAllSnippets().stream()
                .filter(s -> s.getTags() != null && s.getTags().contains(normTag))
                .collect(Collectors.toList());
    }

    public boolean removeSnippet(String titleOrId) {
        if (titleOrId == null) return false;
        List<Snippet> list = vault.getAllSnippets();
        Snippet target = null;
        for (Snippet s : list) {
            if (s.getTitle().equalsIgnoreCase(titleOrId.trim()) || s.getId().equals(titleOrId.trim())) {
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