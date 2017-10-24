package com.techelevator.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.techelevator.MenuItem;
import com.techelevator.VendingMachine;
import com.techelevator.VendingMachineCLI;

public class Menu {
	
	private File salesReport;
	private PrintWriter out;
	private Scanner in;
	private PrintWriter fileOutput;
	private File auditLog;
	private PrintWriter salesFileOutput;

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}
	
	public Object getChoiceFromOptions(Object[] options, VendingMachine vendingMachine) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options, vendingMachine);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	

	private void displayMenuOptions(Object[] options) {
		out.println();
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options[i]);
		}
		// IF IN PURCHASE MENU, DISPLAY CURRENT VALUE
		if(options.length > 2) {
			
		}
		
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	private void displayMenuOptions(Object[] options, VendingMachine vendingMachine) {
		out.println();
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options[i]);
		}
		// IF IN PURCHASE MENU, DISPLAY CURRENT VALUE
		if(options.length > 2) {
			out.println("Current Money Provided: " + String.format("$%#.2f", vendingMachine.getAmountFed()));
		}
		
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	public BigDecimal feedMoney(BigDecimal amountFedDisplay) {
		BigDecimal amountFed = new BigDecimal(0);
		
		int moneyInserted = 0;
		System.out.println("Your current balance is: $" + String.format("%#.2f", amountFedDisplay));
		do {
		System.out.println("Please insert a $1, $2, $5, or $10 bill");
		System.out.print(">>> ");
		moneyInserted = in.nextInt();
			if (moneyInserted == 1) {
				amountFed = amountFed.add(new BigDecimal(moneyInserted));
			} else if (moneyInserted == 2) {
				amountFed = amountFed.add(new BigDecimal(moneyInserted));
			} else if (moneyInserted == 5) {
				amountFed = amountFed.add(new BigDecimal(moneyInserted));
			} else if (moneyInserted == 10) {
				amountFed = amountFed.add(new BigDecimal(moneyInserted));
			} else {
				moneyInserted = 0;
			}
		} while (moneyInserted == 0);
		in.nextLine();
		return amountFed;
	}
	
	public String selectProduct() {
		System.out.println("Please make a selection:");
		System.out.print(">>> ");
		String selection = in.nextLine().toUpperCase();
		return selection;
	}
	
	public void initializeAuditLog() throws FileNotFoundException {
		auditLog = new File("Log.txt");
		fileOutput = new PrintWriter(auditLog);
		
		try {
			auditLog.createNewFile();
		} catch (Exception e) {
			System.out.println("Invalid file path: log not initialized");
		}
	}
	
	
	// Create a Write-To Audit method
	public void writeToAuditLog(String actionLine, BigDecimal previousBalance, BigDecimal newBalance) {
		DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm:ss a ");
		Date dateObj = new Date();
	
		fileOutput.print(df.format(dateObj)); // Audit log string - Date/Time
		fileOutput.println(String.format("%-18s$%#.2f       $%#.2f", actionLine, previousBalance, newBalance));
		fileOutput.flush();
	}
	
	public void createHeader() {
		fileOutput.println(String.format("%-9s%-12s%-18s%-12s%s", "Date", "Time", "Action", "Prev. Bal.", "Balance"));
	}
	
	public void initializeSalesReport() throws FileNotFoundException {
		salesReport = new File("SalesReport.txt");
		salesFileOutput = new PrintWriter(salesReport);
		
		try {
			salesReport.createNewFile();
		} catch (Exception e) {
			System.out.println("Invalid file path: log not initialized");
		}
	}
	
	public void writeToSalesReport(VendingMachine vendingMachine, BigDecimal totalSales) {
		List<MenuItem> itemList = vendingMachine.getItemList();
		for (MenuItem item : itemList) {
			salesFileOutput.println(item.getItemName() + "|" + (5 - item.getQuantity()));
		}
		salesFileOutput.println();
		salesFileOutput.print("**TOTAL SALES** $" + totalSales);
		salesFileOutput.flush();
	}
	
}


