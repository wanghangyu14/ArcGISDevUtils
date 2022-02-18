package com.why.arcgisdevutils.widget.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.widget.model.LayerOption


class LayerChoiceAdapter(
    private val checkboxDrawable: Int,
    private val seekbarThumbDrawable: Int,
    private val seekbarProgressDrawable: Int,
    private val onCheckedChange: (option: LayerOption) -> Unit
) :
    RecyclerView.Adapter<LayerChoiceAdapter.ViewHolder>() {
    private val list: MutableList<LayerOption> = mutableListOf()
    private var onSeekBarChange: ((option: LayerOption, progress: Int, fromUser: Boolean) -> Unit)? =
        null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
        val layerName: TextView = view.findViewById(R.id.layerName)
        val more: ImageView = view.findViewById(R.id.more)
        val seekbar: SeekBar = view.findViewById(R.id.seekbar)
        val opacity: TextView = view.findViewById(R.id.opacity)
        val tool: LinearLayout = view.findViewById(R.id.tool)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_controller_item, parent, false)
        val holder = ViewHolder(view)
        holder.apply {
            if(checkboxDrawable!=0) checkbox.setButtonDrawable(checkboxDrawable)
            if(seekbarThumbDrawable!=0) seekbar.thumb = AppCompatResources.getDrawable(parent.context,seekbarThumbDrawable)
            if(seekbarProgressDrawable!=0) seekbar.progressDrawable = AppCompatResources.getDrawable(parent.context,seekbarProgressDrawable)
            more.setOnClickListener {
                tool.isVisible = !tool.isVisible
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                list[bindingAdapterPosition].isSelected = isChecked
                onCheckedChange(list[bindingAdapterPosition])
            }
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    list[bindingAdapterPosition].apply {
                        opacity = progress
                        layers.forEach {
                            it.opacity = progress / 100f
                        }
                        onSeekBarChange?.invoke(this, progress, fromUser)
                    }
                    opacity.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.apply {
            layerName.text = item.name
            seekbar.progress = item.opacity
            checkbox.isChecked = item.isSelected
        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setLayers(newList: List<LayerOption>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun getLayers():List<LayerOption>{
        return list
    }

    fun setOnSeekBarChangeListener(listener: (option: LayerOption, progress: Int, fromUser: Boolean) -> Unit) {
        onSeekBarChange = listener
    }

}