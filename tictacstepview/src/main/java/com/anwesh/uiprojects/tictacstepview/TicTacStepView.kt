package com.anwesh.uiprojects.tictacstepview

/**
 * Created by anweshmishra on 16/11/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity

val nodes : Int = 5
val lines : Int = 2
val parts : Int = 2
val scGap : Float = 0.1f / parts
val scDiv : Double = 1.0 / parts
val STROKE_FACTOR : Int = 60
val SIZE_FACTOR : Int = 3

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse())) * n

fun Float.getScaleFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.getMirrorValue(a : Int, b : Int) : Float = this * b.getInverse()  + (1 - this) * a.getInverse()

fun Float.updateScale(a : Int, b : Int, dir : Float) : Float = dir * scGap * getScaleFactor().getMirrorValue(a, b)