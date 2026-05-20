
package com.example.justificateq

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var btnEnviar: Button
    private lateinit var txtArchivo: TextView
    private lateinit var imagePreview: ImageView

    private var archivoUri: Uri? = null

    companion object {
        const val PICK_FILE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnEnviar = findViewById(R.id.btnEnviar)
        txtArchivo = findViewById(R.id.txtArchivo)
        imagePreview = findViewById(R.id.imagePreview)

        
        btnSeleccionar.setOnClickListener {
            seleccionarArchivo()
        }

     
        btnEnviar.setOnClickListener {

            if (archivoUri != null) {
                Toast.makeText(
                    this,
                    "Justificante enviado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    this,
                    "Selecciona un archivo primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun seleccionarArchivo() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"

        val tiposPermitidos = arrayOf(
            "image/jpeg",
            "image/png",
            "application/pdf"
        )

        intent.putExtra(Intent.EXTRA_MIME_TYPES, tiposPermitidos)

        startActivityForResult(
            Intent.createChooser(intent, "Selecciona un justificante"),
            PICK_FILE_REQUEST
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {

            archivoUri = data.data

            txtArchivo.text = "Archivo seleccionado"

    
            val tipo = contentResolver.getType(archivoUri!!)

            if (tipo!!.startsWith("image/")) {
                imagePreview.setImageURI(archivoUri)
            } else {
                imagePreview.setImageResource(R.drawable.pdf_icon)
            }
        }
    }
}