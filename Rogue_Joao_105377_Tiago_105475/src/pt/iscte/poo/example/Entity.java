package pt.iscte.poo.example;

import java.util.List;
import java.util.function.Predicate;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class Entity extends GameElement implements Movable {

    private Engine engine = Engine.getInstance(); // Reference to the game engine instance.
    private int hitpoints; // Current hitpoints of the entity.
    private int attack; // Attack points of the entity.

    // Constructs an Entity with the given initial position.
    public Entity(Point2D position) {
        super(position);
    }

    // Returns the current hitpoints of the entity.
    public int getHitpoints() {
        return hitpoints;
    }

    // Sets the hitpoints for the entity.
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    // Reduces the entity's hitpoints by the specified amount, ensuring it does not go below zero.
    public void damage(int hitpoints) {
        this.hitpoints = this.hitpoints - hitpoints > 0 ? this.hitpoints - hitpoints : 0;
    }

    // Restores the entity's hitpoints by the specified amount. (Currently unimplemented.)
    public void healing(int hitpoints) {}

    // Returns the entity's attack points.
    public int getAttack() {
        return attack;
    }

    // Sets the attack points for the entity.
    public void setAttack(int attack) {
        this.attack = attack;
    }

    // Checks if the entity is still alive (hitpoints > 0).
    public boolean isAlive() {
        return hitpoints > 0;
    }

    @Override
    public void interactsWith(GameElement element) {
        // Handles interaction with other game elements, specifically the hero.
        if (element instanceof Hero) {
            if (((Hero) element).hasArmor()) {
                // Hero has armor; there's a 50% chance of receiving damage.
                if (Math.random() < 0.5) {
                    ((Hero) element).damage(getAttack());
                    System.out.println("Hero receives an attack from " + getName() + " in position " + getPosition() +
                        ". Hero: " + ((Hero) element).getHitpoints() + " hitpoints.");
                }
            } else {
                // Hero receives damage without armor.
                ((Hero) element).damage(getAttack());
                System.out.println("Hero receives an attack from " + getName() + " in position " + getPosition() +
                    ". Hero: " + ((Hero) element).getHitpoints() + " hitpoints.");
            }

            HealthBar.getInstance().update(); // Updates the health bar.

            if (!((Hero) element).isAlive()) {
                // If the hero dies, restart the level.
                System.out.println("Hero in position " + element.getPosition() + " is dead.");
                Level.restart();
            }
        }
    }

    @Override
    public void move(Direction... direction) {
        // Prevents enemies from moving if the hero has just opened a door.
        if (this instanceof Enemy && engine.getHero().hasOpenedDoor()) {
            return;
        }

        // Determines the movement vector based on the direction provided or the hero's position.
        Vector2D vector = (direction.length == 1) ? direction[0].asVector()
            : Vector2D.movementVector(super.getPosition(), engine.getHero().getPosition());
        Point2D position = super.getPosition().plus(vector);

        // Finds game elements at the target position.
        Predicate<GameElement> predicate = (element -> element.getPosition().equals(position));
        List<GameElement> listOfElements = engine.getCurrentLevel().getElementsFilteredBy(predicate);
        if (this instanceof Enemy && engine.getHero().getPosition().equals(position)) {
            listOfElements.add(engine.getHero());
        }

        // Handles interactions with items and other entities at the target position.
        if (isIllegalPosition(position)) {
            listOfElements.forEach(element -> {
                if (!(element instanceof Item)) {
                    interactsWith(element);
                }
            });
        } else if (!(listOfElements.stream().anyMatch(element -> element instanceof Enemy))) {
            super.setPosition(position);
            listOfElements.forEach(element -> {
                interactsWith(element);
            });
        }
    }

    // Checks if the position is illegal (e.g., out of bounds, occupied by a wall or closed door).
    private boolean isIllegalPosition(Point2D position) {
        if (!isWithinBounds(position)) {
            return true;
        }

        Predicate<GameElement> predicate = element -> element instanceof Entity || element instanceof Wall ||
            (element instanceof Door && !((Door) element).isOpen());

        List<GameElement> listOfElements = engine.getCurrentLevel().getElementsFilteredBy(predicate);
        if (this instanceof Enemy) {
            listOfElements.remove(this);
            listOfElements.add(engine.getHero());
        }

        for (GameElement element : listOfElements) {
            if (element.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    // Checks if the given position is within the game's grid boundaries.
    private static boolean isWithinBounds(Point2D position) {
        return position.getX() >= 0 && position.getX() < Engine.GRID_WIDTH && position.getY() >= 0 && position.getY() < Engine.GRID_HEIGHT;
    }

    // Factory method to create entities based on their type.
    public static Entity create(String type, String[] info) {
        switch (type) {
            case "Skeleton":
                return new Skeleton(info);
            case "Bat":
                return new Bat(info);
            case "Thug":
                return new Thug(info);
            case "Scorpio":
                return new Scorpio(info);
            case "Thief":
                return new Thief(info);
            default:
                throw new IllegalArgumentException();
        }
    }
}
