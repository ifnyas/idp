package app.ifnyas.idp.view.main

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.adapter.PicsGridAdapter
import app.ifnyas.idp.databinding.FragmentPicsBinding
import app.ifnyas.idp.model.Place
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
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
        initBtn()
        initPanoView()
    }

    private fun initPanoView() {
        binding.imgPlaceImage.apply {
            // hide Google VR view
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
        }
    }

    private fun initBtn() {
        binding.apply {
            btnClear.setOnClickListener { fullscreenToggle(false) }
            btnBack.setOnClickListener { activity?.onBackPressed() }
            btnFull.setOnClickListener { fullscreenToggle(true) }
            btnRandom.setOnClickListener { vm.randomize() }
            btnMap.setOnClickListener { vm.maps() }
            btnWeb.setOnClickListener { vm.web() }
            btnGrid.setOnClickListener { openGrid() }
            btnShoot.setOnClickListener { shootPics() }
            btnAbout.setOnClickListener { createAboutDialog() }
        }
    }

    private fun initVm() {
        vm.apply {
            place.observe(viewLifecycleOwner, {
                setPlaceView(it)
                fullscreenDelay()
            })

            screenshot.observe(viewLifecycleOwner, {
                createShareDialog(it)
            })
        }
    }

    private fun fullscreenDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            fullscreenToggle(true)
        }, 3000)
    }

    private fun beginTransition(vg: ViewGroup) {
        TransitionManager.beginDelayedTransition(vg)
    }

    private fun setPlaceView(place: Place?) {
        binding.apply {
            // start transition
            beginTransition(layRoot)

            loadingToggle(true)

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
                                loadingToggle(false)
                            }
                        })
            }
        }
    }

    private fun fullscreenToggle(isFull: Boolean) {
        binding.apply {
            beginTransition(layRoot)
            layDetails.visibility = if (isFull) View.GONE else View.VISIBLE
            btnClear.visibility = if (isFull) View.VISIBLE else View.GONE
        }
    }

    private fun loadingToggle(isLoading: Boolean) {
        binding.apply {
            beginTransition(layRoot)
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            layDetails.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private lateinit var gridDialog: MaterialDialog
    private fun openGrid() {
        if (!::gridDialog.isInitialized) {
            gridDialog = MaterialDialog(cxt, BottomSheet(LayoutMode.MATCH_PARENT)).apply {
                customView(R.layout.dialog_pics_grid, noVerticalPadding = true)
                setPeekHeight(R.layout.dialog_pics_grid)
                lifecycleOwner(viewLifecycleOwner)
                view.apply {
                    val list = vm.places.value ?: emptyList()
                    findViewById<RecyclerView>(R.id.rv_pics).apply {
                        adapter = PicsGridAdapter(list)
                    }
                }
            }
        }
        gridDialog.show()
    }

    fun gridClicked(place: Place) {
        gridDialog.dismiss()
        vm.gridClicked(place)
    }

    private fun shootPics() {
        // hide details
        (cxt as Activity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN

        // shoot
        Handler(Looper.getMainLooper()).postDelayed({
            binding.layDetails.visibility = View.INVISIBLE
            binding.btnClear.visibility = View.INVISIBLE
            vm.shoot()
            // return details
            binding.layDetails.visibility = View.VISIBLE
        }, 1000)
    }

    private fun createShareDialog(bmp: Bitmap) {
        MaterialDialog(cxt).show {
            customView(R.layout.dialog_pics_screenshot, noVerticalPadding = true)
            lifecycleOwner(viewLifecycleOwner)
            view.apply {
                val imgScreenshot = findViewById<AppCompatImageView>(R.id.img_screenshot)
                val btnShare = findViewById<MaterialButton>(R.id.btn_share)
                val btnWallpaper = findViewById<MaterialButton>(R.id.btn_wallpaper)

                imgScreenshot.setImageBitmap(bmp)
                btnShare.setOnClickListener { vm.share() }
                btnWallpaper.setOnClickListener { vm.wallpaper() }
            }
        }
    }

    private fun createAboutDialog() {
        MaterialDialog(cxt).show {
            lifecycleOwner(viewLifecycleOwner)
            title(text = "Indonesia Panorama")
            message(text = "Seluruh aset foto 360 diambil dari www.indonesia.travel\n\nGit: https://github.com/ifnyas/idp")
            positiveButton(text = "Kembali")
        }
    }
}