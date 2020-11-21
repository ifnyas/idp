package app.ifnyas.idp

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatDelegate
import app.ifnyas.idp.util.FunUtils
import com.facebook.stetho.Stetho


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initFun()
    }

    private fun initFun() {
        // init val
        cxt = applicationContext
        fu = FunUtils()

        // init day/night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // init debug
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) initDebug()
    }

    private fun initDebug() {
        Stetho.initializeWithDefaults(this)
        //sm.clearSession()
    }

    companion object {
        lateinit var cxt: Context
        lateinit var fu: FunUtils
    }
}