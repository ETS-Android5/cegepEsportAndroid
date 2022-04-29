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
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;


import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;

import android.widget.Toast;

import com.mvd.esport.data.donneesUtilisateur;

import com.mvd.esport.pdfService.pdfFunctions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    //section photo
    private ImageView imgPhoto;
    private Button btnPhoto;
    //fin section photo

    //section time et date picker pour le input date et durée
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EditText dateText;
    EditText timeText;
    EditText inputEquipe;
    //fin time et date picker

    //victor - Données utilisateurs et sélecteurs pour les text restants.
    EditText inputNom;
    EditText inputActivite;
    EditText inputpersonelle;
    Spinner choixintense;
    ArrayList<donneesUtilisateur> dataUser = new ArrayList<>();
    Button pdfButton;
    //pdfFunctions : Class que j'ai créer pour travailler avec les fonctions kotlins pour créer un PDF. parce que utiliser les services de pdfServices directement était awkward lol.
    pdfFunctions pdfFunctions;
    String imgPath = " ";

    ////Maxime - voirEntrainement
    public Button btnVoirEntrainement;

    //Maxime
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    //Maxime
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
        initAdditionel();
    }

    //Maxime
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        outState.putString("nom", inputNom.getText().toString());
        outState.putString("équipe", inputEquipe.getText().toString());
        outState.putString("activitéPratiquée", inputActivite.getText().toString());
        outState.putString("date", dateText.getText().toString());
        outState.putString("objectifPersonnel", inputpersonelle.getText().toString());
        outState.putString("durée", timeText.getText().toString());
        outState.putInt("intensité", choixintense.getSelectedItemPosition());
    }

    //Maxime
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        inputNom.setText(savedInstanceState.getString("nom"));
        inputEquipe.setText(savedInstanceState.getString("équipe"));
        inputActivite.setText(savedInstanceState.getString("activitéPratiquée"));
        dateText.setText(savedInstanceState.getString("date"));
        inputpersonelle.setText(savedInstanceState.getString("objectifPersonnel"));
        timeText.setText(savedInstanceState.getString("durée"));
        choixintense.setSelection(savedInstanceState.getInt("intensité"));
    }

    //créateur: David Mamina
    public void initialisationPhoto(){
        //lien avec les objets graphiques
        imgPhoto = findViewById(R.id.imgPhoto);
        btnPhoto = findViewById(R.id.btnPhoto);
        createOnClicPhotoButton();
    }

    //créateur: David Mamina
    private void createOnClicPhotoButton(){
        btnPhoto.setOnClickListener(v -> {
            //accès à la galerie du téléphone
            //https://stackoverflow.com/questions/43519311/java-io-filenotfoundexception-permission-denied-when-saving-image
            if (check_Write_perm()) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    //Victor - Fonction qui retourne faux si permission n'est pas granted, vrai si granted,
    public boolean check_Write_perm(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
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
            imgPath = cursor.getString(columnIndex);
            cursor.close();

            //recuperation image
            //Victor - J'ai butchered un peu le code "for the greater good"
            Bitmap image = null;
            try { image = rotateImage(null, imgPath); } catch (IOException e) { Log.e(TAG,e.toString()); }
            //affiche l'image
            imgPhoto.setImageBitmap(image);
        }
        else
        {
            Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_LONG).show();
        }
    }

    //créateur: Maxime Paulin
    public void initialisationInterface(){
        btnVoirEntrainement = findViewById(R.id.voirEntrainement2);
        dateText = findViewById(R.id.editTextDate);
        timeText = findViewById(R.id.editTextDurée);
        inputEquipe = findViewById(R.id.editTextNomEquipe);
        dateText.setInputType(InputType.TYPE_NULL);
        timeText.setInputType(InputType.TYPE_NULL);

        if(Locale.getDefault().getLanguage().equals("en")){
            //https://stackoverflow.com/questions/52148129/programmatically-set-margin-to-constraintlayout
            //tasse le input pour écrire l'équipe parce que le layout en fracais fit mais pas en anglais

            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) inputEquipe.getLayoutParams();

            newLayoutParams.topMargin = (int) convertDpToPixel(-23.0f,this);
            newLayoutParams.leftMargin = (int) convertDpToPixel(36.0f,this);
            newLayoutParams.rightMargin = 0;
            inputEquipe.setLayoutParams(newLayoutParams);

        }
        //choix intensité combo box - voir resource String pour changer les valeurs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choixIntensité, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = findViewById(R.id.choixIntensité);
        sItems.setAdapter(adapter);
        sItems.setSelection(1);
        //fin choix équipe

        btnVoirEntrainement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, voirEntrainement.class);
            startActivity(intent);
        });
    }

    //Créateur: Victor Bélanger
    public void initAdditionel(){
        inputNom = findViewById(R.id.editTextTextPersonName);
        inputActivite = findViewById(R.id.editTextActivitéPhysique);
        inputpersonelle = findViewById(R.id.editTextObjectifPersonnel);
        choixintense = findViewById(R.id.choixIntensité);

        pdfFunctions = new pdfFunctions(this);

        //Code lié au button PDF
        //Ayyy j'aime don ben ça de faire les event avec une fonction lambda <3
        //"the more I know"
        pdfButton = findViewById(R.id.sauvegardeExercice);
        pdfButton.setOnClickListener(view -> {
            dataUser.clear();
            dataUser.add(new donneesUtilisateur(inputNom.getText().toString(), inputEquipe.getText().toString(),inputActivite.getText().toString(),dateText.getText().toString(),inputpersonelle.getText().toString(),timeText.getText().toString(),choixintense.getSelectedItem().toString())   );
            if(check_Write_perm()){
                pdfFunctions.createPdf(dataUser, imgPath);
            }
        });
    }

    //Victor - Faque on a un petit probleme avec les images photos : On lis pas la metadonné pour savoir l'orientation de la photo.
    //C'est important de le savoir, car les photos pris par téléphone ne tourne pas l'image a l'enregistrement de l'image pour l'afficher a l'écran comme du monde, ils ont juste foutu des données dans l'image.
    //c'est donc au développeur d'orienter l'image selon les données EXIF the l'image. Fun :)
    //Y'a seulement moi qui peut être lâche dans le monde!!!!!

    //Anyway. Voici une façon de gérer ça.
    //https://gist.github.com/tomogoma/788e3b775dd611c9226f8e17781a0f0c
    public static Bitmap rotateImage(Bitmap bitmap, String path) throws IOException {

        if(bitmap == null){ //si l'image n'existe pas. va la créer
            bitmap = BitmapFactory.decodeFile(path);
        }

        int rotate = 0;
        ExifInterface exif = new ExifInterface(path); //va chercher les metadonne de l'image.
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    //créateur: Maxime Paulin
    public void initialisationPickers(){
        dateText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> dateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            datePickerDialog.show();
        });
        //https://codedocu.com/Google/Android/Development/Android-Controls/Android-TimePickerDialog---Digital-Layout?2664
        timeText.setOnClickListener(view -> { //TODO : Faire des ressources String pour éviter le... euh. locale thing...
            final Calendar tempsExercice = Calendar.getInstance();
            int heure = 0;
            int minute = 0;
            // timme picker dialog
            timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view1, heure1, minute1) -> {
                        if(Locale.getDefault().getLanguage().equals("en")){
                            timeText.setText(heure1 + " Hour(s)" + " and " + minute1 + " minute(s)");
                        }
                        else{
                            timeText.setText(heure1 + " Heure(s)" + " et " + minute1 + " minute(s)");
                        }
                    }, heure, minute, true);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Durée de l'exercice");
            timePickerDialog.show();
        });
    }
}