package app.ifnyas.idp.model

import kotlinx.serialization.Serializable

@Serializable
data class Places(
    var title: String? = "",
    var desc: String? = "",
    var loc: String? = "",
    var image: String? = "",
    var thumb: String? = ""
)