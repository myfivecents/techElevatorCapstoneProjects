package com.techelevator;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.techelevator.view.Menu;

public class VendingMachineTest {
	
	private VendingMachine vend;
	private Menu menu;
	
	@Before
	public void setup() {
		vend = new VendingMachine();
		menu = new Menu(System.in, System.out);
		
	}
	
	@Test
	public void vending_maching_stocks_correctly() {
		MenuItem test = vend.getMenuItem(0);
		Assert.assertEquals("should return proper location of item", "A1", test.getLocation());
		Assert.assertEquals("vending machine should have 16 items",  16, vend.getItemsLeft());	// test to make sure that all 16 items were read in properly
	}
	
	@Test
	public void select_product_can_buy() {
		vend.setAmountFed(new BigDecimal(10));
		MenuItem test = vend.getMenuItem(0);
		String result = vend.selectProduct("A1");
		boolean check = test.getItemSound().equals(result);
		Assert.assertTrue("should return true for item selected", check);
	}
	
	@Test
	public void select_product_need_more_money() {
		String result = vend.selectProduct("A1");
		boolean check = result.equals("Please insert more money");
		Assert.assertTrue("should return true for item selected", check);
	}
	
	@Test
	public void select_product_need_invalid_selection() {
		String result = vend.selectProduct("G6");
		boolean check = result.equals("Item does not exist");
		Assert.assertTrue("should return true for invalid item", check);
	}
	
	@Test
	public void select_product_no_inventory() {
		vend.setAmountFed(new BigDecimal(30));
		vend.selectProduct("A1");
		vend.selectProduct("A1");
		vend.selectProduct("A1");
		vend.selectProduct("A1");
		vend.selectProduct("A1");
		String result = vend.selectProduct("A1");
		boolean check = result.equals("Item is sold out");
		Assert.assertTrue("should return item is sold out", check);
	}
	
	@Test
	public void vending_select_product_can_buy() {
		
		MenuItem test = vend.getMenuItem(0);
		test.buyItem();
		String selection = menu.selectProduct();
		Assert.assertEquals("Quantity should decrease after purchase", 4, test.getQuantity());
		Assert.assertEquals("Selection should be 'A1'", "A1", selection);
	}
	
	@Test
	public void get_change_outputs_quarters() {
		vend.setAmountFed(new BigDecimal(1.25));
		int[] test = vend.getChange();
		
		Assert.assertEquals("5 Quarters should be returned",5, test[0]);
	}
	@Test
	public void get_change_outputs_dimes() {
		vend.setAmountFed(new BigDecimal(.2));
		int[] test = vend.getChange();
		
		Assert.assertEquals("2 dimes should be returned", 2, test[1]);
	}
	@Test
	public void get_change_outputs_all_change() {
		vend.setAmountFed(new BigDecimal(".40"));
		int[] test = vend.getChange();
		
		Assert.assertEquals("1 quarter should be returned", 1, test[0]);
		Assert.assertEquals("1 dime should be returned", 1, test[1]);
		Assert.assertEquals("1 nickel should be returned", 1, test[2]);
	}
}



