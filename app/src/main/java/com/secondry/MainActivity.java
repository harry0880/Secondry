package com.secondry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.secondry.Utils.SimpleScannerFragmentActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
SearchableSpinner spModel;
    EditText etQty,etImei;
    FancyButton btnSubmit;
    ImageButton barcode;
    ArrayAdapter<GetSetData> adapter;
    ArrayList<GetSetData> data;
    ListView lv;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
        setModel();
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ArrayList<String> imeino=new ArrayList<String>();
                String[] arr=etImei.getText().toString().split("\n");
               if(Integer.parseInt(etQty.getText().toString()) == (arr.length)) {
                   for (String ar : arr) {
                       imeino.add(ar);
                   }
                   data.add(new GetSetData(spModel.getSelectedItem().toString(), etQty.getText().toString(), imeino));
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

    void setModel()
    {
        ArrayList<String> ar=new ArrayList<>();
        ar.add("MOB1");
        ar.add("MOB2");
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ar);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(Adapter);
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


    void initialize()
    {
        spModel=(SearchableSpinner) findViewById(R.id.spModel);
        etQty=(EditText) findViewById(R.id.etQty);
        btnSubmit=(FancyButton) findViewById(R.id.btnSubmit);
        barcode=(ImageButton) findViewById(R.id.barcode);
       data=new ArrayList<>();
        lv=(ListView) findViewById(R.id.listview);
        etImei=(EditText) findViewById(R.id.etImei);
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
