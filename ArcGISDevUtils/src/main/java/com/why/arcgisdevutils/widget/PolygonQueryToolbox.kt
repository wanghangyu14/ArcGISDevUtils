package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.gis.spatialQuery
import kotlinx.android.synthetic.main.polygon_query_toolbox.view.*
import kotlin.math.roundToInt

class PolygonQueryToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private val graphicsOverlay = GraphicsOverlay()
    private val points = mutableListOf<Point>()
    private val temp = mutableListOf<Point>()
    private val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2F)
    private val fillSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 255, 0),
        lineSymbol
    )
    private var iconColor = Color.BLACK
    private var onQueryResult: ((list: List<Pair<String, List<Feature>>>) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.polygon_query_toolbox, this, true)
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.PolygonQueryToolbox)
        iconColor = typedArray.getColor(R.styleable.PolygonQueryToolbox_polygon_query_toolbox_icon_color,iconColor)
        typedArray.recycle()
        setColor()
    }

    private fun setColor() {
        undo.imageTintList = ColorStateList.valueOf(iconColor)
        redo.imageTintList = ColorStateList.valueOf(iconColor)
        complete.imageTintList = ColorStateList.valueOf(iconColor)
        clear.imageTintList = ColorStateList.valueOf(iconColor)
        quit.imageTintList = ColorStateList.valueOf(iconColor)
    }


    fun bind(mapView: MapView) {
        if(!isBind()){
            mMapView = mapView
            mMapView?.graphicsOverlays?.add(graphicsOverlay)
            initListener()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        if(isBind()){
            mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
            points.clear()
            temp.clear()
            graphicsOverlay.graphics.clear()
            mMapView?.graphicsOverlays?.clear()
            clearSelection()
            mMapView = null
        }
    }

    fun setOnQueryResultListener(listener: (list: List<Pair<String, List<Feature>>>) -> Unit) {
        onQueryResult = listener
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val point = mMapView.screenToLocation(
                    android.graphics.Point(
                        e.x.roundToInt(),
                        e.y.roundToInt()
                    )
                )
                points.add(point)
                drawPolygon()
                return super.onSingleTapConfirmed(e)
            }
        }

        undo.setOnClickListener {
            if (points.isNotEmpty()) {
                temp.add(points.removeAt(points.size - 1))
                drawPolygon()
            }
        }

        redo.setOnClickListener {
            if (temp.isNotEmpty()) {
                points.add(temp.removeAt(temp.size - 1))
                drawPolygon()
            }
        }

        complete.setOnClickListener {
            clearSelection()
            val optionalLayers = mMapView?.map?.operationalLayers
            optionalLayers?.let {
                val result = spatialQuery(
                    Polygon(PointCollection(points)),
                    List(optionalLayers.size) { index -> optionalLayers[index] as FeatureLayer })
                onQueryResult?.invoke(result)
            }
        }


        clear.setOnClickListener {
            graphicsOverlay.graphics.clear()
            points.clear()
            temp.clear()
            clearSelection()
        }

        quit.setOnClickListener {
            unbind()
        }
    }


    private fun drawPolygon() {
        graphicsOverlay.graphics.clear()
        graphicsOverlay.graphics.add(
            Graphic(
                Polygon(PointCollection(points)),
                fillSymbol
            )
        )
    }

    private fun clearSelection() {
        mMapView?.map?.operationalLayers?.forEach { layer ->
            (layer as FeatureLayer).clearSelection()
        }
    }

    fun isBind() = mMapView != null
}