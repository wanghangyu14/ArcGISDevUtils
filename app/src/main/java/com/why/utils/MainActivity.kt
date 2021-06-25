package com.why.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.why.util.DownloadStatus.*
import com.why.util.FileDownloader
import com.why.util.FileUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fd = FileDownloader(this)
        lifecycleScope.launch {
            fd.download(
                "https://unsplash.com/photos/hcEc0qmX2Ts/download?force=true",
                "123"
            )
                .collectLatest {
                    when (it) {
                        is DownloadSuccess -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                val builder = VmPolicy.Builder()
                                StrictMode.setVmPolicy(builder.build())
                            }
                           val intent = Intent(Intent.ACTION_VIEW).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                setDataAndType(it.uri, FileUtils.getMIMEType(it.filename))
                            }
                            startActivity(intent)
                        }
                        is DownloadProcess ->{
                            findViewById<ProgressBar>(R.id.progressBar).progress = it.process.toInt()
                        }
                        is DownloadError ->{
                            Log.d("1111","error!")
                        }
                    }
                }
        }
    }
}