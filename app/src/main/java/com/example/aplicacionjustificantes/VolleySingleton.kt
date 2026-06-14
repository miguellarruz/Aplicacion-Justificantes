package com.example.aplicacionjustificantes

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    private var requestQueue: RequestQueue? = null
    private val ctx: Context = context.applicationContext

    val getRequestQueue: RequestQueue
        get() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(ctx)
            }
            return requestQueue!!
        }

    fun <T> addToRequestQueue(req: Request<T>) {
        getRequestQueue.add(req)
    }

    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
}
