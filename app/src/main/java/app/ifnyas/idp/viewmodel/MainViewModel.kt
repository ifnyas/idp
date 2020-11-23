package app.ifnyas.idp.viewmodel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.api.ApiRequest
import app.ifnyas.idp.model.Place
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        Instacapture.capture(cxt as Activity, object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(bitmap: Bitmap) {
                screenshot.value = bitmap
            }
        })
    }

    fun share() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val query = place.value?.title
                        ?.replace(",", "")
                        ?.replace(" ", "+")

                val text = "${place.value?.title}\n\n" +
                        "https://www.google.com/maps/search/?api=1&query=$query"

                val uri = fu.bmpToUri(screenshot.value)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, text)
                }

                cxt.startActivity(Intent.createChooser(intent, ""))
            }
        }
    }

    fun web() {
        val uri = "https://www.google.com/search?q=${place.value?.title}"
        val intentUri = Uri.parse(uri)
        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        cxt.startActivity(intent)
    }

    fun maps() {
        val uri = "geo:0,0?q=${place.value?.title}"
        val intentUri = Uri.parse(uri)
        val intent = Intent(Intent.ACTION_VIEW, intentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        cxt.startActivity(intent)
    }
}