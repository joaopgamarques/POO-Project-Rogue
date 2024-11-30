package pt.iscte.poo.example;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import java.awt.event.KeyEvent;
import java.io.File;

public class Engine implements Observer {

    // Constants defining the grid size and initial game configuration.
    public static final int GRID_HEIGHT = 10;
    public static final int GRID_WIDTH = 10;
    private static final int HEALTHBAR_HEIGHT = 1;
    public static final int INVENTORY_CAPACITY = 3;
    private static final String FIRST_LEVEL = "room0";
    private static final int STARTING_SCORE = 100;

    // Singleton instance to ensure only one engine exists during gameplay.
    private static Engine INSTANCE = null;

    // Core game objects and state management variables.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance();
    private HashMap<String, Level> levels = new HashMap<>();
    private String levelID; // Current level ID.
    private String lastLevelID; // Last level ID.
    private Hero hero; // The main character controlled by the player.
    private int turns; // Total number of turns taken.
    private int score; // Current score in the game.
    private int lastScore; // Score from the previous level.

    // Ensures a single instance of the engine is used.
    public static Engine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Engine();
        }
        return INSTANCE;
    }

    // Private constructor to initialize the GUI and game engine settings.
    private Engine() {
        gui.registerObserver(this);
        gui.setSize(GRID_WIDTH, GRID_HEIGHT + HEALTHBAR_HEIGHT);
        gui.go();
    }

    // Provides access to the map of levels.
    public HashMap<String, Level> getLevels() {
        return levels;
    }

    // Retrieves the ID of the currently active level.
    public String getCurrentLevelID() {
        return levelID;
    }

    // Retrieves the level object corresponding to the current level ID.
    public Level getCurrentLevel() {
        return levels.get(levelID);
    }

    // Sets the active level to the given ID.
    public void setCurrentLevel(String levelID) {
        this.levelID = levelID;
    }

    // Retrieves the number of turns taken in the game so far.
    public int getTurns() {
        return turns;
    }

    // Retrieves the hero object, which represents the main player character.
    public Hero getHero() {
        return hero;
    }

    // Retrieves the current score of the game.
    public int getScore() {
        return score;
    }

    // Updates the score with the provided value.
    public void setScore(int points) {
        score = points;
    }

    // Increases the score by the specified amount.
    public void increaseScore(int points) {
        score += points;
    }

    // Decreases the score by the specified amount.
    public void decreaseScore(int points) {
        score -= points;
    }

    // Retrieves the last score before transitioning to another level.
    public int getLastScore() {
        return lastScore;
    }

    // Updates the last recorded score with the specified value.
    public void setLastScore(int points) {
        lastScore = points;
    }

    // Retrieves the ID of the last level that was played.
    public String getLastLevelID() {
        return lastLevelID;
    }

    // Updates the ID of the last level with the provided value.
    public void setLastLevelID(String lastLevelID) {
        this.lastLevelID = lastLevelID;
    }

    // Creates the levels by reading configuration files in the "rooms" directory.
    private void create() {
        File directory = new File(System.getProperty("user.dir").concat("\\rooms"));
        if (directory.isDirectory() && directory.listFiles() != null) {
            int index = 0;
            for (File file : directory.listFiles()) {
                String levelID = "room" + index++;
                levels.put(levelID, Level.create(file.getAbsolutePath()));
            }
        }
    }

    // Starts the game by creating levels, initializing the hero, and displaying the first level.
    public void start() {
        create();
        setCurrentLevel(FIRST_LEVEL);
        hero = new Hero(Hero.STARTING_POSITION);
        score = STARTING_SCORE;
        lastScore = STARTING_SCORE;
        lastLevelID = FIRST_LEVEL;
        getCurrentLevel().show();
        gui.setStatusMessage("ROGUE Starter Package - Turns: " + turns + " Score: " + score);
        gui.update();
        Level.save();
    }

    // Responds to updates triggered by the GUI observer.
    @Override
    public void update(Observed source) {

        // Retrieves all enemy elements in the current level.
        List<GameElement> opponents = getCurrentLevel().getElementsFilteredBy(element -> element instanceof Enemy);

        // Gets the key pressed by the user.
        int key = ((ImageMatrixGUI) source).keyPressed();

        if (hero.isAlive()) {
            switch (key) {
                case KeyEvent.VK_UP:
                    // Move hero upward and process opponent actions.
                    hero.move(Direction.UP);
                    opponents.forEach(element -> ((Entity) element).move());
                    turns++;
                    score--;
                    break;

                case KeyEvent.VK_DOWN:
                    // Move hero downward and process opponent actions.
                    hero.move(Direction.DOWN);
                    opponents.forEach(element -> ((Entity) element).move());
                    turns++;
                    score--;
                    break;

                case KeyEvent.VK_LEFT:
                    // Move hero to the left and process opponent actions.
                    hero.move(Direction.LEFT);
                    opponents.forEach(element -> ((Entity) element).move());
                    turns++;
                    score--;
                    break;

                case KeyEvent.VK_RIGHT:
                    // Move hero to the right and process opponent actions.
                    hero.move(Direction.RIGHT);
                    opponents.forEach(element -> ((Entity) element).move());
                    turns++;
                    score--;
                    break;

                case KeyEvent.VK_3:
                    // Drop the first item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.DropFirst);
                    break;

                case KeyEvent.VK_2:
                    // Drop the second item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.DropSecond);
                    break;

                case KeyEvent.VK_1:
                    // Drop the third item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.DropThird);
                    break;

                case KeyEvent.VK_E:
                    // Use the first item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.UseFirst);
                    break;

                case KeyEvent.VK_W:
                    // Use the second item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.UseSecond);
                    break;

                case KeyEvent.VK_Q:
                    // Use the third item in the inventory.
                    Inventory.getInstance().select(Inventory.Selection.UseThird);
                    break;

                case KeyEvent.VK_I:
                    // Print items currently in the hero's inventory.
                    hero.getItems().forEach(item -> System.out.println(item.getName() + " " + item.getPosition().toString()));
                    break;

                case KeyEvent.VK_J:
                    // Print all game elements in the current level, except walls.
                    Predicate<GameElement> predicate = element -> !(element instanceof Wall);
                    getCurrentLevel().getElementsFilteredBy(predicate).forEach(element ->
                            System.out.println(element.getName() + " " + element.getPosition().toString()));
                    break;

                default:
                    break;
            }
        }

        // Ensures the score does not drop below zero.
        score = Math.max(score, 0);
        gui.setStatusMessage("ROGUE Starter Package - Turns: " + turns + " Score: " + score);
        gui.update();
    }
}
