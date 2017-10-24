package com.techelevator;

import java.util.List;

public class DAOManager {
	private Park currentPark;
	private Campground currentCampground;
	private Site currentSite;
//	private Reservation currentReservation;
	
	private ReservationDAO reservationDao;
	private ParkDAO parkDAO;
	private CampgroundDAO campgroundDAO;
	private SiteDAO siteDAO;
	
	
	public List<Park> getAllParks() {
		List<Park> allParks = parkDAO.getAllParks();
		return allParks;
	}
	
}
