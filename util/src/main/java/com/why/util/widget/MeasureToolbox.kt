package com.why.util.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
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
import kotlin.math.roundToInt

class MeasureToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mode = 0
    private var spinnerPosition = 0
    private var mMapView: MapView? = null
    private val graphicOverlay = GraphicsOverlay()
    private var isLen = true
    private val points = mutableListOf<Point>()
    private val temp = mutableListOf<Point>()
    private val dotSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 8F)
    private val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2F)
    private val fillSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50, 0, 255, 0),
        lineSymbol
    )
    private val imageView = ImageView(context)
    private var textColor = Color.BLACK
    private var iconColor = context.resources.getColor(R.color.blue,context.theme)


    init {
        LayoutInflater.from(context).inflate(R.layout.measure_toolbox, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MeasureToolbox)
        mode = typedArray.getInt(R.styleable.MeasureToolbox_mode, 0)
        spinnerPosition = typedArray.getInt(R.styleable.MeasureToolbox_measure_spinner_position, 0)
        textColor = typedArray.getColor(R.styleable.MeasureToolbox_measure_toolbox_text_color,textColor)
        iconColor = typedArray.getColor(R.styleable.MeasureToolbox_measure_toolbox_icon_color,iconColor)
        typedArray.recycle()
        initView()
        setColor()
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initListener() {
        closeMeasure.setOnClickListener {
            unbind()
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

        if (mode == 0) {
            mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    val point = mMapView.screenToLocation(
                        android.graphics.Point(
                            e.x.roundToInt(),
                            e.y.roundToInt()
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
                    return super.onSingleTapConfirmed(e)
                }
            }
        } else {
            addPoint.setOnClickListener {
                mMapView?.let { mapview ->
                    val point = mapview.screenToLocation(
                        android.graphics.Point(
                            mapview.width / 2,
                            mapview.height / 2
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
        }


        undo.setOnClickListener {
            if (points.isNotEmpty()) {
                temp.add(points.removeAt(points.size - 1))
                refresh()
            }
        }

        redo.setOnClickListener {
            if (temp.isNotEmpty()) {
                points.add(temp.removeAt(temp.size - 1))
                refresh()
            }
        }

        trashMeasure.setOnClickListener {
            graphicOverlay.graphics.clear()
            points.clear()
            temp.clear()
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

    fun bind(mapView: MapView) {
        mMapView = mapView
        mMapView?.let { mapview ->
            if (mode != 0) {
                mapview.post {
                    val params = WindowManager.LayoutParams()
                    val location = IntArray(2)
                    val statusBarId =
                        context.resources.getIdentifier("status_bar_height", "dimen", "android")
                    val statusBarHeight = context.resources.getDimensionPixelSize(statusBarId)
                    mapview.getLocationInWindow(location)
                    params.apply {
                        gravity = Gravity.TOP or Gravity.LEFT
                        width = WRAP_CONTENT
                        height = WRAP_CONTENT
                        x = location[0] + (mapview.width - 21.dp2px(context)) / 2
                        y = location[1] + (mapview.height - 21.dp2px(context)) / 2 - statusBarHeight
                        format = PixelFormat.RGBA_8888
                        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        type = WindowManager.LayoutParams.TYPE_APPLICATION
                    }
                    val windowManager = (context as Activity).windowManager
                    windowManager.addView(imageView, params)
                }
            }
        }
        initListener()
        mMapView?.graphicsOverlays?.add(graphicOverlay)
    }


    private fun initView() {
        addPoint.isVisible = mode != 0
        imageView.scaleType = ImageView.ScaleType.CENTER
        imageView.setImageResource(R.drawable.ic_front_sight)
    }

    private fun refresh() {
        graphicOverlay.graphics.clear()
        if (points.size > 1) {
            points.forEach {
                graphicOverlay.graphics.add(Graphic(it, dotSymbol))
            }
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

    private fun setColor(){
        closeMeasure.imageTintList= ColorStateList.valueOf(iconColor)
        undo.imageTintList= ColorStateList.valueOf(iconColor)
        redo.imageTintList= ColorStateList.valueOf(iconColor)
        addPoint.imageTintList= ColorStateList.valueOf(iconColor)
        trashMeasure.imageTintList= ColorStateList.valueOf(iconColor)
        lengthAndArea.imageTintList= ColorStateList.valueOf(iconColor)
        measureResult.setTextColor(textColor)
        measureUnit.setTextColor(textColor)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        if (mode == 0) {
            mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
        }
        graphicOverlay.graphics.clear()
        mMapView?.graphicsOverlays?.clear()
        measureResult.text = "0"
        measureUnit.text = "m"
        isLen = true
        lengthAndArea.setImageResource(R.drawable.length_selected)
        points.clear()
        temp.clear()
        if (mode == 1) {
            (context as Activity).windowManager.removeView(imageView)
        }
        mMapView = null
    }

    fun isBind() = mMapView != null

}