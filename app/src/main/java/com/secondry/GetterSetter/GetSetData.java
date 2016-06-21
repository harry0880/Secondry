package com.secondry.GetterSetter;

import java.util.ArrayList;

/**
 * Created by Administrator on 21/06/2016.
 */
public class GetSetData {
    String Model;


    String Qty;

    public GetSetData(String model, String qty, ArrayList<String> imei) {
        Model = model;
        Qty = qty;
        this.imei = imei;
    }

    public ArrayList<String> getImei() {
        return imei;
    }

    public void setImei(ArrayList<String> imei) {
        this.imei = imei;
    }

    ArrayList<String> imei;



    public String toString()
    {
        return Model+" Qty-"+Qty;
    }
}
