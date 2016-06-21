package com.secondry;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.secondry.GetterSetter.GetSetData;
import com.secondry.GetterSetter.IMEI;
import com.secondry.Utils.SimpleScannerFragmentActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
SearchableSpinner spModel;
    EditText etQty;
    FancyButton btnSubmit;
    ImageButton barcode;
    ArrayAdapter<GetSetData> adapter;
    ArrayList<GetSetData> data;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
        setModel();
        adapter=new ArrayAdapter<GetSetData>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, data);
    lv.setAdapter(adapter);
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etQty.getText().toString().trim().equals(""))
                {
                    IMEI.setCnt(Integer.parseInt(etQty.getText().toString()));
                    startActivity(new Intent(MainActivity.this, SimpleScannerFragmentActivity.class));
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            data.add(new GetSetData(spModel.getSelectedItem().toString(),etQty.getText().toString(),IMEI.getImei()));
             adapter.notifyDataSetChanged();
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
    }

    void setModel()
    {
        ArrayList<String> ar=new ArrayList<>();
        ar.add("MOB1");
        ar.add("MOB2");
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ar);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(Adapter);
    }
    void initialize()
    {
        spModel=(SearchableSpinner) findViewById(R.id.spModel);
        etQty=(EditText) findViewById(R.id.etQty);
        btnSubmit=(FancyButton) findViewById(R.id.btnSubmit);
        barcode=(ImageButton) findViewById(R.id.barcode);
       data=new ArrayList<>();
        lv=(ListView) findViewById(R.id.listview);
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

        return super.onOptionsItemSelected(item);
    }
}
