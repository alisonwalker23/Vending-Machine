package com.techelevator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private BigDecimal customerBalance = new BigDecimal("0");
    private Map<VendingMachineItems, Integer> inventory = new HashMap<VendingMachineItems, Integer>();

    public boolean removeItemFromInventory(String boughtItem) {
        for (Map.Entry<VendingMachineItems, Integer> entry : this.inventory.entrySet()) {
            String currentItemName = entry.getKey().getItemName();
            if (currentItemName.equals(boughtItem)) {
                if (entry.getValue() == 0) {
                    return false;
                }
                this.inventory.put(entry.getKey(),entry.getValue()-1);
            }
        } return true;
    }

    //method
    public void add5Inventory(VendingMachineItems item){
        inventory.put(item,5);

    }

    public BigDecimal getCustomerBalance() {
        return customerBalance;
    }

    public void setCustomerBalance(BigDecimal customerBalance) {
        this.customerBalance = customerBalance;
    }

    public Map<VendingMachineItems, Integer> getInventory() {
        return inventory;
    }

    public void addToCustomerBalance(String dollars) {
        BigDecimal dollarsAsBigDecimal = new BigDecimal(dollars);
        this.customerBalance = this.customerBalance.add(dollarsAsBigDecimal);
    }



}
