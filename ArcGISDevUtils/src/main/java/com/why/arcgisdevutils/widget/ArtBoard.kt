package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import com.why.arcgisdevutils.R
import kotlinx.android.synthetic.main.art_board.view.*
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog

import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import me.jfenn.colorpickerdialog.views.picker.PresetPickerView
import kotlin.math.roundToInt


class ArtBoard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var selectedColor = resources.getColor(R.color.red, context.theme)
    private var mMapView: MapView? = null
    private var lineWidth = 3f
    private val graphicOverlay = GraphicsOverlay()
    private var lineSymbol = SimpleLineSymbol(
        SimpleLineSymbol.Style.SOLID, selectedColor,
        lineWidth
    )
    private val points = mutableListOf<Point>()
    private val lines = mutableListOf<Line>()
    private val tmp = mutableListOf<Line>()
    private var pointers = 0
    private var onUnbind: ((view: ArtBoard) -> Unit)? = null
    private var onBind: ((view: ArtBoard) -> Unit)? = null

    class Line(
        val width: Float,
        @ColorInt val color: Int,
        val points: List<Point>,
        val graphic: Graphic
    )


    init {
        LayoutInflater.from(context).inflate(R.layout.art_board, this)
    }

    fun bind(mapView: MapView) {
        mMapView = mapView
        mMapView?.graphicsOverlays?.add(graphicOverlay)
        initListener()
        onBind?.invoke(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun unbind() {
        mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
        graphicOverlay.graphics.clear()
        mMapView?.graphicsOverlays?.remove(graphicOverlay)
        points.clear()
        lines.clear()
        tmp.clear()
        selectedColor = resources.getColor(R.color.red, context.theme)
        mMapView = null
        pointers = 0
        lineWidth = 3f
        lineSymbol = SimpleLineSymbol(
            SimpleLineSymbol.Style.SOLID, selectedColor,
            lineWidth
        )
        onUnbind?.invoke(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        colorIndicator.setOnClickListener {
            ColorPickerDialog()
                .withColor(selectedColor)
                .withPickers(PresetPickerView::class.java)
                .withListener { dialog, color ->
                    selectedColor = color
                    colorIndicator.setBackgroundColor(color)
                    lineSymbol = SimpleLineSymbol(
                        SimpleLineSymbol.Style.SOLID, selectedColor,
                        lineWidth
                    )
                    dialog?.dismiss()
                }
                .show((context as AppCompatActivity).supportFragmentManager, "色彩选择")
        }

        lineWidthSelector.setOnCheckedChangeListener { _, checkedId ->
            lineWidth = when (checkedId) {
                R.id.small -> 3f
                R.id.medium -> 6f
                else -> 9f
            }
        }

        clear.setOnClickListener {
            graphicOverlay.graphics.clear()
            points.clear()
            lines.clear()
            tmp.clear()
        }

        undo.setOnClickListener {
            if (lines.isNotEmpty()) {
                val line = lines.removeLast()
                tmp.add(line)
                graphicOverlay.graphics.clear()
                drawLines(lines)
            }
        }

        redo.setOnClickListener {
            if (tmp.isNotEmpty()) {
                val line = tmp.removeLast()
                lines.add(line)
                graphicOverlay.graphics.clear()
                drawLines(lines)
            }
        }

        close.setOnClickListener {
            unbind()
        }

        mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val point = android.graphics.Point(event.x.roundToInt(), event.y.roundToInt())
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        pointers = 1
                        points.add(mMapView.screenToLocation(point))
                    }
                    MotionEvent.ACTION_UP -> {
                        pointers = 0
                        points.add(mMapView.screenToLocation(point))
                        lines.add(
                            generateLine(
                                lineWidth,
                                selectedColor,
                                points.toList()
                            )
                        )
                        points.clear()
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        pointers += 1
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                        pointers -= 1
                        lines.add(
                            generateLine(
                                lineWidth,
                                selectedColor,
                                points.toList()
                            )
                        )
                        points.clear()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (pointers == 1) {
                            points.add(mMapView.screenToLocation(point))
                            drawLine(
                                generateLine(
                                    lineWidth,
                                    selectedColor,
                                    points.toList()
                                )
                            )
                        }
                    }
                }
                return true
            }
        }
    }

    private fun drawLine(line: Line) {
        graphicOverlay.graphics.add(line.graphic)
    }

    private fun drawLines(lines:List<Line>){
        graphicOverlay.graphics.addAll(List(lines.size){index -> lines[index].graphic })
    }

    private fun generateLine(
        width: Float,
        @ColorInt color: Int,
        points: List<Point>
    ): Line {
        return Line(
            width,
            color,
            points,
            Graphic(
                Polyline(PointCollection(points)),
                SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, width)
            )
        )
    }


    fun setOnBindListener(listener: (view: ArtBoard) -> Unit) {
        onBind = listener
    }

    fun setOnUnbindListener(listener: (view: ArtBoard) -> Unit) {
        onUnbind = listener
    }

    fun isBind() = mMapView != null
}