package com.why.util.widget

internal sealed class LenUnit

internal sealed class SquareUnit

internal object Meter : LenUnit()

internal object KiloMeter : LenUnit()

internal object Li : LenUnit()

internal object SquareMeter : SquareUnit()

internal object SquareKiloMeter : SquareUnit()

internal object Mu : SquareUnit()