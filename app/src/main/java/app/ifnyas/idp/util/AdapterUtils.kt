package app.ifnyas.idp.util

import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.findFragment
import app.ifnyas.idp.App
import app.ifnyas.idp.R
import app.ifnyas.idp.view.main.MainActivity
import app.ifnyas.idp.view.main.PicsFragment
import app.ifnyas.idp.view.main.VidsFragment
import appifnyasidp.Place
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.otaliastudios.elements.Presenter

class AdapterUtils {

    fun get(layout: Int): Presenter<*>? {
        return when (layout) {
            R.layout.item_pics_grid -> {
                Presenter.simple(App.cxt, R.layout.item_pics_grid, 0) { vh, item: Place ->
                    val imgPlace = vh.findViewById<AppCompatImageView>(R.id.img_place)
                    val btnPlace = vh.findViewById<MaterialButton>(R.id.btn_place)

                    Glide.with(vh).load(item.thumb).centerCrop().into(imgPlace)

                    btnPlace.setOnClickListener {
                        if (App.fu.isPicsFragment()) {
                            (App.cxt as MainActivity)
                                .findViewById<ConstraintLayout>(R.id.lay_root)
                                .findFragment<PicsFragment>()
                                .gridClicked(item)
                        } else {
                            (App.cxt as MainActivity)
                                .findViewById<ConstraintLayout>(R.id.lay_root)
                                .findFragment<VidsFragment>()
                                .gridClicked(item)
                        }
                    }
                }
            }
            else -> {
                null
            }
        }
    }

}