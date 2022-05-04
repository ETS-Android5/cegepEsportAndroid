package com.mvd.esport.pdfService

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.widget.Toast
import androidx.core.net.toUri
import com.mvd.esport.MainActivity
import com.mvd.esport.data.donneesUtilisateur
import java.io.File

class pdfFunctions(context: MainActivity) { //param constructeur sont ici!

    /**
     * paramThis : Le context de l'application.
     */
    private val paramThis: Context = context // Passe variable en param a l'objet.

    /**
     * createPdf
     *
     * Procédure qui va chercher la fonction createUserTable de pdfService pour créer un document PDF.
     * @param userData les données entrées dans l'utilisateur
     * @param image le chemin de l'image
     */
    fun createPdf(userData: List<donneesUtilisateur>, image: String) { //fonction qui va creer le pdf
        val onFinish: (File) -> Unit = { openFile(it) } // <-- la seul raison pourquoi je travaille encore en Kotlin :P
        val pdfService = pdfService()
        pdfService.createUserTable(data = userData, onFinish = onFinish, imageUser = image)
    }

    //https://github.com/annasta13/Pdf-Export
    /**
     * openFile
     */
    fun openFile(file: File) {
        val path = FileHandler().getPathFromUri(paramThis, file.toUri())
        val pdfFile = File(path)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfFile.toUri(), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            paramThis.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            toastErrorMessage("Can't read pdf file")
        }
    }

    /**
     * toastErrorMessage
     */
    fun toastErrorMessage(s: String) {
        Toast.makeText(paramThis, s, Toast.LENGTH_SHORT).show()
    }
}