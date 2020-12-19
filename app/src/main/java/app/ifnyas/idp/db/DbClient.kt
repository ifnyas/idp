package app.ifnyas.idp.db

import app.ifnyas.idp.App
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.Database
import app.ifnyas.idp.R
import com.squareup.sqldelight.android.AndroidSqliteDriver

object DbClient {
    val sqlDriver: AndroidSqliteDriver by lazy {
        AndroidSqliteDriver(
            Database.Schema, cxt, "${App.cxt.getString(R.string.app_name)}.db"
        )
    }
}