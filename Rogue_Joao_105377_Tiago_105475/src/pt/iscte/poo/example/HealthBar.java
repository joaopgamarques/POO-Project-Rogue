package pt.iscte.poo.example;

import java.util.ArrayList;
import java.util.List;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public class HealthBar {

    private static HealthBar INSTANCE; // Singleton instance of the HealthBar.
    private List<ImageTile> tileList = new ArrayList<>(); // List of tiles representing the health bar.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI instance.

    // Private constructor to prevent external instantiation.
    private HealthBar() {
    }

    // Retrieves the singleton instance of the HealthBar.
    public static HealthBar getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HealthBar();
        }
        return INSTANCE;
    }

    // Updates the health bar based on the hero's current hitpoints.
    public void update() {
        int hitpoints = Engine.getInstance().getHero().getHitpoints(); // Retrieves the hero's hitpoints.
        gui.removeImages(tileList); // Removes the existing health bar tiles from the GUI.
        tileList.clear(); // Clears the current list of tiles.

        // Constructs the health bar by adding tiles representing health points.
        for (int x = 0; x != Engine.GRID_WIDTH / 2; x++) {
            if (isEven(hitpoints)) {
                // Handles the case where hitpoints are even.
                if (x < Engine.GRID_WIDTH / 2 - hitpoints / 2) {
                    tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT), "Red")); // Adds red tiles for lost health.
                } else {
                    tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT), "Green")); // Adds green tiles for remaining health.
                }
            } else {
                // Handles the case where hitpoints are odd.
                if (x < Engine.GRID_WIDTH / 2 - hitpoints / 2 - 1) {
                    tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT), "Red")); // Adds red tiles for lost health.
                } else if (x == Engine.GRID_WIDTH / 2 - hitpoints / 2 - 1) {
                    tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT), "RedGreen")); // Adds a mixed red-green tile for partial health.
                } else {
                    tileList.add(new Tile(new Point2D(x, Engine.GRID_HEIGHT), "Green")); // Adds green tiles for remaining health.
                }
            }
        }

        gui.addImages(tileList); // Adds the updated health bar tiles to the GUI.
    }

    // Checks if a number is even.
    private static boolean isEven(int number) {
        return number % 2 == 0;
    }

    // Represents a tile in the health bar.
    private class Tile implements ImageTile {

        private Point2D position; // Position of the tile in the health bar.
        private String color; // Color of the tile (Red, Green, or RedGreen).

        // Constructs a Tile object with the specified position and color.
        public Tile(Point2D position, String color) {
            this.position = position;
            this.color = color;
        }

        @Override
        public String getName() {
            // Returns the color of the tile.
            return color;
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
