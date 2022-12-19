/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.menu2.ext

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.widget.PopupWindowCompat
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.browser.menu2.R
import mozilla.components.concept.menu.MenuStyle
import mozilla.components.concept.menu.Orientation
import mozilla.components.support.ktx.android.util.dpToPx
import mozilla.components.support.ktx.android.view.isRTL

internal fun PopupWindow.displayPopup(
    containerView: View,
    anchor: View,
    preferredOrientation: Orientation? = null,
    forceOrientation: Boolean = false,
    style: MenuStyle? = null,
) {
//    // Measure menu
//    val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//    containerView.measure(spec, spec)
//
//    val (availableHeightToTop, availableHeightToBottom) = getMaxAvailableHeightToTopAndBottom(anchor)
//    val (availableWidthToLeft, availableWidthToRight) = getMaxAvailableWidthToLeftAndRight(anchor)
//    val containerHeight = containerView.measuredHeight

//    val searchIcon = anchor.findViewById<CardView>(R.id.mozac_browser_menu_cardView)
//    val searchIconWidth = searchIcon.width
//    println(searchIcon.toString() + searchIconWidth)

//    val recyclerView = containerView.findViewById<RecyclerView>(R.id.mozac_browser_menu_recyclerView)
//    val horizontalPadding = containerView.measuredWidth - recyclerView.measuredWidth
//    val verticalPadding = containerHeight - recyclerView.measuredHeight
//
//    val fitsUp = availableHeightToTop >= containerHeight
//    val fitsDown = availableHeightToBottom >= containerHeight
//    val reversed = availableWidthToLeft < availableWidthToRight

    val menuPositioningData = inferMenuPositioningData(
        containerView,
        anchor,
        style
    )

    // Popup window does not need a input method. This avoids keyboard flicker when menu is opened.
    inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED

    // Try to use the preferred orientation, if doesn't fit fallback to the best fit.
    when {
        preferredOrientation == Orientation.DOWN && (menuPositioningData.fitsDown || forceOrientation) ->
            showPopupWithDownOrientation(
                anchor,
                menuPositioningData
            )
        preferredOrientation == Orientation.UP && (menuPositioningData.fitsUp || forceOrientation) ->
            showPopupWithUpOrientation(
                anchor,
                menuPositioningData
            )
        else -> {
            showPopupWhereBestFits(
                anchor,
                menuPositioningData
            )
        }
    }
}

@Suppress("LongParameterList")
private fun PopupWindow.showPopupWhereBestFits(
    anchor: View,
    menuPositioningData: MenuPositioningData,
) {
    with(menuPositioningData) {
        when {
            !fitsUp && !fitsDown -> showAtAnchorLocation(anchor, this)
            fitsDown -> showPopupWithDownOrientation(anchor, this)
            else -> showPopupWithUpOrientation(anchor, this)
        }
    }
//    when {
//
//        // Not enough space to show the menu UP neither DOWN.
//        // Let's just show the popup at the location of the anchor.
//        !fitsUp && !fitsDown -> showAtAnchorLocation(
//            anchor,
//            reversed,
//            horizontalPadding,
//            verticalPadding,
//            horizontalOffset,
//            verticalOffset,
//        )
//        // Enough space to show menu down
//        fitsDown -> showPopupWithDownOrientation(
//            anchor,
//            reversed,
//            horizontalPadding,
//            verticalPadding,
//            horizontalOffset,
//            verticalOffset,
//        )
//        // Otherwise, show menu up
//        else -> showPopupWithUpOrientation(
//            anchor,
//            containerHeight,
//            reversed,
//            horizontalPadding,
//            verticalPadding,
//            horizontalOffset,
//            verticalOffset,
//        )
//    }
}

private fun PopupWindow.showPopupWithUpOrientation(
    anchor: View,
    menuPositioningData: MenuPositioningData,
) {
    animationStyle = if (menuPositioningData.reversed) {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuLeftBottom
    } else {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuRightBottom
    }
//    val xOffset = (horizontalPadding / 2) + horizontalOffset.dpToPx(anchor.resources.displayMetrics)
//    val yOffset = containerHeight - (verticalPadding / 2) - verticalOffset.dpToPx(anchor.resources.displayMetrics)
    showAsDropDown(anchor, menuPositioningData.horizontalOffset, -menuPositioningData.containerHeight - menuPositioningData.verticalOffset)

//    val yOffset = containerHeight - anchor.height
//    val xOffset = anchor.width - (8.dpToPx(anchor.context.resources.displayMetrics))
//    showAsDropDown(anchor, -xOffset, -yOffset)
}

