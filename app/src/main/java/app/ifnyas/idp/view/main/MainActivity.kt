package app.ifnyas.idp.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.databinding.ActivityMainBinding
import app.ifnyas.idp.util.viewBinding
import app.ifnyas.idp.view.SplashActivity
import com.dasbikash.android_network_monitor.NetworkStateListener
import com.dasbikash.android_network_monitor.addNetworkStateListener
import com.dasbikash.android_network_monitor.haveNetworkConnection

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFun()
    }

    private fun initFun() {
        cxt = this
        initNetwork()
    }

    private fun initNetwork() {
        addNetworkStateListener(
            NetworkStateListener.getInstance(
                { refresh() },
                { fu.createNetworkDialog() },
                this
            )
        )
        Handler(Looper.getMainLooper()).post {
            if (!haveNetworkConnection()) fu.createNetworkDialog()
        }
    }

    private fun refresh() {
        startActivity(Intent(this, SplashActivity::class.java))
        finishAfterTransition()
    }

    override fun onBackPressed() {
        when (findNavController(R.id.nav_host).currentDestination?.label) {
            "HomeFragment" -> fu.createExitDialog()
            else -> super.onBackPressed()
        }
    }
}