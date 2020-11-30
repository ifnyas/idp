package app.ifnyas.idp.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.ActivityMainBinding
import app.ifnyas.idp.util.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFun()
    }

    private fun initFun() {
        cxt = this
    }

    override fun onBackPressed() {
        when (findNavController(R.id.nav_host).currentDestination?.label) {
            "HomeFragment" -> fu.createExitDialog()
            else -> super.onBackPressed()
        }
    }
}