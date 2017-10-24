package com.techelevator;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;


public class CLIMenu {
	private PrintWriter out;
	private Scanner in;
	

	public CLIMenu(InputStream input, OutputStream output) {
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
	
	public Park getChoiceFromOptions(List<Park> options) {
		Park choice = null;
		while(choice == null) {
			displayMenuOptions(options);
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
	
		
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	private void displayMenuOptions(List<Park> options) {
		out.println();
		for(int i = 0; i < options.size(); i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options.get(i));
		}
		out.println("Q) Quit");
		
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	private Park getChoiceFromUserInput(List<Park> options) {
		Park choice = null;
		String userInput = in.nextLine();
		
		if(userInput.equalsIgnoreCase("Q")) {
			System.exit(0);
		}
		
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption > 0 && selectedOption <= options.size()) {
				choice = options.get(selectedOption - 1);
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
}
