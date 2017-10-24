package com.techelevator;

import java.time.LocalDate;

public class ReservationDetails {
	private Campground campground = null;
	private LocalDate fromDate = null;
	private LocalDate toDate = null;
	private Integer maximumOccupancy = 0;
	private Integer rvLength = 0;
	private Boolean wheelChairAccessible = null;
	private Boolean utilityHookup = null;
	
	
	public Campground getCampground() {
		return campground;
	}
	public void setCampground(Campground campground) {
		this.campground = campground;
	}
	public Integer getMaximumOccupancy() {
		return maximumOccupancy;
	}
	public void setMaximumOccupancy(Integer maximumOccupancy) {
		this.maximumOccupancy = maximumOccupancy;
	}
	public Boolean getWheelChairAccessible() {
		return wheelChairAccessible;
	}
	public void setWheelChairAccessible(Boolean wheelChairAccessible) {
		this.wheelChairAccessible = wheelChairAccessible;
	}
	public Integer getRvLength() {
		return rvLength;
	}
	public void setRvLength(Integer rvLength) {
		this.rvLength = rvLength;
	}
	public Boolean getUtilityHookup() {
		return utilityHookup;
	}
	public void setUtilityHookup(Boolean utilityHookup) {
		this.utilityHookup = utilityHookup;
	}
	public LocalDate getFromDate() {
		return fromDate;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	public LocalDate getToDate() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}
}
