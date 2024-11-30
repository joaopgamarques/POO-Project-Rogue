package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

// Represents a Sword item in the game that increases the hero's attack power.
public class Sword extends Item {

    public static final int ATTACK = 2; // Attack bonus provided by the sword.

    // Constructs a Sword object with the specified position from the info array.
    public Sword(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position of the sword.
    }

    @Override
    public String getName() {
        // Returns the name of the sword.
        return "Sword";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the sword.
        // Layer 1 ensures it is rendered above the floor.
        return 1;
    }
}
