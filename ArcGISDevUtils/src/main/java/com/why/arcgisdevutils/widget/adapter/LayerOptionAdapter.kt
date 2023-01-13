package com.why.arcgisdevutils.widget.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.widget.model.LayerOption


class LayerOptionAdapter(dataSet: MutableList<LayerOption>) :
    DragDropSwipeAdapter<LayerOption, LayerOptionAdapter.ViewHolder>(dataSet) {
    class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.layerName)
        val dragIcon: ImageView = itemView.findViewById(R.id.dragIcon)
        val seekbar:SeekBar = itemView.findViewById(R.id.seekbar)
    }

    override fun getViewHolder(itemView: View): ViewHolder {
        val holder = ViewHolder(itemView)
        holder.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                dataSet[holder.bindingAdapterPosition].apply {
                    opacity = progress
                    layers.forEach { it.opacity = progress/100f }
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

        })
        return holder
    }

    override fun getViewToTouchToStartDraggingItem(
        item: LayerOption,
        viewHolder: ViewHolder,
        position: Int
    ): View {
        return viewHolder.dragIcon
    }

    override fun onBindViewHolder(item: LayerOption, viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemText.text = item.name
            seekbar.progress = item.opacity
        }
    }

    fun getDataSet() = Array(dataSet.size){index->dataSet[index].layers}

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        (dataSet as MutableList).clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(list: List<LayerOption>){
        (dataSet as MutableList).clear()
        (dataSet as MutableList).addAll(list)
        notifyDataSetChanged()
    }

}