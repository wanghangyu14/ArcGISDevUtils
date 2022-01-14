package com.why.arcgisdevutils.widget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.why.arcgisdevutils.*
import com.why.arcgisdevutils.gis.Point
import com.why.arcgisdevutils.utils.navigateWithBD
import com.why.arcgisdevutils.utils.navigateWithGD
import com.why.arcgisdevutils.utils.navigateWithTX

class NaviAdapter(private val start: Point?, private val destination: Point?) :
    RecyclerView.Adapter<NaviAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val icon: ImageView = view.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.navigate_dialog_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            destination?.let {
                when (holder.adapterPosition) {
                    0 -> {
                        parent.context.navigateWithBD(start, destination)
                    }
                    1 -> {
                        parent.context.navigateWithGD(start, destination)
                    }
                    else -> {
                        parent.context.navigateWithTX(start, destination)
                    }
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.name.text = "百度地图"
                holder.icon.setImageResource(R.drawable.baidu)
            }
            1 -> {
                holder.name.text = "高德地图"
                holder.icon.setImageResource(R.drawable.gaode)
            }
            else -> {
                holder.name.text = "腾讯地图"
                holder.icon.setImageResource(R.drawable.tecent)
            }
        }
    }

    override fun getItemCount() = 3

}