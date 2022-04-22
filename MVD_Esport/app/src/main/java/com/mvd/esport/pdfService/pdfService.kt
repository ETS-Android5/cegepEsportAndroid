package com.mvd.esport.pdfService

import android.os.Environment
import android.util.Log
import com.mvd.esport.data.donneesUtilisateur
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Annas Surdyanto on 05/02/22.
 * Modified by Victor Bélanger on 2022-04-11
 * https://github.com/annasta13/Pdf-Export/blob/master/app/src/main/java/com/habileducation/pdfexport/pdfService/PdfService.kt
 *
 */

class pdfService {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD) //TODO: Modifier les polices au besoins
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter //objet PDF

    private val TAG = "pdfService"

    private fun createFile(): File {
        //Prepare file
        val title = "Pdf to export.pdf" //TODO: Renommer le fichier pour quelque chose de plus significatif. Une date avec le nom de l'app, peut-être?
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) //TODO: Le fichier Downloads est intéressant. Fait-on notre propre dossier au lieu?
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun createDocument(): Document { //TODO: peut-être demander à yves s'il veut une page spécifique...
        //Create Document
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        //Open the document
        document.open()
    }

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

    private fun addLineSpace(document: Document, number: Int) { //ajoute une ligne vide dans le document
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun createParagraph(content: String): Paragraph{ //va créer un paragraphe //TODO: Check format du paragraph.
        val paragraph = Paragraph(content, BODY_FONT)
        paragraph.firstLineIndent = 25f
        paragraph.alignment = Element.ALIGN_JUSTIFIED
        return paragraph
    }

    //TODO: créer une fonction qui va inclure l'image de l'utilisateur.

    //temps de créer une fonction pour générer un PDF!
    fun createUserTable(
        data: List<donneesUtilisateur>, //Je vais laisser ceci en list, dans le cas qu'on voudrait un tableau avec plusieurs entrées.
        //TODO: paragraphList: String, //Note personelle. Dans une valeur séparé ou dans la liste data?
        onFinish: (file: File) -> Unit,
    ){
        //Define the document
        val file = createFile()
        val document = createDocument()
        //Setup PDF Writer
        setupPdfWriter(document, file)
        //Va chercher le nom et nom d'equipe de la premiere entree dans la liste.
        val nomUser = data[0].nom
        val nomEquipe = data[0].equipe

        //Titre et Équipe. j'imagine 1 tableau avec le nom et l'équipe.
        val columnWidth = floatArrayOf(1f, 1f)
        val table = createTable(columnWidth.size, columnWidth)
        listOf<String>("Nom","Équipe", nomUser, nomEquipe).forEach() //Fonction anonyme pour la création du tableau qui affiche le nom et equipe de la personne.
        {
            table.addCell(createCell(it))
        }
        //ajout du tableau au document PDF
        document.add(table)
        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            Log.e(TAG,ex.toString())
        } finally {
            onFinish(file)
        }
    }
}