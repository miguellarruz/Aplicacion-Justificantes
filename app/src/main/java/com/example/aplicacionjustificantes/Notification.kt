package com.example.aplicacionjustificantes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Notification : AppCompatActivity() {

    private lateinit var btnRegresarDesdeNotis: Button
    private lateinit var tvTituloNotificaciones: TextView
    private lateinit var tvSinNotificaciones: TextView

    // Contenedores de estado
    private lateinit var layoutEnProceso: LinearLayout
    private lateinit var layoutAprobado: LinearLayout
    private lateinit var layoutRechazado: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notis)

        btnRegresarDesdeNotis = findViewById(R.id.btnRegresarDesdeNotis)
        tvTituloNotificaciones = findViewById(R.id.tvTituloNotificaciones)
        tvSinNotificaciones = findViewById(R.id.tvSinNotificaciones)

        layoutEnProceso = findViewById(R.id.layoutEnProceso)
        layoutAprobado = findViewById(R.id.layoutAprobado)
        layoutRechazado = findViewById(R.id.layoutRechazado)

        val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")
        // IMPORTANTE: Asegúrate de guardar el ID del usuario en SharedPreferences al iniciar sesión
        val idUsuario = sharedPreferences.getInt("id_usuario", 1)

        tvTituloNotificaciones.text = "Notificaciones de $nombreUsuario"

        // Llamamos al servidor para ver el estado real
        consultarEstadoJustificante(idUsuario)

        btnRegresarDesdeNotis.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun consultarEstadoJustificante(idUsuario: Int) {
        // ⚠️ Asegúrate de usar tu IP correcta
        val url = "http://192.168.1.83/justificantes_api/consultar_estado.php?id_usuario=$idUsuario"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val estado = jsonResponse.getString("estado")
                        mostrarTarjetaCorrespondiente(estado)
                    } else {
                        // Si no hay justificantes, dejamos el mensaje de "Sin notificaciones"
                        tvSinNotificaciones.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error leyendo los datos", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun mostrarTarjetaCorrespondiente(estado: String) {
        // 1. Ocultamos el texto de "No hay notificaciones"
        tvSinNotificaciones.visibility = View.GONE

        // 2. Apagamos todas las tarjetas primero por seguridad
        layoutEnProceso.visibility = View.GONE
        layoutAprobado.visibility = View.GONE
        layoutRechazado.visibility = View.GONE

        // 3. Encendemos solo la tarjeta que mandó la doctora en la Base de Datos
        when (estado.lowercase()) {
            "aprobado" -> layoutAprobado.visibility = View.VISIBLE
            "rechazado" -> layoutRechazado.visibility = View.VISIBLE
            "en proceso", "pendiente" -> layoutEnProceso.visibility = View.VISIBLE
            else -> tvSinNotificaciones.visibility = View.VISIBLE // Por si tiene un estado raro
        }
    }
}