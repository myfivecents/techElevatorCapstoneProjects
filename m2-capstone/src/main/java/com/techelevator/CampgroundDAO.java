package com.techelevator;

import java.util.List;

public interface CampgroundDAO {
	public Campground getCampgroundWithCampgroundId(Integer id);
	public List<Campground> getAllCampgroundsAtPark(Park currentPark);
}
