package com.why.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.util.widget.MapScale


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapview = findViewById<MapView>(R.id.mapview)
        val scale = findViewById<MapScale>(R.id.scale)
        val digitalMapLayer =
            ArcGISTiledLayer("http://58.216.48.11:6080/arcgis/rest/services/CZ_Vector/MapServer")
        val map = ArcGISMap()
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map
        mapview.addMapScaleChangedListener {
            scale.setScale(it.source.mapScale.toInt()/100)
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