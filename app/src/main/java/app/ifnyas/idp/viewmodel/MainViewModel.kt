package app.ifnyas.idp.viewmodel

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.api.ApiRequest
import app.ifnyas.idp.db.DbClient
import app.ifnyas.idp.model.Place
import app.ifnyas.idp.model.Places
import app.ifnyas.idp.model.Places.desc
import app.ifnyas.idp.model.Places.image
import app.ifnyas.idp.model.Places.loc
import app.ifnyas.idp.model.Places.thumb
import app.ifnyas.idp.model.Places.title
import app.ifnyas.idp.model.Places.type
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class MainViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }

    val places: MutableLiveData<List<Place>> by lazy { MutableLiveData<List<Place>>() }
    val place: MutableLiveData<Place> by lazy { MutableLiveData<Place>() }
    val visited: MutableLiveData<MutableList<Int>> by lazy { MutableLiveData<MutableList<Int>>() }
    val screenshot: MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }

    init {
        visited.value = mutableListOf()

        // create db file
        val file = File(fu.getPath(), "idp.mv.db")
        try {
            file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initData(types: String) {
        viewModelScope.launch {
            val list = ApiRequest().getPlaces(types)

            newSuspendedTransaction(Dispatchers.IO, DbClient.db) {
                Log.d(TAG, "AAAA: ${Places.selectAll().count()}")
                //SchemaUtils.drop(Places)
                SchemaUtils.create(Places)

                list?.forEach { item ->
                    Places.insert {
                        it[title] = "${item.title}"
                        it[desc] = "${item.desc}"
                        it[loc] = "${item.loc}"
                        it[thumb] = "${item.thumb}"
                        it[type] = "${item.type}"
                        it[image] = "${item.image}"
                    }
                }

                Log.d(TAG, "BBBB: ${Places.selectAll().count()}")
                places.postValue(Places.selectAll().map {
                    Place(it[title], it[desc], it[loc], it[image], it[thumb], it[type])
                })
            }

            randomize()
        }
    }

    fun clear() {
        if (places.value?.isNotEmpty() == true) {
            places.value = emptyList()
            visited.value?.clear()
            screenshot.value = null
            place.value = null
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

    fun share(title: String, bmp: Bitmap) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val query = title
                        .replace(",", "")
                        .replace(" ", "+")

                val text = "$title\nhttps://www.google.com/maps/search/?api=1&query=$query"

                val uri = fu.bmpToUri(bmp)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, text)
                }

                cxt.startActivity(Intent.createChooser(intent, "Bagikan via:"))
            }
        }
    }

    fun web() {
        val uri = "https://www.tripadvisor.com/search?q=${place.value?.title}"
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

    fun wallpaper(bmp: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(cxt)
        wallpaperManager.setBitmap(bmp)
        Toast.makeText(cxt, "Foto sukses dijadikan wallpaper!", Toast.LENGTH_SHORT).show()
    }

}