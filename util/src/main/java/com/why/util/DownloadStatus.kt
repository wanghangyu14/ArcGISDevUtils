package com.why.util

import android.net.Uri

sealed class DownloadStatus {
    class DownloadProcess(val currentLength: Long, val length: Long, val process: Float) :DownloadStatus()
    class DownloadError(val t: Throwable) : DownloadStatus()
    class DownloadSuccess(val uri: Uri?,val filename:String) : DownloadStatus()
}