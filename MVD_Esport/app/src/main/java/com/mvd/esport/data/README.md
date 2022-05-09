# Victor Bélanger dit : 
La class kotlin ici permet de créer un objet qui regroupe toutes les infos qu'on a besoin pour créer une page de suivi PDF.

### Aperçu du contenu du fichier
```Kotlin
data class donneesUtilisateur (
    val nom: String,
    val equipe: String,
    val activitePratique: String,
    val date: String,
    val objectifPersonel: String,
    val dureeMinute: String,
    val intensite: String
)
```
