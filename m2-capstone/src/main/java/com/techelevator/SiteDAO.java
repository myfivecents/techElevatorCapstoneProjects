package com.techelevator;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

	public List<Site> getSitesAvailableAtCampground(Campground campground, LocalDate fromDate, LocalDate toDate);
	public List<Site> getSitesAvailable(ReservationDetails details);
}
