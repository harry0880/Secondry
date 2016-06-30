package com.secondry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.secondry.GetterSetter.GetSetData;
import com.secondry.GetterSetter.IMEI;
import com.secondry.SpinnerAdapters.Model;
import com.secondry.SpinnerAdapters.Retailers;
import com.secondry.Utils.AndroidDatabaseManager;
import com.secondry.Utils.DBConstant;
import com.secondry.Utils.DbHandler;
import com.secondry.Utils.SimpleScannerFragmentActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
SearchableSpinner spModel,spRetailers;
    EditText etQty,etImei;
    FancyButton btnSubmit,btnSave;
    ImageButton barcode;
    ArrayAdapter<GetSetData> adapter;
    ArrayList<GetSetData> data;
    ListView lv;
    Context context;
    DbHandler dbh;
    Model model;
    Retailers retailers;
    ImageButton btnAddnewRetailer;
    ArrayAdapter<Retailers> Retaileradapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
        setModel();
        setRetailers();
        context=this;
        adapter=new ArrayAdapter<GetSetData>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, data);
    lv.setAdapter(adapter);
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etQty.getText().toString().trim().equals(""))
                {
                    IMEI.setCnt(Integer.parseInt(etQty.getText().toString()));
                    startActivityForResult(new Intent(MainActivity.this, SimpleScannerFragmentActivity.class),0);
                }
            }
        });

        spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             model=(Model) spModel.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spRetailers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailers=(Retailers) spRetailers.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ArrayList<String> imeino=new ArrayList<String>();
                String[] arr=etImei.getText().toString().split("\n");
               if(Integer.parseInt(etQty.getText().toString()) == (arr.length)) {
                   for (String ar : arr) {
                       imeino.add(ar);
                   }
                   data.add(new GetSetData(model.getModelId(), etQty.getText().toString(), imeino));
                   adapter.notifyDataSetChanged();
                   etQty.setText("");
                   etImei.setText("");
               }
                else
               {
                   Snackbar.make(v,"Qty and IMEI count don't match",Snackbar.LENGTH_LONG).show();
               }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            String id;
            @Override
            public void onClick(View v) {
                for(GetSetData getset:data)
                {
                    ContentValues cv=new ContentValues();
                    cv.put(DBConstant.C_Model_Id,getset.getModel());
                   cv.put(DBConstant.C_Retailer_Id,retailers.getRetailerId());
                    cv.put(DBConstant.C_Qty,getset.getQty());
                    cv.put(DBConstant.C_SaleDate,getDate());
                    id = dbh.insertSecondry(cv);
                    ArrayList<String> imei=getset.getImei();
                    ContentValues cv1=new ContentValues();
                    for(String no:imei)
                    {
                        cv1.put(DBConstant.C_Id,id);
                        cv1.put(DBConstant.C_Imeino,no);
                        dbh.insertSecondryImei(cv1);
                    }
                }
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetSetData object=(GetSetData) lv.getItemAtPosition(position);
                loadView(object,view);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                   fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnAddnewRetailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterNewRetailer_View(v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> imei=IMEI.getImei();
        String Imei=null;
        for(String emi:imei)
        {
            if(Imei==null)
            Imei=emi;
            else
                Imei=Imei+"\n"+emi;
        }
        etImei.setText(Imei);
    }

    String getDate()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    void setModel()
    {
        ArrayList<String> ar=new ArrayList<>();
        ar.add("MOB1");
        ar.add("MOB2");
        ArrayAdapter<Model> adapter=new ArrayAdapter<Model>(this, android.R.layout.simple_spinner_item, dbh.getModelList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(adapter);
    /*    ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ar);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(Adapter);*/
    }

    void setRetailers()
    {
        ArrayList<String> ar=new ArrayList<>();
        ar.add("MOB1");
        ar.add("MOB2");
        Retaileradapter=new ArrayAdapter<Retailers>(this, android.R.layout.simple_spinner_item, dbh.getRetailerList());
        Retaileradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRetailers.setAdapter(Retaileradapter);
       /* ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ar);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRetailers.setAdapter(Adapter);*/
    }

      private void loadView(GetSetData getset, View v) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.dialog,
                (ViewGroup) v.findViewById(R.id.layout_root));
        ListView ll=(ListView) layout.findViewById(R.id.lview);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,  android.R.layout.simple_list_item_1, android.R.id.text1, getset.getImei());
        ll.setAdapter(adapter);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setView(layout);
        dialog.create();
        dialog.show();
    }

    private void EnterNewRetailer_View(View v) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.addretailer_dialog,
                (ViewGroup) v.findViewById(R.id.layout_root));
        final EditText etRetailername=(EditText) layout.findViewById(R.id.etRetailerName);
        final EditText etRetailerAddress=(EditText) layout.findViewById(R.id.etRetailerAddress);

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
              new SaveRetailer().execute(etRetailername.getText().toString(),etRetailerAddress.getText().toString());
            }
        });

        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setView(layout);
        dialog.create();
        dialog.show();
    }


    void initialize()
    {
        dbh=new DbHandler(MainActivity.this);
        spModel=(SearchableSpinner) findViewById(R.id.spModel);
        etQty=(EditText) findViewById(R.id.etQty);
        btnSubmit=(FancyButton) findViewById(R.id.btnSubmit);
        barcode=(ImageButton) findViewById(R.id.barcode);
        data=new ArrayList<>();
        lv=(ListView) findViewById(R.id.listview);
        etImei=(EditText) findViewById(R.id.etImei);
        btnSave=(FancyButton) findViewById(R.id.btnSave);
        spRetailers=(SearchableSpinner) findViewById(R.id.spRetailer);
        btnAddnewRetailer=(ImageButton) findViewById(R.id.btnAddNew);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId())
        {
            case R.id.Sync:
                new SyncData().execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SaveRetailer extends AsyncTask<String,Void,String>
    {
ProgressDialog dialog=new ProgressDialog(context);


        @Override
        protected void onPreExecute() {
            dialog.setTitle("Please wait!!!");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return dbh.AddNewRetailer(params[0],params[1]);
        }


        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            if(s.equals("Error"))
            {
                new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE).setTitleText("No Internet Connection or Server Unreachable").show();
            }
            else if(s.equals("fail"))
            {
                new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE).setTitleText("Unable to connect with Server!!!").show();
            }
            else {
              new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE).setContentText("Added Successfully").show();
                Retaileradapter.notifyDataSetChanged();
            }
            super.onPostExecute(s);
        }
    }

    private class SyncData extends AsyncTask<Void,Void,String>
    {
        ProgressDialog dialog =new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(Void... params) {
            return  dbh.SyncSecondry();
        }

        @Override
        protected void onPreExecute() {
            if(dialog.isShowing())
                dialog.dismiss();
            dialog.setTitle("Syncing Data");
            dialog.setMessage("Please Wait");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("Success") )
            {
                new SweetAlertDialog(MainActivity.this,SweetAlertDialog.SUCCESS_TYPE).setTitleText("Synced").show();
            }
            else {
                new SweetAlertDialog(MainActivity.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Sync Failed").show();
            }
            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, AndroidDatabaseManager.class));
        super.onBackPressed();
    }
}
