package com.why.utils

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.permissionx.guolindev.PermissionX
import com.why.arcgisdevutils.gis.spatialQuery
import com.why.arcgisdevutils.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

        val featureLayer = FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/27"))

        val map = ArcGISMap()
        map.operationalLayers.add(featureLayer)
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map

        mapview.addMapScaleChangedListener {
            mapScale.setScale(mapview.mapScale.toInt()/100)
        }
        mapController.bind(mapview)

        bufferQueryToolbox.setOnQueryResultListener {geometry->
            lifecycleScope.launch(Dispatchers.IO){
                val result = spatialQuery(geometry, List(map.operationalLayers.size){index -> map.operationalLayers[index] as FeatureLayer })
                runOnUiThread {
                    result.toString().showToast(this@MainActivity)
                }
            }
        }

        polygonQueryToolbox.setOnQueryResultListener {geometry->
            val result = spatialQuery(geometry, List(map.operationalLayers.size){index -> map.operationalLayers[index] as FeatureLayer })
            runOnUiThread {
                result.toString().showToast(this@MainActivity)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.measure->{
                measure.bind(mapview)
                polygonQueryToolbox.unbind()
                bufferQueryToolbox.unbind()
                measure.isVisible = true
                polygonQueryToolbox.isVisible = false
                bufferQueryToolbox.isVisible = false
            }
            R.id.polygon->{
                polygonQueryToolbox.bind(mapview)
                measure.unbind()
                bufferQueryToolbox.unbind()
                measure.isVisible = false
                polygonQueryToolbox.isVisible = true
                bufferQueryToolbox.isVisible = false
            }
            R.id.buffer->{
                bufferQueryToolbox.bind(mapview)
                measure.unbind()
                polygonQueryToolbox.unbind()
                measure.isVisible = false
                polygonQueryToolbox.isVisible = false
                bufferQueryToolbox.isVisible = true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu,menu)
        return true
    }
}