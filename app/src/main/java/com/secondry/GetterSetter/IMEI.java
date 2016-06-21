package com.secondry.GetterSetter;

import java.util.ArrayList;

/**
 * Created by Administrator on 21/06/2016.
 */
public class IMEI {

    public static ArrayList<String> getImei() {
        return imei;
    }

    public static void setImei(ArrayList<String> imei) {
        IMEI.imei = imei;
    }

    static ArrayList<String> imei;

    public static int getCnt() {
        return cnt;
    }

    public static void setCnt(int cnt) {
        IMEI.cnt = cnt;
    }

    static int cnt;
}
