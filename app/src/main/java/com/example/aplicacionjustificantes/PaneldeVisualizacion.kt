package com.example.aplicacionjustificantes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaneldeVisualizacion : AppCompatActivity() {

    // Declaramos las variables de tus componentes visuales
    private lateinit var tvTituloPanel: TextView
    private lateinit var tvContadorPendiente: TextView
    private lateinit var tvContadorAprobado: TextView
    private lateinit var tvContadorRechazado: TextView
    private lateinit var btnRegresarInterfaz: Button

    // 📌 Variable agregada para almacenar el ID del usuario de forma segura
    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 📌 Carga el layout XML. Asegúrate de que en tu carpeta res/layout se llame exactamente "panel.xml"
        setContentView(R.layout.panel)

        // 📌 CAPTURAMOS EL ID BLINDADO: Recibe la estafeta que mandó "Interfaz.kt"
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        // Vinculamos los componentes del XML con Kotlin
        tvTituloPanel = findViewById(R.id.tvTituloPanel)
        tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
        tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
        tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
        btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)

        // Recuperamos el nombre guardado en la memoria interna (SesionUsuario)
        val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")

        // Inyectamos el nombre de la cuenta registrada en el título
        tvTituloPanel.text = "Panel de visualización de estados (Pendiente, Aprobado, Rechazado) ($nombreUsuario)"

        // Acción para volver a la pantalla Interfaz devolviendo el ID para no romper el ciclo
        btnRegresarInterfaz.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado) // 📌 Devolvemos el ID al menú principal
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Por ahora se queda en 0 porque el usuario va empezando desde cero
        // 📌 Nota futura: Aquí podrás meter un método de Volley usando "idUsuarioLogueado" para traer los números reales de MySQL
        mostrarDatosEnPanel(pendientes = 0, aprobados = 0, rechazados = 0)
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}