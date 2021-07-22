package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE };

	private Menu menu;
	private VendingMachine vendingMachine;



	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine();
	}

	public void run() {
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


		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
			}
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
