package com.upn.catatlari.view.run

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upn.catatlari.databinding.ItemRunBinding
import com.upn.catatlari.model.Run
import com.upn.catatlari.utils.formatRunDate

class RunAdapter() : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private var runList = mutableListOf<Run>()

    fun setData(runItems: List<Run>) {
        runList.clear()
        runList.addAll(runItems)
        notifyItemRangeChanged(0, runItems.size)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder =
        RunViewHolder(ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) = holder.bind(runList[position])

    override fun getItemCount(): Int = runList.size

    inner class RunViewHolder(private val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(run: Run) {
            binding.tvTime.text = run.runDate
            binding.tvTitle.text = run.runTitle
            binding.tvLocation.text = run.runLocation
            binding.tvDistance.text = "${run.runDistance} km"
            binding.tvDuration.text = "${run.runDuration} menit"
        }


    }
}