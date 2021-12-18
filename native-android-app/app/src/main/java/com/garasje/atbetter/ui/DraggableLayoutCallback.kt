package com.garasje.atbetter.ui

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper
import com.garasje.atbetter.R
import com.garasje.atbetter.helpers.DisplayHelpers


class DraggableLayoutCallback(private val parent: DraggableConstraintLayout) :
    ViewDragHelper.Callback() {

    private var minimized = false

    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
        return child.visibility == ConstraintLayout.VISIBLE && parent.viewIsDraggableChild(child)
    }

    private fun updateOpen(yVel: Float, child: View) {
        minimized = when {
            yVel > 0 -> {
                true
            }
            yVel < 0 -> {
                false
            }
            else -> {
                child.top > 1000
            }
        }
    }

    private fun snapToPlace() {
        if (minimized) {
            parent.viewDragHelper.settleCapturedViewAt(
                0,
                parent.height - parent.paddingBottom + DisplayHelpers.dimenInPx(
                    R.dimen.minimized_drawer_margin,
                    parent.context
                )
            )
        } else {
            parent.viewDragHelper.settleCapturedViewAt(parent.paddingLeft, parent.paddingTop)
        }
        parent.invalidate()
    }

    override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
        updateOpen(yvel, releasedChild)
        snapToPlace()
    }


    override fun getViewVerticalDragRange(child: View): Int {
        return child.height
    }

    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
        return top
    }
}