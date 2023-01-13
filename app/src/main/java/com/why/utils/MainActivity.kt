package com.why.utils

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.why.arcgisdevutils.gis.Point
import com.why.arcgisdevutils.gis.spatialQuery
import com.why.arcgisdevutils.utils.showToast
import com.why.arcgisdevutils.widget.NavigateDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val digitalMapLayer =
            ArcGISTiledLayer("http://218.2.231.245/historyraster/rest/services/historyVector/js_sldt_grey/MapServer")

        val map = ArcGISMap()
        map.basemap.baseLayers.add(digitalMapLayer)
        mapview.map = map

        mapview.addMapScaleChangedListener {
            mapScale.setScale(mapview.mapScale.toInt() / 100)
        }
        mapController.bind(mapview)

        measureToolbox.setOnUnbindListener { view -> view.isVisible = false }

        bufferQueryToolbox.setOnUnbindListener { view -> view.isVisible = false }

        polygonQueryToolbox.setOnUnbindListener { view -> view.isVisible = false }

        measureToolbox.setOnBindListener { view -> view.isVisible = true }

        artBoard.setOnBindListener {view -> view.isVisible = true   }

        artBoard.setOnUnbindListener { view -> view.isVisible = false  }

        bufferQueryToolbox.setOnBindListener { view -> view.isVisible = true }

        polygonQueryToolbox.setOnBindListener { view -> view.isVisible = true }

        bufferQueryToolbox.setOnQueryResultListener { geometry ->
            lifecycleScope.launch(Dispatchers.IO) {
                spatialQuery(geometry, map)
            }
        }

        polygonQueryToolbox.setOnQueryResultListener { geometry ->
            lifecycleScope.launch(Dispatchers.IO) {
                spatialQuery(geometry, map)
            }
        }
        replaceFragment(R.id.container,LayerControllerFragment(mapview))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.measure -> {
                measureToolbox.bind(mapview)
                polygonQueryToolbox.unbind()
                bufferQueryToolbox.unbind()
                artBoard.unbind()
            }
            R.id.polygon -> {
                polygonQueryToolbox.bind(mapview)
                measureToolbox.unbind()
                bufferQueryToolbox.unbind()
                artBoard.unbind()
            }
            R.id.buffer -> {
                bufferQueryToolbox.bind(mapview)
                measureToolbox.unbind()
                polygonQueryToolbox.unbind()
                artBoard.unbind()
            }
            R.id.artBoard->{
                artBoard.bind(mapview)
                measureToolbox.unbind()
                polygonQueryToolbox.unbind()
                bufferQueryToolbox.unbind()
            }
            R.id.layer -> {
                drawerLayout.openDrawer(GravityCompat.END)
                replaceFragment(R.id.container,LayerControllerFragment(mapview))
            }
            R.id.navi -> {
                NavigateDialog(this)
                    .setDestination(Point(31.78507, 119.973146))
                    .show()
            }
            R.id.sortLayer->{
                drawerLayout.openDrawer(GravityCompat.END)
                replaceFragment(R.id.container,LayerSortFragment(mapview))
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu, menu)
        return true
    }

    override fun onResume() {
        mapview.resume()
        super.onResume()
    }

    override fun onPause() {
        mapview.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mapview.dispose()
        super.onDestroy()
    }

    private fun spatialQuery(
        geometry: Geometry,
        map: ArcGISMap
    ) {
        val result = spatialQuery(
            geometry,
            List(map.operationalLayers.size) { index -> map.operationalLayers[index] as FeatureLayer })
        runOnUiThread {
            result.toString().showToast(this@MainActivity)
        }
    }

    private fun replaceFragment(@IdRes containerID: Int, fragment: Fragment) {
        supportFragmentManager.commit {
            replace(containerID, fragment)
        }
    }
}