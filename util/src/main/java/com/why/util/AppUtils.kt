package com.why.util

import android.content.Context

fun Context.isApplicationAvailable(appPackageName: String): Boolean {
    val packageManager = packageManager
    val packageInfo =
        packageManager.getInstalledPackages(0)
    for (i in packageInfo.indices) {
        val pn = packageInfo[i].packageName
        if (appPackageName == pn) {
            return true
        }
    }
    return false
}