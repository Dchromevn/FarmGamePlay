package model.resourceManagement;

import model.player.Inventory;
import model.exceptions.NotEnoughResourceException;
public class Store {

	public Store() {
		// TODO Auto-generated constructor stub
	}
	public void sellToPlayer(StoreItem item, Inventory inventory, int amount) throws NotEnoughResourceException{
		int cost = item.getTotalCost(amount);
		inventory.spendMoney(cost);
		item.applyToInventory(inventory, amount);
	}

}
