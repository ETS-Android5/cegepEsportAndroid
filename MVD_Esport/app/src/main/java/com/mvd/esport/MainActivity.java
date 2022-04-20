package com.mvd.esport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "";
    // you need to have a list of data that you want the spinner to display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "test");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //choix équipe combo box - voir resource String pour changer les valeurs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choixÉquipe, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = (Spinner) findViewById(R.id.choixÉquipe);
        sItems.setAdapter(adapter);
        //fin choix équipe
    }
}