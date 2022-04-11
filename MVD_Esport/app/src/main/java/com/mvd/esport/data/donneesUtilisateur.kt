package com.mvd.esport.data

//Objet pour storer les donnees utilisateur
data class donneesUtilisateur (
    val nom: String,
    val equipe: String,
    val activitePratique: String,
    val date: String,
    val objectifPersonel: String,
    val dureeMinute: Int,
    val intensite: String
)