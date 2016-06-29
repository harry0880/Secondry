package com.secondry.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.secondry.SpinnerAdapters.Model;
import com.secondry.SpinnerAdapters.Retailers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Administrator on 23/06/2016.
 */
public class DbHandler extends SQLiteOpenHelper {

    final String NameSpace = "http://tempuri.org/";
    /* String URL="http://10.88.229.42:90/Service.asmx";*/
    String URL = "http://192.168.0.100:88/Service.asmx";
    String LoadMasterMathod = "master";
    String SoapLinkMaster = "http://tempuri.org/master";

    String getSaleEntryMethod = "getSaleEntry";
    String SoapGetSaleEntry = "http://tempuri.org/getSaleEntry";

    String getimeiMethod = "getimei";
    String SoapGetimei = "http://tempuri.org/getimei";

    JSONObject jsonResponse;

    public DbHandler(Context context) {
        super(context, DBConstant.DBName, null, DBConstant.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.Create_Table_Secondry);
        db.execSQL(DBConstant.Create_Table_Model);
        db.execSQL(DBConstant.Create_Table_Retailer);
        db.execSQL(DBConstant.Create_Table_Imei);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Model> getModelList() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery("select * from " + DBConstant.T_Model_Master, null);
        ArrayList<Model> list = new ArrayList<>();
        if (cr.getCount() <= 0) {
            list.add(new Model("0", "No Items Found"));
            return list;
        } else
            cr.moveToFirst();
        do {
            list.add(new Model(cr.getString(0), cr.getString(1)));
        } while (cr.moveToNext());
        return list;
    }

