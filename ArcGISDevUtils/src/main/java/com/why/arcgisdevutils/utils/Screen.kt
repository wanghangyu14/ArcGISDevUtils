package com.why.arcgisdevutils.utils

import android.content.Context

fun Int.dp2px(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density + 0.5f).toInt()
}

fun Int.px2dp(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this / density + 0.5f).toInt()
}

fun Int.sp2px(context: Context): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (this * fontScale + 0.5f).toInt()
}

fun Int.px2sp(context: Context): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (this / fontScale + 0.5f).toInt()
}