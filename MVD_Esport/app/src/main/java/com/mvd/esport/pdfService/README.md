# pdfService
La package ici contient toutes les class et fonctions qui permet de créer un document PDF.

## PdfService.kt
**PdfService** est une class qui donne les fonctions pour créer un document PDF à partir de l'application.

### Les variables de PdfService
```Kotlin
/**
 * Variable pour gérer la police d'un titre.
 */
val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD) //TODO: Modifier les polices au besoins
/**
 * Variable pour gérer la police d'un texte.
 */
val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)

/**
 * Objet pour gérer un document PDF.
 */
private lateinit var pdf: PdfWriter //objet PDF
/**
 * Variable String pour le débug l'application
 */
private val TAG = "pdfService"
```

### Les fonctions de PdfService
```Kotlin
/**
 * createFile
 *
 * Fonction pour créer un fichier.
 * @param title Le titre du fichier
 * @return Retourne un objet File
 */
private fun createFile(title: String): File

/**
 * createDocument
 *
 * Fonction qui va définir le format du document PDF, comme les margins et la grosseur du papier.
 * @return Retourne un objet Document
 */
private fun createDocument(): Document

/**
 * setupPdfWriter
 *
 * Procédure qui va attribuer à l'objet pdf le format du document et l'emplacement et nom du fichier PDF utilisé pour enregistrer le document.
 * @param document Objet Document qui décrit le format du document PDF.
 * @param file Objet File qui point au fichier PDF utlisé pour enregistrer le document.
 */
private fun setupPdfWriter(document: Document, file: File)

/**
 * createTable
 *
 * Fonction qui permet de créer un tableau dans le document.
 * @param column le nombre de colonne qu'il y a dans le tableau
 * @param columnWidth la largeur de chaque colonne individuel dans un Array de Float.
 * @return Retourne un objet PdfPTable qui représente le tableau créer.
 */
private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable

/**
 * createCell
 *
 * Fonction qui permet de créer un cellule d'un tableau.
 * @param content Objet String qui contient le texte à mettre dans la cellule.
 * @return retourne un objet PdfPCell, une cellule à insérer dans un tableau.
 */
private fun createCell(content: String): PdfPCell

/**
 * addLineSpace
 *
 * procédure qui ajoute des lignes blanches dans le document pour espacer les paragraphs / tableaux.
 * @param document le document PDF utilisé
 * @param number le nombre de ligne blanche à mettre dans le document.
 */
private fun addLineSpace(document: Document, number: Int)

/**
 * createParagraph
 *
 * Fonction permet de créer un paragraph pour un document PDF.
 * @param content un objet String qui contient le paragraph en question
 * @return retourne un objet Paragraph à insérer dans un document.
 */
private fun createParagraph(content: String): Paragraph

/**
 * resizePhoto
 *
 * Fonction qui redimensionne une photo selon la grosseur des pages d'un document.
 * @param document Objet Document qui contient l'information sur les dimensions des pages du document
 * @param image objet Image qui contient l'image qu'il faut redimensionner.
 * @return Retourne un objet Image de l'image source modifiée.
 */
private fun resizePhoto(document: Document, image: Image): Image

/**
 * rotateImage
 *
 * Fonction qui retourne une valeur pour tourner l'image selon les valeurs EXIF de l'image.
 * @param path le chemin pour aller chercher l'image.
 * @return Retourne un Float négatif représentant les dégrées pour pivoter l'image. À utiliser dans une fonction setRotationDegrees avec un objet Image.
 */
private fun rotateImage(path: String): Float

/**
 * createUserTable
 *
 * Procédure pour générer un document PDF avec le nom et l'équipe de l'utilisateur, un tableau des activité fait durant la journée, une note personelle et une image.
 * @param data une liste d'objet DonneesUtilisateur qui regroupe tout l'information nécessaire pour remplire le document
 * @param imageUser Objet String pour aller chercher l'image à inclure dans le document via le chemin du fichier.
 * @param onFinish fonction anonyme qui définit quoi affaire après avoir fini l'opération. Prend en paramètre un objet File. Inutiliser pour l'instant.
 */
fun createUserTable(
    data: List<DonneesUtilisateur>, //Je vais laisser ceci en list, dans le cas qu'on voudrait un tableau avec plusieurs entrées.
    imageUser: String, //path de l'image
    onFinish: (file: File) -> Unit,
)

/**
 * createPourMois
 *
 * Procédure qui va généré un document PDF qui contiendra le nom et l'équipe de l'utilisateur, ainsi qu'une liste complète de tout les entrainements enregistrés sur le téléphone.
 * @param data une liste d'objet DonneesUtilisateur qui regroupe tout l'information nécessaire pour remplire le document
 * @param onFinish fonction anonyme qui définit quoi affaire après avoir fini l'opération. Prend en paramètre un objet File. Inutiliser pour l'instant.
 */
fun createPourMois(
    data: List<DonneesUtilisateur>, //Je vais laisser ceci en list, dans le cas qu'on voudrait un tableau avec plusieurs entrées.
    onFinish: (file: File) -> Unit,
)
```

## PdfFunctions.kt
**PdfFunctions** est une class qui permet l'accès au fonction de PdfService. Il agit comme intermédiaire entre le code Java et Kotlin. 

### Note
À la construction, il faut lui passer le context de l'activité. 
Exemple Java : 
```Java
  PdfFunctions pdfFunctions;
  pdfFunctions = new PdfFunctions(this);
```

### Les variables de PdfFunctions

```Kotlin
//paramThis : Le context de l'application.
private val paramThis: Context 
```

### Les fonctions de PdfFunctions
```Kotlin
/**
 * createPdf
 *
 * Procédure qui va chercher la fonction createUserTable de pdfService pour créer un document PDF.
 * @param userData les données entrées dans l'utilisateur
 * @param image le chemin de l'image
 */
fun createPdf(userData: List<DonneesUtilisateur>, image: String)

/**
 * createPdfMois
 *
 * Procédure qui va chercher la fonction createPourMois de pdfService pour créer un document PDF.
 * @param userData les données entrées dans l'utilisateur
 */
fun createPdfMois(userData: List<DonneesUtilisateur>)

/**
 * openFile
 * 
 * Procédure qui permet d'ouvrir un document PDF.
 * @param file Objet File pour gérer le document PDF.
 */
fun openFile(file: File)

/**
 * toastErrorMessage
 *
 * Procédure qui envoit un toast s'il y a erreur.
 * @param s objet String pour le message d'erreur.
 */
fun toastErrorMessage(s: String)

```

## PdfHandler.kt
**PdfHandler** est une class qui gère l'accès des fichiers sur le téléphone peu importe la version du téléphone.
