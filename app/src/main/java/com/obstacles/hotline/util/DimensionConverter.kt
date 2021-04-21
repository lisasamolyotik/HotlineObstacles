package com.obstacles.hotline.util

import android.content.Context
import android.util.TypedValue

class DimensionConverter {
    companion object {
        fun dpToPixels(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics
            ).toInt()
        }

        fun pixelsToDp(pixels: Int, context: Context): Int {
            return (pixels / context.resources.displayMetrics.density).toInt()
        }
    }
}