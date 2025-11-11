import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int health;
    private int locationId;
    private List<String> inventory;

    public Player(String name, int startLocation) {
        this.name = name;
        this.health = 100;
        this.locationId = startLocation;
        this.inventory = new ArrayList<>();
    }

    // getters and setters
    public String getName() { return name; }
    public int getHealth() { return health; }
    public void changeHealth(int delta) { this.health = Math.max(0, this.health + delta); }
    public int getLocationId() { return locationId; }
    public void setLocationId(int id) { this.locationId = id; }
    public List<String> getInventory() { return inventory; }
    public void addItem(String item) { if (item != null && !item.isEmpty()) inventory.add(item); }
    public boolean isAlive() { return health > 0; }

    @Override
    public String toString() {
        return String.format("Player{name='%s', health=%d, location=%d, inventory=%s}",
                name, health, locationId, inventory.toString());
    }
}
