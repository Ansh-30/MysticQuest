import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Game {
    private Map<Integer, Scene> scenes = new HashMap<>();
    private Player player;
    private Scanner sc = new Scanner(System.in);

    // 🎨 ANSI colors for fun text
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";

    public Game() {
        setupScenes();
    }

    public void start() {
        printLogo();
        slowPrint( "Welcome, traveler, to the world of Mystoria..." + RESET);
        slowPrint("Your fate will be shaped by your choices.");
        System.out.print("\nEnter your hero name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Hero";

        player = new Player(name, 1);
        slowPrint(GREEN + "Greetings, " + name + "! Your adventure begins...\n" + RESET);
        mainLoop();
    }

    // 🎨 ASCII logo
    private void printLogo() {
        System.out.println(PURPLE +
                "-------------------------------------\n" +
                "         MYSTIC QUEST RPG 🗡️\n" +
                "-------------------------------------" + RESET);
    }

    // 🎭 Dramatic slow print for story narration
    private void slowPrint(String text) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(25); } catch (InterruptedException ignored) {}
        }
        System.out.println();
    }

    private void mainLoop() {
        while (player.isAlive()) {
            Scene scene = scenes.get(player.getLocationId());
            if (scene == null) {
                slowPrint(RED + "You seem lost in time... returning to the village gate." + RESET);
                player.setLocationId(1);
                continue;
            }

            playScene(scene);

            if (scene.isEnding()) {
                slowPrint(YELLOW + "\n✨ Your journey has ended. ✨" + RESET);
                break;
            }

            if (!player.isAlive()) {
                slowPrint(RED + "\n💀 You have perished on your quest." + RESET);
                break;
            }
        }
        slowPrint(CYAN + "\nThank you for playing Mystic Quest!" + RESET);
    }

    private void playScene(Scene scene) {
        System.out.println("\n" + PURPLE + "# " + scene.getTitle() + RESET);
        slowPrint(scene.getDescription());
        System.out.println(YELLOW + "(HP: " + player.getHealth() + ") " + RESET +
                "Type a choice number, or command: " +
                CYAN + "[save] [load] [inventory] [stats] [exit]" + RESET);

        // show choices
        int i = 1;
        for (Choice c : scene.getChoices()) {
            System.out.printf(GREEN + "%d) %s\n" + RESET, i++, c.getDescription());
        }

        System.out.print("> ");
        String input = sc.nextLine().trim().toLowerCase();

        switch (input) {
            case "save": SaveLoad.savePlayer(player); return;
            case "load":
                Player loaded = SaveLoad.loadPlayer();
                if (loaded != null) this.player = loaded;
                return;
            case "inventory":
                System.out.println(CYAN + "Inventory: " + player.getInventory() + RESET);
                return;
            case "stats":
                System.out.println(GREEN + player + RESET);
                return;
            case "exit":
                slowPrint("Exiting game. Progress not saved.");
                System.exit(0);
                return;
        }

        int choiceIndex;
        try {
            choiceIndex = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            slowPrint(RED + "Invalid input. Try again." + RESET);
            return;
        }

        if (choiceIndex < 0 || choiceIndex >= scene.getChoices().size()) {
            slowPrint(RED + "Invalid choice number. Try again." + RESET);
            return;
        }

        Choice chosen = scene.getChoices().get(choiceIndex);
        if (chosen.getHealthChange() != 0) {
            player.changeHealth(chosen.getHealthChange());
            slowPrint((chosen.getHealthChange() > 0 ? GREEN : RED)
                    + "Health " + (chosen.getHealthChange() > 0 ? "increased" : "decreased")
                    + " by " + Math.abs(chosen.getHealthChange())
                    + ". Current HP: " + player.getHealth() + RESET);
        }
        if (chosen.getItemReward() != null) {
            player.addItem(chosen.getItemReward());
            slowPrint(CYAN + "You obtained: " + chosen.getItemReward() + RESET);
        }

        player.setLocationId(chosen.getNextSceneId());
    }

    // 🗺️ Game story setup
    private void setupScenes() {
        Scene s1 = new Scene(1, "The Village Gate",
                "You stand at the ancient gate of Eldoria. The wind whispers of forgotten legends.\n" +
                        "To the north lies the Dark Forest. To the east, a cave glowing faintly with magic.", false);
        s1.addChoice(new Choice("Venture into the Dark Forest 🌲", 2, 0, null));
        s1.addChoice(new Choice("Enter the Mysterious Cave 🕳️", 3, 0, null));
        s1.addChoice(new Choice("Rest at the gate (recover 10 HP)", 1, +10, null));
        scenes.put(1, s1);

        Scene s2 = new Scene(2, "Dark Forest",
                "Tall trees surround you. Shadows move between the branches. A pack of wolves emerges, snarling.", false);
        s2.addChoice(new Choice("Fight the wolves ⚔️", 4, -30, "Wolf Fang"));
        s2.addChoice(new Choice("Run back to the village 🏃", 1, -5, null));
        s2.addChoice(new Choice("Hide and sneak past them 🕵️", 5, -10, null));
        scenes.put(2, s2);

        Scene s3 = new Scene(3, "Mysterious Cave",
                "The cave hums with ancient energy. Strange runes glow on the walls.", false);
        s3.addChoice(new Choice("Explore deeper 🔦", 6, -10, "Glowing Gem"));
        s3.addChoice(new Choice("Take a torch and go back 🔥", 1, 0, "Torch"));
        s3.addChoice(new Choice("Shout to test the echo 📣", 7, -20, null));
        scenes.put(3, s3);

        Scene s4 = new Scene(4, "Aftermath of Battle",
                "You slay the wolves and find a hidden path to an old tower.", false);
        s4.addChoice(new Choice("Approach the Old Tower 🏰", 8, 0, null));
        s4.addChoice(new Choice("Return to the village gate", 1, 0, null));
        scenes.put(4, s4);

        Scene s5 = new Scene(5, "Failed Sneak",
                "An owl hoots loudly, alerting the wolves. You barely escape alive.", false);
        s5.addChoice(new Choice("Head back to the gate 🏃", 1, -5, null));
        scenes.put(5, s5);

        Scene s6 = new Scene(6, "Gem Chamber",
                "You enter a grand chamber. A sleeping stone guardian lies before a radiant gem.", false);
        s6.addChoice(new Choice("Steal the Glowing Gem 💎", 9, 0, "Ancient Gem"));
        s6.addChoice(new Choice("Touch the guardian", 7, -30, null));
        scenes.put(6, s6);

        Scene s7 = new Scene(7, "Guardian Awakened",
                "The guardian roars to life! You must flee before it's too late!", false);
        s7.addChoice(new Choice("Escape to the gate 🏃", 1, -20, null));
        scenes.put(7, s7);

        Scene s8 = new Scene(8, "Old Tower",
                "At the top of the tower, a ghostly knight blocks your way. He demands proof of your worth.", false);
        s8.addChoice(new Choice("Show the Wolf Fang 🐺", 10, 0, null));
        s8.addChoice(new Choice("Show the Ancient Gem 💎", 11, 0, null));
        s8.addChoice(new Choice("Challenge the knight ⚔️", 12, -50, null));
        scenes.put(8, s8);

        Scene s9 = new Scene(9, "Silent Victory",
                "You steal the gem quietly. A hidden passage opens to a chamber of treasures.", true);
        scenes.put(9, s9);

        Scene s10 = new Scene(10, "Proof of Valor",
                "The knight kneels. You have proven your courage. You are named protector of Eldoria.", true);
        scenes.put(10, s10);

        Scene s11 = new Scene(11, "Proof of Wisdom",
                "The gem glows brightly. Peace spreads across the land. You are hailed as a sage.", true);
        scenes.put(11, s11);

        Scene s12 = new Scene(12, "Final Duel",
                "You clash blades with the Cursed Knight. The tower trembles with power.", false);
        s12.addChoice(new Choice("If victorious, claim your destiny ✨", 10, 0, null));
        s12.addChoice(new Choice("If defeated, fall with honor 💀", 13, 0, null));
        scenes.put(12, s12);

        Scene s13 = new Scene(13, "The End — Fallen Hero",
                "Your tale ends in bravery. Your name will be remembered.", true);
        scenes.put(13, s13);
    }
}
