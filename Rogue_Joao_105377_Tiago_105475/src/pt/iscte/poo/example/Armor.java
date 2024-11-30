package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

public class Armor extends Item {

    // Represents an Armor item in the game.
    // This item is positioned on the game grid and interacts with other elements.
    
    public Armor(String[] info) {
        // Creates an Armor object with a specified position.
        // The position is extracted from an array containing x and y coordinates.
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2])));
    }

    @Override
    public String getName() {
        // Provides the name of this item.
        // This name is used to identify the item in the game.
        return "Armor";
    }

    @Override
    public int getLayer() {
        // Determines the layer on which this item is rendered.
        // Lower layers are rendered beneath higher layers. Armor is rendered on layer 1.
        return 1;
    }
}
