package com.techelevator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class ReservationManager {
	private Park selectedPark;
	private Campground selectedCampground;
	private List<Campground> listOfCampgroundsAtPark;
	private List<Site> listOfAvailableSites;
	private ReservationDetails details = new ReservationDetails();
	private Integer numberPerCampground = 5;
	private Site selectedSite;
	
	private JDBCParkDAO parkDao;
	private JDBCCampgroundDAO campgroundDao;
	private JDBCSiteDAO siteDao;
	private JDBCReservationDAO reservationDao;
	
	public ReservationManager(DataSource datasource) {
		parkDao = new JDBCParkDAO(datasource);
		campgroundDao = new JDBCCampgroundDAO(datasource);
		siteDao = new JDBCSiteDAO(datasource);
		reservationDao = new JDBCReservationDAO(datasource);
		
		listOfCampgroundsAtPark = new ArrayList<Campground>();
		listOfAvailableSites = new ArrayList<Site>();
		forgetSelectedDetails();
	}
	
	public List<Park> getAllParks(){
		return parkDao.getAllParks();
	}
	
	public Campground getCampgroundForSite(Site site) {
		Campground campground = campgroundDao.getCampgroundWithCampgroundId(site.getCampgroundId());
		
		return campground;
	}
	
	public void setSelectedPark(Park park) {
		selectedPark = park;
		setListOfCampgroundsAtSelectedPark();
	}
	
	public Park getSelectedPark() {
		return selectedPark;
	}
	
	public void forgetSelectedPark() {
		selectedPark = null;
		selectedCampground = null;
		listOfCampgroundsAtPark.removeAll(listOfCampgroundsAtPark);
		listOfAvailableSites.removeAll(listOfAvailableSites);
	}
	
	public void forgetSelectedDetails() {
		details = new ReservationDetails();
		details.setFromDate(LocalDate.parse("0001-01-01"));
		details.setToDate(LocalDate.parse("0001-01-02"));
		setListOfAvailableSites();
	}
	
	private void setListOfCampgroundsAtSelectedPark(){
		listOfCampgroundsAtPark = campgroundDao.getAllCampgroundsAtPark(selectedPark);
	}
	
	public List<Campground> getListOfCampgroundsAtSelectedPark(){
		return listOfCampgroundsAtPark;
	}
	
	public void setSelectedCampground(Campground campground) {
		selectedCampground = campground;
	}
	
	public void setDetails(ReservationDetails details) {
		this.details = details;
		setListOfAvailableSites();
	}
	
	private void setListOfAvailableSites(){
		listOfAvailableSites = new ArrayList<Site>();
		if (details.getCampground() == null) {
			for (Campground campground : listOfCampgroundsAtPark) {
				details.setCampground(campground);
				if(siteDao.getSitesAvailable(details).size() < numberPerCampground) {
					listOfAvailableSites.addAll(siteDao.getSitesAvailable(details));

				}else {
					listOfAvailableSites.addAll(siteDao.getSitesAvailable(details).subList(0, numberPerCampground));
				}
			}
		} else {
			if(siteDao.getSitesAvailable(details).size() < numberPerCampground) {
				listOfAvailableSites.addAll(siteDao.getSitesAvailable(details));

			}else {
				listOfAvailableSites.addAll(siteDao.getSitesAvailable(details).subList(0, numberPerCampground));
			}
		} 
	}
	
	public List<Site> getListOfAvailableSites(){
		return listOfAvailableSites;
	}
	
	public BigDecimal calculateReservationCost(Site site) {
		BigDecimal lengthOfReservation = new BigDecimal(details.getFromDate().until(details.getToDate(), ChronoUnit.DAYS));
		BigDecimal totalCost = getCampgroundForSite(site).getCost().multiply(lengthOfReservation);
		totalCost.setScale(2, RoundingMode.CEILING);
		return totalCost;
	}
	
	public Boolean setSelectedSite(Integer campgroundId, Integer siteNumber) {
		for (Site site : listOfAvailableSites) {
			if (site.getCampgroundId() == campgroundId && site.getSiteNumber() == siteNumber) {
				selectedSite = site;
				return true;
			}
		}
		return false;
	}
	
	public List<Reservation> get30DayReservationList(){
		return reservationDao.getParkReservationsForNext30Days(selectedPark);
	}
	
	public Site getSiteForSiteId(Integer id) {
		Site site = siteDao.getSiteFromSiteId(id);
		return site;
	}
	
	
	public Reservation makeReservation(String name) {
		Reservation reservation = new Reservation();
		reservation.setName(name);
		reservation.setFromDate(details.getFromDate());
		reservation.setToDate(details.getToDate());
		reservation.setSiteId(selectedSite.getSiteId());
		return reservationDao.addReservation(reservation);
	}
}
