package pt.iscte.poo.example;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.utils.Point2D;

public abstract class Item extends GameElement implements Collectable {

    private Engine engine = Engine.getInstance(); // Reference to the game engine.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI.

    // Constructs an item at a specified position.
    public Item(Point2D position) {
        super(position);
    }

    @Override
    public void collect() {
        // Collects the item by adding it to the hero's inventory.
        gui.removeImage(this); // Removes the item from the GUI.
        engine.getHero().getItems().add(this); // Adds the item to the hero's inventory.
        super.setPosition(new Point2D(Engine.GRID_WIDTH - engine.getHero().getItems().size(), Engine.GRID_HEIGHT)); // Updates position in inventory.
        gui.addImage(this); // Adds the item to the GUI in the inventory.
    }

    @Override
    public void use() {
        // Uses the item and applies its effects.
        gui.removeImage(this); // Removes the item from the GUI.
        engine.getHero().getItems().remove(this); // Removes the item from the inventory.
        engine.getCurrentLevel().getElements().remove(this); // Removes the item from the level.
        Inventory.getInstance().update(); // Updates the inventory display.

        if (this instanceof HealingPotion) {
            // Applies healing if the item is a HealingPotion.
            engine.getHero().healing(HealingPotion.RESTORE_POINTS);
            if (engine.getHero().getState().equals(Hero.State.POISONED)) {
                engine.getHero().setState(Hero.State.NORMAL); // Removes poison effect.
            }
            HealthBar.getInstance().update(); // Updates the health bar.
        }
    }

    @Override
    public void drop() {
        // Drops the item in the hero's current position.
        GameElement item = Item.copy(this); // Creates a copy of the item.
        item.setPosition(engine.getHero().getPosition()); // Places the item at the hero's position.

        gui.removeImage(this); // Removes the item from the GUI.
        engine.getHero().getItems().remove(this); // Removes the item from the inventory.
        engine.getCurrentLevel().getElements().remove(this); // Removes the item from the level.

        engine.getCurrentLevel().getElements().add(item); // Adds the dropped item to the level.
        gui.addImage(item); // Adds the dropped item to the GUI.
        Inventory.getInstance().update(); // Updates the inventory display.
        engine.getCurrentLevel().show(); // Updates the current level display.
    }

    @Override
    public void interactsWith(GameElement element) {
        // Collects the item when interacting with the hero.
        collect();
    }

    // Copies the list of collected items.
    public static List<Item> copy(List<Item> items) {
        List<Item> lastItems = new ArrayList<>();

        items.forEach(item -> {
            String type = item.getName();
            String[] info;

            switch (type) {
                case "Armor":
                case "Sword":
                case "HealingPotion":
                    info = new String[] {
                        item.getName(),
                        String.valueOf(item.getPosition().getX()),
                        String.valueOf(item.getPosition().getY())
                    };
                    lastItems.add(create(type, info));
                    break;

                case "Key":
                    info = new String[] {
                        item.getName(),
                        String.valueOf(item.getPosition().getX()),
                        String.valueOf(item.getPosition().getY()),
                        String.valueOf(((Key) item).getKeyID())
                    };
                    lastItems.add(create(type, info));
                    break;

                default:
                    throw new IllegalArgumentException("Illegal item!");
            }
        });

        return lastItems;
    }

    // Returns a copy of a given item.
    public static Item copy(Item item) {
        String type = item.getName();
        String[] info;

        switch (type) {
            case "Armor":
            case "Sword":
            case "HealingPotion":
                info = new String[] {
                    item.getName(),
                    String.valueOf(item.getPosition().getX()),
                    String.valueOf(item.getPosition().getY())
                };
                return create(type, info);

            case "Key":
                info = new String[] {
                    item.getName(),
                    String.valueOf(item.getPosition().getX()),
                    String.valueOf(item.getPosition().getY()),
                    String.valueOf(((Key) item).getKeyID())
                };
                return create(type, info);

            default:
                throw new IllegalArgumentException("Illegal item!");
        }
    }

    // Factory method to create items based on their type.
    public static Item create(String type, String[] info) {
        switch (type) {
            case "Key":
                return new Key(info);

            case "Sword":
                return new Sword(info);

            case "Armor":
                return new Armor(info);

            case "HealingPotion":
                return new HealingPotion(info);

            case "Treasure":
                return new Treasure(info);

            default:
                throw new IllegalArgumentException();
        }
    }
}
