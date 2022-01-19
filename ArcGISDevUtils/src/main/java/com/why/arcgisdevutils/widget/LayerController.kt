package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.esri.arcgisruntime.layers.Layer
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.widget.adapter.LayerChoiceAdapter
import com.why.arcgisdevutils.widget.model.LayerOption
import kotlinx.android.synthetic.main.layer_controller.view.*

class LayerController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var catalogTitle = ""
    private var checkboxColor = ResourcesCompat.getColor(resources, R.color.blue, context.theme)
    private var seekbarColor = ResourcesCompat.getColor(resources, R.color.blue, context.theme)
    private var mMapView: MapView? = null
    private val layers: MutableList<LayerOption> = mutableListOf()
    private val adapter = LayerChoiceAdapter(layers){option: LayerOption ->
        if(option.isSelected){
            mMapView?.map?.operationalLayers?.addAll(option.layers)
        }else{
            mMapView?.map?.operationalLayers?.removeAll(option.layers)
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayerController)
        catalogTitle = typedArray.getString(R.styleable.LayerController_title) ?: ""
        checkboxColor =
            typedArray.getColor(R.styleable.LayerController_checkbox_color, checkboxColor)
        seekbarColor = typedArray.getColor(R.styleable.LayerController_seekbar_color, seekbarColor)
        LayoutInflater.from(context).inflate(R.layout.layer_controller, this, true)
        title.text = catalogTitle
        typedArray.recycle()
    }


    fun bind(mapView: MapView): LayerController {
        mMapView = mapView
        init()
        return this
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLayers(map:Map<String,List<Layer>>) {
        val list = map.toList()
        layers.clear()
        layers.addAll(List(list.size) { index ->
            LayerOption(
                list[index].first,
                list[index].second,
                100,
                false
            )
        })
        adapter.setLayers(layers)
    }

    private fun init() {
        layerList.adapter = adapter

        spinner.setOnClickListener {
            layerList.isVisible = !layerList.isVisible
        }
    }
}