    public ArrayList<Retailers> getRetailerList() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery("select * from " + DBConstant.T_Retailer_Master, null);
        ArrayList<Retailers> list = new ArrayList<>();
        if (cr.getCount() <= 0) {
            list.add(new Retailers("0", "No Items Found"));
            return list;
        } else
            cr.moveToFirst();
        do {
            list.add(new Retailers(cr.getString(0), cr.getString(1)));
        } while (cr.moveToNext());
        return list;
    }


    public String Load_Master_tables() {
        String res = null;
        SoapObject request = new SoapObject(NameSpace, LoadMasterMathod);
        SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envolpe.dotNet = true;
        envolpe.setOutputSoapObject(request);
        HttpTransportSE androidHTTP = new HttpTransportSE(URL);

        try {
            androidHTTP.call(SoapLinkMaster, envolpe);
            SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
            res = response.toString();
            //System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

        String[] Response = res.split("#"), JsonNames = {"Model", "Retailer"};
        int lengthJsonArr;
        try {
            for (int i = 0; i < JsonNames.length; i++) {
                Response[i] = "{ \"" + JsonNames[i] + "\" :" + Response[i] + " }";
                jsonResponse = new JSONObject(Response[i]);
                JSONArray jsonMainNode = jsonResponse.optJSONArray(JsonNames[i]);
                lengthJsonArr = jsonMainNode.length();
                for (int j = 0; j < lengthJsonArr; j++) {
                    ContentValues values = new ContentValues();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(j);
                    if (i == 0) {
                        values.put(DBConstant.C_Model_Id, jsonChildNode.optString("dcode_ds").toString());
                        values.put(DBConstant.C_Model_Model_Name, jsonChildNode.optString("ds_detail").toString());
                        SQLiteDatabase writeableDB = getWritableDatabase();
                        writeableDB.insert(DBConstant.T_Model_Master, null, values);
                        writeableDB.close();

                    }
                    if (i == 1) {

                        values.put(DBConstant.C_Retailer_Id, jsonChildNode.optString("Retailer_id").toString());
                        values.put(DBConstant.C_Retailer_Name, jsonChildNode.optString("Institutetype_detail").toString());
                        SQLiteDatabase writeableDB = getWritableDatabase();
                        writeableDB.insert(DBConstant.T_Retailer_Master, null, values);
                        writeableDB.close();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ErrorServer";
        }
        return "Success";
    }


    public String AddNewRetailer(String RetailerName, String RetailerAddress) {
        String res = null;
        SoapObject request = new SoapObject(NameSpace, LoadMasterMathod);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("Retailername");
        pi.setValue(RetailerName);
        pi.setType(String.class);
        request.addProperty(pi);

        pi.setName("retaileraddress");
        pi.setValue(RetailerAddress);
        pi.setType(String.class);
        request.addProperty(pi);

        pi.setName("Userid");
        pi.setValue("createdby");
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envolpe.dotNet = true;
        envolpe.setOutputSoapObject(request);
        HttpTransportSE androidHTTP = new HttpTransportSE(URL);

        try {
            androidHTTP.call(SoapLinkMaster, envolpe);
            SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
            res = response.toString();
            //System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        if(SaveRetailer(res))
        {
            return "Suucess";
        }
        else return "fail";
    }


    public String SyncSecondry() {
        String res = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBConstant.T_Secondry + ";", null);
        String id;
        if (cursor.getCount() <= 0) {
            return "Empty";
        } else {
            cursor.moveToFirst();
            do {
                SoapObject request = new SoapObject(NameSpace, getSaleEntryMethod);

                id=cursor.getString(cursor.getColumnIndex(DBConstant.C_Id));
                PropertyInfo pi = new PropertyInfo();

                pi.setName("RetailerID");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Retailer_Id)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("ModelId");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Model_Id)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("Qty");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Qty)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("Saledate");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_SaleDate)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("CreatedBy");
                pi.setValue("createdBy");
                pi.setType(String.class);
                request.addProperty(pi);

                SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envolpe.dotNet = true;
                envolpe.setOutputSoapObject(request);
                HttpTransportSE androidHTTP = new HttpTransportSE(URL);

                try {
                    androidHTTP.call(SoapGetSaleEntry, envolpe);
                    SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
                    res = response.toString();
                        SyncIMEI(res,id);
                    //System.out.println(res);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error";
                }
            } while (cursor.moveToNext());
            return "Success";
        }
    }

    public String SyncIMEI(String webid,String id) {
        String res = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBConstant.T_Imei + " where " + DBConstant.C_Id + "=" + id + ";", null);

        if (cursor.getCount() <= 0) {
            return "Empty";
        } else {
            cursor.moveToFirst();
            do {
                SoapObject request = new SoapObject(NameSpace, getimeiMethod);
                PropertyInfo pi = new PropertyInfo();

                pi.setName("saleid");
                pi.setValue(webid);
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("imei");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Imeino)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi.setName("Userid");
                pi.setValue("createdBy");
                pi.setType(String.class);
                request.addProperty(pi);

                SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envolpe.dotNet = true;
                envolpe.setOutputSoapObject(request);
                HttpTransportSE androidHTTP = new HttpTransportSE(URL);

                try {
                    androidHTTP.call(SoapGetimei, envolpe);
                    SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
                    res = response.toString();
                    //System.out.println(res);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error";
                }
            }while (cursor.moveToNext());
            return "Success";
        }
    }


    Boolean SaveRetailer(String ReceivedfromWeb)
    {
        String[] retInfo=ReceivedfromWeb.split("#");
        ContentValues cv=new ContentValues();
        cv.put(DBConstant.C_Retailer_Id,retInfo[0]);
        cv.put(DBConstant.C_Retailer_Name,retInfo[1]);
        SQLiteDatabase db=getWritableDatabase();
       if( db.insert(DBConstant.T_Retailer_Master,null,cv)==-1)
       {
           db.close();
           return false;
       }
        else
       {
           db.close();
           return true;
       }
    }

    public String insertSecondry(ContentValues cv)
    {
    SQLiteDatabase db=getWritableDatabase();
       long id= db.insert(DBConstant.T_Secondry,null,cv);
        db.close();
        return id+"";
    }

    public void insertSecondryImei(ContentValues cv)
    {
        SQLiteDatabase db=getWritableDatabase();
       db.insert(DBConstant.T_Imei,null,cv);
        db.close();
    }

}
