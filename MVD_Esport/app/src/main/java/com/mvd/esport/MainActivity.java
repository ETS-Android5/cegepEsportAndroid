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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;

import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;


import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;

import android.widget.Toast;

import com.mvd.esport.data.DonneesUtilisateur;

import com.mvd.esport.pdfService.PdfFunctions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Locale;

/**********************************************************************
 Autheurs : Victor Bélanger, Maxime Paulin, David Mamina
 Créer le : 04-01-2022

 but du programme: Une application pour que les athlètes
 de E-sport au cégep de sept-îles puisent prendre en note
 leurs heures d'entrainments
 **********************************************************************/

public class MainActivity extends AppCompatActivity {

    //victor - pour gérer la BD plus facilement.
    int bd_id = 0;
    int bd_name = 1;
    int bd_team = 2;
    int bd_activity = 3;
    int bd_date = 4;
    int bd_objectif = 5;
    int bd_time = 6;
    int bd_intensity = 7;
    int bd_note = 8;


    // Création d'un object helper nous servant de donné un nom à notre BD et pouvoir la créer
    SQLiteOpenHelper helper;
    SQLiteDatabase database;
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

    //fin time et date picker

    //victor - Données utilisateurs et sélecteurs pour les text restants.
    EditText inputNom;
    EditText inputEquipe;
    EditText inputActivite;
    EditText inputpersonelle;
    Spinner choixintense;
    EditText notePerso;
    ArrayList<DonneesUtilisateur> dataUser = new ArrayList<>();
    Button pdfButton;
    //pdfFunctions : Class que j'ai créer pour travailler avec les fonctions kotlins pour créer un PDF. parce que utiliser les services de pdfServices directement était awkward lol.
    PdfFunctions pdfFunctions;
    String imgPath = " ";
    Button pdfButtonAll;


    ////Maxime - voirEntrainement
    public Button btnVoirEntrainement;

    //Maxime
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    //Maxime
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //initialise les variables pour le formulaire
        initMainLayout();
        initPickers();
        //initialise le module à David
        initModulePhoto();
        //initialise le module à Victor
        initModulePDF_BD();

