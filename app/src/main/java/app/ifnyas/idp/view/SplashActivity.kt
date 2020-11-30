package app.ifnyas.idp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.idp.view.main.MainActivity
import com.dasbikash.android_network_monitor.initNetworkMonitor

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNetworkMonitor()
        startActivity(Intent(this, MainActivity::class.java))
        finishAfterTransition()

        // override transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}