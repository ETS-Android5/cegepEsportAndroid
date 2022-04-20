package com.mvd.esport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    public enum WindowSizeClass { COMPACT, MEDIUM, EXPANDED }
    private static final String TAG = "";
    //section photo
    private ImageView imgPhoto;
    private Button btnPhoto;
    //fin section photo

    //section time et date picker pour le input date et durée
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EditText dateText;
    EditText timeText;
    EditText inputÉquipe;
    //fin time et date picker

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initialisationInterface();
        initialisationPickers();
        initialisationPhoto();
    }

    //créateur: David Mamina
    public void initialisationPhoto(){
        //lien avec les objets graphiques
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        //textView = (TextView) findViewById(R.id.textView);
        //Initialisation méthode clic sur boutton
        createOnClicPhotoButton();
    }
    //créateur: David Mamina
    private void createOnClicPhotoButton(){
        btnPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //accès à la galerie du téléphone
                //https://stackoverflow.com/questions/43519311/java-io-filenotfoundexception-permission-denied-when-saving-image
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    else{
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);
                    }
                }
            }
        });
    }
    //https://medium.com/kinandcartacreated/finally-a-clean-way-to-deal-with-permissions-in-android-539786a7846
    //créateur: David Mamina
    public void onActivityResult(int RequestCode, int resultCode, Intent data)
    {

        super.onActivityResult(RequestCode, resultCode, data);

        //vérifie si une image est récupérée
        if(RequestCode==1 && resultCode==RESULT_OK){ //resulteCode verifie si l'image a été sélectionée

            Uri selectedImage = data.getData();
            String[] filePathColum = {MediaStore.Images.Media.DATA};
            //Cuseur d'accès au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColum,null,null,null);
            //position sur la première ligne
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColum[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();

                //recuperation image
                Bitmap image = BitmapFactory.decodeFile((imgPath));
                //affiche
                imgPhoto.setImageBitmap(image);
            }
        else
        {
            Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_LONG).show();
        }
    }

    //créateur: Maxime Paulin
    public void initialisationInterface(){
        //prend la langue du téléphone

        dateText = (EditText) findViewById(R.id.editTextDate);
        timeText = (EditText) findViewById(R.id.editTextDurée);
        inputÉquipe = (EditText) findViewById(R.id.editTextNomEquipe);
        dateText.setInputType(InputType.TYPE_NULL);
        timeText.setInputType(InputType.TYPE_NULL);

        if(Locale.getDefault().getLanguage() == "en"){
            //https://stackoverflow.com/questions/52148129/programmatically-set-margin-to-constraintlayout
            //tasse le input pour écrire l'équipe parce que le layout en fracais fit mais pas en anglais

            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) inputÉquipe.getLayoutParams();

            newLayoutParams.topMargin = (int) convertDpToPixel(-23.0f,this);
            newLayoutParams.leftMargin = (int) convertDpToPixel(36.0f,this);
            newLayoutParams.rightMargin = 0;
            inputÉquipe.setLayoutParams(newLayoutParams);

        }
        //choix intensité combo box - voir resource String pour changer les valeurs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choixIntensité, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = (Spinner) findViewById(R.id.choixIntensité);
        sItems.setAdapter(adapter);
        sItems.setSelection(1);
        //fin choix équipe
    }
    //créateur: Maxime Paulin
    public void initialisationPickers(){
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        //https://codedocu.com/Google/Android/Development/Android-Controls/Android-TimePickerDialog---Digital-Layout?2664
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar tempsExercice = Calendar.getInstance();
                int heure = 0;
                int minute = 0;
                // timme picker dialog
                timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int heure, int minute) {
                                if(Locale.getDefault().getLanguage() == "en"){
                                    timeText.setText(heure + " Hour(s)" + " and " + minute + " minute(s)");
                                }
                                else{
                                    timeText.setText(heure + " Heure(s)" + " et " + minute + " minute(s)");
                                }
                            }
                        }, heure, minute, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.setTitle("Durée de l'exercice");
                timePickerDialog.show();
            }
        });
    }
}