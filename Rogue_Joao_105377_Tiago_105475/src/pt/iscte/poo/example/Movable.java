package pt.iscte.poo.example;

import pt.iscte.poo.utils.Direction;

// Represents a game element that can move in a given direction.
public interface Movable extends Interactable {

    // Moves the game element in the specified direction(s).
    public void move(Direction... direction);
}
