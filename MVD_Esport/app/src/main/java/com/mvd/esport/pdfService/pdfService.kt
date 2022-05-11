package com.mvd.esport.pdfService


import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.mvd.esport.data.DonneesUtilisateur
import java.io.File
import java.io.FileOutputStream

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Annas Surdyanto on 05/02/22.
 * Modified by Victor Bélanger on 2022-04-11
 * https://github.com/annasta13/Pdf-Export/blob/master/app/src/main/java/com/habileducation/pdfexport/pdfService/PdfService.kt
 *
 */

class PdfService {
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

    /**
     * createFile
     *
     * Fonction pour créer un fichier.
     * @param title Le titre du fichier
     * @return Retourne un objet File
     */
    private fun createFile(title: String): File {
        //Prepare file
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) //TODO: Le fichier Downloads est intéressant. Fait-on notre propre dossier au lieu?
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    /**
     * createDocument
     *
     * Fonction qui va définir le format du document PDF, comme les margins et la grosseur du papier.
     * @return Retourne un objet Document
     */
    private fun createDocument(): Document { //TODO: peut-être demander à yves s'il veut une page spécifique...
        //Create Document
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    /**
     * setupPdfWriter
     *
     * Procédure qui va attribuer à l'objet pdf le format du document et l'emplacement et nom du fichier PDF utilisé pour enregistrer le document.
     * @param document Objet Document qui décrit le format du document PDF.
     * @param file Objet File qui point au fichier PDF utlisé pour enregistrer le document.
     */
    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        //Open the document
        document.open()
    }

    /**
     * createTable
     *
     * Fonction qui permet de créer un tableau dans le document.
     * @param column le nombre de colonne qu'il y a dans le tableau
     * @param columnWidth la largeur de chaque colonne individuel dans un Array de Float.
     * @return Retourne un objet PdfPTable qui représente le tableau créer.
     */
    private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable { //createTable. Va créer un tableau générique.
        val table = PdfPTable(column) //nbr de col dans le tableau
        table.widthPercentage = 100f
        table.setWidths(columnWidth) //grosseur des col
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table //TODO: Check si le tableau généré nous convient.
    }

    //TODO : Ajuster le padding au besoin
    /**
     * createCell
     *
     * Fonction qui permet de créer un cellule d'un tableau.
     * @param content Objet String qui contient le texte à mettre dans la cellule.
     * @return retourne un objet PdfPCell, une cellule à insérer dans un tableau.
     */
    private fun createCell(content: String): PdfPCell { //Créer des cells avec text
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    /**
     * addLineSpace
     *
     * procédure qui ajoute des lignes blanches dans le document pour espacer les paragraphs / tableaux.
     * @param document le document PDF utilisé
     * @param number le nombre de ligne blanche à mettre dans le document.
     */
    private fun addLineSpace(document: Document, number: Int) { //ajoute une ligne vide dans le document
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    /**
     * createParagraph
     *
     * Fonction permet de créer un paragraph pour un document PDF.
     * @param content un objet String qui contient le paragraph en question
     * @return retourne un objet Paragraph à insérer dans un document.
     */
    private fun createParagraph(content: String): Paragraph { //va créer un paragraphe //TODO: Check format du paragraph.
        val paragraph = Paragraph(content, BODY_FONT)

        paragraph.alignment = Element.ALIGN_JUSTIFIED
        return paragraph
    }

    //TODO : Fonction qui scale l'image based on la taille de la feuille au besoin.
    //https://stackoverflow.com/questions/11120775/itext-image-resize
    /**
     * resizePhoto
     *
     * Fonction qui redimensionne une photo selon la grosseur des pages d'un document.
     * @param document Objet Document qui contient l'information sur les dimensions des pages du document
     * @param image objet Image qui contient l'image qu'il faut redimensionner.
     * @return Retourne un objet Image de l'image source modifiée.
     */
    private fun resizePhoto(document: Document, image: Image): Image {
        val documentWidth: Float =
            document.pageSize.width - document.leftMargin() - document.rightMargin()
        val documentHeight: Float =
            document.pageSize.height - document.topMargin() - document.bottomMargin()

        if (image.height > documentHeight || image.width > documentWidth) {
            image.scaleToFit(documentWidth, documentHeight)
        }

        return image
    }

    //TODO : Fonction qui rotate l'image selon l'EXIF. Un peu copier coller de la fonction rotateImage() dans MainActivity.
    /**
     * rotateImage
     *
     * Fonction qui retourne une valeur pour tourner l'image selon les valeurs EXIF de l'image.
     * @param path le chemin pour aller chercher l'image.
     * @return Retourne un Float négatif représentant les dégrées pour pivoter l'image. À utiliser dans une fonction setRotationDegrees avec un objet Image.
     */
    private fun rotateImage(path: String): Float {
        val exif = ExifInterface(path) //va chercher metadonnee EXIF
        var rotate = 0F
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270F
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180F
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90F
        }
        return -rotate //pour une certaine raison, rotate normal flip l'image vers le mauvais sense. lmao
    }

    //temps de créer une fonction pour générer un PDF!
    //cette fonction va créer un PDF pour la journée même.
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
    ) {
        //Define the document
        val file = createFile(data[0].nom + " - " + data[0].activitePratique + " (" + data[0].date + ").pdf")
        val document = createDocument()
        //Setup PDF Writer
        setupPdfWriter(document, file)
        //Va chercher le nom et nom d'equipe de la premiere entree dans la liste.
        val nomUser = data[0].nom
        val nomEquipe = data[0].equipe

        //Titre et Équipe. j'imagine 1 tableau avec le nom et l'équipe.
        val columnWidth = floatArrayOf(1f, 1f)
        val table = createTable(columnWidth.size, columnWidth)
        listOf<String>("Nom", "Équipe", nomUser, nomEquipe).forEach() //Fonction anonyme pour la création du tableau qui affiche le nom et equipe de la personne.
        {
            table.addCell(createCell(it))
        }
        //ajout du tableau au document PDF
        document.add(table)
        addLineSpace(document, 2)


        val table2 = createTable(columnWidth.size, columnWidth) //TODO : Trouver un moyen de réutiliser l'objet table en Kotlin
        //créer l'autre tableau.
        listOf<String>(
            "Activité pratiqué : ", data[0].activitePratique,
            "Date : ", data[0].date,
            "Objectif Personel : ", data[0].objectifPersonel,
            "Durée : ", data[0].dureeMinute,
            "Intensité : ", data[0].intensite
        ).forEach() {
            table2.addCell(createCell(it))
        }
        document.add(table2)
        addLineSpace(document, 2)

        document.add(createParagraph("Note Personelle : "))
        addLineSpace(document, 1)
        document.add(createParagraph(data[0].notePerso))
        addLineSpace(document, 2)

        //ajoute la photo au PDF
        //TODO : Au besoin : Resize l'image pour qu'elle "fit" dans la page avec tableau.
        try {
            if (imageUser.isNotBlank()) { //si le string n'est pas vide
                val image = resizePhoto(document, Image.getInstance(imageUser))
                val rotate = rotateImage(imageUser)
                image.setRotationDegrees(rotate)
                document.add(image) //essaye d'ajouter l'image
            }
        } catch (ex: java.io.FileNotFoundException) { //si l'utilisateur delete l'image apres l'avoir choisis, fait rien avec l'image.
            Log.e(TAG, ex.toString())
            //fait rien. faudrait avoir une image noir avec "IMAGE PERDU" écrit dessus pour ce cas.
        }

        document.close()
        try {
            pdf.close()
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        } finally {
            onFinish(file)
        }
    }

