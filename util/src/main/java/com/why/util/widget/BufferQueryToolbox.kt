package com.why.util.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.why.util.R
import com.why.util.spatialQuery
import kotlinx.android.synthetic.main.buffer_query_toolbox.view.*
import kotlin.math.roundToInt

class BufferQueryToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private val graphicOverlay = GraphicsOverlay()
    private val dotSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 8F)
    private val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2F)
    private val fillSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 255, 0),
        lineSymbol
    )
    private var onQueryResult: ((list: List<Pair<String, List<Feature>>>) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.buffer_query_toolbox, this, true)
    }


    fun bind(mapView: MapView) {
        mMapView = mapView
        mMapView?.graphicsOverlays?.add(graphicOverlay)
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
        graphicOverlay.graphics.clear()
        mMapView?.graphicsOverlays?.clear()
        mMapView = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                graphicOverlay.graphics.clear()
                mMapView.map.operationalLayers.forEach { layer ->
                    (layer as FeatureLayer).clearSelection()
                }
                val point = mMapView.screenToLocation(
                    android.graphics.Point(
                        e.x.roundToInt(),
                        e.y.roundToInt()
                    )
                )
                val radius = bufferRadius.text.toString().toDouble()
                val bufferGeometry: Geometry = GeometryEngine.bufferGeodetic(
                    point, radius,
                    LinearUnit(LinearUnitId.METERS), Double.NaN, GeodeticCurveType.GEODESIC
                )
                graphicOverlay.graphics.add(Graphic(bufferGeometry, fillSymbol))
                graphicOverlay.graphics.add(Graphic(point, dotSymbol))
                val optionalLayers = mMapView.map.operationalLayers
                val result = spatialQuery(
                    bufferGeometry,
                    List(optionalLayers.size) { index -> optionalLayers[index] as FeatureLayer })
                onQueryResult?.invoke(result)
                return super.onSingleTapConfirmed(e)
            }
        }

        clearBuffer.setOnClickListener {
            graphicOverlay.graphics.clear()
        }

        closeBuffer.setOnClickListener {
            unbind()
        }
    }

    fun setOnQueryResultListener(listener: (list: List<Pair<String, List<Feature>>>) -> Unit) {
        onQueryResult = listener
    }

    fun isBind() = mMapView != null

}