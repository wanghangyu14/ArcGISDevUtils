package com.why.util.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.why.util.R
import com.why.util.dp2px
import kotlinx.android.synthetic.main.measure_toolbox.view.*
import razerdp.basepopup.BasePopupWindow

class MeasureToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mode = 0
    private var spinnerPosition = 0
    private var mMapView: MapView? = null
    private val graphicOverlay = GraphicsOverlay()
    private var isLen = true
    private val points = mutableListOf<Point>()
    private val dotSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 8F)
    private val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2F)
    private val fillSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 255, 0),
        lineSymbol
    )


    init {
        LayoutInflater.from(context).inflate(R.layout.measure_toolbox, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MeasureToolbox)
        mode = typedArray.getInt(R.styleable.MeasureToolbox_mode, 0)
        spinnerPosition = typedArray.getInt(R.styleable.MeasureToolbox_spinner_position, 0)
        typedArray.recycle()
    }


    @SuppressLint("SetTextI18n")
    private fun initListener() {
        closeMeasure.setOnClickListener {
            onHide()
        }

        measureUnit.setOnClickListener {
            val popup = UnitPopup(
                isLen,
                spinnerPosition == 0, context
            ) { checkedId ->
                when (checkedId) {
                    R.id.meter -> {
                        measureUnit.text = "m"
                        measureResult.text = len()
                    }
                    R.id.kilometer -> {
                        measureUnit.text = "km"
                        measureResult.text = len()
                    }
                    R.id.li -> {
                        measureUnit.text = "里"
                        measureResult.text = len()
                    }
                    R.id.squareMeter -> {
                        measureUnit.text = "m²"
                        measureResult.text = area()
                    }
                    R.id.squareKilometer -> {
                        measureUnit.text = "km²"
                        measureResult.text = area()
                    }
                    R.id.mu -> {
                        measureUnit.text = "亩"
                        measureResult.text = area()
                    }
                }
            }
            val gravity = if (spinnerPosition == 0) {
                Gravity.TOP
            } else {
                Gravity.BOTTOM
            }
            popup.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, gravity)
            popup.showPopupWindow(measureUnit)
        }

        addPoint.setOnClickListener {
            mMapView?.let { mapview ->
                val point = mapview.screenToLocation(
                    android.graphics.Point(
                        mapview.width / 2 + mapview.left,
                        mapview.height / 2 + mapview.top
                    )
                )
                points.add(point)
                graphicOverlay.graphics.add(Graphic(point, dotSymbol))
                if (isLen) {
                    if (points.size > 1) {
                        graphicOverlay.graphics.add(
                            Graphic(
                                Polyline(PointCollection(points)),
                                lineSymbol
                            )
                        )
                        measureResult.text = len()
                    }
                } else {
                    graphicOverlay.graphics.clear()
                    points.forEach {
                        graphicOverlay.graphics.add(Graphic(it, dotSymbol))
                    }
                    graphicOverlay.graphics.add(
                        Graphic(
                            Polygon(PointCollection(points)),
                            fillSymbol
                        )
                    )
                    measureResult.text = area()
                }
            }
        }

        undo.setOnClickListener {
            graphicOverlay.graphics.clear()
            if (points.isNotEmpty()) {
                points.removeAt(points.size - 1)
                points.forEach {
                    graphicOverlay.graphics.add(Graphic(it, dotSymbol))
                }
            }
            if (points.size > 1) {
                if (isLen) {
                    graphicOverlay.graphics.add(
                        Graphic(
                            Polyline(PointCollection(points)),
                            lineSymbol
                        )
                    )
                    measureResult.text = len()
                } else {
                    graphicOverlay.graphics.add(
                        Graphic(
                            Polygon(PointCollection(points)),
                            fillSymbol
                        )
                    )
                    measureResult.text = area()
                }
            } else {
                measureResult.text = "0"
            }
        }

        trashMeasure.setOnClickListener {
            graphicOverlay.graphics.clear()
            points.clear()
            measureResult.text = "0"
        }

        lengthAndArea.setOnClickListener {
            graphicOverlay.graphics.clear()
            if (!isLen) {
                measureUnit.text = "m"
                lengthAndArea.setImageResource(R.drawable.length_selected)
                if (points.isNotEmpty()) {
                    measureResult.text = len()
                    points.forEach { point ->
                        graphicOverlay.graphics.add(Graphic(point, dotSymbol))
                    }
                    graphicOverlay.graphics.add(
                        Graphic(
                            Polyline(PointCollection(points)),
                            lineSymbol
                        )
                    )
                }
            } else {
                measureUnit.text = "m²"
                lengthAndArea.setImageResource(R.drawable.area_selected)
                if (points.isNotEmpty()) {
                    measureResult.text = area()
                    points.forEach { point ->
                        graphicOverlay.graphics.add(Graphic(point, dotSymbol))
                    }
                    graphicOverlay.graphics.add(
                        Graphic(
                            Polygon(PointCollection(points)),
                            fillSymbol
                        )
                    )
                }
            }
            isLen = !isLen
        }
    }

    private fun len(): String {
        return if (points.isEmpty()) {
            "0"
        } else {
            when (measureUnit.text) {
                "m" -> {
                    String.format("%.2f", calculateLength(points, Meter))
                }
                "km" -> {
                    String.format("%.2f", calculateLength(points, KiloMeter))
                }
                else -> {
                    String.format("%.2f", calculateLength(points, Li))
                }
            }
        }
    }

    private fun area(): String {
        return if (points.isEmpty()) {
            "0"
        } else {
            when (measureUnit.text) {
                "m²" -> {
                    String.format("%.2f", calculateArea(points, SquareMeter))
                }
                "km²" -> {
                    String.format("%.2f", calculateArea(points, SquareKiloMeter))
                }
                else -> {
                    String.format("%.2f", calculateArea(points, Mu))
                }
            }
        }
    }

    fun bindMapView(mapView: MapView) {
        mMapView = mapView
        mMapView?.graphicsOverlays?.add(graphicOverlay)
    }

    @SuppressLint("InflateParams")
    fun show() {
        mMapView?.let { mapview ->
            initListener()
            mapview.post {
                val imageView = ImageView(context)
                imageView.scaleType = ImageView.ScaleType.CENTER
                imageView.setImageResource(R.drawable.ic_front_sight)
                val params = WindowManager.LayoutParams()
                params.apply {
                    gravity = Gravity.TOP or Gravity.LEFT
                    width = WRAP_CONTENT
                    height = WRAP_CONTENT
                    x = mapview.x.toInt() + (mapview.width - 21.dp2px(context)) / 2
                    y = mapview.y.toInt() + (mapview.height - 21.dp2px(context)) / 2
                    format = PixelFormat.RGBA_8888
                    flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    type = WindowManager.LayoutParams.TYPE_APPLICATION
                }
                val windowManager = (context as Activity).windowManager
                windowManager.addView(imageView, params)
            }
        }
    }

    fun hide() {
        onHide()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onHide() {
        mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
        this.isVisible = false
        graphicOverlay.graphics.clear()
        mMapView?.graphicsOverlays?.remove(graphicOverlay)
        points.clear()
    }
}