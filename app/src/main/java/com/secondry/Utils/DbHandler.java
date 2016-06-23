package com.secondry.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.secondry.SpinnerAdapters.Model;
import com.secondry.SpinnerAdapters.Retailers;

import java.util.ArrayList;

/**
 * Created by Administrator on 23/06/2016.
 */
public class DbHandler extends SQLiteOpenHelper {
    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Model> getModelList()
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cr=db.rawQuery("select * from "+DBConstant.T_Model_Master,null);
        ArrayList<Model> list=new ArrayList<>();
        if(cr.getCount()<=0)
        {
            list.add(new Model("0","No Items Found"));
            return list;
        }else
            cr.moveToFirst();
        do {
            list.add(new Model(cr.getString(0),cr.getString(1)));
        }while (cr.moveToNext());
        return list;
    }

    public ArrayList<Retailers> getRetailerList()
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cr=db.rawQuery("select * from "+DBConstant.T_Retailer_Master,null);
        ArrayList<Retailers> list=new ArrayList<>();
        if(cr.getCount()<=0)
        {
            list.add(new Retailers("0","No Items Found"));
            return list;
        }else
            cr.moveToFirst();
        do {
            list.add(new Retailers(cr.getString(0),cr.getString(1)));
        }while (cr.moveToNext());
        return list;
    }
}
