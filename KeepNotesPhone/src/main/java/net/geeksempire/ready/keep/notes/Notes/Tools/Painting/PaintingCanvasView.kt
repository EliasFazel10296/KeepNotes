/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import android.annotation.SuppressLint
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Notes.Restoring.RedrawSavedPaints
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingMove
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingStart
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingUp

@SuppressLint("ClickableViewAccessibility")
class PaintingCanvasView(val contextInstance: TakeNote) : View(contextInstance), View.OnTouchListener {

    var readyCanvas: Canvas? = null

    var drawPaint: Paint = Paint()

    var drawingPath: Path = Path()

    var movingX: Float = 0f
    var movingY: Float = 0f

    var movingRedrawX: Float = 0f
    var movingRedrawY: Float = 0f

    var touchTolerance: Float = 4f

    val allDrawingInformation = ArrayList<PaintingData>()

    val undoDrawingInformation = ArrayList<PaintingData>()

    var newPaintingData: NewPaintingData = NewPaintingData()

    val redrawSavedPaints: RedrawSavedPaints = RedrawSavedPaints(this@PaintingCanvasView)

    val overallRedrawPaintingData: ArrayList<ArrayList<RedrawPaintingData>> = ArrayList<ArrayList<RedrawPaintingData>>()

    var overallRedrawPaintingDataRedo: ArrayList<ArrayList<RedrawPaintingData>> = ArrayList<ArrayList<RedrawPaintingData>>()

    var allRedrawPaintingPathData: ArrayList<RedrawPaintingData> = ArrayList<RedrawPaintingData>()

    var fingerPaintingData = NewPaintingData()

    var stylusPaintingData = NewPaintingData()

    init {

        this@PaintingCanvasView.isFocusable = true
        this@PaintingCanvasView.isFocusableInTouchMode = true

        this@PaintingCanvasView.setOnTouchListener(this@PaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.BLUE, paintStrokeWidth: Float = 3.0f) = CoroutineScope(Dispatchers.Main).launch {

        drawPaint.color = paintColor
        drawPaint.strokeWidth = paintStrokeWidth

        drawPaint.isAntiAlias = true
        drawPaint.isDither = true

        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.MITER
        drawPaint.strokeCap = Paint.Cap.ROUND

        newPaintingData = NewPaintingData(paintColor, paintStrokeWidth)

        fingerPaintingData = NewPaintingData(paintColor, paintStrokeWidth)

        stylusPaintingData = NewPaintingData(paintColor, paintStrokeWidth)

    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {

            readyCanvas = canvas

            allDrawingInformation.forEachIndexed { index, paintingPathData ->


                canvas.drawPath(paintingPathData.path, paintingPathData.paint)

            }

            canvas.drawPath(drawingPath, drawPaint)

        }

    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        motionEvent?.let {

            val initialTouchX = (motionEvent.x)
            val initialTouchY = (motionEvent.y)

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                    when (motionEvent.getToolType(0)) {
                        MotionEvent.TOOL_TYPE_FINGER -> {
                            Log.d(this@PaintingCanvasView.javaClass.simpleName, "Finger Touch")

                            contextInstance.inputRecognizer.stylusDetected = false

                            newPaintingData = fingerPaintingData

                        }
                        MotionEvent.TOOL_TYPE_STYLUS -> {
                            Log.d(this@PaintingCanvasView.javaClass.simpleName, "Stylus Touch")

                            contextInstance.inputRecognizer.stylusDetected = true

                            newPaintingData = stylusPaintingData

                        }
                    }

                    touchingStart(initialTouchX, initialTouchY)

                }
                MotionEvent.ACTION_MOVE -> {

                    touchingMove(initialTouchX, initialTouchY)

                }
                MotionEvent.ACTION_UP -> {

                    touchingUp()

                }
            }

        }

        return true
    }

}