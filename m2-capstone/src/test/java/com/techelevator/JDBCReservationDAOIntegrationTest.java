package com.techelevator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class JDBCReservationDAOIntegrationTest {
	private static final String TEST_RESERVATION = "test_reservation";
	
	private static SingleConnectionDataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private JDBCReservationDAO dao;

	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}
	
	@AfterClass
	public static void closeDAtaSource()  throws SQLException {
		dataSource.destroy();
	}
	
	@Before
	public void setup() {
		dao = new JDBCReservationDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void add_Reservation_works_correcty() {
		LocalDate fromDate = LocalDate.parse("2020-01-01");
		LocalDate toDate = LocalDate.parse("2020-01-10");
		Reservation newReservation = new Reservation();
		newReservation.setReservationId(1);
		newReservation.setSiteId(1);
		newReservation.setName(TEST_RESERVATION);
		newReservation.setFromDate(fromDate);
		newReservation.setToDate(toDate);
		dao.addReservation(newReservation);
	}
	
	@Test
	public void get_park_reservations_for_next_30_days_works() {
		List<Park> parkList = new ArrayList<Park>();
		String sqlAllParks = "SELECT * " +
							"FROM park ;";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlAllParks);
		
		while (rowSet.next()) {
			Park thisPark = new Park();
			thisPark.setParkId(rowSet.getInt("park_id"));
			thisPark.setName(rowSet.getString("name"));
			thisPark.setLocation(rowSet.getString("location"));
			thisPark.setEstablishedDate(rowSet.getDate("establish_date"));
			thisPark.setArea(rowSet.getInt("area"));
			thisPark.setVisitors(rowSet.getInt("visitors"));
			thisPark.setDescription(rowSet.getString("description"));
			parkList.add(thisPark);
		}
		
		dao.getParkReservationsForNext30Days(parkList.get(0));
	}
}
