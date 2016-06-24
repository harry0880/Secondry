package com.secondry.SpinnerAdapters;

/**
 * Created by Administrator on 23/06/2016.
 */
public class Retailers {

    public String getRetailerId() {
        return RetailerId;
    }

    public void setRetailerId(String retailerId) {
        RetailerId = retailerId;
    }

    String RetailerId;

    public Retailers(String retailerId, String retailerName) {
        RetailerId = retailerId;
        RetailerName = retailerName;
    }

    String RetailerName;
}
