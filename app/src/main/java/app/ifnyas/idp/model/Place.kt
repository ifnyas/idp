package app.ifnyas.idp.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Place(
        var title: String? = "",
        var desc: String? = "",
        var loc: String? = "",
        var image: String? = "",
        var thumb: String? = "",
        var type: String? = ""
)

object Places : Table("t_place") {
        var title = varchar("title", 255)
        var desc = varchar("desc", 255)
        var loc = varchar("loc", 255)
        var image = varchar("image", 255)
        var thumb = varchar("thumb", 255)
        var type = varchar("type", 255)
        override val primaryKey = PrimaryKey(title)
}