private fun PopupWindow.showPopupWithDownOrientation(
    anchor: View,
    menuPositioningData: MenuPositioningData
) {
    // Apply the best fit animation style based on positioning
    animationStyle = if (menuPositioningData.reversed) {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuLeftTop
    } else {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuRightTop
    }
//  original
//    PopupWindowCompat.setOverlapAnchor(this, true)
//    showAsDropDown(anchor)



//    val xOffset = (horizontalPadding / 2) + horizontalOffset.dpToPx(anchor.resources.displayMetrics)
//    val yOffset = (verticalPadding / 2) + verticalOffset.dpToPx(anchor.resources.displayMetrics)
    PopupWindowCompat.setOverlapAnchor(this, true)
    showAsDropDown(anchor, menuPositioningData.horizontalOffset, menuPositioningData.verticalOffset)

//    println(horizontalPadding + verticalPadding)
//    PopupWindowCompat.setOverlapAnchor(this, true)
//    showAsDropDown(anchor)

//    val xOffset = -((horizontalPadding / 2) + anchor.marginStart)
//    val yOffset = -((verticalPadding / 2) + anchor.marginTop + anchor.marginBottom)

//    val xOffset = (horizontalPadding / 2) - anchor.marginStart// + 2.dpToPx(anchor.resources.displayMetrics)
//    val yOffset = verticalPadding / 2// + 4.dpToPx(anchor.resources.displayMetrics)
//    showAsDropDown(anchor, -xOffset, -yOffset)


//    val yOffset = anchor.height
//    val xOffset = anchor.width / 2 - (8.dpToPx(anchor.context.resources.displayMetrics))
//    showAsDropDown(anchor, 0, -anchor.height, Gravity.START)
//    val anchorPosition = IntArray(2)
//    anchor.getLocationOnScreen(anchorPosition)
//    val (x, y) = anchorPosition
//    println(x + y)
//    PopupWindowCompat.setOverlapAnchor(this, true)
//    showAtLocation(anchor, Gravity.START or Gravity.TOP, 0, 0)

//    val yOffset = anchor.height
//    val xOffset = anchor.width
//    showAsDropDown(anchor, -xOffset, -yOffset)

//    val location = IntArray(2).apply {
//        anchor.getLocationOnScreen(this)
//    }
//    val size = Size(
//        contentView.measuredWidth,
//        contentView.measuredHeight
//    )
//    println(size)
//    showAtLocation(
//        anchor,
//        Gravity.TOP or Gravity.START,
//        location[0],
//        location[1]
//    )
}

private fun PopupWindow.showAtAnchorLocation(
    anchor: View,
    menuPositioningData: MenuPositioningData
) {
    val anchorPosition = IntArray(2)

    // Apply the best fit animation style based on positioning
    animationStyle = if (menuPositioningData.reversed) {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuLeft
    } else {
        R.style.Mozac_Browser_Menu2_Animation_OverflowMenuRight
    }

    anchor.getLocationOnScreen(anchorPosition)
    val (x, y) = anchorPosition

//    val xOffset = (horizontalPadding / 2) + horizontalOffset.dpToPx(anchor.resources.displayMetrics)
//    val yOffset = (verticalPadding / 2) + verticalOffset.dpToPx(anchor.resources.displayMetrics)
//    println(xOffset + yOffset)

    PopupWindowCompat.setOverlapAnchor(this, true)
    showAtLocation(anchor, Gravity.START or Gravity.TOP, x + menuPositioningData.horizontalOffset, y - menuPositioningData.verticalOffset)
}

private fun getMaxAvailableHeightToTopAndBottom(anchor: View): Pair<Int, Int> {
    val anchorPosition = IntArray(2)
    val displayFrame = Rect()

    val appView = anchor.rootView
    appView.getWindowVisibleDisplayFrame(displayFrame)

    anchor.getLocationOnScreen(anchorPosition)

    val bottomEdge = displayFrame.bottom

    val distanceToBottom = bottomEdge - (anchorPosition[1] + anchor.height)
    val distanceToTop = anchorPosition[1] - displayFrame.top

    return distanceToTop to distanceToBottom
}

private fun getMaxAvailableWidthToLeftAndRight(anchor: View): Pair<Int, Int> {
    val anchorPosition = IntArray(2)
    val displayFrame = Rect()

    val appView = anchor.rootView
    appView.getWindowVisibleDisplayFrame(displayFrame)

    anchor.getLocationOnScreen(anchorPosition)

    val distanceToLeft = anchorPosition[0] - displayFrame.left
    val distanceToRight = displayFrame.right - (anchorPosition[0] + anchor.width)

    return distanceToLeft to distanceToRight
}
