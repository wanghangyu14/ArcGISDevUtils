package com.why.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File

class FileDownloader(private val context: Context) {
    private val service = ServiceCreator.create<DownloadService>()
    private val path =
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    private var uri: Uri? = null

    fun download(
        url: String,
        fileName: String = ""
    ) = flow {
        val response = service.downloadFile(url)
        if (response.isSuccessful) {
            response.body()?.let { body ->
                val realFilename = if (fileName == "") {
                    getFileName(response.headers(), body)
                } else {
                    "$fileName.${getFileType(body)}"
                }
                val contentLength = body.contentLength()
                val inputStream = BufferedInputStream(body.byteStream())
                val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.Downloads.DISPLAY_NAME, realFilename)
                    contentValues.put(
                        MediaStore.Downloads.MIME_TYPE,
                        FileUtils.getMIMEType(realFilename)
                    )
                    contentValues.put(
                        MediaStore.Downloads.DATE_TAKEN,
                        System.currentTimeMillis()
                    )
                    context.contentResolver.delete(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        MediaStore.Downloads.DISPLAY_NAME + "=?",
                        arrayOf(realFilename)
                    )
                    uri = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    context.contentResolver.openOutputStream(uri!!)
                } else {
                    val file = File("$path/$realFilename")
                    if (file.exists()) {
                        file.delete()
                    }
                    uri = Uri.fromFile(file)
                    file.outputStream()
                }
                inputStream.use { input ->
                    val buffer = ByteArray(1024 * 8)
                    var len: Int
                    var current = 0L
                    outputStream?.use { output ->
                        while (input.read(buffer).also { len = it } != -1) {
                            output.write(buffer, 0, len)
                            current += len
                            emit(
                                DownloadStatus.DownloadProcess(
                                    current,
                                    contentLength,
                                    current / contentLength.toFloat() * 100
                                )
                            )
                        }
                    }
                    emit(DownloadStatus.DownloadSuccess(uri, realFilename))
                }
            }
        } else {
            emit(DownloadStatus.DownloadError(RuntimeException("网络错误")))
        }
    }.flowOn(Dispatchers.IO)

    private fun getFileName(headers: Headers, body: ResponseBody): String {
        val contentDisposition = headers["content-disposition"]
        return contentDisposition?.split("\"")?.get(1)?.replace("\"", "")
            ?: "${System.currentTimeMillis()}.${getFileType(body)}"
    }

    private fun getFileType(body: ResponseBody): String? {
        val mimeType = "${body.contentType()?.type()}/${body.contentType()?.subtype()}"
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
}