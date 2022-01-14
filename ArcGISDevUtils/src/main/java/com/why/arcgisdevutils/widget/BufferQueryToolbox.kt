package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
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
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.gis.spatialQuery
import kotlinx.android.synthetic.main.buffer_query_toolbox.view.*
import razerdp.basepopup.BasePopupWindow
import kotlin.math.roundToInt

class BufferQueryToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private var spinnerPosition = 0
    private var unit: LinearUnitId = LinearUnitId.METERS
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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BufferQueryToolbox)
        spinnerPosition =
            typedArray.getInt(R.styleable.BufferQueryToolbox_buffer_spinner_position, 0)
        typedArray.recycle()
    }


    fun bind(mapView: MapView) {
        if(!isBind()){
            mMapView = mapView
            mMapView?.graphicsOverlays?.add(graphicOverlay)
            initListener()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        if(isBind()){
            mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
            graphicOverlay.graphics.clear()
            mMapView?.graphicsOverlays?.clear()
            clearSelection()
            bufferRadius.setText("0")
            bufferRadius.clearFocus()
            bufferUnit.text = "m"
            unit = LinearUnitId.METERS
            mMapView = null
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initListener() {
        mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                graphicOverlay.graphics.clear()
                clearSelection()
                val point = mMapView.screenToLocation(
                    android.graphics.Point(
                        e.x.roundToInt(),
                        e.y.roundToInt()
                    )
                )
                val radius = bufferRadius.text.toString().toDouble()
                val bufferGeometry = when (unit) {
                    LinearUnitId.METERS -> GeometryEngine.bufferGeodetic(
                        point, radius,
                        LinearUnit(LinearUnitId.METERS), Double.NaN, GeodeticCurveType.GEODESIC
                    )
                    LinearUnitId.KILOMETERS -> GeometryEngine.bufferGeodetic(
                        point, radius,
                        LinearUnit(LinearUnitId.KILOMETERS), Double.NaN, GeodeticCurveType.GEODESIC
                    )
                    else -> GeometryEngine.bufferGeodetic(
                        point, radius/2,
                        LinearUnit(LinearUnitId.METERS), Double.NaN, GeodeticCurveType.GEODESIC
                    )
                }
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

        bufferUnit.setOnClickListener {
            val popup = UnitPopup(
                isLen = true,
                isBottom = false,
                context = context
            ) { checkedId ->
                when (checkedId) {
                    R.id.meter -> {
                        bufferUnit.text = "m"
                        unit = LinearUnitId.METERS
                    }
                    R.id.li -> {
                        bufferUnit.text = "里"
                        unit = LinearUnitId.OTHER
                    }
                    R.id.kilometer -> {
                        bufferUnit.text = "km"
                        unit = LinearUnitId.KILOMETERS
                    }
                }
            }
            val gravity = if (spinnerPosition == 0) {
                Gravity.TOP
            } else {
                Gravity.BOTTOM
            }
            popup.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, gravity)
            popup.showPopupWindow(bufferUnit)
        }

        closeBuffer.setOnClickListener {
            unbind()
        }
    }

    private fun clearSelection() {
        mMapView?.map?.operationalLayers?.forEach { layer ->
            (layer as FeatureLayer).clearSelection()
        }
    }

    fun setOnQueryResultListener(listener: (list: List<Pair<String, List<Feature>>>) -> Unit) {
        onQueryResult = listener
    }

    fun isBind() = mMapView != null

}