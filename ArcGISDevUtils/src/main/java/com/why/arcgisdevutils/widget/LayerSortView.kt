package com.why.arcgisdevutils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.esri.arcgisruntime.mapping.view.MapView

class LayerSortView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private var onUnbind: ((view: LayerSortView) -> Unit)? = null
    private var onBind: ((view: LayerSortView) -> Unit)? = null


    fun bind(mapView: MapView) {
        if(!isBind()){
            mMapView = mapView
            onBind?.invoke(this)
        }
    }

    fun isBind() = mMapView != null
}