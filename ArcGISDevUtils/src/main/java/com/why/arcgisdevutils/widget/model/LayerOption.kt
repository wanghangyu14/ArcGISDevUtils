package com.why.arcgisdevutils.widget.model

import com.esri.arcgisruntime.layers.Layer


data class LayerOption(
    val name: String,
    val layers: List<Layer>,
    var opacity: Int = 100,
    var isSelected: Boolean = false,
    val canQuery:Boolean = false,
    val category:String,
    val indices:List<Int>,
    val fieldMap:List<List<String>>,
    val legend:String
)