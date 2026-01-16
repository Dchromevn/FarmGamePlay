package model.resourceManagement;
import model.player.Inventory;
public class MedicineItem extends StoreItem{
	public MedicineItem() {
		this.name = "Medicine";
		this.price = 50;
	}
	@Override
	public void applyToInventory(Inventory inventory, int amount) {
		inventory.gainMedicine(amount);
	}

}
