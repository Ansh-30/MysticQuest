import java.io.*;

public class SaveLoad {
    private static final String SAVE_FILE = "player_save.dat";

    public static void savePlayer(Player p) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(p);
            System.out.println("Game saved to " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public static Player loadPlayer() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            System.out.println("No save file found.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof Player) {
                System.out.println("Game loaded from " + SAVE_FILE);
                return (Player) obj;
            } else {
                System.out.println("Save file corrupted.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game: " + e.getMessage());
            return null;
        }
    }
}
