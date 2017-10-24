package com.techelevator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCSiteDAO implements SiteDAO {
private JdbcTemplate jdbcTemplate;
	
	public JDBCSiteDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Site> getAllSites() {
		List<Site> siteList = new ArrayList<Site>();
		String sqlAllSites = "SELECT * " +
							"FROM site";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllSites);
		
		while (results.next()) {
			Site site = makeSiteFromRowSet(results);
			siteList.add(site);
		}
		
		return siteList;
	}
	
	public Site getSiteFromSiteId(Integer id) {
		String sqlQuery = "SELECT * "+
				  "FROM site " +
				  "WHERE site_id = ?";

		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);

		if (rowSet.next()) {
			return makeSiteFromRowSet(rowSet);
		} else {
			return null;
		}
	}


private Site makeSiteFromRowSet(SqlRowSet results) {
		Site site = new Site();
		site.setSiteId(results.getInt("site_id"));
		site.setCampgroundId(results.getInt("campground_id"));
		site.setSiteNumber(results.getInt("site_number"));
		site.setMaxOccupancy(results.getInt("max_occupancy"));
		site.setAccessible(results.getBoolean("accessible"));
		site.setMaxRVLength(results.getInt("max_rv_length"));
		site.setUtilities(results.getBoolean("utilities"));
		
		return site;
	}

@Override
public List<Site> getSitesAvailableAtCampground(Campground campground, LocalDate fromDate, LocalDate toDate) {
	List<Site> siteList = new ArrayList<Site>();
	String sqlAllSites = "SELECT * " +
						"FROM site " +
						"WHERE campground_id = ? " +
						"AND site_id NOT IN " +
							"(SELECT site_id " +
							"FROM reservation " +
							"WHERE (? >= from_date AND ? <= to_date) " +
							"OR (? >= from_date AND ? <= to_date) " +
							"OR (? >= from_date AND ? <= to_date) " +
							"OR (? <= from_date AND ? >= to_date)) " +
						"AND campground_id NOT IN " +
							"(SELECT campground_id " +
							"FROM campground " +
							"WHERE (? <= CAST(open_from_mm as INT) OR ? >= CAST(open_to_mm as INT)) " +
							"AND (? <= CAST(open_from_mm as INT) OR ? >= CAST(open_to_mm as INT))) ";
	SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllSites, 
													campground.getCampgroundId(), 
													fromDate, fromDate, 
													toDate, toDate, 
													fromDate, toDate, 
													fromDate, toDate,
													fromDate.getMonthValue(), fromDate.getMonthValue(),
													toDate.getMonthValue(), toDate.getMonthValue());
	
	while (results.next()) {
		Site site = makeSiteFromRowSet(results);
		siteList.add(site);
	}
	
	return siteList;
	
}

@Override
public List<Site> getSitesAvailable(ReservationDetails details) {
	List<Site> siteList = new ArrayList<Site>();
	if (details.getFromDate().isAfter(details.getToDate())) {
		return siteList;
	}
	List<String> queryStringsList = new ArrayList<String>();
	List<Object> queryObjects = new ArrayList<Object>();
	String mainSelectQuery = "SELECT * " +
			"FROM site " +
			"WHERE campground_id = ? " +
			"AND site_id NOT IN " +
				"(SELECT site_id " +
				"FROM reservation " +
				"WHERE (? >= from_date AND ? <= to_date) " + //our from date between previous
				"OR (? >= from_date AND ? <= to_date) " + //to 
				//"OR (? >= from_date AND ? <= to_date) " + // completely inside
				"OR (? <= from_date AND ? >= to_date)) " + //completely overlaps
			"AND campground_id NOT IN " +
				"(SELECT campground_id " +
				"FROM campground " +
				"WHERE (? <= CAST(open_from_mm as INT) OR ? >= CAST(open_to_mm as INT)) " +
				"AND (? <= CAST(open_from_mm as INT) OR ? >= CAST(open_to_mm as INT))) ";
	
	queryStringsList.add(mainSelectQuery);
	
	LocalDate fromDate = details.getFromDate();
	LocalDate toDate = details.getToDate();
	queryObjects.add(details.getCampground().getCampgroundId());
	//first stanza
	//first line
	queryObjects.add(fromDate);
	queryObjects.add(fromDate);
	//second line
	queryObjects.add(toDate);
	queryObjects.add(toDate);
	//third line
//	queryObjects.add(fromDate);
//	queryObjects.add(toDate);
	//fourth line
	queryObjects.add(fromDate);
	queryObjects.add(toDate);
	//second stanza
	//first line
	queryObjects.add(fromDate.getMonthValue());
	queryObjects.add(fromDate.getMonthValue());
	//second line
	queryObjects.add(toDate.getMonthValue());
	queryObjects.add(toDate.getMonthValue());
	
	if (details.getMaximumOccupancy() > 0) {
		queryStringsList.add(" AND max_occupancy >= ? ");
		queryObjects.add(details.getMaximumOccupancy());
	}
	if (details.getRvLength() > 0) {
		queryStringsList.add(" AND max_rv_length >= ? ");
		queryObjects.add(details.getRvLength());
	}
	if (details.getWheelChairAccessible() != null) {
		queryStringsList.add(" AND accessible = ? ");
		queryObjects.add(details.getWheelChairAccessible());
	}
	if (details.getUtilityHookup() != null) {
		queryStringsList.add(" AND utilities = ? ");
		queryObjects.add(details.getUtilityHookup());
	}
	String queryString = String.join(" ", queryStringsList);
	SqlRowSet results = jdbcTemplate.queryForRowSet(queryString, queryObjects.toArray());
	while (results.next()) {
		Site site = makeSiteFromRowSet(results);
		siteList.add(site);
	}

	return siteList;
}

}
