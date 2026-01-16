package model.resourceManagement;
import model.player.Inventory;
public abstract class StoreItem {
	protected String name;
	protected int price;
	public abstract void applyToInventory(Inventory inventory, int amount);
	public int getTotalCost(int amount) {
		return price*amount;
	}
	public String getName() {
		return name;
	}
}
