package com.mvd.esport.data

//Objet pour storer les donnees utilisateur
//donneesUtilisateur listOf() = ArrayList<donneesUtilisateur> dataUser = new ArrayList<>();
data class DonneesUtilisateur (
    val nom: String,
    val equipe: String,
    val activitePratique: String,
    val date: String,
    val objectifPersonel: String,
    val dureeMinute: String,
    val intensite: String,
    val notePerso: String
)