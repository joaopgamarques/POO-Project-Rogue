package pt.iscte.poo.example;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

// Represents a game element with a position on the game grid.
// This abstract class implements the ImageTile interface, providing
// basic functionality for positioning.
public abstract class GameElement implements ImageTile {

    private Point2D position; // Position of the game element on the grid.

    // Constructs a GameElement with the given position.
    public GameElement(Point2D position) {
        this.position = position;
    }

    @Override
    public Point2D getPosition() {
        // Returns the current position of the game element.
        return position;
    }

    // Updates the position of the game element.
    public void setPosition(Point2D position) {
        this.position = position;
    }
}
