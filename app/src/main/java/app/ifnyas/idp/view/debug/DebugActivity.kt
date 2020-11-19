package app.ifnyas.idp.view.debug

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import app.ifnyas.idp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.google.vr.sdk.widgets.video.VrVideoView

class DebugActivity : AppCompatActivity() {

    private val TAG: String by lazy { javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        initFun()
    }

    private fun initFun() {
        initPics()
        //initVids()
    }

    private fun initPics() {
        // hide unnecessary view
        //
        val vrImg = findViewById<VrPanoramaView>(R.id.vr_img)
        val baseFrameLayout = vrImg[0] as ViewGroup

        // for "Exit fullscreen" button and "About VR View" button
        val relativeLayout = baseFrameLayout.getChildAt(1) as ViewGroup
        val exitFullscreenImageButton = relativeLayout.getChildAt(0)
        val aboutVRViewImageButton = relativeLayout.getChildAt(1)
        exitFullscreenImageButton.visibility = View.GONE
        aboutVRViewImageButton.visibility = View.GONE

        // for "Cardboard" button and "Enter fullscreen" button
        val linearLayout = relativeLayout.getChildAt(2) as ViewGroup
        val cardboardImageButton = linearLayout.getChildAt(0)
        val enterFullscreenImageButton = linearLayout.getChildAt(1)
        cardboardImageButton.visibility = View.GONE
        enterFullscreenImageButton.visibility = View.GONE

        // set pic
        //
        val url =
            "https://www.indonesia.travel/content/dam/indtravelrevamp/image-360/Taman%20Sari.jpg"

        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) { }
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val options = VrPanoramaView.Options()
                    .apply { inputType = VrPanoramaView.Options.TYPE_MONO }
                vrImg.loadImageFromBitmap(resource, options)
            }
        })
    }

    private fun initVids() {
        val vrVid = findViewById<VrVideoView>(R.id.vr_vid)
        val options = VrVideoView.Options()
            .apply { inputType = VrVideoView.Options.TYPE_MONO }

        vrVid.loadVideo(Uri.parse("https://www.indonesia.travel/content/dam/indtravelrevamp/image-360/Jakarta.mp4"), options)
    }

    override fun onPause() {
        super.onPause()
        findViewById<VrPanoramaView>(R.id.vr_img).pauseRendering()
        findViewById<VrPanoramaView>(R.id.vr_vid).pauseRendering()
    }

    override fun onDestroy() {
        super.onDestroy()
        findViewById<VrPanoramaView>(R.id.vr_img).shutdown()
        findViewById<VrPanoramaView>(R.id.vr_vid).shutdown()
    }

    override fun onResume() {
        super.onResume()
        findViewById<VrPanoramaView>(R.id.vr_img).resumeRendering()
        findViewById<VrPanoramaView>(R.id.vr_vid).resumeRendering()
    }
}