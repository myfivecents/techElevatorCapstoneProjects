package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCCampgroundDAO implements CampgroundDAO{
private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

private Campground makeCampgroundFromRowSet(SqlRowSet results) {
		Campground camp = new Campground();
		camp.setCampgroundId(results.getInt("campground_id"));
		camp.setParkId(results.getInt("park_id"));
		camp.setName(results.getString("name"));
		String openMonth = results.getString("open_from_mm");
		camp.setOpenFromMonth(Integer.parseInt(openMonth));
		String toMonth = results.getString("open_to_mm");
		camp.setOpenToMonth(Integer.parseInt(toMonth));
		camp.setCost(results.getBigDecimal("daily_fee"));
		
		return camp;
	}

@Override
public Campground getCampgroundWithCampgroundId(Integer id) {
	String sqlQuery = "SELECT * "+
					  "FROM campground " +
					  "WHERE campground_id = ?";
	
	SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
	
	if (rowSet.next()) {
		return makeCampgroundFromRowSet(rowSet);
	} else {
		return null;
	}
}

@Override
public List<Campground> getAllCampgroundsAtPark(Park currentPark) {
	List<Campground> campgroundList = new ArrayList<Campground>();
	String sqlAllCampgrounds = "SELECT * " +
						"FROM campground " +
						"WHERE park_id = ? ;";
	SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllCampgrounds, currentPark.getParkId());
	
	while (results.next()) {
		Campground campground = makeCampgroundFromRowSet(results);
		campgroundList.add(campground);
	}
	
	return campgroundList;
}


}
