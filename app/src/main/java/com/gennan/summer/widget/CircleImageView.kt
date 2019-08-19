package com.gennan.summer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView


/**
 *Created by Gennan on 2019/8/19.
 */
class CirCleImageView : ImageView {
     var clipPath = Path()
     var mRectF = RectF()

    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        mRectF.set(0f, 0f, width.toFloat(), height.toFloat())
        clipPath.reset()
        clipPath.addOval(mRectF, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }
}