package com.obstacles.hotline.model

import android.animation.ObjectAnimator
import android.content.Context
import com.obstacles.hotline.util.Direction

class Line(context: Context) : androidx.appcompat.widget.AppCompatImageView(context) {
    var lineLocation = IntArray(2)
    var lineAnimator: ObjectAnimator? = null
    var direction: Direction = Direction.RIGHT
    var duration: Long = 0L
}