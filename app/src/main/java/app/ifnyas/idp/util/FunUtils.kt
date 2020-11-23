package app.ifnyas.idp.util

import android.app.Activity
import android.graphics.Bitmap
import androidx.core.text.HtmlCompat
import app.ifnyas.idp.App.Companion.cxt
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener

class FunUtils {

    fun htmlToString(text: String): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }

    fun screenshot(): Bitmap? {
        var bmp: Bitmap? = null
        Instacapture.capture(cxt as Activity, object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(bitmap: Bitmap) {
                bmp = bitmap
            }
        })
        return bmp
    }
}