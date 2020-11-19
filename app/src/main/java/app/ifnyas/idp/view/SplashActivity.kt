package app.ifnyas.idp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.ifnyas.idp.R
import app.ifnyas.idp.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initFun()
    }

    private fun initFun() {
        // next activity
        startActivity(Intent(this, MainActivity::class.java))
        finishAfterTransition()

        // override transition
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}