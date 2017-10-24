package com.techelevator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MenuItem {
	private String location;
	private String itemName;
	private BigDecimal price;
	private String itemSound;
	private int quantity = 5;
	private boolean canBuy = true;
	
	
	public MenuItem(String item) {
		String[] itemArr = item.split("\\|");
		location = itemArr[0];
		itemName = itemArr[1];
		price = new BigDecimal(itemArr[2]);
		price.setScale(2, RoundingMode.CEILING);
		
		if (location.contains("A")) {
			itemSound = "Crunch Crunch, Yum!";
		} else if (location.contains("B")) {
			itemSound = "Munch Munch, Yum!";
		} else if (location.contains("C")) {
			itemSound = "Glug Glug, Yum!";
		} else if (location.contains("D")) {
			itemSound = "Chew Chew, Yum!";
		} else {
			itemSound = "Item not available";
		}
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public String getItemSound() {
		return itemSound;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void buyItem() {
		quantity--;
	}
	
	public boolean canBuy() {
		canBuy = true;
		if(quantity > 0) {
			canBuy = true;
		}
		else canBuy = false;
		return canBuy;
	}
	
	
	
}
