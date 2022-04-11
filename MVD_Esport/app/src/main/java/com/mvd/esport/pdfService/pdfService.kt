package com.mvd.esport.pdfService

import android.os.Environment
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
 */

class pdfService {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD) //TODO: Modifier les polices au besoins
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter //objet PDF

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


}