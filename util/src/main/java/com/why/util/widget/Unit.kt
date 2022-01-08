package com.why.util.widget

sealed class LenUnit

sealed class SquareUnit

object Meter : LenUnit()

object KiloMeter : LenUnit()

object Li : LenUnit()

object SquareMeter : SquareUnit()

object SquareKiloMeter : SquareUnit()

object Mu : SquareUnit()