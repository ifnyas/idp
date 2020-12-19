package app.ifnyas.idp.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceModel(
        var title: String = "",
        var desc: String? = "",
        var loc: String? = "",
        var image: String? = "",
        var thumb: String? = "",
        var type: String? = ""
)