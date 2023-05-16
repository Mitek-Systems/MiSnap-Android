package com.miteksystems.misnap.apputil.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// http://stackoverflow.com/questions/2537238/how-can-i-get-zoom-functionality-for-images
class TouchImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    var newMatrix = Matrix()
    var mode = NONE

    // Remember some things for zooming
    private var last = PointF()
    private var start = PointF()
    var minScale = 1f
    var maxScale = 3f
    var mArray: FloatArray
    var redundantXSpace = 0f
    var redundantYSpace = 0f
    var width = 0f
    var height = 0f
    var saveScale = 1f
    var right = 0f
    var bottom = 0f
    var origWidth = 0f
    var origHeight = 0f
    private var bmWidth = 0f
    private var bmHeight = 0f
    private var mScaleDetector: ScaleGestureDetector

    init {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        newMatrix.setTranslate(1f, 1f)
        mArray = FloatArray(9)
        imageMatrix = newMatrix
        scaleType = ScaleType.MATRIX
        setOnTouchListener { _, event ->
            mScaleDetector.onTouchEvent(event)
            newMatrix.getValues(mArray)
            val x = mArray[Matrix.MTRANS_X]
            val y = mArray[Matrix.MTRANS_Y]
            val curr = PointF(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    last[event.x] = event.y
                    start.set(last)
                    mode = DRAG
                }
                MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                    var deltaX = curr.x - last.x
                    var deltaY = curr.y - last.y
                    val scaleWidth = (origWidth * saveScale).roundToInt().toFloat()
                    val scaleHeight = (origHeight * saveScale).roundToInt().toFloat()
                    if (scaleWidth < width) {
                        deltaX = 0f
                        if (y + deltaY > 0) deltaY = -y else if (y + deltaY < -bottom) deltaY =
                            -(y + bottom)
                    } else if (scaleHeight < height) {
                        deltaY = 0f
                        if (x + deltaX > 0) deltaX = -x else if (x + deltaX < -right) deltaX =
                            -(x + right)
                    } else {
                        if (x + deltaX > 0) deltaX = -x else if (x + deltaX < -right) deltaX =
                            -(x + right)
                        if (y + deltaY > 0) deltaY = -y else if (y + deltaY < -bottom) deltaY =
                            -(y + bottom)
                    }
                    newMatrix.postTranslate(deltaX, deltaY)
                    last[curr.x] = curr.y
                }
                MotionEvent.ACTION_UP -> {
                    mode = NONE
                    val xDiff = abs(curr.x - start.x).toInt()
                    val yDiff = abs(curr.y - start.y).toInt()
                    if (xDiff < CLICK && yDiff < CLICK) performClick()
                }
                MotionEvent.ACTION_POINTER_UP -> mode =
                    NONE
            }
            imageMatrix = newMatrix
            invalidate()
            true // indicate event was handled
        }
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        bmWidth = bm.width.toFloat()
        bmHeight = bm.height.toFloat()
    }

    fun setMaxZoom(x: Float) {
        maxScale = x
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor =
                min(max(.95f, detector.scaleFactor).toDouble(), 1.05)
                    .toFloat()
            if (mScaleFactor > 1) {
                mScaleFactor += mScaleFactor * .10f
            } else if (mScaleFactor < 1) {
                mScaleFactor -= mScaleFactor * .10f
            }
            val origScale = saveScale
            saveScale *= mScaleFactor
            if (saveScale > maxScale) {
                saveScale = maxScale
                mScaleFactor = maxScale / origScale
            } else if (saveScale < minScale) {
                saveScale = minScale
                mScaleFactor = minScale / origScale
            }
            right = width * saveScale - width - 2 * redundantXSpace * saveScale
            bottom = height * saveScale - height - 2 * redundantYSpace * saveScale
            if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
                newMatrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2)
                if (mScaleFactor < 1) {
                    newMatrix.getValues(mArray)
                    val x = mArray[Matrix.MTRANS_X]
                    val y = mArray[Matrix.MTRANS_Y]
                    if (mScaleFactor < 1) {
                        if ((origWidth * saveScale).roundToInt() < width) {
                            if (y < -bottom) newMatrix.postTranslate(0f,
                                -(y + bottom)) else if (y > 0) newMatrix.postTranslate(0f, -y)
                        } else {
                            if (x < -right) newMatrix.postTranslate(-(x + right),
                                0f) else if (x > 0) newMatrix.postTranslate(-x, 0f)
                        }
                    }
                }
            } else {
                newMatrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
                newMatrix.getValues(mArray)
                val x = mArray[Matrix.MTRANS_X]
                val y = mArray[Matrix.MTRANS_Y]
                if (mScaleFactor < 1) {
                    if (x < -right) newMatrix.postTranslate(-(x + right),
                        0f) else if (x > 0) newMatrix.postTranslate(-x, 0f)
                    if (y < -bottom) newMatrix.postTranslate(0f,
                        -(y + bottom)) else if (y > 0) newMatrix.postTranslate(0f, -y)
                }
            }
            return true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        //Fit to screen.
        val scale: Float
        val scaleX = width / bmWidth
        val scaleY = height / bmHeight
        scale = min(scaleX, scaleY)
        newMatrix.setScale(scale, scale)
        imageMatrix = newMatrix
        saveScale = 1f

        // Center the image
        redundantYSpace = height - scale * bmHeight
        redundantXSpace = width - scale * bmWidth
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        newMatrix.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = width - 2 * redundantXSpace
        origHeight = height - 2 * redundantYSpace
        right = width * saveScale - width - 2 * redundantXSpace * saveScale
        bottom = height * saveScale - height - 2 * redundantYSpace * saveScale
        imageMatrix = newMatrix
    }

    companion object {
        // We can be in one of these 3 states
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val CLICK = 3
    }
}
