package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT};

	private Menu menu;
	private VendingMachine vendingMachine;
	private Scanner userInput = new Scanner(System.in);


	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine();
	}

	public void run() {
		System.out.println("Vendo-Matic 800");
		loadInventory();

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				showInventory();

			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				//this.vendingMachine.removeItemFromInventory("Cola");
				//showInventory();
				this.subMenu();
			} else {
				System.out.println("GOODBYE!");
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}

	public void loadInventory() {
		File vendingMachineInventoryFile = new File("vendingmachine.csv");

		try (Scanner fileInput = new Scanner(vendingMachineInventoryFile)) {
			while (fileInput.hasNextLine()) {
				String line = fileInput.nextLine();
				String[] entry = line.split("\\|");
				String location = entry[0];
				String itemName = entry[1];
				BigDecimal price = new BigDecimal(entry[2]);
				String category = entry[3];

				String itemMessage;
				if (category.equals("Chip")){
					itemMessage = "Crunch Crunch, Yum!";
				} else if (category.equals("Candy")) {
					itemMessage = "Munch Munch, Yum!";
				} else if (category.equals("Drink")) {
					itemMessage = "Glug Glug, Yum!";
				} else {
					itemMessage = "Chew Chew, Yum!";
				}

				VendingMachineItem vendingMachineItem = new VendingMachineItem(location,itemName,price,category,itemMessage);
				this.vendingMachine.add5Inventory(vendingMachineItem);
			}

		} catch (FileNotFoundException ex) {
			System.out.println("File not found : " + ex);
		}
	}

	public void showInventory() {
		System.out.println("\nVENDO-MATIC SNACKS:");
		for (Map.Entry<VendingMachineItem, Integer> entry : this.vendingMachine.getInventory().entrySet()) {
			String location = entry.getKey().getLocation();
			String itemName = entry.getKey().getItemName();
			BigDecimal price = entry.getKey().getPrice();
			Integer quantity = entry.getValue();

			if (quantity == 0) {
				System.out.println(location + " " + itemName + " $" + price + " SOLD OUT");
			} else {
				System.out.println(location + " " + itemName + " $" + price + " Available: " + quantity);
			}
		}
	}

	public void subMenu() {
		while (true) {
			Menu subMenu = new Menu(System.in, System.out);
			String subMenuOption1 = "Feed Money";
			String subMenuOption2 = "Select Product";
			String subMenuOption3 = "Finish Transaction";
			String[] subMenuOptions = {subMenuOption1,subMenuOption2,subMenuOption3};
			String choice = (String)subMenu.getChoiceFromSubmenuOptions(subMenuOptions,this.vendingMachine);

			if (choice.equals(subMenuOption1)) {
				//feed money
				System.out.print("Please insert a whole dollar amount: ");
				String insertedBills = userInput.nextLine();
				this.vendingMachine.addToCustomerBalance(insertedBills);
				System.out.println("Current Money Inserted: $" + insertedBills);

			} else if (choice.equals(subMenuOption2)) {
				//select product
				showInventory();
				productSelection();
			} else {
				//finish transaction
				this.vendingMachine.makeChange();
				this.vendingMachine.setCustomerBalance(new BigDecimal("0"));
				break;
			}

		}
	}

	public void productSelection(){
		System.out.print("\nPlease Enter Item Location Code: ");
		String insertedLocationCode = userInput.nextLine().toUpperCase();
		for(Map.Entry<VendingMachineItem, Integer> entry : this.vendingMachine.getInventory().entrySet()){
			String currentLocationCode = entry.getKey().getLocation();
			if(currentLocationCode.equals(insertedLocationCode)){
				//SOLD OUT ITEM
				if(entry.getValue() == 0){
					//if item is sold out
					//inform that item is sold out
					System.out.println("\nITEM IS SOLD OUT! PLEASE MAKE ANOTHER SELECTION");
					//breaks from loop
					//return to submenu
					return;
				}

				BigDecimal currentCustomerBalance = this.vendingMachine.getCustomerBalance();
				BigDecimal selectedItemPrice = entry.getKey().getPrice();
				//VALID ITEM
				if(currentCustomerBalance.compareTo(selectedItemPrice) >= 0){
					//gets remaining balance
					BigDecimal remainingBalance = currentCustomerBalance.subtract(selectedItemPrice);
					//Updates current balance
					this.vendingMachine.setCustomerBalance(remainingBalance);
					this.vendingMachine.removeItemFromInventory(entry.getKey().getItemName());

					System.out.println("\n**DISPENSING ITEM**");
					System.out.println(entry.getKey().getItemName() + " | $" + entry.getKey().getPrice());
					System.out.println("Remaining Balance | $" + remainingBalance);
					System.out.println(entry.getKey().getCategoryMessage());
					return;

					//BALANCE IS NOT ENOUGH
				} else {
					System.out.println("\nCURRENT BALANCE IS NOT ENOUGH. PLEASE ENTER MORE MONEY");
					//Print to user not enough money
					return;
				}

			}
		}
		//Item location doesn't exist:
		System.out.println("\nINVALID ITEM LOCATION! PLEASE TRY AGAIN");
		//return to purchase menu
		return;
	}





}
