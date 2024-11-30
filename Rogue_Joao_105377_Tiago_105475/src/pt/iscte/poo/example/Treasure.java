package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

// Represents a Treasure item in the game, marking the goal for the player.
public class Treasure extends Item {

    // Constructs a Treasure with the specified position from the info array.
    public Treasure(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position of the treasure.
    }

    @Override
    public String getName() {
        // Returns the name of the treasure.
        return "Treasure";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the treasure.
        // Layer 1 ensures it is rendered above the floor.
        return 1;
    }
}
