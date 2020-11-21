package app.ifnyas.idp.util

import androidx.core.text.HtmlCompat

class FunUtils {

    fun htmlToString(text: String): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }
}