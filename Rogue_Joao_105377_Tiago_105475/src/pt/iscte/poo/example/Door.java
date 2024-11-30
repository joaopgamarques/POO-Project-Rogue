package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

public class Door extends GameElement implements Interactable {

    // Represents the states of the door: open or closed.
    enum State {
        OPEN, CLOSED;
    }

    private final String nextLevel; // The level the door leads to.
    private final Point2D nextPosition; // The position in the next level.
    private final String keyID; // The ID of the key required to open the door (if any).
    private State state; // The current state of the door.

    // Constructs a Door object using an array of strings for initialization.
    public Door(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2])));
        nextLevel = info[3]; // Sets the level the door leads to.
        nextPosition = new Point2D(Integer.parseInt(info[4]), Integer.parseInt(info[5])); // Sets the position in the next level.
        keyID = info.length == 6 ? null : info[6]; // Sets the key ID if provided.
        state = info.length == 6 ? State.OPEN : State.CLOSED; // Sets the initial state of the door.
    }

    @Override
    public String getName() {
        // Returns the name of the door based on its current state.
        return state.equals(State.OPEN) ? "DoorOpen" : "DoorClosed";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the door.
        // Open doors are rendered above closed doors.
        return state.equals(State.OPEN) ? 2 : 1;
    }

    // Gets the ID of the next level the door leads to.
    public String getNextLevel() {
        return nextLevel;
    }

    // Gets the position in the next level where the door leads to.
    public Point2D getNextPosition() {
        return nextPosition;
    }

    // Gets the ID of the key required to open the door.
    public String getKeyID() {
        return keyID;
    }

    // Checks if the door is open.
    public boolean isOpen() {
        return state.equals(State.OPEN);
    }

    // Checks if the door is closed.
    public boolean isClosed() {
        return state.equals(State.CLOSED);
    }

    // Opens the door by changing its state to open.
    public void open() {
        state = State.OPEN;
    }

    // Closes the door by changing its state to closed.
    public void close() {
        state = State.CLOSED;
    }

    @Override
    public void interactsWith(GameElement element) {
        // Interacts with another game element.
        // If the door is closed, it will open.
        if (isClosed()) {
            open();
        }
    }

    // Factory method to create a new Door object.
    public static Door create(String[] info) {
        return new Door(info);
    }
}
