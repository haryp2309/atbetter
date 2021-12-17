package com.garasje.atbetter.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper

class DraggableConstraintLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    interface ViewDragListener {
        fun onViewCaptured(view: View)
        fun onViewReleased(view: View)
    }

    private var viewDragHelper: ViewDragHelper
    private val draggableChildren: MutableCollection<View> = ArrayList()
    private var viewDragListener: ViewDragListener? = null

    fun addDraggableChild(child: View) {
        require(child.parent === this)
        draggableChildren.add(child)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun viewIsDraggableChild(view: View): Boolean {
        return draggableChildren.isEmpty() || draggableChildren.contains(view)
    }

    fun setViewDragListener(viewDragListener: ViewDragListener) {
        this.viewDragListener = viewDragListener
    }

    init {
        viewDragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child.visibility == VISIBLE && viewIsDraggableChild(child)
            }

            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                viewDragListener?.onViewCaptured(capturedChild)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                viewDragListener?.onViewReleased(releasedChild)
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return child.height
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }
        })

    }

}