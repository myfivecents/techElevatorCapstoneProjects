package com.techelevator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Campground {
	private int campgroundId;
	private int parkId;
	private String name;
	private int openFromMonth;
	private int openToMonth;
	private BigDecimal cost;
	public int getCampgroundId() {
		return campgroundId;
	}
	
	@SuppressWarnings("serial")
	private static Map<Integer, String> monthFromInteger = new HashMap<Integer, String>(){{
											put(1, "January");
											put(2, "February");
											put(3, "March");
											put(4, "April");
											put(5, "May");
											put(6, "June");
											put(7, "July");
											put(8, "August");
											put(9, "September");
											put(10, "October");
											put(11, "November");
											put(12, "December");
	}};
	
	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
	}
	public int getParkId() {
		return parkId;
	}
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOpenFromMonth() {
		return openFromMonth;
	}
	public void setOpenFromMonth(int openFromMonth) {
		this.openFromMonth = openFromMonth;
	}
	public int getOpenToMonth() {
		return openToMonth;
	}
	public void setOpenToMonth(int openToMonth) {
		this.openToMonth = openToMonth;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public String  getMonthFromNumber(int month) {
		return monthFromInteger.get(month);
	}
	

}
