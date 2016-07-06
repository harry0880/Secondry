package com.secondry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.secondry.Utils.DbHandler;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 29/06/2016.
 */
public class Login extends AppCompatActivity {
    FancyButton login;
    EditText username;
    DbHandler db;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login);
        context=this;
        initialize();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().trim().equals(""))
                {
                    db.insert_user(username.getText().toString());
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                }
                else {
                    Snackbar.make(v,"Please enter your username",Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        super.onCreate(savedInstanceState);
    }

    void initialize()
    {
        login=(FancyButton)findViewById(R.id.btnSubmit);
        username=(EditText) findViewById(R.id.etusername);
        db=new DbHandler(Login.this);
    }

}
