package com.secondry.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.secondry.Login;
import com.secondry.MainActivity;
import com.secondry.R;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Splash extends AppCompatActivity {

    DbHandler dbh;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plash);
        dbh=new DbHandler(Splash.this);
        context=this;
        if(!doesDatabaseExist(getApplicationContext(),DBConstant.DBName))
        {
             new getMaster_Tables().execute();
        }
        else
        {
            startActivity(new Intent(Splash.this,MainActivity.class));
            finish();
        }
    }
    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private class getMaster_Tables extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... params) {

            return dbh.Load_Master_tables();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("Error"))
            {
                new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE).setTitleText("No Internet Connection").show();
            }
            else if(s.equals("ErrorServer"))
            {
                new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE).setTitleText("Unable to connect with Server!!!").show();
            }
            else {
                startActivity(new Intent(Splash.this,Login.class));
                finish();
            }
            super.onPostExecute(s);
        }
    }
}
