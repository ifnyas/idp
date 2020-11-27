package app.ifnyas.idp.view.debug

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.idp.R

class DebugActivity : AppCompatActivity() {

    private val TAG: String by lazy { javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
    }
}