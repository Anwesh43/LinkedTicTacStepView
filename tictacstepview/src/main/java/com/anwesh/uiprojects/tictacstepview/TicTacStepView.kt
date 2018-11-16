package com.anwesh.uiprojects.tictacstepview

/**
 * Created by anweshmishra on 16/11/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.*
import android.util.Log

val nodes : Int = 5
val lines : Int = 2
val parts : Int = 2
val scGap : Float = 0.1f / parts
val scDiv : Double = 1.0 / parts
val STROKE_FACTOR : Int = 60
val SIZE_FACTOR : Int = 3
val TIC_TAC_PARTS : Int = 3

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse())) * n

fun Float.getScaleFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.getMirrorValue(a : Int, b : Int) : Float = this * b.getInverse()  + (1 - this) * a.getInverse()

fun Float.updateScale(a : Int, b : Int, dir : Float) : Float = dir * scGap * getScaleFactor().getMirrorValue(a, b)

fun Int.getCenterPoint(size : Float) : PointF = PointF(size/2 + size * this % 3, size/2 + size * (this / 3))

fun Int.getSquare() : Int = this * this

fun Canvas.drawLines(size : Float, scale : Float, paint : Paint) {
    val gap : Float = 2 * size / TIC_TAC_PARTS
    val x : Float = -size + gap
    val y : Float = -size
    for (j in 0..(lines - 1)) {
        val sc : Float = scale.divideScale(j, lines)
        save()
        rotate(90f * j)
        for (k in 0..1) {
            drawLine(x + size * k, -size, x + size * k, -size + 2 * size * sc, paint)
        }
        restore()
    }
}

fun Canvas.drawTicTac(size : Float, scale : Float, paint : Paint) {
    val gap : Float = 2 * size / (TIC_TAC_PARTS)
    save()
    translate(-size, -size)
    for (j in 0..(TIC_TAC_PARTS.getSquare() - 1)) {
        val x : Float = j.getCenterPoint(gap).x
        val y : Float = j.getCenterPoint(gap).y
        val sc : Float = scale.divideScale(j, TIC_TAC_PARTS.getSquare())
        save()
        translate(x, y)
        if (j % 2 == 0) {
            for (k in 0..1) {
                val cSize : Float = (size / 4) * sc
                save()
                rotate(45f + 90f * k)
                drawLine(0f, -cSize, 0f, cSize, paint)
                restore()
            }
        } else {
            drawArc(RectF(-size/4, -size/4, size/4, size/4),
                    -90f, 360f * sc, false, paint)
        }
        restore()
    }
    restore()
}

fun Canvas.drawTTSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / SIZE_FACTOR
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.strokeWidth = Math.min(w, h) / STROKE_FACTOR
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4A148C")
    save()
    translate(gap * (i + 1), h / 2)
    drawLines(size, sc1, paint)
    drawTicTac(size, sc2, paint)
    restore()
}

class TicTacStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            val k : Float = scale.updateScale(lines, TIC_TAC_PARTS.getSquare(), dir)
            scale += k
            Log.d("debug mode:", "$k")
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TTSNode(var i : Int, val state : State = State()) {

        private var next : TTSNode? = null

        private var prev : TTSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TTSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTTSNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TTSNode {
            var curr : TTSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}