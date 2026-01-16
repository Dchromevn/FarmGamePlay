package model.resourceManagement;
import model.player.Inventory;
public class WaterItem extends StoreItem{
	public WaterItem() {
		this.name = "Water";
		this.price = 2;
	}
	@Override
	public void applyToInventory(Inventory inventory, int amount) {
		inventory.gainWater(amount*25);
	}

}
