package pt.iscte.poo.example;

import pt.iscte.poo.utils.Point2D;

public class HealingPotion extends Item {

    // Amount of hitpoints restored by the healing potion.
    public static int RESTORE_POINTS = 5;

    // Constructs a HealingPotion object with the specified position.
    public HealingPotion(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2])));
    }

    @Override
    public String getName() {
        // Returns the name of the healing potion.
        return "HealingPotion";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer for the healing potion.
        // Layer 1 ensures it is rendered above the floor.
        return 1;
    }

}
