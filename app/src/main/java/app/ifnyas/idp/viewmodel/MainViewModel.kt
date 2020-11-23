package app.ifnyas.idp.viewmodel

import android.app.Activity
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.App
import app.ifnyas.idp.api.ApiRequest
import app.ifnyas.idp.model.Place
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.coroutines.launch

@Suppress("MemberVisibilityCanBePrivate")
class MainViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }

    val places: MutableLiveData<List<Place>> by lazy { MutableLiveData<List<Place>>() }
    val place: MutableLiveData<Place> by lazy { MutableLiveData<Place>() }
    val visited: MutableLiveData<MutableList<Int>> by lazy { MutableLiveData<MutableList<Int>>() }
    val screenshot: MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            visited.value = mutableListOf()
            places.value = ApiRequest().getPlaces()
            randomize()
        }
    }

    fun randomize() {
        // get unique random place
        var newPlaceIndex = getRandomIndex()
        while (visited.value?.contains(newPlaceIndex) == true)
            newPlaceIndex = getRandomIndex()

        // set new place and store index
        setPlace(places.value?.get(newPlaceIndex))
        hasVisited(getPlaceIndex(place.value))
    }

    private fun setPlace(item: Place?) {
        place.value = item
    }

    private fun getPlaceIndex(item: Place?): Int {
        return places.value?.indexOf(item) ?: -1
    }

    private fun getRandomIndex(): Int {
        return getPlaceIndex(places.value?.random())
    }

    private fun hasVisited(i: Int?) {
        visited.value?.apply {
            if (size.plus(1) == places.value?.size) clear()
            if (i != null && !contains(i)) add(i)
        }
    }

    fun gridClicked(item: Place) {
        if (item != place.value) {
            setPlace(item)
            hasVisited(getPlaceIndex(item))
        }
    }

    fun shoot() {
        Instacapture.capture(App.cxt as Activity, object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(bitmap: Bitmap) {
                screenshot.value = bitmap
            }
        })
    }
}