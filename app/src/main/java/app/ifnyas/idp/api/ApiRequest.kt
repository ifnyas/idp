package app.ifnyas.idp.api

import android.app.Activity
import android.os.Handler
import android.os.Looper
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.R
import app.ifnyas.idp.api.ApiClient.ktorClient
import app.ifnyas.idp.model.PlaceModel
import appifnyasidp.Place
import com.afollestad.materialdialogs.MaterialDialog
import io.ktor.client.request.*

class ApiRequest {

    private val TAG: String by lazy { javaClass.simpleName }

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
                positiveButton(text = "Coba lagi") { (cxt as Activity).recreate() }
                negativeButton(text = "Kembali")
            }
        }
    }

    suspend fun getPlaces(): List<Place>? {
        return try {
            ktorClient
                .use { it.get<List<PlaceModel>>(path = "places/all") }
                .mapIndexed { index, item ->
                    Place(
                        index.toLong(),
                        item.title, item.desc, item.loc,
                        item.image, item.thumb, item.type
                    )
                }
        } catch (e: Exception) {
            exHandler(-1, e, null); null
        }
    }
}