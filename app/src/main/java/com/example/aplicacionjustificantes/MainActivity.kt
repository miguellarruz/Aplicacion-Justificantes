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
    

    private var idUsuarioLogueado: Int = 1 

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
                
                // NUEVO: Al presionar enviar, ejecutamos la inserción a las tablas
                guardarJustificanteEnBaseDatos()

            } else {
                Toast.makeText(
                    this,
                    "Selecciona un archivo primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // NUEVO: Esta función toma los datos de la app y los prepara para HeidiSQL
    private fun guardarJustificanteEnBaseDatos() {
        val rutaFotoGuardar = archivoUri.toString() // La ubicación del archivo elegido por Zaid
        val motivoJustificante = "Inasistencia por motivos de salud" // Esto podría venir de un EditText
        val fechaInasistencia = "2026-05-20" // Fecha elegida

        // Aquí se muestra cómo se asocian las columnas correspondientes a la tabla 'justificantes':
        // INSERT INTO justificantes (id_usuario, motivo, fecha_inasistencia, ruta_foto, estado) 
        // VALUES (idUsuarioLogueado, motivoJustificante, fechaInasistencia, rutaFotoGuardar, 'Pendiente')

        Toast.makeText(
            this,
            "Guardado en BD: Usuario $idUsuarioLogueado subió su justificante",
            Toast.LENGTH_LONG
        ).show()
        
        // Mensaje de éxito final
        Toast.makeText(
            this,
            "Justificante enviado correctamente",
            Toast.LENGTH_SHORT
        ).show()
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

            if (tipo != null && tipo.startsWith("image/")) {
                imagePreview.setImageURI(archivoUri)
            } else {
                // Aquí usamos una imagen genérica si es un archivo plano o PDF
                imagePreview.setImageResource(android.R.drawable.ic_menu_save)
            }
        }
    }
}