package app.ifnyas.idp.view.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentPicsBinding
import app.ifnyas.idp.model.Places
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.vr.sdk.widgets.pano.VrPanoramaView

class PicsFragment : Fragment(R.layout.fragment_pics) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentPicsBinding::bind)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        initVm()
    }

    private fun initVm() {
        vm.apply {
            place.observe(viewLifecycleOwner, {
                setPanoView(it)
            })
        }
    }

    private fun setPanoView(place: Places?) {
        binding.vrImg.apply {
            val baseFrameLayout = this[0] as ViewGroup

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
            val url = place?.image

            Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) { }
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val options = VrPanoramaView.Options()
                        .apply { inputType = VrPanoramaView.Options.TYPE_MONO }
                    loadImageFromBitmap(resource, options)
                }
            })
        }
    }
}