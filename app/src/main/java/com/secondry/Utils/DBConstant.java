package com.secondry.Utils;

/**
 * Created by Administrator on 21/06/2016.
 */
public class DBConstant {

    public static final String DBName="DB";
    public static final int DBVersion=1;

    public static final String T_Model_Master="ModelMaster";
    public static final String C_Model_Id="ModelId";
    public static final String C_Model_Model_Name="ModelName";

    public static final String T_Retailer_Master="RetailerMaster";
    public static final String C_Retailer_Id="RetailerId";
    public static final String C_Retailer_Name="RetailerName";

    public static final String T_Secondry="Secondry";
    public static final String C_Id="_id";
    public static final String C_Qty="Qty";
    public static final String C_SaleDate="date";

    public static final String T_Imei="Imei";
    public static final String C_Imeino ="Imeino";

    public static final String Create_Table_Secondry="Create Table "+T_Secondry+" ("+C_Id+ " TEXT,"
            +C_Retailer_Id+" TEXT,"
            +C_Model_Id+" TEXT,"
            +C_Qty+" TEXT,"
            +C_SaleDate+" TEXT) ";

    public static final String Create_Table_Model="Create Table "+T_Model_Master+" ("+C_Model_Id+ " TEXT,"
            +C_Model_Model_Name+" TEXT)";

    public static final String Create_Table_Retailer="Create Table "+T_Retailer_Master+" ("+C_Retailer_Id+ " TEXT,"
            +C_Retailer_Name+" TEXT)";

    public static final String Create_Table_Imei="Create Table "+T_Imei+" ("+C_Id+ " TEXT,"
            +C_Imeino+" TEXT)";



}
