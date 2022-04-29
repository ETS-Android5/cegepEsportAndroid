package com.mvd.esport.data

//Objet pour storer les donnees utilisateur
//donneesUtilisateur listOf() = ArrayList<donneesUtilisateur> dataUser = new ArrayList<>();
data class donneesUtilisateur (
    val nom: String,
    val equipe: String,
    val activitePratique: String,
    val date: String,
    val objectifPersonel: String,
    val dureeMinute: String,
    val intensite: String
)