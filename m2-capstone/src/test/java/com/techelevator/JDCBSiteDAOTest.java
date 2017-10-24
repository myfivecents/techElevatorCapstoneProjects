package com.techelevator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDCBSiteDAOTest {
	private JDBCSiteDAO dao;
	private JdbcTemplate jdbcTemplate;
	private static SingleConnectionDataSource dataSource;
	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}
	
	@Before
	public void setup() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		dao = new JDBCSiteDAO(dataSource);
		jdbcTemplate.update("DELETE FROM reservation");
		jdbcTemplate.update("DELETE FROM site");
		jdbcTemplate.update("DELETE FROM campground");
		jdbcTemplate.update("DELETE FROM park");
		//make park
		jdbcTemplate.update("INSERT INTO park (park_id, name, location, establish_date, area, visitors, description)" + 
				"VALUES (1, 'Acadia', 'Maine', '1919-02-26', 47389, 2563129, 'Covering most of Mount Desert Island and other coastal islands, Acadia features the tallest mountain on the Atlantic coast of the United States, granite peaks, ocean shoreline, woodlands, and lakes. There are freshwater, estuary, forest, and intertidal habitats.');");
		//make campgrounds
		jdbcTemplate.update("INSERT INTO campground (campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee) VALUES (1, 1, 'Blackwoods', '04', '12', 35.00);");
		//make sites
		jdbcTemplate.update("INSERT INTO site (site_id, site_number, campground_id, accessible) VALUES (1, 1, 1, TRUE);" + 
				"INSERT INTO site (site_id, site_number, campground_id, accessible) VALUES (2, 2, 1, TRUE);" + 
				"INSERT INTO site (site_id, site_number, campground_id, accessible) VALUES (3, 3, 1, TRUE);");
		jdbcTemplate.update("INSERT INTO site (site_id, site_number, campground_id, max_rv_length, accessible, utilities) VALUES (4, 4, 1, 35, TRUE, TRUE);" + 
				"INSERT INTO site (site_id, site_number, campground_id, max_rv_length, accessible, utilities) VALUES (5, 5, 1, 35, TRUE, FALSE);" + 
				"INSERT INTO site (site_id, site_number, campground_id, max_rv_length, accessible, utilities) VALUES (6, 6, 1, 40, FALSE, TRUE);");
		//make reservations
		jdbcTemplate.update("INSERT INTO reservation (site_id, name, from_date, to_date) VALUES (1, 'Lockhart Family Reservation', '2017-10-5', '2017-10-10');");
	}
	
	@After
	public void rollback() throws SQLException{
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void query_test() {
		String sqlTemplate = "SELECT site_id " +
							"FROM site " +
							"WHERE accessible = ? " +
							"AND max_occupancy = ?;";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlTemplate, new Object[] {false, new Integer(6)});
		
		while (results.next()) {
			System.out.println(results.getInt("site_id"));
		}
	}
	
	@Test
	public void get_sites_available_gets_all_sites_when_no_overlaps() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 sites", 6, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_campground_season_overlaps() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-03-01");
		LocalDate toDate = LocalDate.parse("2017-03-04");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 0 sites", 0, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_front_overlaps() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-03");
		LocalDate toDate = LocalDate.parse("2017-10-07");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_back_overlaps() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-07");
		LocalDate toDate = LocalDate.parse("2017-10-11");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_reservation_inside_previous_reservation() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-07");
		LocalDate toDate = LocalDate.parse("2017-10-08");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_reservations_identical() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-05");
		LocalDate toDate = LocalDate.parse("2017-10-10");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_excludes_sites_when_reservation_dominates_previous_reservation() {
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		
		LocalDate fromDate = LocalDate.parse("2017-10-03");
		LocalDate toDate = LocalDate.parse("2017-10-11");
		List<Site> siteList = dao.getSitesAvailableAtCampground(campground, fromDate, toDate);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_gets_all_sites() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 6, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_campground_offseason() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-01-01");
		LocalDate toDate = LocalDate.parse("2017-04-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 0, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_reservation_front_overlaps() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-04");
		LocalDate toDate = LocalDate.parse("2017-10-07");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_reservation_back_overlaps() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-07");
		LocalDate toDate = LocalDate.parse("2017-10-11");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_reservation_inside_previous_reservation() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-06");
		LocalDate toDate = LocalDate.parse("2017-10-07");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_reservation_dominates_other_reservation() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-04");
		LocalDate toDate = LocalDate.parse("2017-10-11");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_reservation_the_same_as_previous_reservation() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-05");
		LocalDate toDate = LocalDate.parse("2017-10-10");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_when_max_occupancy_too_small() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-04");
		LocalDate toDate = LocalDate.parse("2017-10-07");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setMaximumOccupancy(10);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 0, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_with_too_small_rv_length() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-04");
		LocalDate toDate = LocalDate.parse("2017-10-07");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setRvLength(40);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 6 site", 1, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_without_accessibility() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setWheelChairAccessible(true);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 5 site", 5, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_with_accessibility() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setWheelChairAccessible(false);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 1 site", 1, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_with_utility() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setUtilityHookup(false);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 4 site", 4, siteList.size());
	}
	
	@Test
	public void get_sites_available_details_excludes_sites_without_utility() {
		ReservationDetails details = new ReservationDetails();
		Campground campground = new Campground();
		campground.setCampgroundId(1);
		details.setCampground(campground);
		LocalDate fromDate = LocalDate.parse("2017-10-01");
		LocalDate toDate = LocalDate.parse("2017-10-04");
		details.setFromDate(fromDate);
		details.setToDate(toDate);
		details.setUtilityHookup(true);
		List<Site> siteList = dao.getSitesAvailable(details);
		
		Assert.assertEquals("There should be 2 site", 2, siteList.size());
	}
}
