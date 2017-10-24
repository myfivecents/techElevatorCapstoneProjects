package com.techelevator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class MenuItemTest {
	private MenuItem item;
	
	@Before
		public void setup() {
		item = new MenuItem("A1|Potato Crisps|3.05");
	}
	
	@Test
	public void menu_item_initializes_correctly() {
		Assert.assertEquals("item location should set properly",  "A1", item.getLocation());
		Assert.assertEquals("item name should set properly",  "Potato Crisps", item.getItemName());
		Assert.assertEquals("item price should set properly", new BigDecimal(3.05).setScale(2, RoundingMode.CEILING), item.getPrice());
		Assert.assertEquals("item quantity should set properly",  5, item.getQuantity());
		Assert.assertEquals("item should return correct sound", "Crunch Crunch, Yum!", item.getItemSound());
	}
	
	@Test
	public void item_sound_location_b () {
		item = new MenuItem("B1|Potato Crisps|3.05");
		String sound = item.getItemSound();
		Assert.assertEquals("sound should return correctly",  "Munch Munch, Yum!", sound);
	}
	@Test
	public void item_sound_location_c () {
		item = new MenuItem("C1|Potato Crisps|3.05");
		String sound = item.getItemSound();
		Assert.assertEquals("sound should return correctly",  "Glug Glug, Yum!", sound);
	}
	
	@Test
	public void item_sound_location_d () {
		item = new MenuItem("D1|Potato Crisps|3.05");
		String sound = item.getItemSound();
		Assert.assertEquals("sound should return correctly",  "Chew Chew, Yum!", sound);
	}
	
	@Test
	public void item_sound_location_unavailable () {
		item = new MenuItem("E1|Potato Crisps|3.05");
		String sound = item.getItemSound();
		Assert.assertEquals("sound should return correctly",  "Item not available", sound);
	}
	
	@Test
	public void buy_item_subtracts_correctly() {
		item.buyItem();
		int itemQuantity = item.getQuantity();
		Assert.assertEquals("should return quantity minus one",  4, itemQuantity);
	}
	
	@Test
	public void can_buy_has_inventory() {
		boolean inventory = item.canBuy();
		Assert.assertTrue("should return true if still inventory", inventory);
	}
	
	@Test
	public void can_buy_has_no_inventory() {
		item.buyItem();
		item.buyItem();
		item.buyItem();
		item.buyItem();
		item.buyItem();
		boolean inventory = item.canBuy();
		Assert.assertFalse("should return false if quantity is 0", inventory);
	}
}

