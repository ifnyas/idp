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
    val visited: MutableLiveData<MutableList<Int>> by lazy { MutableLiveData<MutableList<Int>>() }

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            isLoading.value = true
            visited.value = mutableListOf()
            places.value = ApiRequest().getPlaces()
            randomize()
        }
    }

    fun randomize() {
        // start progress
        isLoading.value = true

        // get unique random place
        var newPlaceIndex = getRandomPlaceIndex()
        while (visited.value?.contains(newPlaceIndex) == true)
            newPlaceIndex = getRandomPlaceIndex()

        // set new place and store index
        place.value = places.value?.get(newPlaceIndex)
        hasVisited(places.value?.indexOf(place.value))

        // end progress
        isLoading.value = false
    }

    private fun getRandomPlaceIndex(): Int {
        val newPlace = places.value?.random()
        return places.value?.indexOf(newPlace) ?: -1
    }

    private fun hasVisited(i: Int?) {
        if (visited.value?.size?.plus(1) == places.value?.size) visited.value?.clear()
        if (i != null) visited.value?.add(i)
    }
}