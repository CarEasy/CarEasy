package com.carona.careasy.careasy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.carona.careasy.careasy.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
    }
}
