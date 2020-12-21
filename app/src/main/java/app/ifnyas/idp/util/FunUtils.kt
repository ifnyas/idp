package app.ifnyas.idp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.transition.TransitionManager
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.BuildConfig
import app.ifnyas.idp.R
import app.ifnyas.idp.view.main.MainActivity
import app.ifnyas.idp.view.main.PicsFragment
import app.ifnyas.idp.viewmodel.MainViewModel
import appifnyasidp.Place
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.google.vr.sdk.widgets.video.VrVideoView
import com.otaliastudios.elements.Adapter
import com.otaliastudios.elements.Source
import java.io.File
import java.io.FileOutputStream

@Suppress("MemberVisibilityCanBePrivate")
class FunUtils {

    private val TAG: String by lazy { javaClass.simpleName }

    fun createNetworkDialog() {
        MaterialDialog(cxt).show {
            lifecycleOwner()
            icon(R.drawable.ic_outline_wifi_off_24)
            title(R.string.no_network_title)
            message(R.string.no_network_msg)
            cancelable(false)
        }
    }

    fun createEmptyDialog() {
        MaterialDialog(cxt).show {
            cancelable(false)
            icon(R.drawable.ic_outline_explore_24)
            title(R.string.exhandler_title)
            message(R.string.list_empty)
            positiveButton(text = "Hubungi") {
                Toast.makeText(cxt, "Terima kasih, pengembang berhasil dihubungi!", Toast.LENGTH_SHORT).show()
                throw RuntimeException("Empty Places")
            }
            negativeButton(text = "Kembali")
        }
    }

    fun createExitDialog() {
        MaterialDialog(cxt).show {
            icon(R.drawable.ic_outline_clear_24)
            title(R.string.exit_title)
            positiveButton(text = "Keluar") {
                (cxt as Activity).finishAffinity()
            }
            negativeButton(text = "Kembali")
        }
    }

    fun htmlToString(text: String): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }

    fun beginTransition(vg: ViewGroup) {
        TransitionManager.beginDelayedTransition(vg)
    }

    fun setFullScreen() {
        (cxt as Activity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun createGridDialog(fragment: Fragment, places: List<Place>): MaterialDialog {
        return MaterialDialog(cxt, BottomSheet(LayoutMode.MATCH_PARENT)).apply {
            lifecycleOwner(fragment.viewLifecycleOwner)
            customView(R.layout.dialog_pics_grid, noVerticalPadding = true)
            setPeekHeight(R.layout.dialog_pics_grid)
            view.apply {
                Adapter.builder(fragment.viewLifecycleOwner)
                    .addSource(Source.fromList(places))
                    .addPresenter(AdapterUtils().get(R.layout.item_pics_grid)!!)
                    .into(findViewById(R.id.rv_pics))
            }
        }
    }

    fun createShareDialog(fragment: Fragment, title: String, bmp: Bitmap) {
        MaterialDialog(cxt).show {
            lifecycleOwner(fragment.viewLifecycleOwner)
            customView(R.layout.dialog_pics_screenshot, noVerticalPadding = true)
            view.apply {
                val imgScreenshot = findViewById<AppCompatImageView>(R.id.img_screenshot)
                val btnShare = findViewById<MaterialButton>(R.id.btn_share)
                val btnWallpaper = findViewById<MaterialButton>(R.id.btn_wallpaper)
                val textTitle = findViewById<AppCompatTextView>(R.id.text_title_dialog_pics_screenshot)

                textTitle.text = title
                imgScreenshot.setImageBitmap(bmp)
                btnShare.setOnClickListener { MainViewModel().share(title, bmp) }
                btnWallpaper.setOnClickListener { MainViewModel().wallpaper(bmp) }
            }
        }
    }

    fun createAboutDialog(fragment: Fragment) {
        MaterialDialog(cxt).show {
            lifecycleOwner(fragment.viewLifecycleOwner)
            title(R.string.about_title)
            message(R.string.about_msg)
            positiveButton(R.string.text_back_dialog)
        }
    }

    fun rootLayout(): ConstraintLayout {
        return (cxt as MainActivity).findViewById(R.id.lay_root)
    }

    fun isPicsFragment(): Boolean {
        return try {
            rootLayout().findFragment<PicsFragment>().isVisible; true
        } catch (e: Exception) {
            false
        }
    }

    fun cleanVrView() {
        // hide Google VR view
        val id = if (isPicsFragment()) R.id.img_place_image else R.id.vr_vid

        // get base layout
        val view: ViewGroup? = rootLayout().findViewById(id)
        (if (isPicsFragment()) (view as VrPanoramaView) else (view as VrVideoView)).apply {
            setInfoButtonEnabled(false)
            setStereoModeButtonEnabled(false)
            setFullscreenButtonEnabled(false)
        }
    }

    @SuppressLint("SetWorldReadable")
    fun bmpToUri(bmp: Bitmap?): Uri {
        val file = File(cxt.externalCacheDir, "screenshot.png")
        val fOut = FileOutputStream(file)
        bmp?.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.apply { flush(); close() }
        file.setReadable(true, false)
        return FileProvider.getUriForFile(
                cxt, "${BuildConfig.APPLICATION_ID}.provider", file
        )
    }
}