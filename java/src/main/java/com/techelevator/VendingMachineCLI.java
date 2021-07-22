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
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE };

	private Menu menu;
	private VendingMachine vendingMachine;
	private Scanner userInput = new Scanner(System.in);


	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine();
	}

	public void run() {
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

				VendingMachineItems vendingMachineItem = new VendingMachineItems(location,itemName,price,category,itemMessage);
				this.vendingMachine.add5Inventory(vendingMachineItem);
			}

		} catch (FileNotFoundException ex) {
			System.out.println("File not found : " + ex);
		}
	}

	public void showInventory() {
		for (Map.Entry<VendingMachineItems, Integer> entry : this.vendingMachine.getInventory().entrySet()) {
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
			String choice = (String)subMenu.getChoiceFromOptions(subMenuOptions);
			System.out.println("Current Money Provided: $" + this.vendingMachine.getCustomerBalance());

			if (choice.equals(subMenuOption1)) {
				//feed money
				System.out.print("Please insert a whole dollar amount: ");
				String insertedBills = userInput.nextLine();
				this.vendingMachine.addToCustomerBalance(insertedBills);
				System.out.println("Current Money Provided: $" + insertedBills);

			} else if (choice.equals(subMenuOption2)) {
				//select product
			} else {
				//finish
				break;
			}

		}
	}
}
