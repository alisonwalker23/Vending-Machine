package com.techelevator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private BigDecimal customerBalance;
    private Map<VendingMachineItems, Integer> inventory = new HashMap<VendingMachineItems, Integer>();


    //method
    public void add5Inventory(VendingMachineItems item){
        inventory.put(item,5);

    }


}
