package app.ifnyas.idp.view.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.FragmentPicsBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.viewmodel.MainViewModel

class WebFragment : Fragment(R.layout.fragment_web) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentPicsBinding::bind)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        //
    }
}