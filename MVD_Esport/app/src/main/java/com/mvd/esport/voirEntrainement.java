package com.mvd.esport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class voirEntrainement extends AppCompatActivity {

    public Button btnRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voir_entrainement);
        btnRetour = findViewById(R.id.btnRetour);

        btnRetour.setOnClickListener(v -> finish());
    }
}