package app.ifnyas.idp.view.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentHomeBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        binding.btnPics.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_picsFragment)
        }
    }

}