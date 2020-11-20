package app.ifnyas.idp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.api.ApiRequest
import app.ifnyas.idp.model.Places
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }

    val place: MutableLiveData<Places> by lazy { MutableLiveData<Places>() }

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            place.value = ApiRequest().getPlaces()?.get(0) ?: Places()
        }
    }
}