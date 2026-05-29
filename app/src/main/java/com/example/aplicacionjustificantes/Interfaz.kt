package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Interfaz : AppCompatActivity() {

    private lateinit var contenedorLista: LinearLayout
    private lateinit var txtListaVacia: TextView

    // Variable para guardar el ID del alumno real que inició sesión
    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interfaz)

        // 🆔 RECUPERAMOS EL ID REAL QUE VIENE DEL LOGIN
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        val btnNuevaSolicitud = findViewById<Button>(R.id.btnNuevaSolicitud)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)

        contenedorLista = findViewById(R.id.contenedorLista)
        txtListaVacia = findViewById(R.id.txtListaVacia)

        actualizarVisibilidadHistorial()

        btnNuevaSolicitud.setOnClickListener {
            // ✅ CORREGIDO: Ahora le pasamos el ID del usuario a Interfaz2A para evitar el crasheo
            val intent = Intent(this, Interfaz2A::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        btnVisualizacion.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }
    }

    private fun agregarJustificanteALaLista(titulo: String, motivo: String) {
        val vistaJustificante = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaJustificante.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaJustificante.findViewById<TextView>(R.id.txtMotivoJustificante)

        txtTitulo.text = titulo
        txtMotivo.text = "Motivo: $motivo"

        contenedorLista.addView(vistaJustificante)
        actualizarVisibilidadHistorial()
    }

    private fun actualizarVisibilidadHistorial() {
        if (contenedorLista.childCount == 0) {
            txtListaVacia.visibility = View.VISIBLE
            contenedorLista.visibility = View.GONE
        } else {
            txtListaVacia.visibility = View.GONE
            contenedorLista.visibility = View.VISIBLE
        }
    }
}