    //victor - modif derniere minute pour Yves
    //fonction pour créer un PDF pour le mois! (sans images malheuresement)
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
    ){
        //va pogner la date d'aujoud'hui https://www.programiz.com/kotlin-programming/examples/current-date-time
        var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        //Define the document
        val file = createFile(data[0].nom  + " (" + date + ").pdf")
        val document = createDocument()
        //Setup PDF Writer
        setupPdfWriter(document, file)
        //Va chercher le nom et nom d'equipe de la premiere entree dans la liste.
        val nomUser = data[0].nom
        val nomEquipe = data[0].equipe

        //Titre et Équipe. j'imagine 1 tableau avec le nom et l'équipe.
        var columnWidth = floatArrayOf(1f, 1f)
        val table = createTable(columnWidth.size, columnWidth)
        listOf<String>("Nom", "Équipe", nomUser, nomEquipe).forEach() //Fonction anonyme pour la création du tableau qui affiche le nom et equipe de la personne.
        {
            table.addCell(createCell(it))
        }
        //ajout du tableau au document PDF
        document.add(table)
        addLineSpace(document, 2)

        //création du 2eme tableau.
        columnWidth = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f)
        val table2 = createTable(columnWidth.size, columnWidth) //TODO : Trouver un moyen de réutiliser l'objet "table" en Kotlin
        //créer l'autre tableau. commence avec les headers.
        listOf<String>(
            "Date : ",
            "Activité pratiqué : ",
            "Objectif Personel : ",
            "Durée : ",
            "Intensité : ",
            "Note : ",
        ).forEach() {
            table2.addCell(createCell(it))
        }
        //maintenant ajoute les données
        data.forEach{
            table2.addCell(createCell(it.date))
            table2.addCell(createCell(it.activitePratique))
            table2.addCell(createCell(it.objectifPersonel))
            table2.addCell(createCell(it.dureeMinute))
            table2.addCell(createCell(it.intensite))
            table2.addCell(createCell(it.notePerso))
        }
        //ajoute le tout dans le document PDF
        document.add(table2)
        addLineSpace(document, 2)


        document.close()
        try {
            pdf.close()
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        } finally {
            onFinish(file)
        }
    }


}