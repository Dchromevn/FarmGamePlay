package model.player;
import java.io.Serializable;

public class Player implements Serializable  {
    private Inventory inventory;

    public Player() {
        this.inventory = new Inventory(500, 200, 100);
    }
    public Inventory getInventory() {
        return inventory;
    }
    @Override
    public String toString() {
        return "Player " + inventory.getStatusString();
    }
}