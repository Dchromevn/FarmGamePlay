package model.resourceManagement;
import model.player.Inventory;
import utility.CropType;
public class SeedItem extends StoreItem {
	protected CropType cropType;
	public SeedItem(CropType cropType) {
		// TODO Auto-generated constructor stub
		this.name = cropType.getCropName()+ " seed";
		this.price = cropType.getSeedPrice();
		this.cropType = cropType;
	}
	@Override
	public void applyToInventory(Inventory inventory, int amount) {
		inventory.addSeed(cropType, amount);
	}

}
