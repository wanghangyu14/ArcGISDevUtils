package com.why.arcgisdevutils.gis

import kotlin.math.*


const val pi = 3.1415926535897932384626
const val a = 6378245.0
const val ee = 0.00669342162296594323


fun transformLat(x: Double, y: Double): Double {
    var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x))
    ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0
    ret += (20.0 * sin(y * pi) + 40.0 * sin(y / 3.0 * pi)) * 2.0 / 3.0
    ret += (160.0 * sin(y / 12.0 * pi) + 320 * sin(y * pi / 30.0)) * 2.0 / 3.0
    return ret
}

fun transformLon(x: Double, y: Double): Double {
    var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + (0.1
            * sqrt(abs(x)))
    ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0
    ret += (20.0 * sin(x * pi) + 40.0 * sin(x / 3.0 * pi)) * 2.0 / 3.0
    ret += (150.0 * sin(x / 12.0 * pi) + 300.0 * sin(
        x / 30.0
                * pi
    )) * 2.0 / 3.0
    return ret
}

fun transform(lat: Double, lon: Double): Point {
    var dLat = transformLat(lon - 105.0, lat - 35.0)
    var dLon = transformLon(lon - 105.0, lat - 35.0)
    val radLat = lat / 180.0 * pi
    var magic = sin(radLat)
    magic = 1 - ee * magic * magic
    val sqrtMagic = sqrt(magic)
    dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
    dLon = dLon * 180.0 / (a / sqrtMagic * cos(radLat) * pi)
    val mgLat = lat + dLat
    val mgLon = lon + dLon
    return Point(mgLat, mgLon)
}

fun wgs84ToGcj02(lat: Double, lon: Double): Point {
    var dLat = transformLat(lon - 105.0, lat - 35.0)
    var dLon = transformLon(lon - 105.0, lat - 35.0)
    val radLat = lat / 180.0 * pi
    var magic = sin(radLat)
    magic = 1 - ee * magic * magic
    val sqrtMagic = sqrt(magic)
    dLat =
        dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
    dLon = dLon * 180.0 / (a / sqrtMagic * cos(radLat) * pi)
    val mgLat = lat + dLat
    val mgLon = lon + dLon
    return Point(mgLat, mgLon)
}

fun gcj02ToWgs84(lat: Double, lon: Double): Point {
    val point = transform(lat, lon)
    val longitude: Double = lon * 2 - point.longitude
    val latitude: Double = lat * 2 - point.latitude
    return Point(latitude, longitude)
}

fun gcj02ToBd09(lat: Double, lon: Double): Point {
    val z = sqrt(lon * lon + lat * lat) + 0.00002 * sin(
        lat * pi
    )
    val theta = atan2(lat, lon) + 0.000003 * cos(lon * pi)
    val bdLon = z * cos(theta) + 0.0065
    val bdLat = z * sin(theta) + 0.006
    return Point(bdLat, bdLon)
}

fun bd09ToGcj02(lat: Double, lon: Double): Point {
    val x = lon - 0.0065
    val y = lat - 0.006
    val z = sqrt(x * x + y * y) - 0.00002 * sin(y * pi)
    val theta = atan2(y, x) - 0.000003 * cos(x * pi)
    val gcjLon = z * cos(theta)
    val gcjLat = z * sin(theta)
    return Point(gcjLat, gcjLon)
}

fun bd09ToWgs84(lat: Double, lon: Double): Point {
    val gcj02 = bd09ToGcj02(lat, lon)
    return gcj02ToWgs84(
        gcj02.latitude,
        gcj02.longitude
    )
}


fun wgs84ToBd09(lat: Double, lon: Double): Point {
    val gcj02 = wgs84ToGcj02(lat, lon)
    return gcj02ToBd09(gcj02.latitude, gcj02.longitude)
}

fun wgs84ToWebMercator(lon: Double, lat: Double): Point {
    val x = lon * 20037508.34 / 180.0
    var y =
        log(tan((90.0 + lat) * pi / 360.0), Math.E) / (pi / 180.0)
    y = y * 20037508.34 / 180.0
    return Point(x, y)
}

fun webMercatorToWgs84(lat: Double, lon: Double): Point {
    val x = lon / 20037508.34 * 180.0
    var y = lat / 20037508.34 * 180.0
    y =
        180 / pi * (2 * atan(exp(y * pi / 180.0)) - pi / 2)
    return Point(y, x)
}