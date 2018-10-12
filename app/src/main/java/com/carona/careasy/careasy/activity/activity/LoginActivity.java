package com.carona.careasy.careasy.activity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.carona.careasy.careasy.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void abrirTelaMapa(View view){
        startActivity( new Intent(this, MapsActivity.class));
    }
}
