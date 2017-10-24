package com.techelevator;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS,
													   MAIN_MENU_OPTION_PURCHASE };
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, 
															PURCHASE_MENU_OPTION_SELECT_PRODUCT, 
															PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
	private Menu menu;
	
	private VendingMachine vendingMachine = new VendingMachine();
	private List<String> soundList = new ArrayList<String>();
	private BigDecimal totalSales = new BigDecimal("0").setScale(2, RoundingMode.CEILING);
	
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}
	
	public void run() throws FileNotFoundException {
		menu.initializeAuditLog(); // Create an Audit Log to write out to
		menu.createHeader();
		menu.initializeSalesReport();
		
		while(true) {
			boolean exitPurchase = false;
			String choice = (String)menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			
			
			if(choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				vendingMachine.listItems();
				
			} else if(choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				while(!exitPurchase) {
					 choice = (String)menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, vendingMachine);
					 
					 
					if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						BigDecimal previousBalance = vendingMachine.getAmountFed();
						vendingMachine.setAmountFed(vendingMachine.getAmountFed().add(menu.feedMoney(vendingMachine.getAmountFed())));
						menu.writeToAuditLog("FEED MONEY:", previousBalance, vendingMachine.getAmountFed());
						exitPurchase = true;
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						BigDecimal previousBalance = vendingMachine.getAmountFed();
						
						//go to menu, ask for a selection
						String selection = menu.selectProduct();
						
						//take selection, to to vending machine
						String itemSound = vendingMachine.selectProduct(selection);
												
						String itemName = vendingMachine.getProductName(selection);
						
						// Print out to Audit Log
						menu.writeToAuditLog(itemName, previousBalance, vendingMachine.getAmountFed());
						
						
						if (itemSound.equals("Item is sold out") || itemSound.equals("Item does not exist") || itemSound.equals("Please insert more money")) {
							System.out.println(itemSound);
						} else {
							soundList.add(itemSound);
							totalSales = totalSales.add(vendingMachine.getProductPrice(selection));
							// Write to Audit form if product was purchased
						}
						//in vending machine, run method to select product
					} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// Create method for dispensing change & Update current balance to be equal to zero
						System.out.println("Your current balance is $" + String.format("%#.2f", vendingMachine.getAmountFed()));
						menu.writeToAuditLog("GIVE CHANGE:", vendingMachine.getAmountFed(), new BigDecimal(0));
						int[] change = vendingMachine.getChange();
						// Display current balance to be equal to zero
						System.out.println("Coins to be dispensed:");
						System.out.println(change[0] + " quarters");
						System.out.println(change[1] + " dimes");
						System.out.println(change[2] + " nickels");
						
						
						System.out.println("The balance is now $" + vendingMachine.getAmountFed());
						// Write GIVE CHANGE to log with balance
						
						// Output strings
						for(String sound : soundList) {
							System.out.println(sound);
						}
						menu.writeToSalesReport(vendingMachine, totalSales);
						// Exit
						System.exit(0);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}


