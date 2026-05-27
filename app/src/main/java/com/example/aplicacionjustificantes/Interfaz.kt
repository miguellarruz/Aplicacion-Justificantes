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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interfaz)

        val btnNuevaSolicitud = findViewById<Button>(R.id.btnNuevaSolicitud)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)

        contenedorLista = findViewById(R.id.contenedorLista)
        txtListaVacia = findViewById(R.id.txtListaVacia)

        // ELIMINADO: Ya no agregamos el justificante de prueba aquí para que inicie limpio.
        // Cuando implementes tu consulta a la base de datos de XAMPP, llamarás a "agregarJustificanteALaLista" desde el éxito de Volley.

        // Ejecutamos la validación visual inicial
        actualizarVisibilidadHistorial()

        btnNuevaSolicitud.setOnClickListener {
            // Manda directo al formulario dinámico que creamos (Interfaz2A)
            val intent = Intent(this, Interfaz2A::class.java)
            startActivity(intent)
        }

        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
        }

        btnVisualizacion.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            startActivity(intent)
        }
    }

    // Tu función encargada de inflar renglones dinámicamente
    private fun agregarJustificanteALaLista(titulo: String, motivo: String) {
        val vistaJustificante = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaJustificante.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaJustificante.findViewById<TextView>(R.id.txtMotivoJustificante)

        txtTitulo.text = titulo
        txtMotivo.text = "Motivo: $motivo"

        // Se añade al contenedor en pantalla
        contenedorLista.addView(vistaJustificante)

        // Validamos de nuevo ya que la lista cambió de tamaño
        actualizarVisibilidadHistorial()
    }

    // Esta función controla mágicamente si se ve el aviso o los justificantes
    private fun actualizarVisibilidadHistorial() {
        if (contenedorLista.childCount == 0) {
            txtListaVacia.visibility = View.VISIBLE    // Muestra: "Aún no tienes justificantes"
            contenedorLista.visibility = View.GONE     // Oculta el contenedor vacío
        } else {
            txtListaVacia.visibility = View.GONE       // Oculta el aviso
            contenedorLista.visibility = View.VISIBLE  // Muestra los justificantes reales
        }
    }
}