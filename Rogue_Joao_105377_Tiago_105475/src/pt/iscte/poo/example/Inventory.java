package pt.iscte.poo.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public class Inventory {

    // Represents different inventory actions the hero can perform.
    enum Selection {
        DropFirst, DropSecond, DropThird, UseFirst, UseSecond, UseThird;
    }

    private List<ImageTile> tileList = new ArrayList<>(); // List of tiles for the inventory background.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI instance.
    private Engine engine = Engine.getInstance(); // Reference to the game engine.
    private static Inventory INSTANCE; // Singleton instance of the Inventory.

    // Private constructor to prevent external instantiation.
    private Inventory() {
    }

    // Retrieves the singleton instance of the Inventory.
    public static Inventory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Inventory();
        }
        return INSTANCE;
    }

    // Performs a selection action (drop or use an item) based on the input.
    public void select(Selection selection) {
        List<Item> items = Engine.getInstance().getHero().getItems(); // Get the hero's inventory.
        if (items.isEmpty()) {
            return; // Do nothing if the inventory is empty.
        }

        switch (selection) {
            case DropFirst:
                // Drops the first item if it exists.
                if (items.size() >= Engine.INVENTORY_CAPACITY - 2) {
                    items.get(0).drop();
                }
                break;

            case DropSecond:
                // Drops the second item if it exists.
                if (items.size() >= Engine.INVENTORY_CAPACITY - 1) {
                    items.get(1).drop();
                }
                break;

            case DropThird:
                // Drops the third item if it exists.
                if (items.size() == Engine.INVENTORY_CAPACITY) {
                    items.get(2).drop();
                }
                break;

            case UseFirst:
                // Uses the first item if it is a HealingPotion.
                if (items.size() >= Engine.INVENTORY_CAPACITY - 2) {
                    if (items.get(0) instanceof HealingPotion) {
                        items.get(0).use();
                    }
                }
                break;

            case UseSecond:
                // Uses the second item if it is a HealingPotion.
                if (items.size() >= Engine.INVENTORY_CAPACITY - 1) {
                    if (items.get(1) instanceof HealingPotion) {
                        items.get(1).use();
                    }
                }
                break;

            case UseThird:
                // Uses the third item if it is a HealingPotion.
                if (items.size() == Engine.INVENTORY_CAPACITY) {
                    if (items.get(2) instanceof HealingPotion) {
                        items.get(2).use();
                    }
                }
                break;
        }
    }

    // Updates the inventory displayed in the GUI.
    public void update() {
        addBackground(); // Add the inventory background.

        // Remove any game elements in the inventory area from the GUI.
        Predicate<GameElement> predicate = element -> Inventory.isWithinBounds(element);
        List<GameElement> elements = engine.getCurrentLevel().getElementsFilteredBy(predicate);
        elements.forEach(element -> {
            gui.removeImage(element);
        });

        // Get the items in the hero's inventory.
        List<Item> items = engine.getHero().getItems();
        if (items.isEmpty()) {
            elements.forEach(element -> {
                engine.getCurrentLevel().getElements().remove(element);
            });
        }

        // Display each item in the inventory at its corresponding position.
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            item.setPosition(new Point2D((Engine.GRID_WIDTH - 1) - i, Engine.GRID_HEIGHT));
            gui.addImage(item);
        }
    }

    // Adds the inventory background to the GUI.
    private void addBackground() {
        gui.removeImages(tileList); // Remove any existing background tiles.
        tileList.clear(); // Clear the list of tiles.

        // Add black tiles to represent the inventory area.
        for (int x = Engine.GRID_WIDTH / 2; x != Engine.GRID_WIDTH; x++) {
            tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT)));
        }

        gui.addImages(tileList); // Add the new tiles to the GUI.
    }

    // Checks if a game element is within the inventory area.
    private static boolean isWithinBounds(GameElement element) {
        return isBetween(element.getPosition().getX(), Engine.GRID_WIDTH / 2, Engine.GRID_WIDTH) && element.getPosition().getY() == Engine.GRID_HEIGHT;
    }

    // Checks if a value is between two bounds.
    private static boolean isBetween(int value, int lower, int upper) {
        return value >= lower && value < upper;
    }

    // Represents a tile in the inventory background.
    private class Tile implements ImageTile {

        private Point2D position; // Position of the tile in the inventory.

        // Constructs a Tile at the specified position.
        public Tile(Point2D position) {
            this.position = position;
        }

        @Override
        public String getName() {
            // Returns the name of the tile (used for rendering).
            return "Black";
        }

        @Override
        public Point2D getPosition() {
            // Returns the position of the tile.
            return position;
        }

        @Override
        public int getLayer() {
            // Returns the rendering layer of the tile.
            return 0;
        }
    }
}
