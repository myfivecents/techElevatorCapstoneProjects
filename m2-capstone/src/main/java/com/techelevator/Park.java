package com.techelevator;

import java.sql.Date;
import java.time.LocalDate;

public class Park {
	private int parkId;
	private String name;
	private String location;
	private LocalDate establishedDate;
	private int area;
	private int visitors;
	private String description;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getEstablishedDate() {
		return establishedDate;
	}
	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate.toLocalDate();
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getVisitors() {
		return visitors;
	}
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
