package pt.iscte.poo.example;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Bat extends Entity implements Enemy {

    // Points awarded when the bat is defeated.
    public static final int SCORE_POINTS = 35;

    // Starting hitpoints for the bat.
    private static final int STARTING_HITPOINTS = 3;

    // Attack points for the bat.
    private static final int ATTACK_POINTS = 1;

    // Hitpoints the bat can leech when attacking.
    private static final int LEECHING_POINTS = 1;

    // Creates a Bat instance with initial position and attributes.
    public Bat(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2])));
        super.setHitpoints(STARTING_HITPOINTS); // Sets the starting hitpoints.
        super.setAttack(ATTACK_POINTS); // Sets the attack points.
    }

    @Override
    public String getName() {
        // Returns the name of the bat.
        return "Bat";
    }

    @Override
    public int getLayer() {
        // Defines the rendering layer for the bat.
        // Higher layers appear above lower layers.
        return 2;
    }

    @Override
    public void move(Direction... direction) {
        // Moves the bat in a random or default direction.
        // 50% chance to move randomly.
        if (Math.random() < 0.5) {
            super.move();
        } else {
            super.move(Direction.random());
        }
    }

    @Override
    public void healing(int hitpoints) {
        // Increases the bat's hitpoints by the specified amount.
        // Cannot exceed the starting hitpoints.
        super.setHitpoints(Math.min(super.getHitpoints() + hitpoints, STARTING_HITPOINTS));
    }

    @Override
    public int getAttack() {
        // Returns the bat's attack points.
        // 50% chance to leech hitpoints when attacking.
        if (Math.random() < 0.5) {
            healing(LEECHING_POINTS); // Leeches hitpoints.
            return super.getAttack();
        }
        return 0; // No attack occurs.
    }
}
