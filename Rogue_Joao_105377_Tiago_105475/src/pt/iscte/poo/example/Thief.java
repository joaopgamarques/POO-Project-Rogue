package pt.iscte.poo.example;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

// Represents a Thief enemy in the game that can steal items from the hero.
public class Thief extends Entity implements Enemy {

    // States defining the behavior of the Thief.
    enum State {
        STEALTHING, // The Thief moves without interacting with the hero.
        PURSUING,   // The Thief actively pursues the hero to steal an item.
        FLEEING;    // The Thief flees after stealing an item.
    }

    public static final int SCORE_POINTS = 75; // Points awarded for defeating the Thief.
    private static final int STARTING_HITPOINTS = 5; // Initial hitpoints for the Thief.
    private static final int ATTACK_POINTS = 0; // Attack points of the Thief.

    private Engine engine = Engine.getInstance(); // Reference to the game engine.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI.
    private Item item; // Item stolen by the Thief.
    private State state; // Current state of the Thief.

    // Constructs a Thief with the specified position from the info array.
    public Thief(String[] info) {
        super(new Point2D(Integer.parseInt(info[1]), Integer.parseInt(info[2]))); // Sets the initial position.
        super.setHitpoints(STARTING_HITPOINTS); // Sets the starting hitpoints.
        super.setAttack(ATTACK_POINTS); // Sets the attack points.
        item = null; // Initializes without a stolen item.
        state = State.STEALTHING; // Starts in the STEALTHING state.
    }

    @Override
    public String getName() {
        // Returns the name of the Thief.
        return "Thief";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the Thief.
        return 3;
    }

    // Returns the item that the Thief has stolen.
    public Item getItem() {
        return item;
    }

    // Checks if the Thief has stolen an item.
    public boolean hasItem() {
        return item != null;
    }

    @Override
    public void move(Direction... direction) {
        // Handles the Thief's movement and behavior based on its current state.

        switch (state) {
            case STEALTHING:
                // Switches to PURSUING if the hero has items to steal.
                if (engine.getHero().hasItems()) {
                    state = State.PURSUING;
                }
                break;

            case PURSUING:
                // Switches to FLEEING after stealing an item.
                if (hasItem()) {
                    state = State.FLEEING;
                }
                break;

            case FLEEING:
                // Remains in the FLEEING state.
                break;
        }

        // Moves toward the hero when PURSUING.
        if (state.equals(State.PURSUING)) {
            super.move();
        }

        // Moves away from the hero when STEALTHING or FLEEING.
        if (state.equals(State.STEALTHING) || state.equals(State.FLEEING)) {
            if (!isAlive()) {
                // Drops the stolen item if the Thief is defeated.
                gui.addImage(item);
                gui.removeImage(this);
                engine.getCurrentLevel().getElements().remove(this);
                return;
            }

            // Prevents movement if the hero enters a different level.
            if (engine.getHero().hasOpenedDoor()) {
                return;
            }
            run(); // Moves the Thief away from the hero.
        }
    }

    // Moves the Thief to a position away from the hero.
    private void run() {
        List<Point2D> positions = removeIllegalPositions(super.getPosition().getNeighbourhoodPoints());
        Point2D position = getRandomPosition(positions);
        super.setPosition(position); // Updates the Thief's position.
        if (hasItem()) {
            item.setPosition(position); // Updates the stolen item's position.
        }
    }

    // Returns a random position from a list of valid positions.
    private Point2D getRandomPosition(List<Point2D> positions) {
        Random random = new Random();
        return positions.size() != 0 ? positions.get(random.nextInt(positions.size())) : super.getPosition();
    }

    // Filters out illegal positions for the Thief to move to.
    private List<Point2D> removeIllegalPositions(List<Point2D> positions) {
        Predicate<GameElement> predicate = (element -> element instanceof Entity || element instanceof Wall || element instanceof Door);
        List<GameElement> listOfElements = engine.getCurrentLevel().getElementsFilteredBy(predicate);
        listOfElements.add(engine.getHero());

        listOfElements.forEach(element -> {
            if (element instanceof Wall || element instanceof Door) {
                positions.remove(element.getPosition()); // Avoids walls and doors.
            } else if (element instanceof Enemy && !element.equals(this)) {
                positions.remove(element.getPosition()); // Avoids other enemies.
            } else if (element instanceof Hero) {
                positions.remove(element.getPosition()); // Avoids the hero.
                positions.removeAll(element.getPosition().getNeighbourhoodPoints()); // Avoids adjacent positions.
            }
        });

        return positions;
    }

    @Override
    public void interactsWith(GameElement element) {
        // Handles interaction with the hero, stealing an item.

        if (element instanceof Hero) {
            Item item = getRandomItem(engine.getHero().getItems()); // Randomly selects an item to steal.
            this.item = item; // Stores the stolen item.
            item.setPosition(super.getPosition()); // Updates the stolen item's position.
            engine.getHero().getItems().remove(item); // Removes the item from the hero's inventory.
            gui.removeImage(item); // Removes the item from the GUI.
            Inventory.getInstance().update(); // Updates the inventory display.
            state = State.FLEEING; // Switches to the FLEEING state.
            System.out.println(getName() + " steals an item (" + item.getName() + ") from Hero in position " + getPosition() +
                    ". Hero: " + ((Entity) element).getHitpoints() + " hitpoints.");
        }
    }

    // Selects a random item from the hero's inventory.
    private Item getRandomItem(List<Item> items) {
        Random random = new Random();
        return items.get(random.nextInt(items.size()));
    }
}
