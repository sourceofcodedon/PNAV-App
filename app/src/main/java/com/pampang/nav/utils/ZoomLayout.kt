package com.pampang.nav.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

class ZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var scaleFactor = 1f
    private val minScale = 1f
    private val maxScale = 4f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var posX = 0f
    private var posY = 0f

    private var onScaleChangedListener: OnScaleChangedListener? = null

    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

    interface OnScaleChangedListener {
        fun onScaleChanged(scaleFactor: Float)
    }

    fun setOnScaleChangedListener(listener: OnScaleChangedListener) {
        onScaleChangedListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        val action = event.action

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (scaleFactor > minScale && !scaleGestureDetector.isInProgress) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    posX += dx
                    posY += dy
                    clampTranslation()
                    applyTranslation()
                }
                lastTouchX = event.x
                lastTouchY = event.y
            }
        }
        return true
    }

    private fun clampTranslation() {
        val child = getChildAt(0) ?: return
        val maxPanX = (child.width * (scaleFactor - 1)) / 2
        val maxPanY = (child.height * (scaleFactor - 1)) / 2
        posX = max(-maxPanX, min(maxPanX, posX))
        posY = max(-maxPanY, min(maxPanY, posY))
    }

    private fun applyTranslation() {
        val child = getChildAt(0) ?: return
        child.translationX = posX
        child.translationY = posY
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val previousScaleFactor = scaleFactor
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(minScale, maxScale)

            if (scaleFactor == minScale && previousScaleFactor > minScale) {
                posX = 0f
                posY = 0f
                applyTranslation()
            } else if (scaleFactor > minScale) {
                clampTranslation()
                applyTranslation()
            }

            applyScale()
            onScaleChangedListener?.onScaleChanged(scaleFactor)
            return true
        }
    }

    private fun applyScale() {
        val child = getChildAt(0) ?: return
        child.scaleX = scaleFactor
        child.scaleY = scaleFactor
    }
}
