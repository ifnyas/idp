package app.ifnyas.idp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import app.ifnyas.idp.App.Companion.cxt
import app.ifnyas.idp.App.Companion.fu
import app.ifnyas.idp.R
import app.ifnyas.idp.view.main.MainActivity
import app.ifnyas.idp.view.main.PicsFragment
import app.ifnyas.idp.view.main.VidsFragment
import appifnyasidp.Place
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class VrGridAdapter(private var items: List<Place>) :
        RecyclerView.Adapter<VrGridAdapter.BaseViewHolder>() {

    class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
            BaseViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_pics_grid, parent, false)
            )

    override fun onBindViewHolder(h: BaseViewHolder, i: Int) {
        val item = items[i]
        h.itemView.apply {
            val imgPlace = findViewById<AppCompatImageView>(R.id.img_place)
            val btnPlace = findViewById<MaterialButton>(R.id.btn_place)

            Glide.with(this).load(item.thumb).centerCrop().into(imgPlace)
            btnPlace.setOnClickListener {
                if (fu.isPicsFragment()) {
                    (cxt as MainActivity)
                            .findViewById<ConstraintLayout>(R.id.lay_root)
                            .findFragment<PicsFragment>()
                            .gridClicked(item)
                } else {
                    (cxt as MainActivity)
                            .findViewById<ConstraintLayout>(R.id.lay_root)
                            .findFragment<VidsFragment>()
                            .gridClicked(item)
                }
            }
        }
    }
}