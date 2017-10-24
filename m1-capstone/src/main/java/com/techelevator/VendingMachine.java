package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendingMachine {
	private List<MenuItem> vendingItems = new ArrayList<MenuItem>();	//list to hold information about menu items
	private BigDecimal amountFed = new BigDecimal(0).setScale(2, RoundingMode.CEILING);		//amount fed into the machine
	
	
	
	public VendingMachine() {
		File stockFile = null;
		
		try {
			stockFile = new File("vendingmachine.csv");		//file to read in options
			try (Scanner stockInput = new Scanner(stockFile)) {		//take in information from file, store in list
				while(stockInput.hasNextLine()) {
					MenuItem currentItem = new MenuItem(stockInput.nextLine());
					vendingItems.add(currentItem);
					
				}
			
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch ( NullPointerException e){
			System.out.println("stock file does not exist");
		}
		
		
		
	}
	
	public MenuItem getMenuItem(int index) {	//returns item at index given
		return vendingItems.get(index);
	}
	
	public int getItemsLeft() {				//this method is to test the number of items read in. actual function limited, see test
		int itemsLeft = vendingItems.size();
		for (MenuItem item : vendingItems) {
			if (item.getQuantity() == 0) {
				itemsLeft--;
			}
		}
		return itemsLeft;
	}
	
	//this method is to display options when called from main
	public void listItems() {
		System.out.println(String.format("%-4s%-20s%-8s%-8s", "##", "Item Name", "Price", "Quantity"));
		System.out.println("----------------------------------------");
		for (MenuItem item : vendingItems) {
			String quantity = "";
			if (item.getQuantity() == 0) {
				quantity = "SOLD OUT";
			}else {
				quantity = Integer.toString(item.getQuantity());
			}
			System.out.println(String.format("%-4s%-20s$%#.2f   %-8s", item.getLocation(), item.getItemName(), item.getPrice(), quantity));
		}
	}
	
	public BigDecimal getAmountFed() {
		return amountFed.setScale(2, RoundingMode.CEILING);
	}

	public void setAmountFed(BigDecimal amountFed) {
		this.amountFed = amountFed.setScale(2, RoundingMode.CEILING);
	}
	
	public String selectProduct(String selection) {
		for (MenuItem item : vendingItems) {
			if (item.getLocation().equals(selection) && item.canBuy() && item.getPrice().compareTo(amountFed) <= 0) {
				item.buyItem();
				amountFed = amountFed.subtract(item.getPrice()).setScale(2, RoundingMode.CEILING);
				return item.getItemSound();
			} else if (item.getLocation().equals(selection) && !item.canBuy()){
				return "Item is sold out";
			} else if (item.getLocation().equals(selection) && item.getPrice().compareTo(amountFed) == 1) {
				return "Please insert more money";
			}
		}
		return "Item does not exist";
	}
	
	public String getProductName(String selection) {
		for(MenuItem item : vendingItems) {
			if(item.getLocation().equals(selection)) {
				return item.getItemName();
			}
		}
		return "Invalid Selection";
			
	}
	
	public BigDecimal getProductPrice(String selection) {
		for(MenuItem item : vendingItems) {
			if(item.getLocation().equals(selection)) {
				return item.getPrice();
			}
		}
		return new BigDecimal("0");
			
	}
	
	
	public int[] getChange() {
		int [] change = new int[3];
		while (amountFed.compareTo(new BigDecimal("0").setScale(1, RoundingMode.CEILING)) == 1) {
			if (amountFed.compareTo(new BigDecimal(".25").setScale(2, RoundingMode.CEILING)) >= 0) {
				amountFed = amountFed.subtract(new BigDecimal("0.25").setScale(2, RoundingMode.CEILING));
				change[0]++;
			} else if (amountFed.compareTo(new BigDecimal(".1").setScale(1, RoundingMode.CEILING)) >= 0) {
				amountFed = amountFed.subtract(new BigDecimal("0.1").setScale(1, RoundingMode.CEILING));
				change[1]++;
			} else {
				amountFed = amountFed.subtract(new BigDecimal("0.05").setScale(2, RoundingMode.CEILING));
				change[2]++;
				
			}
		}
		
		return change;
	}
	
	public List<MenuItem> getItemList() {
		return vendingItems;
	}
	
}

















