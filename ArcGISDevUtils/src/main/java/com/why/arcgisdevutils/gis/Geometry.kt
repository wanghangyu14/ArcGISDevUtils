package com.why.arcgisdevutils.gis

import com.esri.arcgisruntime.geometry.*


fun calculateLength(
    list: List<com.esri.arcgisruntime.geometry.Point>,
    unit: LinearUnitId,
    type: GeodeticCurveType = GeodeticCurveType.GEODESIC
) = GeometryEngine.lengthGeodetic(
    Polyline(PointCollection(list)),
    LinearUnit(unit),
    type
)


fun calculateArea(
    list: List<com.esri.arcgisruntime.geometry.Point>,
    unit: AreaUnitId,
    type: GeodeticCurveType = GeodeticCurveType.GEODESIC
) = GeometryEngine.areaGeodetic(
    Polygon(PointCollection(list)),
    AreaUnit(unit),
    type
)