package com.garasje.atbetter.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

class DraggableConstraintLayout(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    var viewDragHelper: ViewDragHelper
        private set

    private var drawer: View? = null
    private val callback: DraggableLayoutCallback = DraggableLayoutCallback(this)

    fun setDrawer(child: View) {
        require(child.parent === this)
        drawer = child
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }


    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun viewIsDraggableChild(view: View): Boolean {
        return drawer == view
    }


    init {
        viewDragHelper = ViewDragHelper.create(this, callback)
        viewDragHelper.minVelocity = 1000F
    }

}