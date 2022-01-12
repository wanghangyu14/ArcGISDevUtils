package com.why.util.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.why.util.R
import kotlinx.android.synthetic.main.buffer_query_toolbox.view.*

class BufferQueryToolbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView:MapView? = null
    private val graphicOverlay = GraphicsOverlay()
    private val dotSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 8F)
    init {
        LayoutInflater.from(context).inflate(R.layout.buffer_query_toolbox,this,true)
    }

    fun bindMapView(mapView: MapView){
        mMapView = mapView
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mMapView?.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView){
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return super.onSingleTapConfirmed(e)
            }
        }

        closeBuffer.setOnClickListener {
            isVisible = false
            mMapView?.onTouchListener = DefaultMapViewOnTouchListener(context, mMapView)
        }
    }


}