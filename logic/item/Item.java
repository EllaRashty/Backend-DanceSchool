package twins.logic.item;


import twins.logic.Id;

public class Item {
	private Id itemId;
	
	public Item() {
		
	}
	
	public Item(Id itemId) {
		this.itemId = itemId;
	}

	public Id getItemId() {
		return itemId;
	}
	
	public void setItemId(Id itemId) {
		this.itemId = itemId;
	}
}
