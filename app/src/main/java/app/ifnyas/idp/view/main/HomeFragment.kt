package app.ifnyas.idp.view.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentHomeBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: HomeViewModel by viewModels()
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        initView()
    }

    private fun initView() {
        binding.apply {
            btnPics.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_picsFragment)
            }

            btnVids.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_vidsFragment)
            }

            vm.apply {
                picsUrl.observe(viewLifecycleOwner, {
                    setImage(binding.imgPics, it)
                })

                vidsUrl.observe(viewLifecycleOwner, {
                    setImage(binding.imgVids, it)
                })
            }
        }
    }

    private fun setImage(view: AppCompatImageView, url: String) {
        Glide.with(view).asBitmap().load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                            resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        transition?.let {
                            val didSucceedTransition = transition.transition(resource, BitmapImageViewTarget(view))
                            if (!didSucceedTransition) view.setImageBitmap(resource)
                        }
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        vm.thumbnails()
    }

}