package app.ifnyas.idp

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatDelegate
import app.ifnyas.idp.db.DbClient.sqlDriver
import app.ifnyas.idp.util.FunUtils
import com.facebook.stetho.Stetho
import com.google.firebase.analytics.FirebaseAnalytics


class
App : Application() {

    override fun onCreate() {
        super.onCreate()
        initFun()
    }

    private fun initFun() {
        // init context
        cxt = applicationContext

        // init analytics
        FirebaseAnalytics.getInstance(this)

        // init day/night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // init debug
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) initDebug()
    }

    private fun initDebug() {
        Stetho.initializeWithDefaults(this)
        db.mainQueries.deleteAll()
    }

    companion object {
        lateinit var cxt: Context
        val db: Database by lazy { Database(sqlDriver) }
        val fu: FunUtils by lazy { FunUtils() }
    }
}