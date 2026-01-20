package com.mc.gameengine.engine.math

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(decimals: Int): Float {
    val factor = 10f.pow(decimals)
    return try {
        (this * factor).roundToInt() / factor
    } catch (e: Exception){
        0f
    }
}

fun ClosedRange<Float>.random() = start + (endInclusive - start) * Math.random().toFloat()