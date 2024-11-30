package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

public class Floor extends GameElement {

    // Creates a Floor object with the given position.
    public Floor(Point2D position) {
        super(position);
    }

    @Override
    public String getName() {
        // Returns the name of the floor.
        return "Floor";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer for the floor.
        // Layer 0 ensures the floor is rendered below all other elements.
        return 0;
    }

}
