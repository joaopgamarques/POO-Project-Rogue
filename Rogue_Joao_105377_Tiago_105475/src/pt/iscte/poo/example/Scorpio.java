package pt.iscte.poo.example;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Represents a Scorpio enemy in the game that can poison the hero.
public class Scorpio extends Entity implements Enemy {

    public static final int SCORE_POINTS = 35; // Points awarded for defeating a Scorpio.
    private static final int STARTING_HITPOINTS = 2; // Initial hitpoints for the Scorpio.
    private static final int ATTACK_POINTS = 0; // Attack points of the Scorpio.

    private Engine engine = Engine.getInstance(); // Reference to the game engine.

    // Constructs a Scorpio with the given position from the info array.
    public Scorpio(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position.
        super.setHitpoints(STARTING_HITPOINTS); // Sets the starting hitpoints.
        super.setAttack(ATTACK_POINTS); // Sets the attack points.
    }

    @Override
    public String getName() {
        // Returns the name of the Scorpio.
        return "Scorpio";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the Scorpio.
        return 2;
    }

    @Override
    public void move(Direction... direction) {
        // Moves the Scorpio using its default behavior.
        super.move();
    }

    @Override
    public int getAttack() {
        // Poisons the hero when attacking and returns the Scorpio's attack points.
        engine.getHero().setState(Hero.State.POISONED); // Sets the hero's state to poisoned.
        System.out.println("The hero is poisoned."); // Logs the poisoning action.
        return super.getAttack(); // Returns the attack points.
    }
}
