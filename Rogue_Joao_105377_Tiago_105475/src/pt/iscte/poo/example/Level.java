package pt.iscte.poo.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

// Represents a game level with elements like walls, floors, enemies, and items.
public class Level {

    private Engine engine = Engine.getInstance(); // Reference to the game engine.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI.
    private List<GameElement> elements = new ArrayList<>(); // List of game elements.
    private HashMap<Point2D, GameElement> map = new HashMap<>(); // Map of walls and floors by position.

    // Returns the map containing wall and floor elements.
    public HashMap<Point2D, GameElement> getMap() {
        return map;
    }

    // Sets the map containing wall and floor elements.
    public void setMap(HashMap<Point2D, GameElement> map) {
        this.map = map;
    }

    // Returns the list of all game elements in the level.
    public List<GameElement> getElements() {
        return elements;
    }

    // Returns a filtered list of game elements based on the provided predicate.
    public List<GameElement> getElementsFilteredBy(Predicate<GameElement> predicate) {
        return elements.stream().filter(predicate).collect(Collectors.toList());
    }

    // Adds wall and floor elements to the GUI.
    private void addWallsAndFloor() {
        List<ImageTile> tileList = new ArrayList<>();
        map.values().forEach(element -> tileList.add(element));
        gui.addImages(tileList);
    }

    // Adds the remaining game elements (excluding walls) to the GUI.
    private void addElements() {
        List<ImageTile> tileList = new ArrayList<>();
        elements.forEach(element -> {
            if (!(element instanceof Wall)) {
                tileList.add(element);
            }
        });

        // Removes stolen items from the GUI.
        List<GameElement> elements = engine.getCurrentLevel().getElementsFilteredBy(element -> element instanceof Thief);
        elements.forEach(element -> {
            tileList.remove(((Thief) element).getItem());
        });

        gui.addImages(tileList);
    }

    // Adds the hero to the GUI.
    private void addHero() {
        gui.addImage(engine.getHero());
    }

    // Displays the current level in the GUI.
    public void show() {
        gui.clearImages();
        addWallsAndFloor();
        addElements();
        addHero();
        HealthBar.getInstance().update();
        Inventory.getInstance().update();
    }

    // Creates a level by reading from the given file.
    public static Level create(String filename) {
        HashMap<Point2D, GameElement> map = new HashMap<>();
        Level level = new Level();
        File file = new File(filename);

        try {
            Scanner scanner = new Scanner(file);
            int n = 0; // Line number.

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                char[] arrayOfCharacters = line.toCharArray();

                if (n < Engine.GRID_HEIGHT) {
                    // Adds wall and floor elements for the grid.
                    int y = n;
                    for (int x = 0; x < Engine.GRID_WIDTH && y < Engine.GRID_HEIGHT; x++) {
                        if (arrayOfCharacters[x] == '#') {
                            map.put(new Point2D(x, y), new Wall(new Point2D(x, y)));
                            level.elements.add(new Wall(new Point2D(x, y)));
                        } else {
                            map.put(new Point2D(x, y), new Floor(new Point2D(x, y)));
                        }
                    }
                } else {
                    // Processes other elements (e.g., items, enemies).
                    level.setMap(map);
                    String[] info = line.split(",");

                    switch (info[0]) {
                        case "Door":
                            level.elements.add(Door.create(info));
                            break;

                        case "Key":
                            level.elements.add(Item.create("Key", info));
                            break;

                        case "Sword":
                            level.elements.add(Item.create("Sword", info));
                            break;

                        case "Armor":
                            level.elements.add(Item.create("Armor", info));
                            break;

                        case "HealingPotion":
                            level.elements.add(Item.create("HealingPotion", info));
                            break;

                        case "Treasure":
                            level.elements.add(Item.create("Treasure", info));
                            break;

                        case "Skeleton":
                            level.elements.add(Entity.create("Skeleton", info));
                            break;

                        case "Bat":
                            level.elements.add(Entity.create("Bat", info));
                            break;

                        case "Thug":
                            level.elements.add(Entity.create("Thug", info));
                            break;

                        case "Scorpio":
                            level.elements.add(Entity.create("Scorpio", info));
                            break;

                        case "Thief":
                            level.elements.add(Entity.create("Thief", info));
                            break;

                        default:
                            break;
                    }
                }

                n++;
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.err.println("File Not Found!");
        }
        return level;
    }

    // Saves the current level to a file.
    public static void save() {
        File filename = new File(System.getProperty("user.dir").concat("\\saves\\save.txt"));
        if (!(filename.exists() && filename.isFile())) {
            return;
        }

        Predicate<GameElement> predicate = element -> !(element instanceof Wall);
        List<GameElement> elements = Engine.getInstance().getCurrentLevel().getElementsFilteredBy(predicate);
        HashMap<Point2D, GameElement> map = Engine.getInstance().getCurrentLevel().getMap();

        try {
            PrintWriter writer = new PrintWriter(filename);

            // Writes the grid layout (walls and floors).
            for (int y = 0; y < Engine.GRID_HEIGHT; y++) {
                for (int x = 0; x < Engine.GRID_WIDTH; x++) {
                    if (map.get(new Point2D(x, y)) instanceof Wall) {
                        writer.print('#');
                    } else {
                        writer.print(' ');
                    }
                }
                writer.println();
            }

            writer.println();

            // Writes other game elements.
            elements.forEach(element -> {
                if (element instanceof Enemy) {
                    writer.println(element.getName() + "," + element.getPosition().getX() + "," + element.getPosition().getY());
                } else if (element instanceof Item && !(element instanceof Key)) {
                    writer.println(element.getName() + "," + element.getPosition().getX() + "," + element.getPosition().getY());
                } else if (element instanceof Key) {
                    writer.println(element.getName() + "," + element.getPosition().getX() + "," + element.getPosition().getY() + ","
                            + ((Key) element).getKeyID());
                } else if (element instanceof Door) {
                    writer.println("Door" + "," + element.getPosition().getX() + "," + element.getPosition().getY() + ","
                            + ((Door) element).getNextLevel() + "," + ((Door) element).getNextPosition().getX() + ","
                            + ((Door) element).getNextPosition().getY() + (element instanceof Key ? "," + ((Door) element).getKeyID() : ""));
                }
            });

            writer.close();

        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        }

        removeLastEmptyLine(filename);
    }

    // Removes the last line from a file to avoid empty lines.
    private static void removeLastEmptyLine(File filename) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");
            long length = file.length();
            file.setLength(length - 2);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Loads the saved level state from a file.
    public static void load() {
        File file = new File(System.getProperty("user.dir").concat("\\saves\\save.txt"));
        Level level = Engine.getInstance().getCurrentLevel();

        // Remove existing elements from the GUI and level.
        List<GameElement> elements = level.getElements();
        elements.forEach(element -> ImageMatrixGUI.getInstance().removeImage(element));
        level.getElements().removeAll(elements);

        if (file.isFile()) {
            Engine.getInstance().getLevels().put(Engine.getInstance().getCurrentLevelID(), Level.create(file.getAbsolutePath()));
        }
    }

    // Restores the game to the last saved state.
    public static void restart() {
        load();
        Engine engine = Engine.getInstance();
        Hero hero = (Hero) engine.getHero();
        hero.setPosition(hero.getLastPosition());
        hero.setHitpoints(hero.getLastHitpoints());
        hero.setState(hero.getLastState());
        hero.getItems().clear();
        hero.getItems().addAll(Item.copy(hero.getLastItems()));
        engine.setScore(engine.getLastScore());
        engine.getCurrentLevel().show();
    }
}
