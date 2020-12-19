package app.ifnyas.idp.view.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentPicsBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel
import appifnyasidp.Place
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
        vm.initData("image")
        fu.cleanVrView()
        initBtn()
        initVm()
    }

    private fun initBtn() {
        binding.apply {
            btnClear.setOnClickListener { fullToggle(false) }
            btnBack.setOnClickListener { activity?.onBackPressed() }
            btnAbout.setOnClickListener { fu.createAboutDialog(this@PicsFragment) }
            btnGrid.setOnClickListener { openGrid() }
            btnFull.setOnClickListener { fullToggle(true) }
            btnRandom.setOnClickListener { vm.randomize() }
            btnMaps.setOnClickListener { vm.maps() }
            btnWeb.setOnClickListener { vm.web() }
            btnShoot.setOnClickListener { shootPics() }
        }
    }

    private fun initVm() {
        vm.apply {
            place.observe(viewLifecycleOwner, {
                it?.let { setPlaceView(it) }
            })

            screenshot.observe(viewLifecycleOwner, {
                it?.let {
                    fu.createShareDialog(
                            this@PicsFragment,
                            place.value?.title ?: "",
                            it
                    )
                }
            })
        }
    }

    private fun setPlaceView(place: Place?) {
        binding.apply {
            // begin transition
            fu.beginTransition(layRoot)

            // start progress
            loadToggle(true)

            // set image
            imgPlaceImage.apply {
                Glide.with(this)
                        .asBitmap().load(place?.image)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {}
                            override fun onResourceReady(
                                    resource: Bitmap, transition: Transition<in Bitmap>?
                            ) {
                                // set image
                                val options = VrPanoramaView.Options().apply {
                                    inputType = VrPanoramaView.Options.TYPE_MONO
                                }
                                loadImageFromBitmap(resource, options)
                                visibility = View.VISIBLE

                                // set details
                                textPlaceTitle.text = fu.htmlToString(place?.title ?: "")
                                textPlaceDesc.text = fu.htmlToString(place?.desc ?: "")

                                // end progress
                                loadToggle(false)
                            }
                        })
            }
        }
    }

    private fun fullToggle(isFull: Boolean) {
        binding.apply {
            fu.beginTransition(layRoot)
            layDetails.visibility = if (isFull) View.GONE else View.VISIBLE
            btnClear.visibility = if (isFull) View.VISIBLE else View.GONE
        }
    }

    private fun loadToggle(isLoad: Boolean) {
        binding.apply {
            fu.beginTransition(layRoot)
            progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
            layDetails.visibility = if (isLoad) View.GONE else View.VISIBLE
        }
    }

    private lateinit var gridDialog: MaterialDialog
    private fun openGrid() {
        if (!::gridDialog.isInitialized) {
            gridDialog = fu.createGridDialog(this, vm.places.value ?: emptyList())
        }
        gridDialog.show()
    }

    fun gridClicked(place: Place) {
        gridDialog.dismiss()
        vm.gridClicked(place)
    }

    private fun shootPics() {
        // hide navigation bar
        fu.setFullScreen()

        // shoot
        Handler(Looper.getMainLooper()).postDelayed({
            // hide details
            binding.layDetails.visibility = View.INVISIBLE

            // shoot
            vm.shoot()

            // return details
            binding.layDetails.visibility = View.VISIBLE
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        binding.imgPlaceImage.resumeRendering()
        fu.cleanVrView()
    }

    override fun onPause() {
        super.onPause()
        binding.imgPlaceImage.pauseRendering()
        fullToggle(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.clear()
    }
}