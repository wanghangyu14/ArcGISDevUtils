package com.why.utils

import androidx.lifecycle.ViewModel
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import com.why.arcgisdevutils.widget.model.LayerOption

class MainViewModel:ViewModel() {
    val selectedLayerOptions = mutableListOf<LayerOption>()
    val layerOptions = listOf(
        LayerOption(
            "高架路网",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/27")),
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/28"))
            ),
            isSelected = true
        ),
        LayerOption(
            "高架下道路路网",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/29")),
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/30"))
            )
        ),
        LayerOption(
            "匝道",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/23")),
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/24"))
            ),
            isSelected = true
        ),
        LayerOption(
            "互通",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/25")),
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/26"))
            )
        ),
        LayerOption(
            "路线坐标",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://58.216.48.11:6080/arcgis/rest/services/CZ_Vector/MapServer/22"))
            )
        ),
        LayerOption(
            "道路绿化",
            listOf(
                FeatureLayer(ServiceFeatureTable("http://www.czch.com.cn:6080/arcgis/rest/services/SZGJ/MapServer/37"))
            )
        )
    )
    init {
        selectedLayerOptions.add(layerOptions[0])
        selectedLayerOptions.add(layerOptions[2])
    }
}