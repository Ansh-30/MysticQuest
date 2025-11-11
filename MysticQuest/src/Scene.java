import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private String description;
    private List<Choice> choices;
    private boolean isEnding;

    public Scene(int id, String title, String description, boolean isEnding) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.choices = new ArrayList<>();
        this.isEnding = isEnding;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Choice> getChoices() { return choices; }
    public boolean isEnding() { return isEnding; }

    public void addChoice(Choice c) { choices.add(c); }
}
