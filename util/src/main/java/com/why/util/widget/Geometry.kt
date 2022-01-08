package com.why.util.widget

import com.esri.arcgisruntime.geometry.*

fun calculateLength(list: List<Point>, unit: LenUnit) = when (unit) {
    Meter -> GeometryEngine.lengthGeodetic(
        Polyline(PointCollection(list)),
        LinearUnit(LinearUnitId.METERS),
        GeodeticCurveType.GEODESIC
    )
    KiloMeter -> GeometryEngine.lengthGeodetic(
        Polyline(PointCollection(list)),
        LinearUnit(LinearUnitId.KILOMETERS),
        GeodeticCurveType.GEODESIC
    )
    Li -> GeometryEngine.lengthGeodetic(
        Polyline(PointCollection(list)),
        LinearUnit(LinearUnitId.METERS),
        GeodeticCurveType.GEODESIC
    ) / 500
}


fun calculateArea(list: List<Point>, unit: SquareUnit) = when (unit) {
    SquareMeter -> GeometryEngine.areaGeodetic(
        Polygon(PointCollection(list)),
        AreaUnit(AreaUnitId.SQUARE_METERS),
        GeodeticCurveType.GEODESIC
    )
    SquareKiloMeter -> GeometryEngine.areaGeodetic(
        Polygon(PointCollection(list)),
        AreaUnit(AreaUnitId.SQUARE_KILOMETERS),
        GeodeticCurveType.GEODESIC
    )
    Mu -> GeometryEngine.areaGeodetic(
        Polygon(PointCollection(list)),
        AreaUnit(AreaUnitId.SQUARE_METERS),
        GeodeticCurveType.GEODESIC
    ) / 667.67
}