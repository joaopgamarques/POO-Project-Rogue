package pt.iscte.poo.example;

public interface Collectable extends Interactable {

    // Collect an item and add it to the inventory.
    public void collect();

    // Use an item and trigger its effect.
    public void use();

    // Drop an item and remove it from the inventory.
    public void drop();

}
