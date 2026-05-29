package com.example.aplicacionjustificantes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaneldeVisualizacion : AppCompatActivity() {

    private lateinit var tvTituloPanel: TextView
    private lateinit var tvContadorPendiente: TextView
    private lateinit var tvContadorAprobado: TextView
    private lateinit var tvContadorRechazado: TextView
    private lateinit var btnRegresarInterfaz: Button

    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // 📌 Vinculamos con tu diseño XML que se llama "panel.xml"
            setContentView(R.layout.panel)

            // Capturamos el ID del usuario enviado desde Interfaz
            idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

            // Vincular componentes con los IDs exactos de tu XML
            tvTituloPanel = findViewById(R.id.tvTituloPanel)
            tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
            tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
            tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
            btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)

            // Recuperar el nombre guardado en memoria interna
            val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
            val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")

            // Inyectamos el texto dinámico en el título
            tvTituloPanel.text = "Panel de visualización de estados (Pendiente, Aprobado, Rechazado) ($nombreUsuario)"

            // Configurar el botón para volver atrás sin romper la cadena del ID
            btnRegresarInterfaz.setOnClickListener {
                val intent = Intent(this, Interfaz::class.java)
                intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }

            // Mostramos los valores iniciales en los contadores
            mostrarDatosEnPanel(0, 0, 0)

        } catch (e: Exception) {
            // Si hay un error al inflar la vista, la app no morirá; te dirá qué pasó
            Toast.makeText(this, "Error al cargar la interfaz: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}