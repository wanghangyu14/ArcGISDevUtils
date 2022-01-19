package com.why.arcgisdevutils.widget.model

import com.esri.arcgisruntime.layers.Layer


data class LayerOption(
    val name: String,
    val layers: List<Layer>,
    var opacity: Int = 100,
    var isSelected: Boolean = false
)