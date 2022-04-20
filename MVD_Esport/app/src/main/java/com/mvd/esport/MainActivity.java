package com.mvd.esport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mvd.esport.data.donneesUtilisateur;
import com.mvd.esport.pdfService.pdfService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.io.File;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity{


    private static final String TAG = "";
    //sélecteur de date et de temps pour le input date et durée
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EditText dateText;
    EditText timeText;
    EditText inputÉquipe;
    EditText inputNom;
    EditText inputActivite;
    EditText inputpersonelle;
    Spinner choixintense;
    //fin sélecteur

    //Données utilisateurs
    ArrayList<donneesUtilisateur> dataUser = new ArrayList<>();
    Button pdfButton;

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //prend la langue du téléphone

        dateText = findViewById(R.id.editTextDate);
        timeText = findViewById(R.id.editTextDurée);
        inputÉquipe = findViewById(R.id.editTextNomEquipe);
        inputNom = findViewById(R.id.editTextTextPersonName);
        inputActivite = findViewById(R.id.editTextActivitéPhysique);
        inputpersonelle = findViewById(R.id.editTextObjectifPersonnel);
        choixintense = findViewById(R.id.choixIntensité);
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
        //choix intensité combo box - voir resource String pour changer les valeurs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choixIntensité, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = (Spinner) findViewById(R.id.choixIntensité);
        sItems.setAdapter(adapter);
        sItems.setSelection(1);
        //fin choix équipe

        //Code lié au button PDF
        //Ayyy j'aime don ben ça de faire les event avec une fonction lambda <3
        //"the more I know"
        pdfButton = findViewById(R.id.sauvegardeExercice);
        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataUser.clear();
                dataUser.add(new donneesUtilisateur(inputNom.toString(),inputÉquipe.toString(),inputActivite.toString(),dateText.toString(),inputpersonelle.toString(),timeText.toString(),choixintense.toString()));
                //createPDF(); //TODO : Kotlin Unit to Java void. Voir aussi openFile() dans le projet pdf export
                    //TODO: Je pourrais aussi mettre createPDF() et openFile() dans leur propre class Kotlin. c'est awkward de traduire des objets Kotlin en Java parfois.
            }
        });
    }

    /*private void createPDF() {
        pdfService pdfService = new pdfService();
        pdfService.createUserTable(dataUser, dataUser.get(3).toString(), (Function1<? super File, Unit>) fichier); //Cast fichier to unit file
    }*/
}