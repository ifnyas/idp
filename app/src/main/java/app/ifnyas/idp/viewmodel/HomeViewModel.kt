package app.ifnyas.idp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.api.ApiRequest
import com.dasbikash.android_network_monitor.haveNetworkConnection
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val picsUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val vidsUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun thumbnails() {
        viewModelScope.launch {
            val picsRes = ApiRequest().getThumb("image")
            val vidsRes = ApiRequest().getThumb("video")

            if (haveNetworkConnection()) {
                while (picsRes == picsUrl.value) ApiRequest().getThumb("image")
                while (vidsRes == vidsUrl.value) ApiRequest().getThumb("video")
            }

            picsUrl.value = picsRes?.replace("\"", "")
            vidsUrl.value = vidsRes?.replace("\"", "")
        }
    }

}