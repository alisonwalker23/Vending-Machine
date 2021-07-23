package com.techelevator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendingMachine {
    private BigDecimal customerBalance = new BigDecimal("0");
    private Map<VendingMachineItem, Integer> inventory = new HashMap<VendingMachineItem, Integer>();

    public boolean removeItemFromInventory(String boughtItem) {
        for (Map.Entry<VendingMachineItem, Integer> entry : this.inventory.entrySet()) {
            String currentItemName = entry.getKey().getItemName();
            if (currentItemName.equals(boughtItem)) {
                if (entry.getValue() == 0) {
                    return false;
                }
                this.inventory.put(entry.getKey(),entry.getValue()-1);
            }
        } return true;
    }

    //methods
    public void add5Inventory(VendingMachineItem item){
        inventory.put(item,5);

    }

    public void makeChange(){
        int balanceAsInt = (int)(this.customerBalance.doubleValue()*100);
        int[] coins = {1, 5, 10, 25, 100, 500};
        List<Integer> amountOwed = new ArrayList<Integer>();
        while(balanceAsInt > 0) {
            int largestCoin = findLargestCoin(coins, balanceAsInt);
            amountOwed.add(largestCoin);
            balanceAsInt -= largestCoin;
        }
        int pennyCounter = 0;
        int nickelCounter = 0;
        int dimeCounter = 0;
        int quarterCounter = 0;
        int dollarCounter = 0;
        int fiveDollarCounter = 0;
        for(Integer coin : amountOwed) {
            if (coin == 1) {
                pennyCounter++;
            } else if (coin == 5) {
                nickelCounter++;
            } else if (coin == 10) {
                dimeCounter++;
            } else if (coin == 25) {
                quarterCounter++;
            } else if (coin == 100) {
                dollarCounter++;
            } else {
                fiveDollarCounter++;
            }
        }
        System.out.println("\n**DISPENSING CHANGE**\n");
        if(fiveDollarCounter > 0){
            System.out.println("Five Dollar Bills : " + fiveDollarCounter);
        } if (dollarCounter > 0) {
            System.out.println("One Dollar Bills : " + dollarCounter);
        } if (quarterCounter > 0) {
            System.out.println("Quarters : " + quarterCounter);
        } if (dimeCounter > 0) {
            System.out.println("Dimes : " + dimeCounter);
        } if (nickelCounter > 0) {
            System.out.println("Nickels : " + nickelCounter);
        } if (pennyCounter > 0) {
            System.out.println("Pennies : " + pennyCounter);
        }
    }

    public static int findLargestCoin(int[] coins, int balanceAsInt){
        for(int i=coins.length-1; i>=0; i--) {
            int currentCoin = coins[i];
            if(balanceAsInt >= currentCoin){
                return currentCoin;
            }
        }
            return 0;
    }

    public void addToCustomerBalance(String dollars) {
        BigDecimal dollarsAsBigDecimal = new BigDecimal(dollars);
        this.customerBalance = this.customerBalance.add(dollarsAsBigDecimal);
    }

    //getters & setters
    public BigDecimal getCustomerBalance() {
        return customerBalance;
    }

    public void setCustomerBalance(BigDecimal customerBalance) {
        this.customerBalance = customerBalance;
    }

    public Map<VendingMachineItem, Integer> getInventory() {
        return inventory;
    }





}
