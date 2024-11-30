package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

// Represents a Thug enemy in the game with high attack points and hitpoints.
public class Thug extends Entity implements Enemy {

    public static final int SCORE_POINTS = 85; // Points awarded for defeating a Thug.
    private static final int STARTING_HITPOINTS = 10; // Initial hitpoints for the Thug.
    private static final int ATTACK_POINTS = 3; // Attack points of the Thug.

    // Constructs a Thug with the specified position from the info array.
    public Thug(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position.
        super.setHitpoints(STARTING_HITPOINTS); // Sets the starting hitpoints.
        super.setAttack(ATTACK_POINTS); // Sets the attack points.
    }

    @Override
    public String getName() {
        // Returns the name of the Thug.
        return "Thug";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the Thug.
        return 2;
    }

    @Override
    public int getAttack() {
        // Returns the attack points with a 30% chance to attack.
        if (Math.random() < 0.3) {
            return super.getAttack(); // Performs the attack.
        }
        return 0; // No attack occurs.
    }
}
