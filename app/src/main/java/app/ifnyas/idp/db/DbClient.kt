package app.ifnyas.idp.db

import app.ifnyas.idp.App.Companion.fu
import org.jetbrains.exposed.sql.Database

object DbClient {
    val db by lazy {
        Database.connect("jdbc:h2:${fu.getPath()}/idp", "org.h2.Driver")
    }
}