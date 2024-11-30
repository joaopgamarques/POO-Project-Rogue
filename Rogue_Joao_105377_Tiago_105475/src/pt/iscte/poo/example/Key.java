package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

// Represents a Key item in the game that can open specific doors.
public class Key extends Item {

    private final String keyID; // Unique identifier for the key.

    // Constructs a Key object with the specified position and key ID.
    public Key(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the position of the key.
        this.keyID = info[3]; // Sets the unique key ID.
    }

    // Retrieves the unique ID of the key.
    public String getKeyID() {
        return keyID;
    }

    @Override
    public String getName() {
        // Returns the name of the key.
        return "Key";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the key.
        // Layer 1 ensures it is rendered above the floor.
        return 1;
    }
}
