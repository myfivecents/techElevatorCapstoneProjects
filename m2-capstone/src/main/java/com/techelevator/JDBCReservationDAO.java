package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCReservationDAO implements ReservationDAO{
private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Reservation addReservation(Reservation reservation) {
		String sqlInsertReservation = "INSERT INTO reservation (reservation_id, site_id, name, from_date, to_date) " +
									 "VALUES (?, ?, ?, ?, ?);";
		int nextReservationId = getNextReservationId();
		jdbcTemplate.update(sqlInsertReservation, nextReservationId, reservation.getSiteId(), reservation.getName(), reservation.getFromDate(),
				reservation.getToDate());
		
		reservation.setReservationId(nextReservationId);
		return reservation;
	}
	
	public List<Reservation> getParkReservationsForNext30Days(Park park){
		List<Reservation> reservationList = new ArrayList<Reservation>();
		String sqlSelectParkReservationsForNext30Days =  "SELECT * " + 
														"FROM reservation " + 
														"JOIN site ON site.site_id = reservation.site_id " + 
														"JOIN campground ON campground.campground_id = site.campground_id " + 
														"JOIN park ON campground.park_id = park.park_id " + 
														"WHERE park.park_id = ? " + 
														"AND from_date >= NOW() " + 
														"AND to_date <= NOW() + INTERVAL '30 days' " + 
														"ORDER BY from_date, to_date;";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectParkReservationsForNext30Days, park.getParkId());
		
		while(results.next()) {
			Reservation reservation = makeReservationFromRowSet(results);
			reservationList.add(reservation);
		}
		return reservationList;
	}
	
	private int getNextReservationId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('reservation_reservation_id_seq')");
		if(nextIdResult.next()) {
			return nextIdResult.getInt(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the next reservation");
		}
	}

private Reservation makeReservationFromRowSet(SqlRowSet results) {
		Reservation reservation = new Reservation();
		reservation.setReservationId(results.getInt("reservation_id"));
		reservation.setSiteId(results.getInt("site_id"));
		reservation.setName(results.getString("name"));
		reservation.setFromDate(results.getDate("from_date").toLocalDate());
		reservation.setToDate(results.getDate("to_date").toLocalDate());
		reservation.setCreationDate(results.getDate("create_date").toLocalDate());
		
		return reservation;
}




}
