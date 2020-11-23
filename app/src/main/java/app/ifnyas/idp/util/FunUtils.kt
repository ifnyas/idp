package app.ifnyas.idp.util

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.BuildConfig
import java.io.File
import java.io.FileOutputStream

class FunUtils {

    fun htmlToString(text: String): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }

    fun bmpToUri(bmp: Bitmap?): Uri {
        val file = File(cxt.externalCacheDir, "screenshot.png")
        val fOut = FileOutputStream(file)
        bmp?.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
        file.setReadable(true, false)
        return FileProvider.getUriForFile(
                cxt, "${BuildConfig.APPLICATION_ID}.provider", file
        )
    }
}