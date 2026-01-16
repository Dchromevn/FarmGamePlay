package model.resourceManagement;
import model.player.Inventory;
public class FertilizerItem extends StoreItem{
	public FertilizerItem() {
		this.name = "Fertilizer";
		this.price = 3;
	}
	@Override
	public void applyToInventory(Inventory inventory, int amount) {
		inventory.gainFertilizer(amount*25);
	}

}
