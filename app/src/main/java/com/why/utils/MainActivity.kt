package com.why.utils

import android.Manifest
import android.app.Activity
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(deniedList, "申请的权限是程序必须依赖的权限", "确定", "取消")
        }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消")
            }
            .request { _, _, _ -> }
        val digitalMapLayer =
            ArcGISTiledLayer("http://218.2.231.245/historyraster/rest/services/historyVector/js_sldt_grey/MapServer")
        val map = ArcGISMap()
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map
        m.bindMapView(mapview)
        btn.setOnClickListener {
            m.show()
        }

//        val fd = FileDownloader(this)
//        lifecycleScope.launch {
//            fd.download(
//                "https://unsplash.com/photos/hcEc0qmX2Ts/download?force=true",
//                "123"
//            )
//                .collectLatest {
//                    when (it) {
//                        is DownloadSuccess -> {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                val builder = VmPolicy.Builder()
//                                StrictMode.setVmPolicy(builder.build())
//                            }
//                           val intent = Intent(Intent.ACTION_VIEW).apply {
//                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                setDataAndType(it.uri, FileUtils.getMIMEType(it.filename))
//                            }
//                            startActivity(intent)
//                        }
//                        is DownloadProcess ->{
//                            findViewById<ProgressBar>(R.id.progressBar).progress = it.process.toInt()
//                        }
//                        is DownloadError ->{
//                            Log.d("1111","error!")
//                        }
//                    }
//                }
//        }
    }
}