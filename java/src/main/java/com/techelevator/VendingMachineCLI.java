package com.techelevator;

import com.techelevator.view.Menu;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "Sales Report";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_SALES_REPORT};

	private Menu menu;
	private VendingMachine vendingMachine;
	private Scanner userInput = new Scanner(System.in);
	private String currentDirectory = System.getProperty("user.dir");
	private File logFile = new File(currentDirectory + "/Log.txt");


	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine();
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}

	public void run() {
		System.out.println("\nVendo-Matic 800");
		this.vendingMachine.loadInventory();

		//Main Menu
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				this.vendingMachine.showInventory();

			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// purchase options
				this.subMenu();
			} else if (choice.equals(MAIN_MENU_OPTION_SALES_REPORT)) {
				System.out.println("\n**Printing Sales Report**\n");
				generateSalesReport();
			} else {
				System.out.println("GOODBYE!");
				System.exit(0);
			}
		}
	}

	//Submenu
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
				System.out.println("\nMachine accepts: $1, $2, $5, $10");
				System.out.print("Please insert a whole dollar amount: ");
				String insertedBills = userInput.nextLine();

				List<String> validDollarAmounts = new ArrayList<String>(Arrays.asList("1", "2", "5", "10"));
				if(!validDollarAmounts.contains(insertedBills)){
					System.out.println("\nINSERTED MONEY IS INVALID. PLEASE ENTER A VALID WHOLE DOLLAR AMOUNT");
				} else {
					this.vendingMachine.addToCustomerBalance(insertedBills);
					System.out.println("\nCurrent Money Inserted: $" + insertedBills);
					writeToLogFile("FEED MONEY", new BigDecimal(insertedBills), this.vendingMachine.getCustomerBalance());
				}
			} else if (choice.equals(subMenuOption2)) {
				//select product
				this.vendingMachine.showInventory();
				BigDecimal preActionBalance = this.vendingMachine.getCustomerBalance();
				String itemNameAndLocation = productSelection();
				if(!itemNameAndLocation.equals("")){
					writeToLogFile(itemNameAndLocation, preActionBalance, this.vendingMachine.getCustomerBalance());
				}
			} else {
				//finish transaction
				this.vendingMachine.makeChange();
				BigDecimal preActionBalance = this.vendingMachine.getCustomerBalance();
				this.vendingMachine.setCustomerBalance(new BigDecimal("0"));
				writeToLogFile("GIVE CHANGE", preActionBalance, this.vendingMachine.getCustomerBalance());
				break;
			}
		}
	}

	//Submenu Option 2
	public String productSelection(){
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
					//return to submenu
					return "";
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
					//Gives us the item name and location to add to Log.txt
					return entry.getKey().getItemName() + " " + entry.getKey().getLocation();

					//BALANCE IS NOT ENOUGH
				} else {
					System.out.println("\nCURRENT BALANCE IS NOT ENOUGH. PLEASE ENTER MORE MONEY");
					//Print to user not enough money
					return "";
				}

			}
		}
		//Item location doesn't exist:
		System.out.println("\nINVALID ITEM LOCATION! PLEASE TRY AGAIN");
		//return to purchase menu
		return "";
	}

	//AUDIT LOG.TXT
	public void writeToLogFile(String printMessage, BigDecimal preActionBalance, BigDecimal remainingBalance){
		//get time with format
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
		LocalDateTime currentDateTime = LocalDateTime.now();
		String formattedDateTime = currentDateTime.format(dateFormat);

		String logEntry = formattedDateTime + " " + printMessage + ": $" + preActionBalance.setScale(2) + " $" + remainingBalance.setScale(2);
		//try() and catch()
		try(
				FileWriter fileWriter = new FileWriter(this.logFile, true);
				PrintWriter pw = new PrintWriter(fileWriter)
		){
			pw.println(logEntry);

		} catch(IOException ex){
			System.out.println("File not found : " + ex);

		}
	}

	//SALES REPORT
	public void generateSalesReport() {
		//Creates the map of item to quantity sold from the beginning of the machine
		makeItemQuantitySoldMap();

		//make new file with the date/time formatted
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy_hh-mm-ssa");
		LocalDateTime currentDateTime = LocalDateTime.now();
		String formattedDateTime = currentDateTime.format(dateFormat);
		//Make new file with date/time format
		File dateTimeFile = new File(this.currentDirectory + "/" + formattedDateTime + "_SalesReport" + ".txt");
		//write to file
		try(PrintWriter dataOutput = new PrintWriter(dateTimeFile)){
			//loop over the new map
			for(Map.Entry<String, Integer> entry : this.vendingMachine.getNumberOfItemsSold().entrySet()){
				//println to new file (itemName|totalQuantitySold)
				dataOutput.println(entry.getKey() + "|" + entry.getValue());
			}
			//after the loop write to the file the total amount sold
			dataOutput.println("Total Sales: $" + this.vendingMachine.getTotalAmountSold());
		} catch(FileNotFoundException ex) {
			System.out.println("File was not found : " + ex);
		}
	}

	private void makeItemQuantitySoldMap(){
		//Create a new map <String itemName, Integer itemQuantitySold>
		Map<String, Integer> numberOfItemsSold = this.vendingMachine.getNumberOfItemsSold();
		//Create a variable to keep track of total amount sold
		BigDecimal totalAmountSold = new BigDecimal("0.00");
		//loop through this.vendingMachine's inventory
		for(Map.Entry<VendingMachineItem, Integer> entry : this.vendingMachine.getInventory().entrySet()){
			String itemName = entry.getKey().getItemName();
			//Add each item to new map with a starting int of 0
			numberOfItemsSold.put(itemName, 0);
		}

		//read from log.txt file
		try(Scanner logFileInput = new Scanner(this.logFile)){
			while(logFileInput.hasNextLine()){
				//get the line
				String line = logFileInput.nextLine();
				String[] lineArray = line.split(" ");
				//get the item name and price
				String itemName;
				BigDecimal itemPrice;
				if(!lineArray[3].equals("FEED") && !lineArray[3].equals("GIVE")){
					if(lineArray.length == 7){
						itemName = lineArray[3];
					} else {
						itemName = lineArray[3] + " " + lineArray[4];
					}

					//if item name is in the new map, add to its itemQuantitySold
					//if its not in the map add it with a default of 1
					numberOfItemsSold.put(itemName, numberOfItemsSold.getOrDefault(itemName, 0) + 1);

					//get price
					// last two items in array without the dollar sign, minus each other
					String firstPrice = lineArray[lineArray.length - 2].substring(1);
					String secondPrice = lineArray[lineArray.length - 1].substring(1);
					//Convert them to Big decimals
					BigDecimal firstPriceAsBigDecimal = new BigDecimal(firstPrice);
					BigDecimal secondPriceAdBigDecimal = new BigDecimal(secondPrice);
					itemPrice = firstPriceAsBigDecimal.subtract(secondPriceAdBigDecimal);
					//Add the items price to the totalAmountSold
					totalAmountSold = totalAmountSold.add(itemPrice);
				}
			}
		} catch(FileNotFoundException ex){
			System.out.println("File was not found : " + ex);
		}

		//Update the map and the totalAmountSold to be stored inside the instance of the vending machine
		//to have access for when we need to write to the sales report file
		this.vendingMachine.setNumberOfItemsSold(numberOfItemsSold);
		this.vendingMachine.setTotalAmountSold(totalAmountSold);
	}


}
