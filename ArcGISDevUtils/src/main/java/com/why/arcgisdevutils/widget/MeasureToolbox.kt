package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.gis.calculateArea
import com.why.arcgisdevutils.gis.calculateLength
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
    private var canMapRotate = false
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
    private var iconColor = context.resources.getColor(R.color.blue, context.theme)
    private var frontSightColor = Color.BLACK
    private val mapListener by lazy {
        object : DefaultMapViewOnTouchListener(context, mMapView) {
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

            override fun onRotate(event: MotionEvent?, rotationAngle: Double): Boolean {
                return if(canMapRotate){
                    super.onRotate(event, rotationAngle)
                }else{
                    false
                }
            }
        }

    }
    private var onUnbind: ((view: MeasureToolbox) -> Unit)? = null
    private var onBind: ((view: MeasureToolbox) -> Unit)? = null
    private val drawable =
        ResourcesCompat.getDrawable(resources, R.drawable.ic_front_sight, context.theme)!!


    init {
        LayoutInflater.from(context).inflate(R.layout.measure_toolbox, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MeasureToolbox)
        mode = typedArray.getInt(R.styleable.MeasureToolbox_mode, 0)
        spinnerPosition = typedArray.getInt(R.styleable.MeasureToolbox_measure_spinner_position, 0)
        textColor =
            typedArray.getColor(R.styleable.MeasureToolbox_measure_toolbox_text_color, textColor)
        iconColor =
            typedArray.getColor(R.styleable.MeasureToolbox_measure_toolbox_icon_color, iconColor)
        frontSightColor = typedArray.getColor(R.styleable.MeasureToolbox_measure_toolbox_front_sight_color,frontSightColor)
        canMapRotate = typedArray.getBoolean(R.styleable.MeasureToolbox_measure_toolbox_can_map_rotate,false)
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
            mMapView?.onTouchListener = mapListener
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
                    String.format("%.2f", calculateLength(points, LinearUnitId.METERS))
                }
                "km" -> {
                    String.format("%.2f", calculateLength(points, LinearUnitId.KILOMETERS))
                }
                else -> {
                    String.format("%.2f", calculateLength(points, LinearUnitId.KILOMETERS) * 2)
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
                    String.format("%.2f", calculateArea(points, AreaUnitId.SQUARE_METERS))
                }
                "km²" -> {
                    String.format("%.2f", calculateArea(points, AreaUnitId.SQUARE_KILOMETERS))
                }
                else -> {
                    String.format("%.2f", calculateArea(points, AreaUnitId.SQUARE_METERS) / 666.7)
                }
            }
        }
    }

    fun bind(mapView: MapView) {
        if (!isBind()) {
            mMapView = mapView
            mMapView?.let { mapview ->
                if (mode != 0) {
                    mapview.post {
                        DrawableCompat.setTint(drawable,frontSightColor)
                        val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        bounds.offset((mapview.width-bounds.width())/2,(mapview.height-bounds.height())/2)
                        drawable.bounds = bounds
                        mapView.overlay.add(drawable)
                    }
                }
            }
            initListener()
            mMapView?.graphicsOverlays?.add(graphicOverlay)
            onBind?.invoke(this)
        }
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

    private fun setColor() {
        closeMeasure.imageTintList = ColorStateList.valueOf(iconColor)
        undo.imageTintList = ColorStateList.valueOf(iconColor)
        redo.imageTintList = ColorStateList.valueOf(iconColor)
        addPoint.imageTintList = ColorStateList.valueOf(iconColor)
        trashMeasure.imageTintList = ColorStateList.valueOf(iconColor)
        lengthAndArea.imageTintList = ColorStateList.valueOf(iconColor)
        measureResult.setTextColor(textColor)
        measureUnit.setTextColor(textColor)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        if (isBind()) {
            if (mode == 0 && mMapView?.onTouchListener == mapListener) {
                mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
            }
            graphicOverlay.graphics.clear()
            mMapView?.graphicsOverlays?.remove(graphicOverlay)
            measureResult.text = "0"
            measureUnit.text = "m"
            isLen = true
            lengthAndArea.setImageResource(R.drawable.length_selected)
            points.clear()
            temp.clear()
            if (mode == 1) {
                mMapView?.overlay?.remove(drawable)
            }
            mMapView = null
            onUnbind?.invoke(this)
        }
    }

    fun setOnBindListener(listener: (view: MeasureToolbox) -> Unit) {
        onBind = listener
    }

    fun setOnUnbindListener(listener: (view: MeasureToolbox) -> Unit) {
        onUnbind = listener
    }

    fun isBind() = mMapView != null

}