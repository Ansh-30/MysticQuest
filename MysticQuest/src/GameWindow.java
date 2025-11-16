import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private JTextArea storyArea;
    private JPanel choicesPanel;
    private JLabel healthLabel;
    private JLabel itemsLabel;
    private JLabel titleLabel;

    private Game game;
    private Player player;

    private JButton saveBtn, loadBtn, invBtn, statsBtn, exitBtn;

    // Neon Theme Colors
    private final Color BG_COLOR = new Color(20, 20, 30);
    private final Color FG_COLOR = new Color(0, 255, 200);
    private final Color ACCENT_COLOR = new Color(100, 100, 150);
    private final Color BUTTON_BG = new Color(40, 40, 60);
    private final Color BUTTON_FG = new Color(200, 255, 200);

    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 28);
    private final Font TEXT_FONT = new Font("Courier New", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Serif", Font.PLAIN, 14);

    public GameWindow() {

        // ---------- Backend ----------
        game = new Game();

        String name = JOptionPane.showInputDialog(this, "Enter your hero name:");
        if (name == null || name.trim().isEmpty()) name = "Hero";

        player = new Player(name, 1);

        // ---------- Window ----------
        setTitle("Mystic Quest - Futuristic Adventure");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_COLOR);

        // ---------- Title ----------
        titleLabel = new JLabel("Mystic Quest", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(FG_COLOR);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(BG_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // ---------- Story Box ----------
        storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setFont(TEXT_FONT);
        storyArea.setForeground(FG_COLOR);
        storyArea.setBackground(new Color(10, 10, 20));
        storyArea.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));

        JScrollPane scrollPane = new JScrollPane(storyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Stats panel ----------
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBackground(BG_COLOR);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                "HUD", 0, 0, BUTTON_FONT, FG_COLOR));

        healthLabel = new JLabel("Health: ");
        healthLabel.setForeground(FG_COLOR);
        healthLabel.setFont(TEXT_FONT);

        itemsLabel = new JLabel("Items: ");
        itemsLabel.setForeground(FG_COLOR);
        itemsLabel.setFont(TEXT_FONT);

        statsPanel.add(healthLabel);
        statsPanel.add(itemsLabel);
        add(statsPanel, BorderLayout.EAST);

        // ---------- Choices Panel ----------
        choicesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        choicesPanel.setBackground(BG_COLOR);
        choicesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                "Choices", 0, 0, BUTTON_FONT, FG_COLOR));
        add(choicesPanel, BorderLayout.SOUTH);

        // ---------- Menu Buttons Panel ----------
        JPanel menuPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        menuPanel.setBackground(BG_COLOR);
        menuPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                "Menu", 0, 0, BUTTON_FONT, FG_COLOR));

        saveBtn = createButton("Save");
        loadBtn = createButton("Load");
        invBtn = createButton("Inventory");
        statsBtn = createButton("Stats");
        exitBtn = createButton("Exit");

        menuPanel.add(saveBtn);
        menuPanel.add(loadBtn);
        menuPanel.add(invBtn);
        menuPanel.add(statsBtn);
        menuPanel.add(exitBtn);
        add(menuPanel, BorderLayout.WEST);

        // ---------- Menu Actions ----------
        saveBtn.addActionListener(e -> {
            SaveLoad.savePlayer(player);
            JOptionPane.showMessageDialog(this, "Game Saved!");
        });

        loadBtn.addActionListener(e -> {
            Player loaded = SaveLoad.loadPlayer();
            if (loaded != null) {
                player = loaded;
                JOptionPane.showMessageDialog(this, "Game Loaded!");
                updateScene();
            }
        });

        invBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "Inventory:\n" + player.getInventory()
        ));

        statsBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Name: " + player.getName() +
                        "\nHealth: " + player.getHealth() +
                        "\nLocation: " + player.getLocationId() +
                        "\nItems: " + player.getInventory()
        ));

        exitBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(
                    this, "Exit without saving?", "Exit", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) System.exit(0);
        });

        // ---------- Load First Scene ----------
        updateScene();
    }

    // ---------- Neon Button ----------
    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BUTTON_BG);
        b.setForeground(BUTTON_FG);
        b.setFont(BUTTON_FONT);
        b.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------- Scene Update ----------
    private void updateScene() {

        Scene scene = game.getScene(player.getLocationId());

        titleLabel.setText(scene.getTitle());
        storyArea.setText(scene.getDescription());

        healthLabel.setText("Health: " + player.getHealth());
        itemsLabel.setText("Items: " + player.getInventory());

        choicesPanel.removeAll();

        // END SCENE
        if (scene.isEnding()) {
            JButton endBtn = createButton("THE END — CLOSE GAME");
            endBtn.addActionListener(e -> System.exit(0));
            choicesPanel.add(endBtn);
            refreshChoices();
            return;
        }

        // NORMAL CHOICES
        for (Choice c : scene.getChoices()) {
            JButton btn = createButton(c.getDescription());

            btn.addActionListener(e -> {
                if (c.getHealthChange() != 0)
                    player.changeHealth(c.getHealthChange());

                if (c.getItemReward() != null)
                    player.addItem(c.getItemReward());

                player.setLocationId(c.getNextSceneId());

                if (!player.isAlive()) {
                    storyArea.setText("💀 You have fallen.\nGame Over.");
                    choicesPanel.removeAll();
                    refreshChoices();
                    return;
                }

                updateScene();
            });

            choicesPanel.add(btn);
        }

        refreshChoices();
    }

    private void refreshChoices() {
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {

        // ❗ REMOVE Windows Look & Feel (it forces WHITE buttons)
        // Default Metal Look & Feel supports custom button colors perfectly.

        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}
