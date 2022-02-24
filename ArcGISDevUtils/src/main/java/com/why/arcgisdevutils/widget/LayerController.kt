package com.why.arcgisdevutils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.widget.adapter.LayerChoiceAdapter
import com.why.arcgisdevutils.widget.model.LayerOption
import kotlinx.android.synthetic.main.layer_controller.view.*

class LayerController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var catalogTitle = ""
    private var checkboxDrawable=0
    private var seekbarThumbDrawable=0
    private var seekbarProgressDrawable=0
    private var collapsable = true
    private var isCollapsed = false
    private var showTitle = true
    private var mMapView: MapView? = null
    private var onCheck: ((option: LayerOption) -> Unit)? = null
    private val adapter by lazy {
        LayerChoiceAdapter(
            checkboxDrawable,
            seekbarThumbDrawable,
            seekbarProgressDrawable
        ) { option: LayerOption ->
            if (option.isSelected) {
                mMapView?.map?.operationalLayers?.addAll(option.layers)
            } else {
                mMapView?.map?.operationalLayers?.removeAll(option.layers)
            }
            onCheck?.invoke(option)
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayerController)
        catalogTitle = typedArray.getString(R.styleable.LayerController_title) ?: ""
        collapsable = typedArray.getBoolean(R.styleable.LayerController_collapsable,true)
        isCollapsed = typedArray.getBoolean(R.styleable.LayerController_isCollapsed,false)
        showTitle = typedArray.getBoolean(R.styleable.LayerController_showTitle,true)
        checkboxDrawable =
            typedArray.getResourceId(R.styleable.LayerController_checkbox_drawable,0)
        seekbarThumbDrawable =
            typedArray.getResourceId(R.styleable.LayerController_seekbar_thumb_drawable,0)
        seekbarProgressDrawable =
            typedArray.getResourceId(R.styleable.LayerController_seekbar_progress_drawable,0)
        LayoutInflater.from(context).inflate(R.layout.layer_controller, this, true)
        typedArray.recycle()
    }


    fun bind(mapView: MapView) {
        if(!isBind()){
            mMapView = mapView
            if(showTitle){
                title.text = catalogTitle
            }else{
                spinner.isVisible = false
            }
            if(collapsable){
                spinner.setOnClickListener {
                    layerList.isVisible = !layerList.isVisible
                }
                if(isCollapsed){
                    layerList.isVisible = false
                }
            }
            layerList.adapter = adapter
        }
    }

    fun setLayers(list: List<LayerOption>) {
        adapter.setLayers(list)
    }


    fun setOnCheckListener(listener: (option: LayerOption) -> Unit) {
        onCheck = listener
    }

    fun setOnSeekBarChangeListener(listener: (option: LayerOption, progress: Int, fromUser: Boolean) -> Unit) {
        adapter.setOnSeekBarChangeListener(listener)
    }

    fun getSelectedLayer():List<LayerOption>{
        val list = mutableListOf<LayerOption>()
        adapter.getLayers().forEach {
            if(it.isSelected) list.add(it)
        }
        return list
    }

    fun isBind() = mMapView!=null
}