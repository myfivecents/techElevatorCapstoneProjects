package com.techelevator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class CampgroundCLI {
	
	private static final String VIEW_CAMPGROUNDS_OPTION = "View Campgrounds";
	private static final String RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String SEARCH_FOR_AVAILABLE_RESERVATION = "Search for Available Reservation";
	private static final String SHOW_ALL_RESERVATIONS_WITHIN_30_DAYS = "Show all reservations in the next 30 days";
	private static final String[] PARK_INFO_OPTIONS = { VIEW_CAMPGROUNDS_OPTION, 
															SEARCH_FOR_AVAILABLE_RESERVATION, 
															RETURN_TO_PREVIOUS_SCREEN, 
															SHOW_ALL_RESERVATIONS_WITHIN_30_DAYS};

	private static final String[] VIEW_CAMPGROUNDS_OPTIONS = { SEARCH_FOR_AVAILABLE_RESERVATION,
															RETURN_TO_PREVIOUS_SCREEN,
															SHOW_ALL_RESERVATIONS_WITHIN_30_DAYS};

	private CLIMenu menu = new CLIMenu(System.in, System.out);
	private Scanner userInput;
	
	private ReservationManager manager;
	
	private Stack<String> screenStack = new Stack<String>();
	private static final String SELECT_PARK = "screen1";
	private static final String PARK_INFO = "screen2";
	private static final String VIEW_CAMPGROUNDS = "screen3";
	private static final String SELECT_CAMPGROUND = "screen4";
	private static final String SELECT_SITE = "screen5";
	private static final String THIRTY_DAY_RESERVATION = "screen6";

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		CampgroundCLI cli = new CampgroundCLI(dataSource);
		cli.run();
	}

	public CampgroundCLI(DataSource dataSource) {
		// create your DAOs here
		manager = new ReservationManager(dataSource);
		userInput = new Scanner(System.in);
	}
	
	public void run() {
		screenStack.push(SELECT_PARK);
		while (true) {
			switch (screenStack.peek()) {
			case SELECT_PARK:
				displayScreenOne();
				break;
			case PARK_INFO:
				displayScreenTwo();
				break;
			case VIEW_CAMPGROUNDS:
				displayScreenThree();
				break;
			case SELECT_CAMPGROUND:
				displayScreenFour();
				break;
			case SELECT_SITE:
				displayScreenFive();
				break;
			case THIRTY_DAY_RESERVATION:
				displayScreenSix();
				break;
			
			}
		}
	}
	


	public void displayScreenOne() {
		System.out.println("Select a Park for Further Details");
		List<Park> parks = manager.getAllParks();
		Park selectedPark = menu.getChoiceFromOptions(parks);
		manager.setSelectedPark(selectedPark);
		screenStack.push(PARK_INFO);
		
	}
	
	private void displayScreenTwo() {
		Park selectedPark = manager.getSelectedPark();
		System.out.println(selectedPark.getName() + " National Park");
		System.out.println("Location: " + selectedPark.getLocation());
		System.out.println("Established: " + selectedPark.getEstablishedDate());
		System.out.println("Area: " + selectedPark.getArea() + " sq km");
		System.out.println("Annual Visitors: " + selectedPark.getVisitors());
		System.out.println();
		System.out.println(selectedPark.getDescription());
		System.out.println();
		
		Object choice = menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
		switch(choice.toString()) {
			case VIEW_CAMPGROUNDS_OPTION:
				screenStack.push(VIEW_CAMPGROUNDS);
				break;
			case SEARCH_FOR_AVAILABLE_RESERVATION:
				screenStack.push(SELECT_CAMPGROUND);
				break;
			case RETURN_TO_PREVIOUS_SCREEN:
				selectedPark = null;
				screenStack.pop();
				break;
			case SHOW_ALL_RESERVATIONS_WITHIN_30_DAYS:
				screenStack.push(THIRTY_DAY_RESERVATION);
				break;
		}
	}
	
	private void displayScreenThree() {
		Park selectedPark = manager.getSelectedPark();
		System.out.println(selectedPark.getName() + " National Park Campgrounds");
		System.out.println();
		List<Campground> campgrounds = manager.getListOfCampgroundsAtSelectedPark();
		printCampgrounds(campgrounds);
		Object choice = menu.getChoiceFromOptions(VIEW_CAMPGROUNDS_OPTIONS);
		switch(choice.toString()) {
			case SEARCH_FOR_AVAILABLE_RESERVATION:
				screenStack.push(SELECT_CAMPGROUND);
				break;
			case RETURN_TO_PREVIOUS_SCREEN:
				screenStack.pop();
				break;
			case SHOW_ALL_RESERVATIONS_WITHIN_30_DAYS:
				screenStack.push(THIRTY_DAY_RESERVATION);
				break;
		}
		
		
	}
	
	private void displayScreenFour() {
		List<Campground> campgrounds = manager.getListOfCampgroundsAtSelectedPark();
		printCampgrounds(campgrounds);
		System.out.println("Which campground (enter '0' to cancel or 'a' for all)? ");
		
		String stringCampgroundChoice = userInput.nextLine();
		
		
		ReservationDetails details = new ReservationDetails();
		int intCampgroundChoice = 0;
		if (stringCampgroundChoice.equalsIgnoreCase("a")) {
			// don't set a campground
		} else {
			try {
				intCampgroundChoice = Integer.parseInt(stringCampgroundChoice);
			} catch (NumberFormatException e) {
				System.out.println("That is not a valid option");
			}
			if (intCampgroundChoice == 0) {
				screenStack.pop();
				return;
			} else {
				details.setCampground(campgrounds.get(intCampgroundChoice - 1));
			}
		}
		//from date
		System.out.print("What is the arrival date? yyyy/mm/dd" );
		System.out.println();
		String fromDateString = userInput.nextLine();
		fromDateString = fromDateString.replaceAll("/", "-");
		LocalDate fromDate = LocalDate.parse(fromDateString);
		details.setFromDate(fromDate);
		//to date
		System.out.println("What is the departure date? yyyy/mm/dd ");
		String toDateString = userInput.nextLine();
		toDateString = toDateString.replaceAll("/", "-");
		LocalDate toDate = LocalDate.parse(toDateString);
		details.setToDate(toDate);
		//maximum occupancy
		System.out.println("What is the maximum Occupancy (enter '0' to skip)?");
		Integer maximumOccupancy = userInput.nextInt();
		userInput.nextLine();
		if (maximumOccupancy > 0) {
			details.setMaximumOccupancy(maximumOccupancy);
		}
		//rv length
		System.out.println("What is your RV Length (enter '0' to skip)?");
		Integer rvLength = userInput.nextInt();
		userInput.nextLine();
		if (rvLength > 0) {
			details.setRvLength(maximumOccupancy);
		}
		//wheel chair accessible
		System.out.println("Do you need a wheelchair accessible site? yes/no");
		String wheelchair = userInput.nextLine();
		if (wheelchair == "yes") {
			details.setWheelChairAccessible(true);
		}
		//utility hookup
		System.out.println("Do you need a utility hookup? yes/no");
		String utilityHookup = userInput.nextLine();
		if (utilityHookup == "yes") {
			details.setUtilityHookup(true);
		}
		manager.setDetails(details);
		screenStack.push(SELECT_SITE);
		
	}
	
	private void displayScreenFive() {
		System.out.println("Results Matching Your Search Criteria");
		printAvailableSites();
		Integer siteNumber= null;
		while (true) {
			System.out.println("Which campground id is your chosen site in (enter 0 to cancel)?");
			Integer campgroundId = userInput.nextInt();
			userInput.nextLine();
			if(campgroundId == 0) {
				screenStack.pop();
				return;
			}
			System.out.println("Which site should be reserved");
			siteNumber = userInput.nextInt();
			userInput.nextLine();
			if (siteNumber == 0) {
				screenStack.pop();
				return;
			}
			if (manager.setSelectedSite(campgroundId, siteNumber)) {
				//site was successfully found
				break;
			} else {
				System.out.println("That is not an available site. Please try again.");
			}
		}
		System.out.println("What name should the reservation be made under?");
		String reservationName = userInput.nextLine(); 
		Reservation reservation = new Reservation();
		reservation = manager.makeReservation(reservationName);
		
		System.out.println(String.format("The reservation has been made and the confirmation id is %d", reservation.getReservationId()));
		while (screenStack.size() > 1) {
			screenStack.pop();
		}
		
	}
	
	private void displayScreenSix(){
		List<Reservation> thirtyDayReservationList = manager.get30DayReservationList();
		System.out.println(String.format("%-20s%-15s%-15s%-30s%-20s%s", "Campground Name","Site Number", "Reservation ID", "Name", "From Date", "To Date" ));
		for (int i = 0; i < thirtyDayReservationList.size(); i++) {
			Site site = manager.getSiteForSiteId(thirtyDayReservationList.get(i).getSiteId());
			Campground campground = manager.getCampgroundForSite(site);
			System.out.println(String.format("%-20s%-15d%-15d%-30s%-20s%s", 
															campground.getName(),
															site.getSiteNumber(),
															thirtyDayReservationList.get(i).getReservationId(),
															thirtyDayReservationList.get(i).getName(),
															thirtyDayReservationList.get(i).getFromDate(),
															thirtyDayReservationList.get(i).getToDate()));
		}
		screenStack.pop();
		System.out.println("Press enter to continue");
		userInput.nextLine();
	}
	
	private void printCampgrounds(List<Campground> campgrounds) {
		System.out.println(String.format("      %-20s%-11s%-11s%s", "Name", "Open", "Close", "Daily Fee"));
		for (int i = 0; i < campgrounds.size(); i++) {
			System.out.println(String.format("#%-5d%-20s%-11s%-11s$%#.2f", (i+1), 
																		  campgrounds.get(i).getName(), 
																		  campgrounds.get(i).getMonthFromNumber(campgrounds.get(i).getOpenFromMonth()), 
																		  campgrounds.get(i).getMonthFromNumber(campgrounds.get(i).getOpenToMonth()), 
																		  campgrounds.get(i).getCost()));
		}
	}
	
	private void printAvailableSites() {
		List<Site> availableSites = manager.getListOfAvailableSites();
		System.out.println(String.format("%-20s%-15s%-11s%-15s%-13s%-16s%-10s%s", "Campground Name", "Campground ID", "Site No.", "Max Occup.", "Accessible?", "Max RV Length", "Utility", "Cost"));

		for(int i = 0; i < availableSites.size(); i++) {
			Site currentSite = availableSites.get(i);
			Campground currentCampground = manager.getCampgroundForSite(currentSite);
			BigDecimal cost = manager.calculateReservationCost(currentSite);
			
			System.out.println(String.format("%-20s%-15s%-11d%-15d%-13s%-16s%-10s$%#.2f", 
																		currentCampground.getName(),
																		currentCampground.getCampgroundId(),
																		availableSites.get(i).getSiteNumber(), 
																		 availableSites.get(i).getMaxOccupancy(), 
																		 ((availableSites.get(i).isAccessible()) ? "Yes": "No"), 
																		 (availableSites.get(i).getMaxRVLength() == 0 ? "N/A" : Integer.toString(availableSites.get(i).getMaxRVLength())), 
																		 (availableSites.get(i).isUtilities() ? "Yes" : "N/A"), 
																		 cost));
		}
	}
}










