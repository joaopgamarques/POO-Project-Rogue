package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

// Represents a Wall element in the game that blocks movement.
public class Wall extends GameElement {

    // Constructs a Wall object at the specified position.
    public Wall(Point2D position) {
        super(position); // Sets the position of the wall.
    }

    @Override
    public String getName() {
        // Returns the name of the wall.
        return "Wall";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the wall.
        // Layer 1 ensures it is rendered above the floor but below entities.
        return 1;
    }
}