        //Initialisation de helper pour créer la BD, tables ...
        helper = new SQLiteOpenHelper(MainActivity.this, "DataSemaine.db", null, 4) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                //création d'une table
                db.execSQL("CREATE TABLE Esport (_Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Name TEXT, Team TEXT, ActivityPerformed TEXT, Date DATE, ObjectifPersonnel TEXT, Time TIME, Intensity TEXT, Note TEXT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.d(TAG, "onUpgrade(): Mise à jour de la BD de la version " + oldVersion + " à la version " + newVersion);
                db.execSQL(" DROP TABLE Esport");
                onCreate(db);
            }
        };
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
    public void initModulePhoto() {
        //lien avec les objets graphiques
        imgPhoto = findViewById(R.id.imgPhoto);
        btnPhoto = findViewById(R.id.btnPhoto);
        createOnClicPhotoButton();
    }

    //créateur: David Mamina
    private void createOnClicPhotoButton() {
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
    public boolean check_Write_perm() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    //https://medium.com/kinandcartacreated/finally-a-clean-way-to-deal-with-permissions-in-android-539786a7846
    //créateur: David Mamina
    public void onActivityResult(int RequestCode, int resultCode, Intent data) {

        super.onActivityResult(RequestCode, resultCode, data);

        //vérifie si une image est récupérée
        if (RequestCode == 1 && resultCode == RESULT_OK) { //resulteCode verifie si l'image a été sélectionée

            Uri selectedImage = data.getData();
            String[] filePathColum = {MediaStore.Images.Media.DATA};
            //Cuseur d'accès au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColum, null, null, null);
            //position sur la première ligne
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColum[0]);
            imgPath = cursor.getString(columnIndex);
            cursor.close();

            //recuperation image
            //Victor - J'ai butchered un peu le code "for the greater good"
            Bitmap image = null;
            try {
                image = rotateImage(null, imgPath);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            //affiche l'image
            imgPhoto.setImageBitmap(image);
        } else {
            Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_LONG).show();
        }
    }

    //créateur: Maxime Paulin
    public void initMainLayout() {
        //nom de la personne ansi que son équipe
        inputNom = findViewById(R.id.editTextTextPersonName);
        inputEquipe = findViewById(R.id.editTextNomEquipe);

        //bouton pour changer d'interface (voir les activités antérieur)
        btnVoirEntrainement = findViewById(R.id.voirEntrainement2);

        //formulaire
        inputActivite = findViewById(R.id.editTextActivitéPhysique);
        dateText = findViewById(R.id.editTextDate);
        inputpersonelle = findViewById(R.id.editTextObjectifPersonnel);
        timeText = findViewById(R.id.editTextDurée);
        choixintense = findViewById(R.id.choixIntensité);
        //fin formulaire
        //note Perso
        notePerso = findViewById(R.id.voirEntrainementNotePerso);
        //fin note Perso

        //Si la langue du téléphone est en anglais ont tasse la boite de texte équipe pour l'aligné avec la boite nom
        if (Locale.getDefault().getLanguage().equals("en")) {
            //https://stackoverflow.com/questions/52148129/programmatically-set-margin-to-constraintlayout
            //Prend les paramètres du Layouts en lien avec la boite de texte équipe
            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) inputEquipe.getLayoutParams();

            //ont change la marge pour tassé le texte
            newLayoutParams.topMargin = (int) convertDpToPixel(-23.0f, this);
            newLayoutParams.leftMargin = (int) convertDpToPixel(36.0f, this);
            newLayoutParams.rightMargin = 0;
            //applique les modifications
            inputEquipe.setLayoutParams(newLayoutParams);
        }

        //choix intensité est un combo box - voir les resource String pour changer les chaines de caractères
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choixIntensité, android.R.layout.simple_spinner_item);

        //prend la ressource
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = findViewById(R.id.choixIntensité);
        sItems.setAdapter(adapter);
        //modéré par défaut - indice commence à 0
        sItems.setSelection(1);

        btnVoirEntrainement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, voirEntrainement.class);
            startActivity(intent);
        });
    }

    //Créateur: Victor Bélanger
    public void initModulePDF_BD() {

        pdfFunctions = new PdfFunctions(this);

        //Code lié au button PDF
        //Ayyy j'aime don ben ça de faire les event avec une fonction lambda <3
        //"the more I know"
        pdfButton = findViewById(R.id.sauvegardeExercice);
        //Maxime 05-03-2022 18:15 : J'ai ajouter les lignes à david au bouton export pdf pour écrire dans la BD les champs du Layout
        pdfButton.setOnClickListener(view -> {
            //maxime 05-03-2022 18:27: J'ai ajouter des Toasts pour avertir les utilisateurs
            if (check_Write_perm()) {
                database = helper.getWritableDatabase();
                //entre dans la BD les champs du layout Principal
                //TODO : Cette commande ne traite pas la valeur des contenues. L'applicatin peut crasher s'il rencontre des characteres specials.
                database.execSQL("INSERT INTO Esport (Name, Team, ActivityPerformed, Date, ObjectifPersonnel, Time, Intensity, Note) VALUES ('"
                        + inputNom.getText().toString() + "', '" + inputEquipe.getText().toString() + "', '"
                        + inputActivite.getText().toString() + "', '" + dateText.getText().toString() + "', '" + timeText.getText().toString() + "', '"
                        + inputpersonelle.getText().toString() + "', '" + choixintense.getSelectedItem().toString() + "', '" + notePerso.getText().toString() + "')");
                //fin du module database

                //le code en commentaire est pour créer un pdf tout seul avec un seul entrainement.
                /*
                dataUser.add(new donneesUtilisateur(inputNom.getText().toString(), inputEquipe.getText().toString(), inputActivite.getText().toString(),
                        dateText.getText().toString(), inputpersonelle.getText().toString(), timeText.getText().toString(), choixintense.getSelectedItem().toString(), notePerso.getText().toString()));

                pdfFunctions.createPdf(dataUser, imgPath);
                Toast.makeText(this, "PDF créer", Toast.LENGTH_SHORT).show();
                */
            } else {
                Toast.makeText(this, "Veuillez appliquer les accèes", Toast.LENGTH_SHORT).show();
            }
        });

        //victor - modif derniere minute pour Yves
        pdfButtonAll = findViewById(R.id.exportAll);
        pdfButtonAll.setOnClickListener(view -> {
            if (check_Write_perm()) { //si il a les permissions
                try {
                    database = helper.getWritableDatabase();
                    Cursor c = database.rawQuery("SELECT * FROM Esport", null); //va chercher les donnees
                    if (c.getCount() > 0) { //s'il y a de quoi dans la base de données
                        //clear le tableau au cas ou.
                        dataUser.clear();
                        for (int i = 0; i < c.getCount(); i++) { //l'Id commence a 1

                            //insert les données dans dataUser pour PDF.
                            c.moveToNext();
                            DonneesUtilisateur temp = new DonneesUtilisateur(
                                    inputNom.getText().toString(), inputEquipe.getText().toString(),
                                    c.getString(bd_activity), c.getString(bd_date), c.getString(bd_objectif),
                                    c.getString(bd_time), c.getString(bd_intensity), c.getString(bd_note)
                            );
                            dataUser.add(temp);
                        }
                        //maintenant va créer le PDF
                        pdfFunctions.createPdfMois(dataUser);

                    } else {
                        Toast.makeText(this, "Vous avez aucun entrainement enregistré sur votre téléphone", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLiteException databaseError) {
                    Toast.makeText(this, "Il y a une erreur avec la base de données!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Veuillez appliquer les accès", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Maxime 05-03-17:44 - problème photo règler
    //Victor: Bruh enleve pas mon commentaire :(

    //Victor - Faque on a un petit probleme avec les images photos : On lis pas la metadonné pour savoir l'orientation de la photo.
    //C'est important de le savoir, car les photos pris par téléphone ne tourne pas l'image a l'enregistrement de l'image pour l'afficher a l'écran comme du monde, ils ont juste foutu des données dans l'image.
    //c'est donc au développeur d'orienter l'image selon les données EXIF the l'image. Fun :)
    //Y'a seulement moi qui peut être lâche dans le monde!!!!!

    //Anyway. Voici une façon de gérer ça.
    //https://gist.github.com/tomogoma/788e3b775dd611c9226f8e17781a0f0c
    public static Bitmap rotateImage(Bitmap bitmap, String path) throws IOException {

        if (bitmap == null) { //si l'image n'existe pas. va la créer
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
    public void initPickers() {
        dateText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> dateText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
            datePickerDialog.show();
        });
        //https://codedocu.com/Google/Android/Development/Android-Controls/Android-TimePickerDialog---Digital-Layout?2664
        timeText.setOnClickListener(view -> { //TODO : Faire des ressources String pour éviter le... euh. locale thing...
            int heure = 0;
            int minute = 0;
            // timme picker dialog
            timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view1, heure1, minute1) -> {
                        if (Locale.getDefault().getLanguage().equals("en")) {
                            timeText.setText(heure1 + " Hour(s)" + " and " + minute1 + " minute(s)");
                        } else {
                            timeText.setText(heure1 + " Heure(s)" + " et " + minute1 + " minute(s)");
                        }
                    }, heure, minute, true);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Durée de l'exercice");
            timePickerDialog.show();
        });
    }
}