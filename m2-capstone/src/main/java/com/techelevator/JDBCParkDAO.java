package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCParkDAO implements ParkDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCParkDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Park> getAllParks() {
		List<Park> parkList = new ArrayList<Park>();
		String sqlAllParks = "SELECT * " +
							"FROM park";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllParks);
		
		while (results.next()) {
			Park park = makeParkFromRowSet(results);
			parkList.add(park);
		}
		
		return parkList;
	}


private Park makeParkFromRowSet(SqlRowSet rowSet) {
	Park thisPark = new Park();
	thisPark.setParkId(rowSet.getInt("park_id"));
	thisPark.setName(rowSet.getString("name"));
	thisPark.setLocation(rowSet.getString("location"));
	thisPark.setEstablishedDate(rowSet.getDate("establish_date"));
	thisPark.setArea(rowSet.getInt("area"));
	thisPark.setVisitors(rowSet.getInt("visitors"));
	thisPark.setDescription(rowSet.getString("description"));
	
	return thisPark;
}

}