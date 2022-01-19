package com.why.utils

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.permissionx.guolindev.PermissionX
import com.why.arcgisdevutils.gis.Point
import com.why.arcgisdevutils.gis.spatialQuery
import com.why.arcgisdevutils.utils.showToast
import com.why.arcgisdevutils.widget.NavigateDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.right_navigation_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        val digitalMapLayer =
            ArcGISTiledLayer("http://218.2.231.245/historyraster/rest/services/historyVector/js_sldt_grey/MapServer")

        val map = ArcGISMap()
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map

        mapview.addMapScaleChangedListener {
            mapScale.setScale(mapview.mapScale.toInt() / 100)
        }
        mapController.bind(mapview)

        layerController.bind(mapview).setLayers(
            mapOf(
                "高架路网" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/27")),
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/28"))
                ),
                "高架下道路路网" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/29")),
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/30"))
                ),
                "匝道" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/23")),
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/24"))
                ),
                "互通" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/25")),
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/26"))
                ),
                "路线坐标" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://58.216.48.11:6080/arcgis/rest/services/CZ_Vector/MapServer/22"))
                ),
                "道路绿化" to listOf(
                    FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/37"))
                )
            )
        )

        measureToolbox.setOnUnbindListener { view -> view.isVisible = false }

        bufferQueryToolbox.setOnUnbindListener { view -> view.isVisible = false }

        polygonQueryToolbox.setOnUnbindListener { view -> view.isVisible = false }

        measureToolbox.setOnBindListener { view -> view.isVisible = true }

        bufferQueryToolbox.setOnBindListener { view -> view.isVisible = true }

        polygonQueryToolbox.setOnBindListener { view -> view.isVisible = true }

        bufferQueryToolbox.setOnQueryResultListener { geometry ->
            lifecycleScope.launch(Dispatchers.IO) {
                val result = spatialQuery(
                    geometry,
                    List(map.operationalLayers.size) { index -> map.operationalLayers[index] as FeatureLayer })
                runOnUiThread {
                    result.toString().showToast(this@MainActivity)
                }
            }
        }

        polygonQueryToolbox.setOnQueryResultListener { geometry ->
            val result = spatialQuery(
                geometry,
                List(map.operationalLayers.size) { index -> map.operationalLayers[index] as FeatureLayer })
            runOnUiThread {
                result.toString().showToast(this@MainActivity)
            }
        }
    }

    private fun requestPermissions() {
        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "申请的权限是程序必须依赖的权限", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消")
            }
            .request { _, _, _ -> }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.measure -> {
                measureToolbox.bind(mapview)
                polygonQueryToolbox.unbind()
                bufferQueryToolbox.unbind()
            }
            R.id.polygon -> {
                polygonQueryToolbox.bind(mapview)
                measureToolbox.unbind()
                bufferQueryToolbox.unbind()
            }
            R.id.buffer -> {
                bufferQueryToolbox.bind(mapview)
                measureToolbox.unbind()
                polygonQueryToolbox.unbind()
            }
            R.id.layer -> {
                drawerLayout.openDrawer(GravityCompat.END)
            }
            R.id.navi -> {
                NavigateDialog(this)
                    .setDestination(Point(31.78507, 119.973146))
                    .show()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu, menu)
        return true
    }
}