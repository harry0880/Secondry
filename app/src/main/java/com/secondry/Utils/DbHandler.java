package com.secondry.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
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
    String URL = "http://www.gurukirpa.somee.com/Service.asmx";
    String LoadMasterMathod = "master";
    String SoapLinkMaster = "http://tempuri.org/master";

    String getSaleEntryMethod = "getSaleEntry";
    String SoapGetSaleEntry = "http://tempuri.org/getSaleEntry";

    String getimeiMethod = "getimei";
    String SoapGetimei = "http://tempuri.org/getimei";

    String getretailerMethod = "getretailer";
    String Soapgetretailer = "http://tempuri.org/getretailer";

    String getcount = "getcount";
    String Soapgetcount = "http://tempuri.org/getcount";

    JSONObject jsonResponse;
    private static final String TAG = "DBHandler";

    public DbHandler(Context context) {
        super(context, DBConstant.DBName, null, DBConstant.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.Create_Table_Secondry);
        db.execSQL(DBConstant.Create_Table_Model);
        db.execSQL(DBConstant.Create_Table_Retailer);
        db.execSQL(DBConstant.Create_Table_Imei);
        db.execSQL(DBConstant.Create_Table_User);
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
            FirebaseCrash.logcat(Log.ERROR, TAG, "Error");
            FirebaseCrash.report(e);
            e.printStackTrace();
            return "Error";
        }

        String[] Response = res.split("#"), JsonNames = {"Model", "Retailer"};
        int lengthJsonArr;
        try {
            SQLiteDatabase db=getWritableDatabase();
            db.execSQL("delete from "+DBConstant.T_Retailer_Master);
            db.execSQL("delete from "+DBConstant.T_Model_Master);
            for (int i = 0; i < JsonNames.length; i++) {
                Response[i] = "{ \"" + JsonNames[i] + "\" :" + Response[i] + " }";
                jsonResponse = new JSONObject(Response[i]);
                JSONArray jsonMainNode = jsonResponse.optJSONArray(JsonNames[i]);
                lengthJsonArr = jsonMainNode.length();
                for (int j = 0; j < lengthJsonArr; j++) {
                    ContentValues values = new ContentValues();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(j);
                    if (i == 0) {
                        values.put(DBConstant.C_Model_Id, jsonChildNode.optString("Model_id").toString());
                        values.put(DBConstant.C_Model_Model_Name, jsonChildNode.optString("Model_name").toString());
                        SQLiteDatabase writeableDB = getWritableDatabase();
                        writeableDB.insert(DBConstant.T_Model_Master, null, values);
                        writeableDB.close();

                    }
                    if (i == 1) {

                        values.put(DBConstant.C_Retailer_Id, jsonChildNode.optString("Retailerid").toString());
                        values.put(DBConstant.C_Retailer_Name, jsonChildNode.optString("RetailerName").toString());
                        SQLiteDatabase writeableDB = getWritableDatabase();
                        writeableDB.insert(DBConstant.T_Retailer_Master, null, values);
                        writeableDB.close();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "Error_getting server data");
            FirebaseCrash.report(e);
            return "ErrorServer";
        }
        return "Success";
    }


    public String AddNewRetailer(String RetailerName, String RetailerAddress) {
        String res = null;
        SoapObject request = new SoapObject(NameSpace, getretailerMethod);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("Retailername");
        pi.setValue(RetailerName);
        pi.setType(String.class);
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("retaileraddress");
        pi.setValue(RetailerAddress);
        pi.setType(String.class);
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("Userid");
        pi.setValue("createdby");
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envolpe.dotNet = true;
        envolpe.setOutputSoapObject(request);
        HttpTransportSE androidHTTP = new HttpTransportSE(URL);

        try {
            androidHTTP.call(Soapgetretailer, envolpe);
            SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
            res = response.toString();
            //System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "Error_AddingRet");
            FirebaseCrash.report(e);
            return "Error";
        }
        if(SaveRetailer(res))
        {
            return "Suucess";
        }
        else return "fail";
    }


    public void insert_user(String user)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBConstant.C_User,user);
        db.insert(DBConstant.T_User,null,cv);
    }


    public String SyncSecondry() {
        String res = null;
        String user;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBConstant.T_Secondry + ";", null);
        Cursor cursor2=db.rawQuery("select * from " + DBConstant.T_User + ";", null);
        if(cursor2.getCount()>0)
        {
            cursor2.moveToFirst();
            user=cursor2.getString(0);
        }
        else user="NA";
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

                pi = new PropertyInfo();
                pi.setName("ModelId");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Model_Id)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi = new PropertyInfo();
                pi.setName("Qty");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Qty)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi = new PropertyInfo();
                pi.setName("Saledate");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_SaleDate)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi = new PropertyInfo();
                pi.setName("CreatedBy");
                pi.setValue(user);
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
                } catch (Exception e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Error");
                   FirebaseCrash.report(e);
                    String exp= e.toString();
                    return exp;
                }
            } while (cursor.moveToNext());
            db.close();
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

                pi = new PropertyInfo();
                pi.setName("imei");
                pi.setValue(cursor.getString(cursor.getColumnIndex(DBConstant.C_Imeino)));
                pi.setType(String.class);
                request.addProperty(pi);

                pi = new PropertyInfo();
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
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Error_IMEI");
                    FirebaseCrash.report(e);
                    return "Error";
                }
            }while (cursor.moveToNext());
            db.close();
            return "Success";
        }
    }

    public void clearSecondryTable()
    {
        SQLiteDatabase db=getWritableDatabase();
        String qry="delete from "+DBConstant.T_Secondry;
        String qry2="delete from "+DBConstant.T_Imei;
        db.execSQL(qry);
        db.execSQL(qry2);
        db.close();
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

    public String MatchCount()
    {
        String res;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+DBConstant.T_Model_Master,null);
        String modelcnt=cursor.getCount()+"";
        cursor=db.rawQuery("select * from "+DBConstant.T_Retailer_Master,null);
        String retailercnt=cursor.getCount()+"";
        SoapObject request = new SoapObject(NameSpace, getcount);
        PropertyInfo pi = new PropertyInfo();

        pi.setName("ModelCount");
        pi.setValue(modelcnt);
        pi.setType(String.class);
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("RetailerCount");
        pi.setValue(retailercnt);
        pi.setType(String.class);
        request.addProperty(pi);


        SoapSerializationEnvelope envolpe = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envolpe.dotNet = true;
        envolpe.setOutputSoapObject(request);
        HttpTransportSE androidHTTP = new HttpTransportSE(URL);

        try {
            androidHTTP.call(Soapgetcount, envolpe);
            SoapPrimitive response = (SoapPrimitive) envolpe.getResponse();
            res = response.toString();
            if(res.equals("false"))
            return "true";
            else
            return "false";
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "Error_MatchCount");
            FirebaseCrash.report(e);
            return "error";
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

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }

    }

}
