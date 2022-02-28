package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
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
import kotlinx.android.synthetic.main.buffer_query_toolbox.view.*
import razerdp.basepopup.BasePopupWindow
import kotlin.math.roundToInt

class BufferQueryToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private var spinnerPosition = 0
    private var unit: LinearUnitId = LinearUnitId.METERS
    private val graphicsOverlay = GraphicsOverlay()
    private val dotSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 8F)
    private val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2F)
    private val fillSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 255, 0),
        lineSymbol
    )
    private var onQueryResult: ((geometry: Geometry) -> Unit)? = null
    private var onUnbind :((view:BufferQueryToolbox)->Unit)? = null
    private var onBind :((view:BufferQueryToolbox)->Unit)? = null
    private val mapListener by lazy {
        object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                graphicsOverlay.graphics.clear()
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
                        point, radius / 2,
                        LinearUnit(LinearUnitId.METERS), Double.NaN, GeodeticCurveType.GEODESIC
                    )
                }
                graphicsOverlay.graphics.add(Graphic(bufferGeometry, fillSymbol))
                graphicsOverlay.graphics.add(Graphic(point, dotSymbol))
                onQueryResult?.invoke(bufferGeometry)
                return super.onSingleTapConfirmed(e)
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.buffer_query_toolbox, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.UtilsBufferQueryToolbox)
        spinnerPosition =
            typedArray.getInt(R.styleable.UtilsBufferQueryToolbox_buffer_spinner_position, 0)
        typedArray.recycle()
    }


    fun bind(mapView: MapView) {
        if (!isBind()) {
            mMapView = mapView
            mMapView?.graphicsOverlays?.add(graphicsOverlay)
            initListener()
            onBind?.invoke(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        if (isBind()) {
            if (mMapView?.onTouchListener == mapListener) {
                mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
            }
            graphicsOverlay.graphics.clear()
            mMapView?.graphicsOverlays?.remove(graphicsOverlay)
            clearSelection()
            bufferRadius.setText("0")
            bufferRadius.clearFocus()
            bufferUnit.text = "m"
            unit = LinearUnitId.METERS
            mMapView = null
            onUnbind?.invoke(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initListener() {
        mMapView?.onTouchListener = mapListener

        clearBuffer.setOnClickListener {
            graphicsOverlay.graphics.clear()
            clearSelection()
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

    fun setOnQueryResultListener(listener: (geometry: Geometry) -> Unit) {
        onQueryResult = listener
    }

    fun setOnBindListener(listener:(view:BufferQueryToolbox)->Unit){
        onBind = listener
    }

    fun setOnUnbindListener(listener:(view:BufferQueryToolbox)->Unit){
        onUnbind = listener
    }

    fun isBind() = mMapView != null

}