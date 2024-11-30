package pt.iscte.poo.example;

// Represents an element that can interact with another game element.
public interface Interactable {

    // Defines the interaction between this element and another game element.
    public void interactsWith(GameElement element);

}
