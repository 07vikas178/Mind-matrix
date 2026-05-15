package com.nammaplatform.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaplatform.app.databinding.ItemStationBinding
import com.nammaplatform.app.model.Station

/**
 * RecyclerView adapter for the station list.
 *
 * Uses `ListAdapter` + `DiffUtil` so updates animate correctly and we
 * never call `notifyDataSetChanged()` — best practice for production.
 */
class StationAdapter(
    private val onClick: (Station) -> Unit
) : ListAdapter<Station, StationAdapter.VH>(DIFF) {

    inner class VH(val b: ItemStationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(s: Station) {
            b.tvNameKn.text = s.nameKn
            b.tvNameEn.text = s.nameEn
            b.tvCode.text   = s.id
            b.root.setOnClickListener { onClick(s) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return VH(ItemStationBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Station>() {
            override fun areItemsTheSame(a: Station, b: Station)     = a.id == b.id
            override fun areContentsTheSame(a: Station, b: Station)  = a == b
        }
    }
}
