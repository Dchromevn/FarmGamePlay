package player;

public class Player {
    private Inventory inventory;

    public Player() {
        this.inventory = new Inventory(500, 100, 50);
    }
    public Inventory getInventory() {
        return inventory;
    }
    @Override
    public String toString() {
        return "Player " + inventory.getStatusString();
    }
}