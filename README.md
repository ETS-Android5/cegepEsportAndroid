# TODO

### Pour la base de données :

Sample Code
```Java
// pour afficher la Bd
database = helper.getReadableDatabase();
Cursor c = database.rawQuery("SELECT * FROM Esport", null); //TODO : Faire une requete avec WHERE //"SELECT * FROM Esport WHERE _Id = [userInput]"
c.moveToNext();
c.moveToNext();
c.moveToNext();
// test pour afficher un champ de la BD à l'écran
tx_affichBd.setText(c.getString(1)); //TODO : Insérer la sélection dans l'interface graphique
//Relier avec le bouton Sauvegarder pour le mettre dans la BD
sauvegardeExercice.setOnClickListener(new View.OnClickListener() { //TODO : mettre le code dans le meme event du button EXPORTER. (voir PDF)
    @Override
    public void onClick(View v){
        database = helper.getWritableDatabase();
        database.execSQL("INSERT INTO Esport (Name, Team, ActivityPerformed, Date, ObjectifPersonnel, Time, Intensity) VALUES ('" + editTextTextPersonName.getText() + "', '" + editTextNomEquipe.getText()+ "', '" + editTextActivitéPhysique.getText()+ "', '" + editTextDate.getText()+ "', '" + editTextDurée.getText()+ "', '" + editTextObjectifPersonnel.getText()+ "', '" + choixIntensité.getSelectedItem().toString()+ "')");
    }
});
```

- Faire une requete avec WHERE //"SELECT * FROM Esport WHERE _Id = [userInput]"
- Insérer la sélection dans l'interface graphique
- mettre le code pour sauvegarder dans la base de donnees dans le meme event du button EXPORTER pour PDF.

