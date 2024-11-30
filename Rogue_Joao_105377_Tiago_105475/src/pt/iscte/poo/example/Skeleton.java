package pt.iscte.poo.example;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Represents a Skeleton enemy in the game that alternates between idle and pursuing states.
public class Skeleton extends Entity implements Enemy {

    // States defining the behavior of the Skeleton.
    enum State {
        IDLE, // The Skeleton remains stationary.
        PURSUING; // The Skeleton actively moves toward its target.
    }

    public static final int SCORE_POINTS = 65; // Points awarded for defeating a Skeleton.
    private static final int STARTING_HITPOINTS = 5; // Initial hitpoints for the Skeleton.
    private static final int ATTACK_POINTS = 1; // Attack points of the Skeleton.

    private State state; // Current state of the Skeleton.

    // Constructs a Skeleton with the specified position from the info array.
    public Skeleton(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position.
        super.setHitpoints(STARTING_HITPOINTS); // Sets the starting hitpoints.
        super.setAttack(ATTACK_POINTS); // Sets the attack points.
        state = State.IDLE; // Initializes the Skeleton in the IDLE state.
    }

    @Override
    public String getName() {
        // Returns the name of the Skeleton.
        return "Skeleton";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the Skeleton.
        return 2;
    }

    @Override
    public void move(Direction... direction) {
        // Alternates the Skeleton's state between IDLE and PURSUING each turn.

        switch (state) {
            case IDLE:
                // Switches to pursuing state.
                state = State.PURSUING;
                break;

            case PURSUING:
                // Switches to idle state.
                state = State.IDLE;
                break;
        }

        // Moves the Skeleton if it is in the PURSUING state.
        if (state.equals(State.PURSUING)) {
            super.move(); // Uses the default movement logic.
        }
    }
}
