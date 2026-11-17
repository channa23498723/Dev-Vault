import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Snippet implements Serializable {
    private String id;
    private String title;
    private String language;
    private String content;
    private List<String> tags;

    public Snippet(String title, String language, String content, List<String> tags) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.language = language;
        this.content = content;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getContent() { return content; }
    public List<String> getTags() { return tags; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setLanguage(String language) { this.language = language; }
    public void setContent(String content) { this.content = content; }
    
    public void setTags(List<String> tags) { 
        this.tags = tags != null ? tags : new ArrayList<>(); 
    }

    // Tag Normalization Utility
    public static String normalizeTag(String tag) {
        if (tag == null) return "";
        tag = tag.trim().toLowerCase();
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }
        return tag;
    }

    // File Persistence Format Conversion (for Storage Engine)
    public String toFileFormat() {
        return id + "|||" + title + "|||" + language + "|||" + 
               String.join(",", tags) + "|||" + 
               (content != null ? content.replace("\n", "\\n") : "");
    }

    public static Snippet fromFileFormat(String line) {
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