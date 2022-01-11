package com.why.util

import android.content.Context
import android.content.Intent
import android.net.Uri

//高德地图使用的坐标系是gcj02
fun Context.navigateWithGD(start: Point? = null, destination: Point) {
    if (isApplicationAvailable("com.autonavi.minimap")) {
        val uriStr = if (start == null) {
            "amapuri://route/plan/?dlat=${destination.latitude}&dlon=${destination.longitude}&dev=0&t=0"
        } else {
            "amapuri://route/plan/?slat=${start.latitude}&slon=${start.longitude}&dlat=${destination.latitude}&dlon=${destination.longitude}&dev=0&t=0"
        }
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                uriStr
            )
        ).apply {
            setPackage("com.autonavi.minimap")
        }
        startActivity(intent)
    } else {
        "请先安装高德地图".showToast(this)
    }
}

//腾讯地图使用的坐标系是gcj02
fun Context.navigateWithTX(start: Point?=null, destination: Point) {
    if (isApplicationAvailable("com.tencent.map")) {
        val uriStr = if (start == null) {
            "qqmap://map/routeplan?type=drive&fromcoord=CurrentLocation&tocoord=${destination.latitude},${destination.longitude}&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77"
        } else {
            "qqmap://map/routeplan?type=drive&fromcoord=${start.latitude},${start.longitude}&tocoord=${destination.latitude},${destination.longitude}&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77"
        }
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                uriStr
            )
        ).apply {
            setPackage("com.tencent.map")
        }
        startActivity(intent)
    } else {
        "请先安装腾讯地图".showToast(this)
    }
}

//百度地图默认的坐标系是bd09ll这里指定为gcj02坐标s
fun Context.navigateWithBD(start: Point?=null, destination: Point){
    if(isApplicationAvailable("com.baidu.BaiduMap")){
        val uriStr = if (start == null) {
            "baidumap://map/direction?destination=${destination.latitude},${destination.longitude}&coord_type=gcj02&src=andr.baidu.openAPIdemo"
        }else{
            "baidumap://map/direction?origin=latlng:${start.latitude},${start.longitude}&destination=${destination.latitude},${destination.longitude}&coord_type=gcj02&src=andr.baidu.openAPIdemo"
        }
        val intent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(
                uriStr
            )
        ).apply {
            setPackage("com.baidu.BaiduMap")
        }
        startActivity(intent)
    }else{
        "请先安装百度地图".showToast(this)
    }
}