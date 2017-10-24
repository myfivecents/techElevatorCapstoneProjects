package com.techelevator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;



public class JDBCSiteDAOIntegrationTest {

		/* Using this particular implementation of DataSource so that
		 * every database interaction is part of the same database
		 * session and hence the same database transaction */
		private static SingleConnectionDataSource dataSource;
		private JdbcTemplate jdbcTemplate;
		private JDBCSiteDAO siteDAO;
		
		/* Before any tests are run, this method initializes the datasource for testing. */
		@BeforeClass
		public static void setupDataSource() {
			dataSource = new SingleConnectionDataSource();
			dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
			dataSource.setUsername("postgres");
			dataSource.setPassword("postgres1");
			/* The following line disables autocommit for connections 
			 * returned by this DataSource. This allows us to rollback
			 * any changes after each test */
			dataSource.setAutoCommit(false);
		}
		
		/* After all tests have finished running, this method will close the DataSource */
		@AfterClass
		public static void closeDataSource() throws SQLException {
			dataSource.destroy();
		}

		/* After each test, we rollback any changes that were made to the database so that
		 * everything is clean for the next test */
		@After
		public void rollback() throws SQLException {
			dataSource.getConnection().rollback();
		}
		
		/* This method provides access to the DataSource for subclasses so that 
		 * they can instantiate a DAO for testing */
		protected DataSource getDataSource() {
			return dataSource;
		}
		
		@Before
		public void setup() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			siteDAO = new JDBCSiteDAO(dataSource);
		}
		
		@Test
		public void get_sites_available_includes_added_site() {
			
			int siteId = 0;
			int reservationId = 0;
			String sqlSiteId = "SELECT nextval(pg_get_serial_sequence('site', 'site_id'))";
			SqlRowSet nextSiteId = jdbcTemplate.queryForRowSet(sqlSiteId);
			if (nextSiteId.next()) {
				siteId = nextSiteId.getInt(1);
			}
			
			Site site = new Site();
			site.setSiteId(siteId);
			site.setCampgroundId(1);
			site.setMaxOccupancy(77);
			site.setMaxRVLength(20);
			site.setSiteNumber(999);
			site.setUtilities(true);
			site.setAccessible(true);
			
			String sqlInsertSite = "INSERT INTO site (site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) VALUES (?, ?, ?, ?, ?, ?, ?);";
			jdbcTemplate.update(sqlInsertSite, site.getSiteId(), site.getCampgroundId(), site.getSiteNumber(), site.getMaxOccupancy(), site.isAccessible(), site.getMaxRVLength(), site.isUtilities());
			
			String sqlReservationId = "SELECT nextval(pg_get_serial_sequence('reservation', 'reservation_id'))";
			SqlRowSet nextReservationId = jdbcTemplate.queryForRowSet(sqlReservationId);
			if (nextReservationId.next()) {
				reservationId = nextReservationId.getInt(1);
			}
			String sqlInsertReservation = "INSERT INTO reservation (reservation_id, site_id, name, from_date, to_date, create_date) VALUES (?, 1, 'testSite', '2100-01-01', '2100-02-02', '2017-10-08');";
			jdbcTemplate.update(sqlInsertReservation, reservationId);
			
			Campground camp = new Campground();
			camp.setCampgroundId(1);
			camp.setCost(new BigDecimal(35));
			camp.setName("Test Camp1");
			camp.setOpenFromMonth(01);
			camp.setOpenToMonth(12);
			camp.setParkId(1);
			
			LocalDate tempFromDate = LocalDate.parse("2100-01-01");
			LocalDate tempToDate = LocalDate.parse("2100-02-02");
			LocalDate tempFromDate2 = LocalDate.parse("2200-01-01");
			LocalDate tempToDate2 = LocalDate.parse("2200-02-02");
			
			List<Site> siteList = siteDAO.getSitesAvailableAtCampground(camp, tempFromDate, tempToDate);
			List<Site> allSites = siteDAO.getSitesAvailableAtCampground(camp, tempFromDate2, tempToDate2);
			
			Assert.assertEquals("there should only be one site not included in the first list", 1,  (allSites.size() - siteList.size()));
		}
		
}









