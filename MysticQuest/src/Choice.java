import java.io.Serializable;

public class Choice implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private int nextSceneId;
    private int healthChange;      // positive or negative
    private String itemReward;     // item name (nullable)

    public Choice(String description, int nextSceneId, int healthChange, String itemReward) {
        this.description = description;
        this.nextSceneId = nextSceneId;
        this.healthChange = healthChange;
        this.itemReward = itemReward;
    }

    public String getDescription() { return description; }
    public int getNextSceneId() { return nextSceneId; }
    public int getHealthChange() { return healthChange; }
    public String getItemReward() { return itemReward; }
}
