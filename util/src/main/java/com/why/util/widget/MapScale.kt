package com.why.util.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.why.util.R
import com.why.util.dp2px
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt


class MapScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val ppi = getPPIOfDevice()
    private val ppcm = ppi / 2.54
    private var fontSize = 8.dp2px(context).toFloat()
    private var scale = 100
    private var scaleWidth = (100 * ppcm / scale).toInt()
    private var scaleHeight = 8
    private val scaleSpaceText = 8
    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = fontSize
        typeface = Typeface.DEFAULT_BOLD
    }
    private var text = "100米"

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapScale)
        fontSize = typedArray.getDimensionPixelSize(R.styleable.MapScale_font_size, 8.dp2px(context)).toFloat()
        scale = typedArray.getInt(R.styleable.MapScale_scale,100)
        calculateWidth()
        paint.textSize = fontSize
        typedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = getWidthSize(widthMeasureSpec)
        val heightSize = getHeightSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    private fun getWidthSize(widthMeasureSpec: Int): Int {
        return MeasureSpec.getSize(widthMeasureSpec)
    }

    private fun getHeightSize(heightMeasureSpec: Int): Int {
        return when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                fontSize.toInt() + scaleSpaceText + scaleHeight
            }
            MeasureSpec.EXACTLY -> {
                MeasureSpec.getSize(heightMeasureSpec)
            }
            MeasureSpec.UNSPECIFIED -> {
                max(
                    fontSize.toInt() + scaleSpaceText + scaleHeight,
                    MeasureSpec.getSize(heightMeasureSpec)
                )
            }
            else -> 0
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val textWidth: Float = paint.measureText(text)
        canvas.drawText(text, (scaleWidth - textWidth) / 2, fontSize, paint)
        val scaleRect = Rect(
            0,
            fontSize.toInt() + scaleSpaceText,
            scaleWidth,
            fontSize.toInt() + scaleSpaceText + scaleHeight
        )
        drawNinePatch(canvas, R.drawable.scale, scaleRect)
    }

    private fun drawNinePatch(canvas: Canvas, resId: Int, rect: Rect) {
        val bmp = BitmapFactory.decodeResource(resources, resId)
        val patch = NinePatch(bmp, bmp.ninePatchChunk, null)
        patch.draw(canvas, rect)
    }

    private fun getPPIOfDevice(): Double {
        val point = Point()
        val activity = context as Activity
        activity.windowManager.defaultDisplay.getRealSize(point)//获取屏幕的真实分辨率
        val dm = resources.displayMetrics
        val x = (point.x.toDouble() / dm.xdpi.toDouble()).pow(2.0)
        val y = (point.y.toDouble() / dm.ydpi.toDouble()).pow(2.0)
        val screenInches = sqrt(x + y)
        return sqrt(point.x.toDouble().pow(2.0) + point.y.toDouble().pow(2.0)) / screenInches
    }

    private fun refreshScaleView() {
        calculateWidth()
        invalidate()
    }

    private fun calculateWidth() {
        when (scale) {
            in 1..20 -> {
                text = "20米"
                scaleWidth = (20 * ppcm / scale).toInt() //264为ppi，264/2.54为1厘米的像素数
            }
            in 21..50 -> {
                text = "50米"
                scaleWidth = (50 * ppcm / scale).toInt()
            }
            in 51..100 -> {
                text = "100米"
                scaleWidth = (100 * ppcm / scale).toInt()
            }
            in 101..200 -> {
                text = "200米"
                scaleWidth = (200 * ppcm / scale).toInt()
            }
            in 201..500 -> {
                text = "500米"
                scaleWidth = (500 * ppcm / scale).toInt()
            }
            in 501..1000 -> {
                text = "1公里"
                scaleWidth = (1000 * ppcm / scale).toInt()
            }
            in 1001..2000 -> {
                text = "2公里"
                scaleWidth = (2000 * ppcm / scale).toInt()
            }
            in 2001..5000 -> {
                text = "5公里"
                scaleWidth = (5000 * ppcm / scale).toInt()
            }
            in 5001..10000 -> {
                text = "10公里"
                scaleWidth = (10000 * ppcm / scale).toInt()
            }
            in 10001..20000 -> {
                text = "20公里"
                scaleWidth = (20000 * ppcm / scale).toInt()
            }
            in 20001..25000 -> {
                text = "25公里"
                scaleWidth = (25000 * ppcm / scale).toInt()
            }
            in 25001..50000 -> {
                text = "50公里"
                scaleWidth = (50000 * ppcm / scale).toInt()
            }
            in 50001..100000 -> {
                text = "100公里"
                scaleWidth = (100000 * ppcm / scale).toInt()
            }
            in 100001..200000 -> {
                text = "200公里"
                scaleWidth = (200000 * ppcm / scale).toInt()
            }
            in 200001..250000 -> {
                text = "250公里"
                scaleWidth = (250000 * ppcm / scale).toInt()
            }
            in 250001..500000 -> {
                text = "500公里"
                scaleWidth = (500000 * ppcm / scale).toInt()
            }
            in 500001..scale -> {
                text = "1000公里"
                scaleWidth = (1000000 * ppcm / scale).toInt()
            }
        }
    }

    fun setScale(newScale:Int){
        scale = newScale
        refreshScaleView()
    }
}