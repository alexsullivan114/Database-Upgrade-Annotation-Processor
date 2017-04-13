package com.example.alexscomputerminedonttouch.dbtestapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteHelper helper = new SQLiteHelper(this, "MyDatabase", null, 3);
        SQLiteDatabase database = helper.getWritableDatabase();

    }
}
