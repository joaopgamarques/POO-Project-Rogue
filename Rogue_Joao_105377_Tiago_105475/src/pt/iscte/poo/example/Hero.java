package pt.iscte.poo.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Hero extends Entity {

    // Represents the possible states of the hero.
    enum State {
        NORMAL, POISONED;
    }

    public static final Point2D STARTING_POSITION = new Point2D(1, 1); // Hero starting position.
    public static final int STARTING_HITPOINTS = 10; // Initial hitpoints for the hero.
    private static final int ATTACK_POINTS = 1; // Hero's base attack points.
    private static final int POISONED_POINTS = 1; // Damage taken per turn when poisoned.

    private Engine engine = Engine.getInstance(); // Reference to the game engine.
    private ImageMatrixGUI gui = ImageMatrixGUI.getInstance(); // Reference to the GUI.
    private List<Item> items = new ArrayList<>(); // List of items collected by the hero.
    private State state; // Current state of the hero.

    // Variables to store the last save point.
    private Point2D lastPosition;
    private int lastHitpoints;
    private Hero.State lastState;
    private List<Item> lastItems = new ArrayList<>();

    // Constructs a hero at the specified position and initializes its attributes.
    public Hero(Point2D position) {
        super(position);
        super.setHitpoints(STARTING_HITPOINTS);
        super.setAttack(ATTACK_POINTS);
        state = State.NORMAL;

        // Initialize save point.
        lastPosition = STARTING_POSITION;
        lastHitpoints = STARTING_HITPOINTS;
        lastState = State.NORMAL;
        lastItems.clear();
    }

    @Override
    public String getName() {
        // Returns the name of the hero.
        return "Hero";
    }

    @Override
    public int getLayer() {
        // Returns the rendering layer of the hero.
        return 2;
    }

    @Override
    public void move(Direction... direction) {
        // Handles poison effect and hero movement.
        if (state.equals(State.POISONED)) {
            damage(POISONED_POINTS); // Apply poison damage.
            HealthBar.getInstance().update();

            // Restart the game if the hero dies.
            if (!super.isAlive()) {
                Level.restart();
            }
        }
        super.move(direction);
    }

    // Sets the hero's state to either NORMAL or POISONED.
    public void setState(State state) {
        this.state = state;
    }

    // Returns the current state of the hero.
    public State getState() {
        return state;
    }

    @Override
    public void healing(int hitpoints) {
        // Restores the hero's hitpoints up to the starting maximum.
        super.setHitpoints(Math.min(super.getHitpoints() + hitpoints, STARTING_HITPOINTS));
    }

    // Returns the list of items collected by the hero.
    public List<Item> getItems() {
        return items;
    }

    // Sets the hero's inventory.
    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Checks if the hero has a sword.
    public boolean hasSword() {
        return items.stream().anyMatch(item -> item instanceof Sword);
    }

    // Checks if the hero has armor.
    public boolean hasArmor() {
        return items.stream().anyMatch(item -> item instanceof Armor);
    }

    // Checks if the hero has any items in the inventory.
    public boolean hasItems() {
        return items.size() != 0;
    }

    @Override
    public int getAttack() {
        // Returns the attack points, enhanced by the sword if equipped.
        return hasSword() ? Sword.ATTACK * ATTACK_POINTS : ATTACK_POINTS;
    }

    // Checks if the hero has entered an open door.
    public boolean hasOpenedDoor() {
        Predicate<GameElement> predicate = element -> element instanceof Door && ((Door) element).isOpen();
        List<GameElement> doors = engine.getCurrentLevel().getElementsFilteredBy(predicate);

        for (GameElement door : doors) {
            if (super.getPosition().equals(door.getPosition())) {
                return true;
            }
        }

        return false;
    }

    // Checks if the hero has the key to open the specified door.
    private boolean hasKey(Door door) {
        List<Item> keys = items.stream().filter(item -> item instanceof Key).collect(Collectors.toList());
        Predicate<Item> predicate = key -> ((Key) key).getKeyID().equals(door.getKeyID());
        return keys.stream().anyMatch(predicate);
    }

    // Retrieves the key that opens the specified door.
    private Item getKey(Door door) {
        List<Item> keys = items.stream().filter(item -> item instanceof Key).collect(Collectors.toList());
        Predicate<Item> predicate = key -> ((Key) key).getKeyID().equals(door.getKeyID());
        return keys.stream().filter(predicate).findFirst().get();
    }

    // Returns the hero's hitpoints at the last save point.
    public int getLastHitpoints() {
        return lastHitpoints;
    }

    // Returns the hero's position at the last save point.
    public Point2D getLastPosition() {
        return lastPosition;
    }

    // Returns the hero's state at the last save point.
    public State getLastState() {
        return lastState;
    }

    // Returns the hero's items at the last save point.
    public List<Item> getLastItems() {
        return lastItems;
    }

    @Override
    public void interactsWith(GameElement element) {
        // Handles interaction with enemies, items, doors, and treasures.

        if (element instanceof Enemy) {
            // Attack the enemy.
            ((Entity) element).damage(getAttack());
            System.out.println("Hero attacks " + element.getName() + " in position " + element.getPosition() + ". " +
                element.getName() + ": " + ((Entity) element).getHitpoints() + " hitpoints.");

            if (!((Entity) element).isAlive()) {
                // Enemy is defeated.
                System.out.println(element.getName() + " in position " + element.getPosition() + " is dead.");
                gui.removeImage(element);
                engine.getCurrentLevel().getElements().remove(element);
                updateScore((Enemy) element);
            }

        } else if (element instanceof Item && !(element instanceof Treasure)) {
            // Interact with regular items.
            System.out.println(element.getName() + " found in position " + element.getPosition() + ".");
            if (items.size() < Engine.INVENTORY_CAPACITY) {
                ((Item) element).interactsWith(this);
            }

        } else if (element instanceof Door && ((Door) element).isClosed()) {
            // Interact with a closed door.
            System.out.println("Door found in position " + element.getPosition() + ".");
            if (!hasKey((Door) element)) {
                return;
            }
            ((Door) element).interactsWith(this);
            ((Key) getKey((Door) element)).use();

        } else if (element instanceof Door && ((Door) element).isOpen()) {
            // Enter an open door and transition to the next level.
            engine.getCurrentLevel().getElements().removeAll(items);
            engine.setCurrentLevel(((Door) element).getNextLevel());
            engine.getHero().setPosition(((Door) element).getNextPosition());
            engine.getCurrentLevel().getElements().addAll(items);
            engine.getCurrentLevel().show();
            createSavePoint();

        } else if (element instanceof Treasure) {
            // Interact with a treasure item.
            System.out.println(element.getName() + " found in position " + element.getPosition() + ".");
            if (items.size() < Engine.INVENTORY_CAPACITY) {
                ((Item) element).interactsWith(this);
                gui.setMessage("Treasure found. You've won!");
                HallOfFame.getInstance().print();
                gui.dispose();
            }
        }
    }

    // Updates the score when an enemy is defeated.
    private void updateScore(Enemy enemy) {
        if (enemy instanceof Bat) {
            engine.increaseScore(Bat.SCORE_POINTS);
        } else if (enemy instanceof Skeleton) {
            engine.increaseScore(Skeleton.SCORE_POINTS);
        } else if (enemy instanceof Thug) {
            engine.increaseScore(Thug.SCORE_POINTS);
        } else if (enemy instanceof Scorpio) {
            engine.increaseScore(Scorpio.SCORE_POINTS);
        } else if (enemy instanceof Thief) {
            engine.increaseScore(Thief.SCORE_POINTS);
        }
    }

    // Creates a save point for the current game state.
    private void createSavePoint() {
        Level.save();
        lastHitpoints = super.getHitpoints();
        lastState = state.equals(State.NORMAL) ? State.NORMAL : State.POISONED;
        lastPosition = new Point2D(super.getPosition().getX(), super.getPosition().getY());

        if (!engine.getLastLevelID().equals(engine.getCurrentLevelID())) {
            lastItems = Item.copy(items);
            engine.setLastLevelID(engine.getCurrentLevelID());
        }

        engine.setLastScore(engine.getScore());
    }
}
