package com.nammaplatform.app.ui.platform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaplatform.app.databinding.ItemTrainBinding
import com.nammaplatform.app.model.Train

/**
 * Lists the next 3 trains. Tapping one updates the coach strip below.
 */
class TrainAdapter(
    private val onClick: (Train) -> Unit
) : ListAdapter<Train, TrainAdapter.VH>(DIFF) {

    private var selectedNo: String? = null

    fun setSelected(trainNo: String?) {
        val old = selectedNo
        selectedNo = trainNo
        // Refresh affected rows so the selection highlight redraws.
        currentList.forEachIndexed { i, t ->
            if (t.trainNo == old || t.trainNo == trainNo) notifyItemChanged(i)
        }
    }

    inner class VH(val b: ItemTrainBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: Train) {
            b.tvTrainNameKn.text = t.nameKn
            b.tvTrainNameEn.text = "${t.trainNo}  •  ${t.nameEn}"
            b.tvDeparture.text   = t.departure
            b.tvPlatform.text    = t.platform.toString()
            val highlight = t.trainNo == selectedNo
            b.root.isSelected    = highlight
            b.root.setOnClickListener { onClick(t) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return VH(ItemTrainBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Train>() {
            override fun areItemsTheSame(a: Train, b: Train)    = a.trainNo == b.trainNo
            override fun areContentsTheSame(a: Train, b: Train) = a == b
        }
    }
}
