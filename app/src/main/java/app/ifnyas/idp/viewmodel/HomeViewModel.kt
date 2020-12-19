package app.ifnyas.idp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.App.Companion.db
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.api.ApiRequest
import com.dasbikash.android_network_monitor.haveNetworkConnection
import kotlinx.coroutines.launch

@Suppress("MemberVisibilityCanBePrivate")
class HomeViewModel : ViewModel() {

    val TAG: String by lazy { javaClass.simpleName }

    val picsUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val vidsUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun thumbnails() {
        if (haveNetworkConnection()) {
            viewModelScope.launch {
                if (db.mainQueries.selectAll().executeAsList().isEmpty()) {
                    val list = ApiRequest().getPlaces()
                    if (list.isNullOrEmpty()) fu.createEmptyDialog()
                    else list.forEach { db.mainQueries.insert(it) }
                }

                val picsRes = getThumbRandom("image")
                val vidsRes = getThumbRandom("video")

                while (picsRes == picsUrl.value) getThumbRandom("image")
                while (vidsRes == vidsUrl.value) getThumbRandom("video")

                picsUrl.value = picsRes
                vidsUrl.value = vidsRes
            }
        }
    }

    fun getThumbRandom(type: String): String? {
        return db.mainQueries.selectThumbRandom(type, type).executeAsOne().thumb
    }

}