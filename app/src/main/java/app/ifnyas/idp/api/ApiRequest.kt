package app.ifnyas.idp.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.R
import app.ifnyas.idp.model.Places
import com.afollestad.materialdialogs.MaterialDialog
import io.ktor.client.request.*

class ApiRequest {

    private val TAG: String by lazy { javaClass.simpleName }
    private val client = ApiClient.ktorClient

    // exHandler
    private fun exHandler(c: Int?, e: Exception?, m: String?) {
        // set msg
        val msg =
            if (e != null) "${e.stackTrace[2].methodName}: ${e.fillInStackTrace()}"
            else "$m"

        // create alert
        Handler(Looper.getMainLooper()).post {
            MaterialDialog(cxt).show {
                title(R.string.exhandler_title)
                message(text = "${cxt.getString(R.string.exhandler_msg)}\n\nError $c: $msg}")
                positiveButton(text = "Kembali")
            }
        }

        Log.d(TAG, "exHandler: $msg")
    }

    suspend fun getPlaces(): List<Places>? {
        return try {
            client.get<List<Places>>(path = "places")
        } catch (e: Exception) {
            exHandler(-1, e, null); null
        }
    }
}