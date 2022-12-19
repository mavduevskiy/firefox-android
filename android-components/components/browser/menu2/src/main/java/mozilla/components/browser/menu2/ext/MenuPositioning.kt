package mozilla.components.browser.menu2.ext

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.browser.menu2.R
import mozilla.components.concept.menu.MenuStyle
import mozilla.components.support.ktx.android.util.dpToPx

internal fun inferMenuPositioningData(
    containerView: View,
    anchor: View,
    style: MenuStyle? = null,
): MenuPositioningData {
    // Measure the menu allowing it to expand entirely.
    val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    containerView.measure(spec, spec)

    val (availableHeightToTop, availableHeightToBottom) = getMaxAvailableHeightToTopAndBottom(anchor)
    val (availableWidthToLeft, availableWidthToRight) = getMaxAvailableWidthToLeftAndRight(anchor)
    val containerHeight = containerView.measuredHeight

    val recyclerView =
        containerView.findViewById<RecyclerView>(R.id.mozac_browser_menu_recyclerView)
    val horizontalPadding = containerView.measuredWidth - recyclerView.measuredWidth
    val verticalPadding = containerHeight - recyclerView.measuredHeight

    val fitsUp = availableHeightToTop >= containerHeight
    val fitsDown = availableHeightToBottom >= containerHeight
    val reversed = availableWidthToLeft < availableWidthToRight

    val horizontalPaddingOffset = if (style?.completelyOverlap == true) {
        (horizontalPadding / 2)
    } else {
        0
    }
    val verticalPaddingOffset = if (style?.completelyOverlap == true) {
        (verticalPadding / 2)
    } else {
        0
    }

    val customHorizontalOffset = style?.horizontalOffset ?: 0
    val customVerticalOffset = style?.verticalOffset ?: 0

    val horizontalOffset = -(horizontalPaddingOffset + customHorizontalOffset)
    val verticalOffset = -(verticalPaddingOffset + customVerticalOffset)

//    val horizontalOffset = -((horizontalPadding / 2) + (style?.horizontalOffset?.dpToPx(anchor.resources.displayMetrics) ?: 0))
//    val verticalOffset = -((verticalPadding / 2) - (style?.verticalOffset?.dpToPx(anchor.resources.displayMetrics) ?: 0))
//
    return MenuPositioningData(
        fitsUp,
        fitsDown,
        reversed,
        containerHeight,
        horizontalOffset,
        verticalOffset,
    )
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

data class MenuPositioningData(
    val fitsUp: Boolean = false,
    val fitsDown: Boolean = false,
    val reversed: Boolean = false,
    @Px val containerHeight: Int = 0,
    @Px val horizontalOffset: Int = 0,
    @Px val verticalOffset: Int = 0,

//    @Px val availableHeightToTop: Int = 0,
//    @Px val availableHeightToBottom: Int = 0,
//    @Px val containerViewHeight: Int = 0,
)