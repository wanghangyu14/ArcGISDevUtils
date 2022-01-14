package com.why.arcgisdevutils.gis

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.layers.FeatureLayer

fun spatialQuery(
    geom: Geometry,
    list: List<FeatureLayer>
): List<Pair<String, List<Feature>>> {
    val queryParameters = QueryParameters().apply {
        geometry = geom
    }
    val tables =
        List(list.size) { index -> list[index].featureTable as ServiceFeatureTable}
    return List(tables.size) { index ->
        val selected = tables[index].queryFeaturesAsync(
            queryParameters,
            ServiceFeatureTable.QueryFeatureFields.LOAD_ALL
        ).get().toList()
        list[index].selectFeatures(selected)
        list[index].name to selected
    }
}