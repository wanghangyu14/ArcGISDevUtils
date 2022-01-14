package com.why.utils

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "申请的权限是程序必须依赖的权限", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消")
            }
            .request { _, _, _ -> }
        val digitalMapLayer =
            ArcGISTiledLayer("http://218.2.231.245/historyraster/rest/services/historyVector/js_sldt_grey/MapServer")

        val f = FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/27"))

        val map = ArcGISMap()
        map.operationalLayers.add(f)
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map

        mapview.addMapScaleChangedListener {
            mapscale.setScale(mapview.mapScale.toInt()/100)
        }

//        measure.bind(mapview)
//        btn.setOnClickListener {
//            if(!polygonQuery.isBind()){
//                polygonQuery.bind(mapview)
//            }else{
//                polygonQuery.unbind()
//            }
//        }
//        polygonQuery.setOnQueryResultListener { list ->
//            list.toString().showToast(this)
//        }
        mc.bind(mapview)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.measure->{
                measure.bind(mapview)
                polygonQuery.unbind()
                bufferQueryToolbox.unbind()
                measure.isVisible = true
                polygonQuery.isVisible = false
                bufferQueryToolbox.isVisible = false
            }
            R.id.polygon->{
                polygonQuery.bind(mapview)
                measure.unbind()
                bufferQueryToolbox.unbind()
                measure.isVisible = false
                polygonQuery.isVisible = true
                bufferQueryToolbox.isVisible = false
            }
            R.id.buffer->{
                bufferQueryToolbox.bind(mapview)
                measure.unbind()
                polygonQuery.unbind()
                measure.isVisible = false
                polygonQuery.isVisible = false
                bufferQueryToolbox.isVisible = true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}