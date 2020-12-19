package app.ifnyas.idp.view.main

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentVidsBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel
import appifnyasidp.Place
import com.afollestad.materialdialogs.MaterialDialog
import com.google.vr.sdk.widgets.video.VrVideoView

class VidsFragment : Fragment(R.layout.fragment_vids) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentVidsBinding::bind)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        vm.initData("video")
        fu.cleanVrView()
        initBtn()
        initVm()
    }

    private fun initBtn() {
        binding.apply {
            btnClear.setOnClickListener { fullToggle(false) }
            btnBack.setOnClickListener { activity?.onBackPressed() }
            btnAbout.setOnClickListener { fu.createAboutDialog(this@VidsFragment) }
            btnGrid.setOnClickListener { openGrid() }
            btnFull.setOnClickListener { fullToggle(true) }
            btnRandom.setOnClickListener { vrVid.pauseVideo(); vm.randomize() }
            btnMaps.setOnClickListener { vm.maps() }
            btnWeb.setOnClickListener { vm.web() }
            btnShoot.setOnClickListener { playToggle(null) }
        }
    }

    private fun initVm() {
        vm.apply {
            binding.apply {
                place.observe(viewLifecycleOwner, {
                    it?.let { setPlaceView(it) }
                })
            }
        }
    }

    private fun setPlaceView(place: Place?) {
        binding.apply {
            // start transition
            fu.beginTransition(layRoot)

            // start progress
            loadToggle(true)

            // set video
            val uri = place?.image
            val options = VrVideoView.Options().apply { inputType = VrVideoView.Options.TYPE_MONO }

            vrVid.loadVideo(Uri.parse(uri), options)
            vrVid.playVideo()

            // set details
            textPlaceTitle.text = fu.htmlToString(place?.title ?: "")
            textPlaceDesc.text = fu.htmlToString(place?.desc ?: "")
            btnShoot.tag = R.drawable.ic_outline_play_arrow_24

            // set play layout
            playToggle(true)

            // end progress
            loadToggle(false)
        }
    }

    private fun playToggle(isPlay: Boolean?) {
        binding.apply {
            fu.beginTransition(layRoot)

            val play = isPlay ?: (btnShoot.tag == R.drawable.ic_outline_play_arrow_24)
            vrVid.apply { if (play) playVideo() else pauseVideo() }

            btnShoot.setImageResource(
                    if (play) R.drawable.ic_outline_pause_24
                    else R.drawable.ic_outline_play_arrow_24
            )

            btnShoot.tag =
                    if (play) R.drawable.ic_outline_pause_24
                    else R.drawable.ic_outline_play_arrow_24
        }
    }

    fun gridClicked(place: Place) {
        gridDialog.dismiss()
        vm.gridClicked(place)
        //activity?.recreate()
    }

    private fun loadToggle(isLoad: Boolean) {
        binding.apply {
            fu.beginTransition(layRoot)
            progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
            layDetails.visibility = if (isLoad) View.GONE else View.VISIBLE
        }
    }

    private fun fullToggle(isFull: Boolean) {
        binding.apply {
            fu.beginTransition(layRoot)
            layDetails.visibility = if (isFull) View.GONE else View.VISIBLE
            btnClear.visibility = if (isFull) View.VISIBLE else View.GONE
        }
    }

    private lateinit var gridDialog: MaterialDialog
    private fun openGrid() {
        if (!::gridDialog.isInitialized) {
            gridDialog = fu.createGridDialog(
                    this, vm.places.value ?: emptyList()
            )
        }
        gridDialog.show()
    }

    override fun onResume() {
        super.onResume()
        binding.vrVid.resumeRendering()
        fu.cleanVrView()
    }

    override fun onPause() {
        super.onPause()
        binding.vrVid.apply { pauseVideo(); pauseRendering() }
        fullToggle(false)
        playToggle(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.clear()
    }
}