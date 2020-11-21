package app.ifnyas.idp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.api.ApiRequest
import app.ifnyas.idp.model.Place
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }

    val places: MutableLiveData<List<Place>> by lazy { MutableLiveData<List<Place>>() }
    val place: MutableLiveData<Place> by lazy { MutableLiveData<Place>() }
    val isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            isLoading.value = true
            places.value = ApiRequest().getPlaces()
            randomize()
        }
    }

    fun randomize() {
        isLoading.value = true
        place.value = places.value?.random()
        isLoading.value = false
    }
}