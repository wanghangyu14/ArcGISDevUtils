package com.why.util.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.util.R
import kotlinx.android.synthetic.main.map_controller.view.*

class MapController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var hasZoom = true
    private var magnification = 2
    private var iconColor = Color.BLACK

    init {
        LayoutInflater.from(context).inflate(R.layout.map_controller, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapController)
        hasZoom = typedArray.getBoolean(R.styleable.MapController_magnification, true)
        magnification = typedArray.getInt(R.styleable.MapController_magnification,2)
        iconColor = typedArray.getColor(R.styleable.MapController_map_controller_icon_color,iconColor)
        typedArray.recycle()
        zoom.isVisible = hasZoom
        setColor()
    }

    private fun setColor() {
        relocation.imageTintList = ColorStateList.valueOf(iconColor)
        zoomIn.imageTintList = ColorStateList.valueOf(iconColor)
        zoomOut.imageTintList = ColorStateList.valueOf(iconColor)
    }

    fun bind(mapView: MapView) {

        relocation.setOnClickListener {
            val locationDisplay = mapView.locationDisplay
            val location = locationDisplay.mapLocation
            mapView.setViewpointCenterAsync(location)
        }

        if (hasZoom) {
            zoomIn.setOnClickListener {
                mapView.setViewpointScaleAsync(mapView.mapScale / magnification)
            }

            zoomOut.setOnClickListener {
                mapView.setViewpointScaleAsync(mapView.mapScale * magnification)
            }
        }

    }
}